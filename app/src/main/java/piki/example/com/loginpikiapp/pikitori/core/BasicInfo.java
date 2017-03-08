package piki.example.com.loginpikiapp.pikitori.core;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import piki.example.com.loginpikiapp.R;

/**
 * Created by admin on 2017-01-10.
 */

public class BasicInfo {

         public static final Boolean develope_test = false;
        public static final String domain = "http://www.pikitori.com/pikitori/";
//        public static final String domain = "http://192.168.1.4:8080/facebookSite1/";
//        public static final String domain = "http://192.168.1.48:8088/facebookSite1";
//    public static final String domain = "http://192.168.1.4:8080/pikitori/";
    public static final String tmpfolder = "/pikitmp";
    public static final int RESPONSE_OK = 200;

    //permission
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 30;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 31;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 32;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 33;

    // Activity
    public static final int RESULT_CODE_LOGIN_USER = 51;
    public static final int RESULT_CODE_JOIN_USER = 52;
    public static final int RESULT_CODE_PIKIMAIN = 53;
    public static final int RESULT_CODE_PROFILE_MODIFY = 54;
    public static final int RESULT_CAMERA = 55;
    public static final int RESULT_SELECT_FILE = 56;
    public static final int RESULT_LOAD_IMAGE = 57;
    public static final int RESULT_CODE_IMAGE_GALLERY = 57;
    public static final int RESULT_CODE_IMAGE_SELECT_LIST = 58;
    public static final int RESULT_IMAGE_SELECT = 59;
    public static final int RESULT_POST_UPDATE = 60;
    public static final int RESULT_CODE_CAMERA = 61;

    //  Google Login
    public static final int SIGNED_IN = 0;
    public static final int STATE_SIGNING_IN = 1;
    public static final int STATE_IN_PROGRESS = 2;
    public static final int RC_SIGN_IN = 0;

    /*DB DATA*/
    public static final int PIKIUSER = 1;
    public static final int GOOGLEUSER = 2;
    public static final int FACEUSER = 3;

    public static final boolean PIKIUSER_SIGNED_IN = true;

    public static String session = "";

    public static DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            // .showImageOnLoading( R.drawable.ic_default_profile )// resource or drawable
            .showImageForEmptyUri(R.drawable.ic_default_profile)// resource or drawable
            .showImageOnFail(R.drawable.ic_default_profile)// resource or drawable
            //.resetViewBeforeLoading( false )// default
            .delayBeforeLoading(0)
            //.cacheInMemory( false )// default
            .cacheOnDisc(true)// false is default
            //.preProcessor(...)
            //.postProcessor(...)
            //.extraForDownloader(...)
            //.considerExifParams( false )// default
            //.imageScaleType( ImageScaleType.IN_SAMPLE_POWER_OF_2 )// default
            //.bitmapConfig( Bitmap.Config.ARGB_8888 )// default
            //.decodingOptions(...)
            //.displayer( new SimpleBitmapDisplayer() )// default
            //.handler( new Handler() )// default
            .build();

}
