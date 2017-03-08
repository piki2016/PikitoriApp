package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PictureService;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

public class ImageGalleryActivity extends AppCompatActivity {

    private static final String TAG = "ImageGalleryActivity";
    private String[] IMAGE_URLS;
    private List<String> images = new ArrayList<String>();
    PictureService pictureService = new PictureService();
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().clearDiskCache();
        setContentView(R.layout.activity_image_gallery);
        getSupportActionBar().hide();

        if(getIntent().getStringArrayExtra("imageList")!=null){
            Log.d(TAG,"imageList" );
            IMAGE_URLS = getIntent().getStringArrayExtra("imageList");
            for(String s: IMAGE_URLS){
                Log.d(TAG,"image map: " + s);
            }
            updateUI();
        }else{
//          Long no =  getIntent().getLongExtra("postno",0) > 0 ? getIntent().getLongExtra("postno",0) : ((UserVo) Utils.getUserPreferences(this,"PikiUser")).getUser_no() ;
            Long no =  getIntent().getLongExtra("post_no",0) > 0 ? getIntent().getLongExtra("post_no",0) : -1 ;
            Log.d(TAG,"post_no: "+ no);
            PostVo post =new PostVo();
            post.setPost_no(no);
            new FetchGalleryTask(post).execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void updateUI(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_image_gallery);
        viewPager.setAdapter(new ImagePagerAdapter());
        viewPager.setCurrentItem(0);
    }

    private class ImagePagerAdapter extends PagerAdapter{
//        private  final String[] IMAGE_URLS = Constants.IMAGES;
        private DisplayImageOptions options;
        private LayoutInflater inflater;

        ImagePagerAdapter(){
            inflater = LayoutInflater.from(getApplicationContext());

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }


         @Override
        public int getCount() {
             return IMAGE_URLS.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
             View imageLayout = inflater.inflate(R.layout.gallery_pager_item, container, false);
             assert imageLayout !=null;

            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            ImageLoader.getInstance().displayImage(
                    IMAGE_URLS[position],
                    imageView,
                    options,
                    new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    });
            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

    private class FetchGalleryTask extends SafeAsyncTask<List<PictureVo>>{

        PostVo post;
        public FetchGalleryTask(PostVo post) {
            this.post = post;
        }

        @Override
        public List<PictureVo> call() throws Exception {
            return pictureService.getPictureGallery(getApplicationContext(),post);
        }

        @Override
        protected void onSuccess(List<PictureVo> list) throws Exception {
            super.onSuccess(list);
            for(PictureVo post: list){
                images.add(post.getPicture_url());
                Log.d(TAG, ""+post);
            }
            IMAGE_URLS = images.toArray(new String[images.size()]);
            Log.d(TAG,"list:" + IMAGE_URLS);

            updateUI();
        }
    }
}
