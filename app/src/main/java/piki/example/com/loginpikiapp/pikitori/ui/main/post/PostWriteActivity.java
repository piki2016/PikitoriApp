package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PictureService;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.core.ImageUtils;
import piki.example.com.loginpikiapp.pikitori.core.TagParse;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.ImageModel;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.TagVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;


public class PostWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PostWriteActivity";
    private String[] videoSpeedValue = {"0.5초", "1초", "1.5초", "2초"};

    private PostService postService = new PostService();
    private PictureService pictureService = new PictureService();
    private ImageUtils imageUtils = new ImageUtils();

    private Timer timer = new Timer();
    private int timeLimit;
    private double speed = 1;


//  private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private List<String> selectedImage;
    private List<PictureVo> pictureList = new ArrayList<>();
    private List<TagVo> tagList = new ArrayList<TagVo>();

    private ImageView sampleVideo;
    private EditText editTextPost;
    private SeekBar seekBar;
    private TextView videoSpeedSet;
    private RadioGroup radioGroup;
    private Button btn_play;

    ProgressDialog postDialog;

    private Long ispublic = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        sampleVideo = (ImageView) findViewById(R.id.sampleVideo);
        editTextPost = (EditText) findViewById(R.id.editTextPost);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        videoSpeedSet = (TextView) findViewById(R.id.videoSpeedSet);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        sampleVideo.setOnClickListener(this);
        btn_play.setVisibility(View.INVISIBLE);

        selectedImage = getIntent().getStringArrayListExtra("selectedImage");
        Log.d(TAG, "selectedImage: recv: "+ selectedImage);
        timeLimit = selectedImage.size();
        timer = new Timer();
        timer.schedule(new SampleImageTimerTask(),1000, 1000);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioPublic:
                        ispublic = 1L;
                        break;
                    case R.id.radioPrivate:
                        ispublic = 2L;
                        break;
                }
            }
        });

        seekBar.setProgress(1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                videoSpeedSet.setText(videoSpeedValue[i]);

                timer.cancel();
                timer = new Timer();

                if (videoSpeedValue[i] == "0.5초") {
                    speed = 0.5;
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(), 1000, 500);
                } else if (videoSpeedValue[i] == "1초") {
                    speed = 1;
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(), 1000, 1000);
                } else if (videoSpeedValue[i] == "1.5초") {
                    speed = 1.5;
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(), 1000, 1500);
                } else if (videoSpeedValue[i] == "2초") {
                    speed = 2;
                    timer.scheduleAtFixedRate(new SampleImageTimerTask(), 1000, 2000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sampleVideo: {
                timer.cancel();
                timer.purge();
                timer = null;
                btn_play.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.btn_play: {
                timer = new Timer();
                timer.schedule(new SampleImageTimerTask(), 1000, 1000);
                btn_play.setVisibility(View.INVISIBLE);
            }
            break;
        }
    }

    private class SampleImageTimerTask extends TimerTask {

        private int seconds = 0;

        @Override
        public void run() {

            if (timeLimit <= seconds) {
                seconds = 0;
            }

            //ui변경
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateImage(seconds++);
                }
            });
        }

        private void updateImage(int seconds) {
//            sampleVideo.setImageDrawable(new BitmapDrawable(getResources(), bitmapList.get(seconds)));
//            Log.d(TAG,"file: " + selectedImage.get(seconds));
            if(selectedImage.get(seconds).startsWith("http")){
                ImageLoader.getInstance().displayImage(selectedImage.get(seconds),sampleVideo);
            }else if(selectedImage.get(seconds).startsWith("file")){
                Log.d(TAG,"start file:" + selectedImage.get(seconds));
                ImageLoader.getInstance().displayImage(Uri.decode(selectedImage.get(seconds)),sampleVideo);
            }else{
                Toast.makeText(getApplicationContext(),"그런형식의 파일이 없습니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next: {
                timer.cancel();
                postDialog = new ProgressDialog(PostWriteActivity.this);
                postDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                postDialog.setMessage("파일을 생성하고 있습니다.");
                postDialog.setCancelable(false);
                postDialog.show();
                postDialog.setMessage("게시글을 생성하고 있습니다.");
                new AddPostAsyncTask(createPostAndFileUpload()).execute();
            }
            break;
        }
        return false;
    }

    private PostVo createPostAndFileUpload() {
        PostVo post = new PostVo();

        if (!editTextPost.getText().toString().equals(null)) {
            post.setPost_content(editTextPost.getText().toString());
        }
        post.setPost_ispublic(ispublic);
        post.setUser_no(((UserVo) Utils.getUserPreferences(this, "PikiUser")).getUser_no());

        // 2. 태그
        createTag();
        post.setTagList(tagList);

        //3. 사진
        createPicture();
        post.setPictureList(pictureList);
        return post;
    }

    private void createPicture() {
        Log.d(TAG,"selectedImage size:" + selectedImage.size() );
        for(PictureVo vo: pictureList){
            Log.d(TAG," selected Image content: "+ vo);
        }

        PictureVo[] picture = new PictureVo[selectedImage.size()];
        File[] file = new File[selectedImage.size()];

        for (int i = 0; i < selectedImage.size(); i++) {
            picture[i] = new PictureVo();

/*            try {*/
                if(selectedImage.get(i).startsWith("file")) {
/*                    ExifInterface exif = new ExifInterface(selectedImage.get(i).replace("file://", ""));

                    Double latitude = null;
                    Double longitude = null;

                    if ((exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null)) {
//                        latitude = imageUtils.convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                        latitude = Double.valueOf(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                        picture[i].setPicture_lat(latitude);
                        Log.d(TAG, "selectedImage write latitude :  " + latitude);
                    }
                    if ((exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null)) {
                        longitude = Double.valueOf(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                        picture[i].setPicture_lng(longitude);
                        Log.d(TAG, "selectedImage write longitude :  " + longitude);
                    }

                    if (latitude != null && longitude != null) {
                        String address = imageUtils.findAddress(getApplicationContext(), latitude, longitude);
                        System.out.println(address);
                        picture[i].setPicture_location(address);
                    }*/

                    ImageModel tmp = ImageUtils.getImagesFromExternal(this,selectedImage.get(i).replace("file:/",""));
                    if(tmp.getLat()!=null){
                        picture[i].setPicture_lat(Double.valueOf(tmp.getLat()));
                        Log.d(TAG, "selectedImage latitude: " + tmp.getLat());
                    }
                    if(tmp.getLng()!=null){
                        picture[i].setPicture_lng(Double.valueOf(tmp.getLng()));
                        Log.d(TAG, "selectedImage longitude: " + tmp.getLng());
                    }
                    if(tmp.getLng()!=null && tmp.getLat()!=null){
                        picture[i].setPicture_location(imageUtils.findAddress(this,Double.valueOf(tmp.getLat() ),Double.valueOf(tmp.getLng() )));
                        Log.d(TAG, "address: " + imageUtils.findAddress(this,Double.valueOf(tmp.getLat() ),Double.valueOf(tmp.getLng() )));
                    }

                }

                if(selectedImage.get(i).startsWith("http")){
                    picture[i].setPicture_url(selectedImage.get(i));
                }

                picture[i].setUser_no(((UserVo) Utils.getUserPreferences(this, "PikiUser")).getUser_no());
                picture[i].setPicture_local_url(selectedImage.get(i));
                pictureList.add(picture[i]);


/*            } catch (IOException e) {
                e.printStackTrace();
            }*/

        }
    }

    private void createTag() {
        List<String> parseList = new TagParse().exportHashTag(editTextPost.getText().toString());
        TagVo[] tag = new TagVo[parseList.size()];
        for (int i = 0; i < parseList.size(); i++) {
            tag[i] = new TagVo();
            tag[i].setTag_name(parseList.get(i));
            tagList.add(tag[i]);
        }
    }

    private class AddPostAsyncTask extends SafeAsyncTask<PostVo> {

        PostVo post;

        AddPostAsyncTask(PostVo post) {
            this.post = post;
        }

        @Override
        public PostVo call() throws Exception {
            return postService.addPost(getApplicationContext(), post);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.d(TAG, "AddPostAsyncTask onException " + e);
        }

        @Override
        protected void onSuccess(PostVo post) throws Exception {
            new AddImageFileAsyncTask(makeFile(),post).execute();
            postDialog.setMessage("동영상을 전송하고 있습니다. 잠시 기다려 주세요.");
        }
    }

    private List<File> makeFile() {
        postDialog.setMessage("동영상을 변환하고 있습니다. 잠시 기다려 주세요.");
        List<File> fileList = new ArrayList<File>();
        //1. render를 한다.
        for(int i =0; i<selectedImage.size();i++){
            String filepath = selectedImage.get(i);
            if(filepath.startsWith("http")){
                File f = ImageLoader.getInstance().getDiskCache().get(selectedImage.get(i));
                f.renameTo(new File(f.getName()+".png"));
                Log.d("makeFile", f.getName());

                String orgFileName = f.getName();
                Log.d(TAG,"orgFileName:"+ f.getPath());
                String fileExtName = orgFileName.substring( orgFileName.lastIndexOf('.') + 1, orgFileName.length() );
                Log.d(TAG,"exif:"+ fileExtName);

                fileList.add(f);
            }else if(filepath.startsWith("file")){
                String filename = filepath.substring(filepath.lastIndexOf("/"), filepath.indexOf("."));
                Log.d("makeFile", filename);
                //1. 비트맵 사진 사이즈를 변경한다.
                File f = new File(imageUtils.saveBitmaptoPNG(getApplicationContext(),imageUtils.renderImage(getApplicationContext(), selectedImage.get(i)),filename));
                fileList.add(f);
            }
//            no render
//            File f = ImageLoader.getInstance().getDiskCache().get(selectedImage.get(i));
//            f.renameTo(new File(f.getName()+".png"));
//            Log.d("makeFile", f.getName());
//            fileList.add(f);
        }

//        //2. 비트맵 저장
//        fileList.addAll(imageUtils.saveFiles(bitmapList, selectedImage));
        return fileList;
    }

    private class AddImageFileAsyncTask extends SafeAsyncTask<String> {

        List<File> fileList = new ArrayList<File>();
        PostVo post;

        AddImageFileAsyncTask(List<File> fileList, PostVo post) {
            this.fileList = fileList;
            this.post = post;
        }

        @Override
        public String call() throws Exception {
            for(File f: fileList){
                Log.d(TAG, "file" + f.getName());
            }
            return pictureService.makeMovie(getApplicationContext(),fileList, post,speed);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.d(TAG, "AddImageFileAsyncTask onException :" + e);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            postDialog.dismiss();
            boolean del_result = imageUtils.deleteBitmapCache(getApplicationContext());
            if (del_result) {
//                Toast.makeText(PostWriteActivity.this, "쿠키 파일이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(PostWriteActivity.this, pikiMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
        timer=null;
    }
}
