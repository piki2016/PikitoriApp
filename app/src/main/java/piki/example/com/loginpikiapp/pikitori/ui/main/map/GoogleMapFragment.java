package piki.example.com.loginpikiapp.pikitori.ui.main.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PictureService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.ImageGalleryActivity;

/**
 * Created by admin on 2017-01-22.
 */
public class GoogleMapFragment extends Fragment {

    private static final String TAG = "GoogleMapFragment";
    MapView mMapView;
    private GoogleMap googleMap;
    private ClusterManager<Picture> mClusterManager;

    private List<PictureVo> pictureList = new ArrayList<PictureVo>();
    private List<Bitmap> bitmapList1 = new ArrayList<>();
    private Long user_no;
    private PictureService pictureService = new PictureService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        user_no = ((UserVo) Utils.getUserPreferences(getActivity(),"PikiUser")).getUser_no();
        new GetPictureAsyncTask(user_no).execute();

        return rootView;
    }

    @Override
    public void onStart() {
//        Toast.makeText(getActivity(),"map start",Toast.LENGTH_SHORT).show();
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Toast.makeText(getActivity(),"map 멈춰",Toast.LENGTH_SHORT).show();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bitmapList1 = null;
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private class GetPictureAsyncTask extends SafeAsyncTask<List<PictureVo>> {
            Long user_no;

            GetPictureAsyncTask(Long user_no){
            this.user_no = user_no;
        }

        @Override
        public List<PictureVo> call() throws Exception {
            pictureList = pictureService.getPicture(user_no);
            return pictureList;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            System.out.println("----------------Error GetPicture");
        }

        @Override
        protected void onSuccess(final List<PictureVo> pictureList) throws Exception {
            System.out.println("------------------------------Success GetPicture");
            for(PictureVo vo : pictureList){
                Log.d(TAG, "map: " + vo.getPicture_url());
            }

            new MakeBitmapToURLAsyncTask(pictureList).execute();

        }

        private class MakeBitmapToURLAsyncTask extends SafeAsyncTask<List<Bitmap>>{

            List<PictureVo> pictureList;

            MakeBitmapToURLAsyncTask(List<PictureVo> pictureList){
                this.pictureList = pictureList;
            }

            @Override
            public List<Bitmap> call() throws Exception {
                List<Bitmap> bitmapList = new ArrayList<>();
                for(int i = 0; i<pictureList.size();i++){
//                    bitmapList.add(ImageLoader.getInstance().displayImage(););
                    bitmapList.add(GetBitmapfromUrl(pictureList.get(i).getPicture_url_thumb()));
                    //bitmapList.add(GetBitmapfromUrl("http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif"));
                }
                return bitmapList;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                System.out.println("----------------Error GetPicture");
            }

            @Override
            protected void onSuccess(final List<Bitmap> bitmapList) throws Exception {
                bitmapList1 = bitmapList;
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;
                        //현재 위치를 표시.
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(36,128)),7));


                        mClusterManager = new ClusterManager<Picture>(getContext(), googleMap);
                        mClusterManager.setRenderer(new PersonRender());
                        googleMap.setOnCameraIdleListener(mClusterManager);
                        googleMap.setOnMarkerClickListener(mClusterManager);
                        googleMap.setOnInfoWindowClickListener(mClusterManager);

                        //사진이 모여있을때 마커의 반응
                        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Picture>() {
                            @Override
                            public boolean onClusterClick(Cluster<Picture> cluster) {
                                // Show a toast with some info when the cluster is clicked.
                                Log.d(TAG, "cluster: "+ cluster + "zoon: " + googleMap.getCameraPosition().zoom);
                                if(googleMap.getCameraPosition().zoom > 13){

                                    List<Picture> pictureList = new ArrayList<Picture>(cluster.getItems());
                                    ArrayList<String> imageList = new ArrayList<String>();
                                    for(Picture p : pictureList){
                                        imageList.add(p.filePath);
                                    }
                                    Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
                                    intent.putExtra("imageList", imageList.toArray(new String[imageList.size()]));
                                    ((pikiMainActivity)getActivity()).startActivityForResult(intent, BasicInfo.RESULT_CODE_IMAGE_GALLERY);

                                }else{
                                    String firstName = cluster.getItems().iterator().next().name;
                                    Toast.makeText(getContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

                                    // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
                                    // inside of bounds, then animate to center of the bounds.

                                    // Create the builder to collect all essential cluster items for the bounds.
                                    LatLngBounds.Builder builder = LatLngBounds.builder();
                                    for (ClusterItem item : cluster.getItems()) {
                                        builder.include(item.getPosition());
                                    }
                                    // Get the LatLngBounds
                                    final LatLngBounds bounds = builder.build();

                                    // Animate camera to the bounds
                                    try {
                                        googleMap.setMinZoomPreference(5);
                                        googleMap.setMaxZoomPreference(14);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                return true;
                            }
                        });

                        //여기선 반응 없음
                        mClusterManager.setOnClusterInfoWindowClickListener(new ClusterManager.OnClusterInfoWindowClickListener<Picture>() {
                            @Override
                            public void onClusterInfoWindowClick(Cluster<Picture> cluster) {
                            }
                        });

                        //사진이 하나만있을때 마커의 반응
                        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Picture>() {
                            @Override
                            public boolean onClusterItemClick(Picture picture) {
                                ArrayList<String> imageList = new ArrayList<String>();
                                imageList.add(picture.filePath);
                                Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
                                intent.putExtra("imageList", imageList.toArray(new String[imageList.size()]));
                                ((pikiMainActivity)getActivity()).startActivityForResult(intent, BasicInfo.RESULT_CODE_IMAGE_GALLERY);
                                return false;
                            }
                        });

                        //사진의 정보를 클릭했을때 반응
                        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Picture>() {
                            @Override
                            public void onClusterItemInfoWindowClick(Picture picture) {
                            }
                        });

                        for(int i = 0; i<pictureList.size(); i++) {
                            addItems(pictureList.get(i).getPicture_url(),
                                    pictureList.get(i).getPicture_lat(),
                                    pictureList.get(i).getPicture_lng(),
                                    pictureList.get(i).getPicture_location(),
                                    i);
                        }
                        mClusterManager.cluster();
                    }
                });
            }
        }
    }

    private class PersonRender extends DefaultClusterRenderer<Picture> {

        private final IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRender(){
            super(getActivity().getApplicationContext(),googleMap,mClusterManager);
            View multiProfile = getLayoutInflater(null).inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getActivity().getApplicationContext());
            mDimension = (int)getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int)getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Picture picture, MarkerOptions markerOptions) {
            // Draw a single picture.
            // Set the info window to show their name.
            //mImageView.setImageResource(picture.profilePhoto);

            //ImageLoader.getInstance().displayImage("http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif", mImageView, BasicInfo.displayImageOption );

            //mImageView.setImageURI(Uri.fromFile(new File(picture.filePath)));



            mImageView.setImageBitmap(bitmapList1.get(picture.getBitmapNo()));

            //ImageLoader.getInstance().displayImage( "file://" + picture.filePath, mImageView, displayImageOption );
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(picture.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Picture> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

             for(Picture p : cluster.getItems()) {

                // Draw 4 at most.
                if (profilePhotos.size() == 4) {
                    break;
                }
                String filePath = p.filePath;
//                InputStream is=null;
//             try {
//                    String url = "http://img.naver.net/static/www/u/2013/0731/nmms_224940510.gif";
//                    is = (InputStream)new URL(url).getContent();

                Drawable drawable = new BitmapDrawable(bitmapList1.get(p.getBitmapNo()));
                //Drawable drawable = new BitmapDrawable(getResources(), filePath);

//               } catch (Exception e) {
//                    e.printStackTrace();
//               }
//                Drawable drawable = Drawable.createFromStream(is,"src");

                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);

            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }

    }

    private void addItems(String picture_local_url, Double picture_lat, Double picture_lng, String picture_location, int bitmapNo) {
        mClusterManager.addItem(new Picture(position(picture_lat,picture_lng), picture_location, picture_local_url, bitmapNo));
    }

    private LatLng position(Double picture_lat, Double picture_lng) {
        return new LatLng(picture_lat,picture_lng);
    }

    public Bitmap GetBitmapfromUrl(String scr) {
        try {
            URL url=new URL(scr);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input=connection.getInputStream();
            Bitmap bmp = BitmapFactory.decodeStream(input);
            return bmp;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}