package piki.example.com.loginpikiapp.pikitori.ui;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.ui.Login.JoinUserActivity;
import piki.example.com.loginpikiapp.pikitori.ui.Login.LoginUserActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.DirectLinkVideoItem;

import static piki.example.com.loginpikiapp.R.id.btn_start;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_CAMERA;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_LOCATION;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SplashActivity";

    private PostService postService = new PostService();
    Long post_no = null;
    String request_user_id = null;
    static ImageView backgroundOne = null;
    static ImageView backgroundTwo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                if(key.equals("post_no")){
                    post_no = Long.parseLong((String)value);
                } else if(key.equals("request_user_id")){
                    request_user_id = (String)value;
                }
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
            if(post_no!=null) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("ALERT");
                dialog.setMessage(request_user_id + "님 에게\n" + post_no + "번 글의 수정권한을 허락하시겠습니까?`");
                dialog.setCancelable(true);
                dialog.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SplashActivity.this, "수락 누름", Toast.LENGTH_SHORT).show();
                        new RequestAcceptAsyncTask(i, post_no, request_user_id).execute();
                    }
                });
                dialog.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(SplashActivity.this, "거절 누름", Toast.LENGTH_SHORT).show();
                        new RequestAcceptAsyncTask(i, post_no, request_user_id).execute();
                    }
                });
                dialog.show();
            }
        }
        getSupportActionBar().hide();

        TextView btn_login = (TextView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        //animation
        backgroundOne = (ImageView) findViewById(R.id.background_one);
        backgroundTwo = (ImageView) findViewById(R.id.background_two);

        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.reverse();
        checkFirstPermission();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void checkFirstPermission() {
        //1. READ_EXTERNAL_STORAGE / WRITE_EXTERNAL_STORAGE / CAMERA
        Utils.checkPermissionStorageAndCamera(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login: {
                Intent i = new Intent(this, LoginUserActivity.class);
                startActivityForResult(i, BasicInfo.RESULT_CODE_LOGIN_USER);
            }
            break;
            case btn_start: {
                if (Utils.getBooleanPreferences(this, "user_login_status")) {
                    Intent i = new Intent(this, LoginUserActivity.class);
                    startActivityForResult(i, BasicInfo.RESULT_CODE_LOGIN_USER);
                    break;
                }
                    Intent i = new Intent(this, JoinUserActivity.class);
                    startActivityForResult(i, BasicInfo.RESULT_CODE_JOIN_USER);
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];

                    if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(getApplicationContext(),"READ_EXTERNAL_STORAGE ok",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"외부저장장치 읽기 권한 사용을 동의해 주셔야 이용이 가능합니다.",Toast.LENGTH_SHORT).show();
                            checkFirstPermission();
                        }
                    }

                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(getApplicationContext(),"WRITE_EXTERNAL_STORAGE ok",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"외부저장장치 쓰기 권한 사용을 동의해주셔야 이용이 가능합니다",Toast.LENGTH_SHORT).show();
                            checkFirstPermission();
                        }
                    }

                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(getApplicationContext(),"CAMERA ok",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"카메라 권한 사용을 동의해주셔야 이용이 가능합니다.",Toast.LENGTH_SHORT).show();
                            checkFirstPermission();
                        }
                    }
                }
            }
            break;
        }
    }

    public class RequestAcceptAsyncTask extends SafeAsyncTask<Object>{

        int status;
        Long post_no;
        String request_user_id;


        public RequestAcceptAsyncTask(int status, Long post_no, String request_user_id){
            this.status = status;
            this.post_no = post_no;
            this.request_user_id = request_user_id;
        }
        @Override
        public Object call() throws Exception {
            postService.postPermissionRequestAccept(status, post_no, request_user_id);
            return null;
        }

        @Override
        protected void onSuccess(Object o) throws Exception {
            super.onSuccess(o);
        }
    }
}
