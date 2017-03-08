package piki.example.com.loginpikiapp.pikitori.ui.main.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.mariotaku.simplecamera.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.ImageUtils;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.ui.main.camera.FocusAreaView;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostImageSelectActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.relay.PostRelayActivity;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_CAMERA;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_LOCATION;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.RESULT_CODE_CAMERA;

public class CameraActivity extends Activity implements CameraView.CameraListener, View.OnTouchListener, View.OnClickListener {

    private static final String TAG = "SimpleCameraSample";
    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/PIKITORI/";
    private CameraView mCameraView;
    private FocusAreaView mFocusAreaView;
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private LinearLayout wholeLinearLayout;
    private LinearLayout linearLayout;
    private LinearLayout horizontalScrollView;
    private ImageView btn_submit;
    private ImageView btn_cancle;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private ArrayList<String> captureList = new ArrayList<String>();
    private LocationManager locationManager;
    private GPSTracker gps;
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        mFocusAreaView = (FocusAreaView) findViewById(R.id.focus_area);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findViewById(R.id.take_photo).setOnClickListener(this);
        findViewById(R.id.front_camera).setOnClickListener(this);
        findViewById(R.id.back_camera).setOnClickListener(this);
        //findViewById(R.id.layer_paint).setOnClickListener(this);

        mCameraView.setCameraListener(this);
        mCameraView.setOnTouchListener(this);

        linearLayout = (LinearLayout) findViewById(R.id.scrolllinearlayout);
        horizontalScrollView = (LinearLayout) findViewById(R.id.horizontalscrollview);
        wholeLinearLayout = (LinearLayout) findViewById(R.id.wholelinearlayout);
        btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        btn_submit = (ImageView) findViewById(R.id.btn_submit);

        btn_cancle.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gps = new GPSTracker(this);

//        // GPS 프로바이더 사용가능여부
//        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // 네트워크 프로바이더 사용가능여부
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//        Utils.checkPermissionStorageAndCamera(this);
//        Log.d("Main", "isGPSEnabled=" + isGPSEnabled);
//        Log.d("Main", "isNetworkEnabled=" + isNetworkEnabled);
//
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                double lat = location.getLatitude();
//                double lng = location.getLongitude();
//
//                Log.d(TAG, "latitude: " + lat + ", longitude: " + lng);
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.d(TAG, "onStatusChanged");
//            }
//
//            public void onProviderEnabled(String provider) {
//                Log.d(TAG, "onProviderEnabled");
//            }
//
//            public void onProviderDisabled(String provider) {
//                Log.d(TAG, "onProviderDisabled");
//            }
//        };
//
//        // Register the listener with the Location Manager to receive location updates
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if(!gps.canGetLocation){
            Utils.checkPermissionStorageAndCamera(this);
            Utils.checkPermissionLocation(this);
//            gps.showSettingsAlert();
            /*위치정보 설정창*/
            chkGpsService();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wholeLinearLayout.getWidth() - 100, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalScrollView.setLayoutParams(params);
    }

    @Override
    public void onCameraInitialized(Camera camera) {

        findViewById(R.id.front_camera).setVisibility(Camera.getNumberOfCameras() > 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCameraOpeningError(Exception e) {
        Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setParameterBeforeStartPreview(Camera camera, Camera.Parameters parameters) {
        final List<int[]> fpsRanges = parameters.getSupportedPreviewFpsRange();
        Collections.sort(fpsRanges, Utils.FPS_RANGE_COMPARATOR);
        if (!fpsRanges.isEmpty()) {
            final int[] fpsRange = fpsRanges.get(0);
            parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mCameraView.isCameraAvailable()) {
            mCameraView.openCamera(0);
        }
    }

    @Override
    protected void onPause() {
        mCameraView.releaseCamera();
        super.onPause();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP: {
                if (mCameraView.touchFocus(event, new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        mFocusAreaView.endFocus();
                    }
                })) {
                    mFocusAreaView.startFocus(event.getX(), event.getY());
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.take_photo: {
                if (mCameraView.isAutoFocusing()) return;

                mCameraView.takePicture(null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        final Context context = getApplicationContext();

                        /*Bitmap For Write*/
                        final BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, o);

                        long dateTaken = System.currentTimeMillis();
                        String filename = DateFormat.format("yyyy-MM-dd-kk-mm-ss", dateTaken).toString() + ".jpg";
                        insertImage(getContentResolver(),gps, filename, dateTaken, PATH, filename, bitmap, data);
                        addExifInfo(filename);

                        try {
                            Bitmap saveBitmap = BitmapFactory.decodeFile(PATH + filename);
                            System.out.println("-----------------" + saveBitmap);
                            ExifInterface exif = new ExifInterface(PATH + filename);
                            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            int exifDegree = exifOrientationToDegrees(exifOrientation);
                            saveBitmap = rotate(saveBitmap, 90);

                            insertImage(getContentResolver(),gps, filename, dateTaken, PATH, filename, saveBitmap, data);
                        } catch (Exception e) {
                            System.out.println("----------------error 비트맵 rotate 90");
                        }

                        captureList.add("file:/" + PATH + filename);

                        Log.d(TAG, "captureList: " + captureList);

                        final int requiredWidth = 320, requiredHeight = 320;
                        final float widthRatio = o.outWidth / (float) requiredWidth;
                        final float heightRatio = o.outHeight / (float) requiredHeight;
                        o.inJustDecodeBounds = false;
                        o.inSampleSize = (int) Math.max(1, Math.floor(Math.max(widthRatio, heightRatio)));
                        final Bitmap captured = BitmapFactory.decodeByteArray(data, 0, data.length, o);
                        if (captured == null) return;
                        final int capturedWidth = captured.getWidth(), capturedHeight = captured.getHeight();
                        final float requiredRatio = (float) requiredWidth / requiredHeight;
                        final float capturedRatio = (float) capturedWidth / capturedHeight;
                        final Matrix m = new Matrix();
                        final int x, y, width, height;
                        if (requiredRatio > capturedRatio) {
                            // fit width
                            /**
                             * +---------------------+
                             * |                     |
                             * |+-------------------+|
                             * ||                   ||
                             * ||      Required     ||
                             * ||                   ||
                             * |+-------------------+|
                             * |        Captured     |
                             * +---------------------+
                             * =OR=
                             *    +-------------+
                             *    |  Captured   |
                             * +--+-------------+--+
                             * |  |             |  |
                             * |  |   Required  |  |
                             * |  |             |  |
                             * +--+-------------+--+
                             *    |             |
                             *    +-------------+
                             */
                            width = capturedWidth;
                            height = Math.round(capturedWidth / requiredRatio);
                            x = 0;
                            y = (capturedHeight - height) / 2;
                            if (capturedWidth > requiredWidth) {
                                // Captured picture is larger than required
                                float scale = (float) requiredWidth / capturedWidth;
                                m.setScale(scale, scale);
                            }
                        } else {
                            width = Math.round(capturedHeight * requiredRatio);
                            height = capturedHeight;
                            x = (capturedWidth - width) / 2;
                            y = 0;
                            if (capturedHeight > requiredHeight) {
                                // Captured picture is larger than required
                                float scale = (float) requiredHeight / capturedHeight;
                                m.setScale(scale, scale);
                            }
                        }
                        m.postRotate(mCameraView.getPictureRotation());
                        final Bitmap transformed = Bitmap.createBitmap(captured, x, y, width, height, m, true);
                        final ImageView view = new ImageView(context);
                        view.setImageBitmap(transformed);
                        final Toast toast = new Toast(context);
                        toast.setView(view);
                        toast.show();


                        if (linearLayout.getChildCount() > 0) {
                            linearLayout.removeAllViews();
                        }

                        bitmapList.add(transformed);
                        addImageViewFromLinearLayout(bitmapList);
//                        System.out.println(uri.toString());
                    }
                });
                break;
            }
            case R.id.front_camera: {
                mCameraView.openCamera(1);
                break;
            }
            case R.id.back_camera: {
                mCameraView.openCamera(0);
                break;
            }
            case R.id.btn_submit: {
                Intent intent = new Intent(getApplicationContext(), PostRelayActivity.class);
                intent.putStringArrayListExtra("selectedImage", captureList);
                startActivityForResult(intent, RESULT_CODE_CAMERA);
            }
        }
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            System.out.println("--------------rotate 들어옴");
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                System.out.println("-----------------메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.");// 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    @Override
    public void onError(int error, Camera camera) {
        switch (error) {
            case Camera.CAMERA_ERROR_SERVER_DIED: {
                finish();
                break;
            }
        }
    }

    private Uri insertImage(ContentResolver cr, GPSTracker gps, String name, long dateTaken,
                            String directory, String filename, Bitmap source, byte[] jpegData) {
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            //if (file.createNewFile()) {
            outputStream = new FileOutputStream(file);
            if (source != null) {
                source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                System.out.println("----------------22222");
            } else {
                outputStream.write(jpegData);
                System.out.println("----------------11111");
            }
            //}
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, filePath);
        if(gps.canGetLocation){
            values.put(MediaStore.Images.Media.LATITUDE,gps.getLatitude());
            values.put(MediaStore.Images.Media.LONGITUDE,gps.getLongitude());
            Log.d(TAG, " getLatitude : "+gps.getLatitude()+" getLongitude: " + gps.getLongitude() + "address : " );
        }

        Log.d(TAG, " values : " + values.toString());
        Log.d(TAG, " filepath: " + filePath);
        return cr.insert(IMAGE_URI, values);
    }

    private void addImageViewFromLinearLayout(List<Bitmap> bitmapList) {

        for (int i = 0; i < bitmapList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 8, 2);
            imageView.setImageBitmap(ImageUtils.getRoundedCroppedBitmap(bitmapList.get(i), 200));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            linearLayout.addView(imageView);
        }
    }

    private boolean chkGpsService() {

        /*str = "network" | "gps"*/
        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).create().show();
            return false;

        } else {
            return true;
        }
    }

    private boolean addExifInfo(String filename) {
        try {
            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;

            Utils.checkPermissionStorageAndCamera(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            double lng = 0;
            double lat = 0;
            if (lastKnownLocation != null) {

                lng = lastKnownLocation.getLongitude();
                lat = lastKnownLocation.getLatitude();

                Log.d(TAG, "addExifInfo: "+"longtitude=" + lng + ", latitude=" + lat);
            }

            ExifInterface exif = new ExifInterface(PATH + filename);

            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Double.toString(lng));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(lat));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, grantResults.toString());
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "외부저장장치 읽기 권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "외부저장장치 쓰기 권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "카메라 권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //사용권한에 대한 콜백을 받음.
                } else {
                    //사용자가 권한 동의를 안한 경우.
                    Toast.makeText(getApplicationContext(), "위치(GPS)권한 사용을 동의해주셔야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
        }
    }

}