package piki.example.com.loginpikiapp.pikitori.ui.main.post.update;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;


import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;

public class PostUpdateActivity extends AppCompatActivity {
    private static final String TAG = "PostUpdateActivity";
    PostService postService = new PostService();

    VideoPlayerView post_update_video;
    ImageView mVideoCover;
    RadioGroup post_update_radioGroup;
    RadioButton post_update_radioPublic;
    RadioButton post_update_radioPrivate;
    EditText post_update_editTextPost;

    Long ispublic;
    PostVo updatepost;

    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update);

        post_update_video = (VideoPlayerView) findViewById(R.id.post_update_video);
        mVideoCover = (ImageView) findViewById(R.id.video_cover);
        post_update_radioGroup = (RadioGroup) findViewById(R.id.post_update_radioGroup);
        post_update_radioPublic = (RadioButton) findViewById(R.id.post_update_radioPublic);
        post_update_radioPrivate = (RadioButton) findViewById(R.id.post_update_radioPrivate);
        post_update_editTextPost = (EditText) findViewById(R.id.post_update_editTextPost);

        post_update_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.post_update_radioPublic: {
//                        Toast.makeText(getApplicationContext(),"public",Toast.LENGTH_SHORT).show();
                        ispublic = 1L;
                    }
                    break;
                    case R.id.post_update_radioPrivate: {
//                        Toast.makeText(getApplicationContext(),"private",Toast.LENGTH_SHORT).show();
                        ispublic = 2L;
                    }
                    break;
                }
            }
        });

        getSupportActionBar().setTitle("수정하기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ComponentName callingApplication = getCallingActivity();
        switch (callingApplication.getShortClassName()) {
            case ".pikitori.ui.main.pikiMainActivity": {
                Long no = getIntent().getLongExtra("post_no", 0L);
                if (no.equals(0L)) {
                    no = ((UserVo) Utils.getUserPreferences(this, "PikiUser")).getUser_no();
                }
                PostVo post = new PostVo();
                post.setPost_no(no);
                new FetchPostInfoTask(post).execute();
            }
            break;
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
            case android.R.id.home: {
                Intent i = new Intent(PostUpdateActivity.this, pikiMainActivity.class);
                startActivity(i);
                finish();
            }
            break;
            case R.id.next: {
                if(ispublic !=null) {
                    updatepost.setPost_ispublic(ispublic);
                }
                updatepost.setPost_content(post_update_editTextPost.getText().toString());
                new updatePostTask(updatepost).execute();
            }
            break;
        }
        return false;
    }

    private class FetchPostInfoTask extends SafeAsyncTask<PostVo> {

        PostVo post;

        public FetchPostInfoTask(PostVo post) {
            this.post = post;
        }

        @Override
        public PostVo call() throws Exception {
            return postService.getPostInfo(getApplicationContext(), post);
        }

        @Override
        protected void onSuccess(PostVo post) throws Exception {
            super.onSuccess(post);
            updatepost = post;
            Log.d(TAG, "" + post);
            updateUI(post);
        }
    }

    private class updatePostTask extends SafeAsyncTask<String>{
        PostVo post;
        updatePostTask(PostVo updatepost){
            this.post = updatepost;
        }
        @Override
        public String call() throws Exception {
            return postService.updatePost(getApplicationContext(),post);
        }

        @Override
        protected void onSuccess(String s) throws Exception {
            super.onSuccess(s);
            if("success".equals(s)){
                Intent i = new Intent(PostUpdateActivity.this, pikiMainActivity.class);
                startActivity(i);
                finish();

            }

            if("fail".equals(s)){
                Intent i = new Intent();
                setResult(RESULT_CANCELED,i);
                finish();
            }

        }
    }

    private void updateUI(PostVo post) {

        Picasso.with(this).load(R.drawable.background).into(mVideoCover);
        post_update_video.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
                mVideoCover.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
                mVideoCover.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
                mVideoCover.setVisibility(View.VISIBLE);
            }

        });
        //무한 반복 3.1
        post_update_video.addMediaPlayerListener(new MediaPlayerWrapper.MainThreadMediaPlayerListener(){

            @Override
            public void onVideoSizeChangedMainThread(int width, int height) {

            }

            @Override
            public void onVideoPreparedMainThread() {
                mVideoCover.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                post_update_video.start();
            }

            @Override
            public void onErrorMainThread(int what, int extra) {

            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {

            }

            @Override
            public void onVideoStoppedMainThread() {
                mVideoCover.setVisibility(View.VISIBLE);
            }
        } );

        mVideoPlayerManager.playNewVideo(null, post_update_video, post.getPost_movie());
        Toast.makeText(getApplicationContext(),"updateUI:" + post.getPost_ispublic(),Toast.LENGTH_SHORT).show();
        if(post.getPost_ispublic() == 1L ){
            Toast.makeText(getApplicationContext(),"public:" + post.getPost_ispublic(),Toast.LENGTH_SHORT).show();
            post_update_radioPublic.setSelected(true);
            post_update_radioPublic.setChecked(true);
            post_update_radioPrivate.setSelected(false);
            post_update_radioPrivate.setChecked(false);
        }else if(post.getPost_ispublic() == 2L){
            Toast.makeText(getApplicationContext(),"private:" + post.getPost_ispublic(),Toast.LENGTH_SHORT).show();
            post_update_radioPublic.setChecked(false);
            post_update_radioPublic.setSelected(false);
            post_update_radioPrivate.setChecked(true);
            post_update_radioPrivate.setSelected(true);
        }

        post_update_editTextPost.setText(post.getPost_content());
    }

    @Override
    public void onStop() {
        super.onStop();
        // in case we exited screen in playback
        mVideoCover.setVisibility(View.VISIBLE);;

        mVideoPlayerManager.stopAnyPlayback();
    }




}
