package piki.example.com.loginpikiapp.pikitori.core;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.domain.ImageModel;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ImageUtils {

    public final static int DISPLAYWIDHT = 1024;
    public final static int DISPLAYHEIGHT = 768;
    private static final String TAG = "ImageUtils";

    public static Map<String, ArrayList<ImageModel>> getImagesFromExternalByDay(Context context) {

        TreeMap<String, ArrayList<ImageModel>> folders = new TreeMap<>();

        String pictureCols[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pictureCols, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        ArrayList<ImageModel> imageslist = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",Locale.ENGLISH);
        try {
            for (int index = 0; index < cursor.getCount(); index++) {

                String id = cursor.getString(cursor.getColumnIndex(pictureCols[0]));
                String imageUri = cursor.getString(cursor.getColumnIndex(pictureCols[1]));
                String bucket = cursor.getString(cursor.getColumnIndex(pictureCols[2]));
                String latitude = cursor.getString(cursor.getColumnIndex(pictureCols[3]));
                String longitude = cursor.getString(cursor.getColumnIndex(pictureCols[4]));
                Long date1 = cursor.getLong(cursor.getColumnIndex(pictureCols[5]));
                String day = sdf.format(new Date(date1));

                Log.d(TAG, "latitude: " + latitude + " longitude: " + longitude + "date: " + day + " id: " + id + "image:" + imageUri );

                if (folders.containsKey(day)) {
                    ImageModel tmp = new ImageModel(id, "file:/" + imageUri, bucket, false);
                    tmp.setLat(latitude);
                    tmp.setLng(longitude);
                    tmp.setDay(day);
                    folders.get(day).add(tmp);
                } else {
                    imageslist = new ArrayList<ImageModel>();
                    ImageModel tmp = new ImageModel(id, "file:/" + imageUri, bucket, false);
                    tmp.setLat(latitude);
                    tmp.setLng(longitude);
                    tmp.setDay(day);
                    imageslist.add(tmp);
                    Log.d(TAG, "day added : " + day);
                    folders.put(day, imageslist);
                }
                cursor.moveToPosition(index + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return folders;
    }

    public static ArrayList<ImageModel> getImagesFromExternal(Context context) {

        ArrayList<ImageModel> imageslist = new ArrayList<ImageModel>();

        String pictureCols[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pictureCols, null, null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        try {
            for (int index = 0; index < cursor.getCount(); index++) {

                String id = cursor.getString(cursor.getColumnIndex(pictureCols[0]));
                String imageUri = cursor.getString(cursor.getColumnIndex(pictureCols[1]));
                String bucket = cursor.getString(cursor.getColumnIndex(pictureCols[2]));
                String latitude = cursor.getString(cursor.getColumnIndex(pictureCols[3]));
                String longitude = cursor.getString(cursor.getColumnIndex(pictureCols[4]));

                ImageModel tmp = new ImageModel(id, imageUri, bucket, false);
                tmp.setLat(latitude);
                tmp.setLng(longitude);
                imageslist.add(tmp);

                cursor.moveToPosition(index + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return imageslist;
    }

    public static ImageModel getImagesFromExternal(Context context,String filename) {

        ImageModel tmp = null;

        String pictureCols[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
        };

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pictureCols, MediaStore.Images.Media.DATA+"=?", new String[]{filename},null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }

        try {
            for (int index = 0; index < cursor.getCount(); index++) {

                String id = cursor.getString(cursor.getColumnIndex(pictureCols[0]));
                String imageUri = cursor.getString(cursor.getColumnIndex(pictureCols[1]));
                String bucket = cursor.getString(cursor.getColumnIndex(pictureCols[2]));
                String latitude = cursor.getString(cursor.getColumnIndex(pictureCols[3]));
                String longitude = cursor.getString(cursor.getColumnIndex(pictureCols[4]));

                tmp = new ImageModel(id, imageUri, bucket, false);
                tmp.setLat(latitude);
                tmp.setLng(longitude);
                Log.d(TAG,""+tmp);

                cursor.moveToPosition(index + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tmp;
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        if (uri.toString().startsWith("file://")) {
            return uri.toString().replaceAll("file://", "");
        }
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(columnIndex);
    }

    public static DisplayImageOptions universalImageConfiguration(final int width, final int height) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_default_profile)
                .showImageOnFail(R.drawable.ic_default_profile)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, width, height, false);
                    }
                })
                .build();
        return options;
    }

    public List<Bitmap> renderImages(Context context, List<String> selectedImage) {
        int size = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 4;
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = false;

        for (String uri : selectedImage) {
            if (uri.startsWith("file")) {
                uri = uri.substring(6, uri.length());
                Log.d(TAG, "file " + uri);
            }
            Bitmap org = BitmapFactory.decodeFile(uri, bmpFactoryOptions);

            //캔버스 생성
//            Bitmap mbitmap = Bitmap.createBitmap(4 * size, 3 * size, Bitmap.Config.ARGB_8888);
//            Canvas mcanvas = new Canvas(mbitmap);
//            mcanvas.drawColor(Color.BLACK);

            //mcanvas.drawBitmap();

            Bitmap msized = null;
            //비율 만들기
            float widthRatio = org.getWidth() / (float) DISPLAYWIDHT;
            float heightRatio = org.getHeight() / (float) DISPLAYHEIGHT;

            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio >= heightRatio) {

                    msized = Bitmap.createScaledBitmap(org, (int) ((float) org.getWidth() / widthRatio), (int) ((float) org.getHeight() / widthRatio), false);
                    System.out.println("너비가 큰 사진수정: " + msized.getWidth() + ":" + msized.getHeight());
//                    mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
                } else {

                    msized = Bitmap.createScaledBitmap(org, (int) (org.getWidth() / heightRatio), (int) (org.getHeight() / heightRatio), false);
                    System.out.println("높이가 큰 사진수정: " + msized.getWidth() + ":" + msized.getHeight());
//                    mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
                }
            } else {
                System.out.println("그냥 작은사진");
                msized = org;
//                mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
            }
            System.out.println("ratio " + widthRatio + ":" + heightRatio);
            System.out.println("image size: w: " + org.getWidth() + " h: " + org.getHeight() + " rew: " + msized.getWidth() + " reh: " + msized.getHeight());
            bitmapList.add(msized);
        }
        return bitmapList;
    }

    public Bitmap renderImage(Context context, String renderImage) {
        int size = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 4;

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = false;

        if (renderImage.startsWith("file")) {
            renderImage = renderImage.substring(6, renderImage.length());
            Log.d(TAG, "file " + renderImage);
        }
        Bitmap org = BitmapFactory.decodeFile(renderImage, bmpFactoryOptions);

        Bitmap msized = null;
        //비율 만들기
        float widthRatio = org.getWidth() / (float) DISPLAYWIDHT;
        float heightRatio = org.getHeight() / (float) DISPLAYHEIGHT;

        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio >= heightRatio) {

                msized = Bitmap.createScaledBitmap(org, (int) ((float) org.getWidth() / widthRatio), (int) ((float) org.getHeight() / widthRatio), false);
                System.out.println("너비가 큰 사진수정: " + msized.getWidth() + ":" + msized.getHeight());
//                    mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
            } else {

                msized = Bitmap.createScaledBitmap(org, (int) (org.getWidth() / heightRatio), (int) (org.getHeight() / heightRatio), false);
                System.out.println("높이가 큰 사진수정: " + msized.getWidth() + ":" + msized.getHeight());
//                    mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
            }
        } else {
            System.out.println("그냥 작은사진");
            msized = org;
//                mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
        }
        System.out.println("ratio " + widthRatio + ":" + heightRatio);
        System.out.println("image size: w: " + org.getWidth() + " h: " + org.getHeight() + " rew: " + msized.getWidth() + " reh: " + msized.getHeight());
        return msized;

    }

    public List<File> saveFiles(List<Bitmap> bitmapList, List<String> selectedImage) {
        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < bitmapList.size(); i++) {
            if(bitmapList.get(i)!=null) {
                String filepath = selectedImage.get(i);
                String filename = filepath.substring(filepath.lastIndexOf("/"), filepath.indexOf("."));
                fileList.add(i, new File(saveBitmaptoPNG(getApplicationContext(), bitmapList.get(i), filename)));
            }
        }
        return fileList;
    }

    public String saveBitmaptoPNG(Context context, Bitmap bitmap, String name) {
        String ex_storage = context.getCacheDir().getAbsolutePath();
//        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        System.out.println(ex_storage);
        String file_name = name ;
        String folder_path = ex_storage + BasicInfo.tmpfolder;
        String path = folder_path + File.separator + file_name;

        File file_path = null;
        try {
            file_path = new File(folder_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        return path;
    }

    public boolean deleteBitmapCache(Context context) {
        String ex_storage = context.getCacheDir().getAbsolutePath();
        String pikitmp = ex_storage + File.separator + BasicInfo.tmpfolder;
        File tempFile = new File(pikitmp);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        return true;
    }

    public static Uri makePath(Context context, String filename) {

        String path = context.getCacheDir().getAbsolutePath();
        System.out.println(path);
        String folderPath = path + File.separator + "pikitmp";

        //1. pikitmp 폴더 생성.
        File fileFolderPath = new File(folderPath);
        fileFolderPath.mkdir();

        //2. 파일 생성.
        String filePath = folderPath + File.separator + filename + ".png";
        File file = new File(filePath);

        return Uri.fromFile(file);
    }

    public Double convertToDegree(String stringDMS) {
        Double result = null;

        if(stringDMS.contains("/")||stringDMS.contains(",")) {
            String[] DMS = stringDMS.split(",", 3);

            String[] stringD = DMS[0].split("/", 2);
            Double D0 = new Double(stringD[0]);
            Double D1 = new Double(stringD[1]);
            Double FloatD = D0 / D1;

            String[] stringM = DMS[1].split("/", 2);
            Double M0 = new Double(stringM[0]);
            Double M1 = new Double(stringM[1]);
            Double FloatM = M0 / M1;

            String[] stringS = DMS[2].split("/", 2);
            Double S0 = new Double(stringS[0]);
            Double S1 = new Double(stringS[1]);
            Double FloatS = S0 / S1;

            result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));
        } else{
            result = Double.parseDouble(stringDMS);
        }
        return result;
    }

    public  String findAddress(Context context, double lat, double lng) {
        String currentLocationAddress = null;
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> list;
        try {
            if (geocoder != null) {
                // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                list = geocoder.getFromLocation(lat, lng, 1);
                // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                if (list != null && list.size() > 0) {
                    Address addr = list.get(0);
                    // 주소
                    currentLocationAddress = addr.getCountryName() + " " + " " + addr.getLocality() + " " + addr.getThoroughfare() + " " + addr.getFeatureName();
                }
            }

        } catch (IOException e) {
            Toast.makeText(context, "주소취득 실패", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return currentLocationAddress;
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if(bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        }else {
            finalBitmap = bitmap;
        }
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(finalBitmap.getWidth() / 2+0.7f, finalBitmap.getHeight() / 2+0.7f,
                finalBitmap.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }
}
