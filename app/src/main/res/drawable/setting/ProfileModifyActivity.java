package com.example.pikitoryprofilesample.ui.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pikitoryprofilesample.R;
import com.example.pikitoryprofilesample.core.service.UserService;
import com.example.pikitoryprofilesample.core.utility.Utility;
import com.example.pikitoryprofilesample.network.SafeAsyncTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;

public class ProfileModifyActivity extends AppCompatActivity {
    private UserService userService = new UserService();
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private String userChoosenTask;

    private String userProfileImage;
    private String userName;
    private String userId;
    private String userProfileMsg;
    private TextView textUserId;
    private EditText editTextUserName;
    private EditText editTextUserProfileMsg;
    private ImageView imgUserProfileImage;

    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            // .showImageOnLoading( R.drawable.ic_default_profile )// resource or drawable
            .showImageForEmptyUri( R.drawable.ic_default_profile )// resource or drawable
            .showImageOnFail( R.drawable.ic_default_profile )// resource or drawable
            //.resetViewBeforeLoading( false )// default
            .delayBeforeLoading( 0 )
            //.cacheInMemory( false )// default
            .cacheOnDisc( true )// false is default
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

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        Intent intent = getIntent();

        userId = intent.getStringExtra("userId");
        System.out.println(userId);
        userProfileImage = intent.getStringExtra("userProfileImage");
        userName = intent.getStringExtra("userName");
        userProfileMsg = intent.getStringExtra("userProfileMsg");

        textUserId = (TextView) findViewById(R.id.tvUserId);
        editTextUserName = (EditText) findViewById(R.id.etUserName);
        editTextUserProfileMsg = (EditText) findViewById(R.id.etUserProfileMsg);
        imgUserProfileImage = (ImageView) findViewById(R.id.ProfileImageView);

        textUserId.setText(userId);
        //editTextUserName.setHint(userName);
        editTextUserName.setText(userName);
        editTextUserProfileMsg.setText(userProfileMsg);

        ImageLoader.getInstance().displayImage(userProfileImage, imgUserProfileImage, displayImageOption );
        imgUserProfileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {//code for deny
                }
                break;
        }
    }

    //사진찍을건지 갤러리에서 가져올건지
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(com.example.pikitoryprofilesample.ui.setting.ProfileModifyActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(com.example.pikitoryprofilesample.ui.setting.ProfileModifyActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //갤러리에서 사진파일 가져오기
    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    //카메라앱에서 사진찍고 사진가져오기
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_CAMERA);
        Log.e("--------------Camera","켜짐");
    }


    //가져온 사진 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA) {
                Log.e("--------------Camera", "꺼짐");
                galleryIntent();
                //onCaptureImageResult(data);
            }

        }
    }

    //카메라로 찍었을때 사진처리
//    private void onCaptureImageResult(Intent data) {
//
//        Log.e("--------------Camera",data.getExtras().get("data").toString());
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//
//
//        File imageFile = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//
//        FileOutputStream fo;
//
//        try {
//            imageFile.createNewFile();
//            fo = new FileOutputStream(imageFile);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        new FetchUserProfileImageAsyncTask(imageFile).execute();
//
//        imgUserProfileImage.setImageBitmap(thumbnail);
//    }

    //갤러리에서 가져왔을때 사진처리
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                System.out.println(picturePath);

                File imageFile = new File(picturePath);

                new FetchUserProfileImageAsyncTask(imageFile).execute();

                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgUserProfileImage.setImageBitmap(bm);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profilemodify,menu);
        return true;
    }

    //프로필 저장 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();
        String menuTitle = item.getTitle().toString();

        switch(itemid){
            case R.id.item1:{
                String updateUserName;
                String updateUserProfileMsg;

                System.out.println(editTextUserName.getText().toString());
                if("".equals(editTextUserName.getText().toString())){
                    Toast.makeText(this,"닉네임은 필수입니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "저장완료",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    updateUserName = editTextUserName.getText().toString();

                    updateUserProfileMsg = editTextUserProfileMsg.getText().toString();

                    new FetchUserProfileUpdateAsyncTask(userId,updateUserName,updateUserProfileMsg).execute();

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
            }
            case R.id.item2:{
                Intent intent = new Intent();
                Toast.makeText(this, "취소",Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //프로필 이미지 UPDATE
    private class FetchUserProfileImageAsyncTask extends SafeAsyncTask {

        File imageFile;

        FetchUserProfileImageAsyncTask(File imageFile){
            this.imageFile = imageFile;
        }
        @Override
        public Object call() throws Exception {
            return userService.updateUserProfileImage(imageFile,userId);
        }
//        @Override
//        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
//        }
//        @Override
//        protected void onSuccess(Object o) throws Exception {
//            super.onSuccess(o);
//        }
    }

    //닉네임, 상태메세지 UPDATE
    private class FetchUserProfileUpdateAsyncTask extends SafeAsyncTask {

        String user_id;
        String updateUserName;
        String updateUserProfileMsg;

        FetchUserProfileUpdateAsyncTask(String user_id, String updateUserName, String updateUserProfileMsg){
            this.user_id = user_id;
            this.updateUserName = updateUserName;
            this.updateUserProfileMsg = updateUserProfileMsg;
        }
        @Override
        public Object call() throws Exception {

            return userService.updateUserProfile(user_id, updateUserName, updateUserProfileMsg);
        }
//        @Override
//        protected void onException(Exception e) throws RuntimeException {
//            super.onException(e);
//        }
//        @Override
//        protected void onSuccess(Object o) throws Exception {
//            super.onSuccess(o);
//        }
    }
}
