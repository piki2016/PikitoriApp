package piki.example.com.loginpikiapp.pikitori.ui.main.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostImageSelectActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostWriteActivity;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.displayImageOption;
import static piki.example.com.loginpikiapp.pikitori.core.ImageUtils.getRealPathFromURI;


public class ProfileModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileModifyActivity";

    private String userChoosenTask;
    private boolean file_updated = false;

    private ImageView profile_bg;
    private TextView textUserId;
    private EditText editTextUserName;
    private EditText editTextUserProfileMsg;
    private ImageView imgUserProfileImage;

    private File imageFile;
    Uri imageFileUri;
    ProgressDialog progressDialog;
    UserService userService = new UserService();
    UserVo user;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        getSupportActionBar().setTitle("프로필 수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_bg = (ImageView) findViewById(R.id.profile_background);
        textUserId = (TextView) findViewById(R.id.tvUserId);
        editTextUserName = (EditText) findViewById(R.id.etUserName);
        editTextUserProfileMsg = (EditText) findViewById(R.id.etUserProfileMsg);
        imgUserProfileImage = (ImageView) findViewById(R.id.ProfileImageView);
        imgUserProfileImage.setOnClickListener(this);

        user = Utils.getUserPreferences(getApplicationContext(), "PikiUser");
        Log.d(TAG, "trans user success : " + user);
        if (user == null) {
            Toast.makeText(getApplicationContext(), "권한이 없는 접근 입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        new UpdateInfoTask(getApplicationContext()).execute();

    }

    @Override
    protected void onStart() {
        super.onStart();

//        ComponentName callingApplication = getCallingActivity();
//        Log.d(TAG,callingApplication.getShortClassName());
    }

    private void updateUI(UserVo user) {
        textUserId.setText(user.getUser_id());
        editTextUserName.setText(user.getUser_name());
        editTextUserName.addTextChangedListener(new ValidationWatcher(editTextUserName));
        editTextUserProfileMsg.setText(user.getUser_profile_msg());
        editTextUserProfileMsg.addTextChangedListener(new ValidationWatcher(editTextUserProfileMsg));
        ImageLoader.getInstance().displayImage(user.getUser_profile_url_thumb(), imgUserProfileImage, displayImageOption);
        ImageLoader.getInstance().displayImage(user.getUser_profile_url(), profile_bg,displayImageOption);
        profile_bg.setColorFilter(0xff888888, PorterDuff.Mode.SCREEN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ProfileImageView: {
                file_updated = true;
                selectImage();
            }
            break;
        }
    }

    private void selectImage() {
        final String take_photo = "CAMERA";
        final String take_Library = "GALLERY";
        final String cancel = "CANCEL";
        final CharSequence[] items = {take_photo, take_Library, cancel};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean grant = Utils.checkPermissionStorageAndCamera(ProfileModifyActivity.this);

                if (items[i].equals(take_photo)) {
                    userChoosenTask = take_photo;
                    if (grant) {
                        cameraIntent();
                    }
                } else if (items[i].equals(take_Library)) {
                    userChoosenTask = take_Library;
                    if (grant) {
                        galleryIntent();
                    }
                } else if (items[i].equals(cancel)) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, BasicInfo.RESULT_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent(this, PostImageSelectActivity.class);
        startActivityForResult(intent,BasicInfo.RESULT_CODE_IMAGE_SELECT_LIST);
//        intent.setType("image/*");
//        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(Intent.createChooser(intent, "Select File"), BasicInfo.RESULT_SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("camera onActivityResult: " + data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == BasicInfo.RESULT_CODE_IMAGE_SELECT_LIST){
                if(("fail".equals(data.getStringExtra("uri")))){
                    Toast.makeText(getApplicationContext(),"올바르지 못합니다.",Toast.LENGTH_SHORT).show();
                }else{
                    imageFileUri = Uri.fromFile(new File(data.getStringExtra("uri").replace("file://","")));
                }
            }

                if (imageFileUri != null) {
                    Log.d(TAG, "update UIL "+ imageFileUri  );
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    try {
                       bmp = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(imageFileUri), null, options);
//                        bmp = BitmapFactory.decodeFile(data.getStringExtra("uri"));
                        imgUserProfileImage.setImageBitmap(bmp);
                        profile_bg.setImageBitmap(bmp);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

//    //갤러리에서 가져왔을때 사진처리
//    private void onSelectFromGalleryResult(Intent data) {
//
//        Bitmap bm = null;
//        if (data != null) {
//            try {
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
//
//                System.out.println(picturePath);
//                imageFile = new File(picturePath);
//                new UpdateUserProfile_img(imageFile).execute();
//
//                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        imgUserProfileImage.setImageBitmap(bm);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_modify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
            case R.id.save: {
                progressDialog = new ProgressDialog(ProfileModifyActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("수정하고 있습니다. 기다려주세요. ");
                progressDialog.show();
                UserVo update_user = user;
                update_user.setUser_name(editTextUserName.getText().toString());
                update_user.setUser_profile_msg(editTextUserProfileMsg.getText().toString());
                if (check_name || check_status_msg) {
                    //1. 사진을 저장합니다.
                    if (imageFileUri == null) {
                        new UpdateUserProfile(update_user).execute();
                    } else {
                        imageFile = new File(getRealPathFromURI(getApplicationContext(), imageFileUri));
                        Log.d(TAG,"imagefile: " + imageFile);
                        new UpdateUserProfileWithImage(update_user, imageFile).execute();
                    }
                }

            }
        }
        return true;
    }

    private boolean check_name = true;
    private boolean check_status_msg = true;

    private class ValidationWatcher implements TextWatcher {
        private View target_view;

        public ValidationWatcher(View view) {
            this.target_view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (target_view.getId()) {
                case R.id.etUserName: {
                    EditText editTextUserName = (EditText) target_view;
                    if (charSequence.length() >= 1) {
                        if (!Utils.isLengthNickname(charSequence.toString())) {
                            editTextUserName.setError("3자리이상 20자이하로 닉네임을 설정해주세요 ");
                            check_name = false;
                        } else {
                            editTextUserName.setError(null);
                            check_name = true;
                        }
                    }
                }
                break;
                case R.id.etUserProfileMsg: {
                    EditText etUserProfileMsg = (EditText) target_view;
                    if (!Utils.isLengthProfileMsg(charSequence.toString())) {
                        etUserProfileMsg.setError("상태메시지는 100자 이하로 적어주세요. ");
                        check_status_msg = false;
                    } else {
                        etUserProfileMsg.setError(null);
                        check_status_msg = true;
                    }

                }
                break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class UpdateUserProfileWithImage extends SafeAsyncTask<JSONResult> {
        UserVo update_user;
        File updateImg;

        public UpdateUserProfileWithImage(UserVo user, File imageFile) {
            this.update_user = user;
            this.updateImg = imageFile;
            Log.d(TAG, "updateUserProfileImage: " + update_user);
        }

        @Override
        public JSONResult call() throws Exception {
            return userService.updateProfile(getApplicationContext(), update_user, updateImg);
        }

        @Override
        protected void onSuccess(JSONResult result) throws Exception {
            Log.d(TAG, "success With Image: " + (UserVo) result.getData());
            if ("success".equals(result.getResult())) {
                //3. 로그인 유저 정보 저장
                Utils.setUserPreferences(getApplicationContext(), "PikiUser", (UserVo) result.getData());
            }
            progressDialog.dismiss();
            finish();

        }
    }

    private class UpdateUserProfile extends SafeAsyncTask<JSONResult> {
        UserVo update_user;

        public UpdateUserProfile(UserVo user) {
            this.update_user = user;
        }

        @Override
        public JSONResult call() throws Exception {
            return userService.updateProfile(getApplicationContext(), user);
        }

        @Override
        protected void onSuccess(JSONResult result) throws Exception {
            Log.d(TAG, "success : " + (UserVo) result.getData());
            if ("success".equals(result.getResult())) {
                //3. 로그인 유저 정보 저장
                Utils.setUserPreferences(getApplicationContext(), "PikiUser", (UserVo) result.getData());
            }
            progressDialog.dismiss();
            finish();
        }
    }

    private class UpdateInfoTask extends SafeAsyncTask<JSONResult> {
        Context context;
        public UpdateInfoTask(Context context) {
            this.context = context;
        }

        @Override
        public JSONResult call() throws Exception {
            Log.d(TAG," 프로필 가져오기");
            return userService.getUserProfileInfo(context);
        }

        @Override
        protected void onSuccess(JSONResult jsonResult) throws Exception {
            if("fail".equals(jsonResult.getResult())){
                Toast.makeText(getApplicationContext(),"update user info 실패",Toast.LENGTH_SHORT).show();
            }else{
                user = (UserVo)jsonResult.getData();
                updateUI(user);
                Log.d(TAG,"UpdateTask" + (UserVo)jsonResult.getData());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgUserProfileImage.setImageBitmap(null);
        profile_bg.setImageBitmap(null);
        System.gc();

    }
}