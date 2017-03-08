package piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.ProfileViewActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.ImageGalleryActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostListArrayAdapter.ViewHolder;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostListFragment;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostTagListActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.relay.PostRelayActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.update.PostUpdateActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.reply.ReplyListActivity;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.setListViewHeightBasedOnChildren;

/**
 * Created by joohan on 2017-02-06.
 */

public class DirectLinkVideoItem extends BaseVideoItem {

    private static final String TAG = "DirectLinkVideoItem";
    private PostService postService = new PostService();

    private final Picasso mImageLoader;
    private final int mImageResource;
    private final PostVo mPost;
    private final Context mContext;
    private ViewHolder viewHolder = null;

    private Boolean postPermissionExist = false;

    public DirectLinkVideoItem(PostVo post, VideoPlayerManager videoPlayerManager, Picasso imageLoader, int imageResource, Context context) {
        super(videoPlayerManager);
        mImageLoader = imageLoader;
        mImageResource = imageResource;
        mPost = post;
        mContext = context;
    }

    @Override
    public void update(int position, final ViewHolder viewHolder, VideoPlayerManager videoPlayerManager) {
        this.viewHolder = viewHolder;
        viewHolder.mCover.setVisibility(View.VISIBLE);
        mImageLoader.load(mImageResource).into(viewHolder.mCover);
        new PostPermissionCheckTask(mPost.getPost_no(),(Utils.getUserPreferences(mContext,"PikiUser")).getUser_no()).execute();

        Onclick listener = new Onclick();

        //profile
        mImageLoader.load(mPost.getUser_profile_url()).placeholder(mContext.getResources().getDrawable(R.drawable.ic_default_profile)).into(viewHolder.mProfileImageView);
        viewHolder.mProfileImageView.setOnClickListener(listener);

        //name
        viewHolder.mTextview.setText(mPost.getUser_name());

        if(mPost.getPost_ispublic() == 1){
            viewHolder.mbtn_public.setVisibility(View.VISIBLE);
            viewHolder.mbtn_private.setVisibility(View.GONE);
        }else {
            viewHolder.mbtn_private.setVisibility(View.VISIBLE);
            viewHolder.mbtn_public.setVisibility(View.GONE);
        }

        //btn_gallery
        viewHolder.mbtn_gallery.setOnClickListener(listener);

        //btn_setting
        viewHolder.mbtn_setting.setOnClickListener(listener);

        //content
        viewHolder.mtv_content.setText(mPost.getPost_content());

        //video click pause 2017.2.15
        viewHolder.mPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.mPlayer !=null) {
                    if (viewHolder.mPlayer.getCurrentState() == MediaPlayerWrapper.State.STARTED) {
                        viewHolder.mPlayer.pause();
                    } else if (viewHolder.mPlayer.getCurrentState() == MediaPlayerWrapper.State.PAUSED) {
                        viewHolder.mPlayer.start();
                    }
                }
            }
        });

        //btn_heart
        viewHolder.mbtn_heart.setOnClickListener(listener);
        viewHolder.mbtn_heart_selected.setOnClickListener(listener);

        viewHolder.mtv_heart.setText(String.format(mContext.getResources().getString(R.string.btn_heart),String.valueOf(mPost.getPost_heart_count())));
        if(mPost.isHeartflag()){
            viewHolder.mbtn_heart.setVisibility(View.GONE);
            viewHolder.mbtn_heart_selected.setVisibility(View.VISIBLE);
        }else{
            viewHolder.mbtn_heart.setVisibility(View.VISIBLE);
            viewHolder.mbtn_heart_selected.setVisibility(View.GONE);
        }

        //reply
        viewHolder.mbtn_reply.setOnClickListener(listener);
        viewHolder.mtv_reply.setText(String.format(mContext.getResources().getString(R.string.btn_reply),String.valueOf(mPost.getPost_comment_count())));

        //set tv_date
        viewHolder.mtv_date.setText(mPost.getPost_regdate());

        // set comment list
        UserReplyArrayAdapter replyArrayAdapter = new UserReplyArrayAdapter(mContext);
        replyArrayAdapter.clear();
        replyArrayAdapter.add(mPost.getCommentList());
        viewHolder.mCommentList.setAdapter(replyArrayAdapter);
        setListViewHeightBasedOnChildren(viewHolder.mCommentList);
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager) {
        if(mPost.getPost_movie() !=null || !mPost.getPost_movie().equals("")) {
            Log.d(TAG, "1");
            if(player != null) {
                Log.d(TAG, "2");
                videoPlayerManager.playNewVideo(currentItemMetaData, player, mPost.getPost_movie());
            }
        }
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        if(mPost.getPost_movie() !=null || !mPost.getPost_movie().equals("")) {
            videoPlayerManager.stopAnyPlayback();
        }
    }

    class Onclick implements View.OnClickListener {
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.post_no:{
//                    Toast.makeText(context,"post no" + position, Toast.LENGTH_SHORT).show();
//                }break;
                case R.id.profilepic: {
                    Intent intent = new Intent(mContext, ProfileViewActivity.class);
                    intent.putExtra("userProfileImage", mPost.getUser_profile_url());
                    mContext.startActivity(intent);
                }
                break;
                case R.id.btn_gallery: {
                    Intent intent = new Intent(mContext, ImageGalleryActivity.class);
                    intent.putExtra("post_no", mPost.getPost_no());
                    Log.d(TAG,"--------------------------"+mContext.toString());
                    if(mContext.toString().contains("PostTagListActivity")){
                        ((PostTagListActivity)mContext).startActivityForResult(intent, BasicInfo.RESULT_CODE_IMAGE_GALLERY);
                    }else if (mContext.toString().contains("pikiMainActivity")){
                        ((pikiMainActivity)mContext).startActivityForResult(intent,BasicInfo.RESULT_CODE_IMAGE_GALLERY);
                    }
                }
                break;
                case R.id.btn_setting: {

                    final String update_photo = "update Photo";
                    final String history_photo = "history Photo";
                    final String cancel = "Cancel";
                    final String delete = "Delete";
                    final String permission_request = "Permission Request";
                    final List<CharSequence> items = new ArrayList<CharSequence>();
                    final CharSequence[] item_arr = {update_photo, history_photo, permission_request, delete, cancel, };

                    if((Utils.getUserPreferences(mContext,"PikiUser")).getUser_no()!= Long.parseLong(Utils.getStringPreferences(mContext,"PostUserNo"))){
                        items.add(item_arr[1]);
                        items.add(item_arr[2]);
                        items.add(item_arr[4]);
                    } else{
                        items.add(item_arr[0]);
                        items.add(item_arr[1]);
                        items.add(item_arr[3]);
                        items.add(item_arr[4]);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Post Menu");
                    builder.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (items.get(i).equals(update_photo)) {
                                Intent intent = new Intent(mContext, PostUpdateActivity.class);
                                intent.putExtra("post_no", mPost.getPost_no());

                                if(mContext.toString().contains("PostTagListActivity")){
                                    ((PostTagListActivity) mContext).startActivityForResult(intent, BasicInfo.RESULT_POST_UPDATE);
                                    ((PostTagListActivity) mContext).finish();
                                }else if (mContext.toString().contains("pikiMainActivity")){
                                    ((pikiMainActivity) mContext).startActivityForResult(intent, BasicInfo.RESULT_POST_UPDATE);
                                    ((pikiMainActivity) mContext).finish();
                                }


                            } else if (items.get(i).equals(history_photo)) {
                                if (postPermissionExist || ((Utils.getUserPreferences(mContext,"PikiUser")).getUser_no()== Long.parseLong(Utils.getStringPreferences(mContext,"PostUserNo")))) {
                                    Intent intent = new Intent(mContext, PostRelayActivity.class);
                                    intent.putExtra("post_no", mPost.getPost_no());
                                    if(mContext.toString().contains("PostTagListActivity")){
                                        ((PostTagListActivity) mContext).startActivityForResult(intent, BasicInfo.RESULT_POST_UPDATE);
                                    }else if (mContext.toString().contains("pikiMainActivity")){
                                        ((pikiMainActivity) mContext).startActivityForResult(intent, BasicInfo.RESULT_POST_UPDATE);
                                    }

                                }
                                else {
                                    Toast.makeText(mContext, "권한을 먼저 요청해주세요.", Toast.LENGTH_LONG).show();
                                }
                            } else if (items.get(i).equals(permission_request)) {
                                if (postPermissionExist || ((Utils.getUserPreferences(mContext,"PikiUser")).getUser_no()== Long.parseLong(Utils.getStringPreferences(mContext,"PostUserNo")))) {
                                    Toast.makeText(mContext, "이미 권한이 있습니다.", Toast.LENGTH_LONG).show();
                                } else {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                    final EditText input = new EditText(mContext);
                                    dialog.setView(input);
                                    dialog.setTitle("ALERT");
                                    dialog.setMessage("글의 수정권한을 요청하시겠습니까?");
                                    input.setHint("글쓴이에게 메세지를 남기세요.");
                                    dialog.setCancelable(true);
                                    dialog.setPositiveButton("요청", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d(TAG, "----------------" + input.getText());
                                            new ModifyPermissionRequestAsyncTask(input.getText().toString(), mPost.getPost_no(), (Utils.getUserPreferences(mContext, "PikiUser")).getUser_id()).execute();
                                        }
                                    });
                                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(mContext, "취소 누름", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialog.show();
                                }
                            } else if (items.get(i).equals(delete)) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                dialog.setTitle("ALERT");
                                dialog.setMessage("정말 삭제하시겠습니까? 게시글의 사진도 삭제 됩니다.");
                                dialog.setCancelable(true);
                                dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new DeletePostAsyncTask(mPost, position).execute();
                                    }
                                });
                                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(mContext, "취소 누름", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.show();
                            } else if (items.get(i).equals(cancel)) {
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    builder.show();
                }
                break;
                case R.id.btn_heart: {
                    viewHolder.mheartflg = true;
                    viewHolder.mbtn_heart.setVisibility(View.GONE);
                    viewHolder.mbtn_heart_selected.setVisibility(View.VISIBLE);
                    new UpdateHeartCountTask(mPost).execute();
                }
                break;
                case R.id.btn_heart_selected: {
                    viewHolder.mheartflg = false;
                    viewHolder.mbtn_heart.setVisibility(View.VISIBLE);
                    viewHolder.mbtn_heart_selected.setVisibility(View.GONE);
                    new UpdateUnHearCountTask(mPost).execute();
                }
                break;
                case R.id.tv_heart: {
                    Toast.makeText(mContext, "post tv_heart" + position, Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.btn_reply: {
                    Intent intent = new Intent(mContext, ReplyListActivity.class);
                    intent.putExtra("post_no", mPost.getPost_no());
                    mContext.startActivity(intent);
                }
                break;
                case R.id.tv_reply: {
                    Toast.makeText(mContext, "post tv_reply" + position, Toast.LENGTH_SHORT).show();
                }
                break;
                case R.id.tv_date: {
                    Toast.makeText(mContext, "post tv_date" + position, Toast.LENGTH_SHORT).show();
                }
                break;
//                case R.id.post_list_reply:{
//                    Toast.makeText(context,"post post_list_reply" + position, Toast.LENGTH_SHORT).show();
//                }break;
            }
        }

        private class UpdateHeartCountTask extends SafeAsyncTask<JSONResult> {
            private PostVo post;

            public UpdateHeartCountTask(PostVo post) {
                this.post = post;
            }

            @Override
            public JSONResult call() throws Exception {
                return postService.updateHeart(mContext, post);
            }

            @Override
            protected void onSuccess(JSONResult s) throws Exception {
                super.onSuccess(s);
                if ("success".equals(s.getResult())) {
                    Toast.makeText(mContext, "좋아요 합니다.", Toast.LENGTH_SHORT).show();
                    Long count = ((PostVo) s.getData()).getPost_heart_count();
                    viewHolder.mtv_heart.setText(String.format(mContext.getResources().getString(R.string.btn_heart),String.valueOf(count)));
                }else{
                    Toast.makeText(mContext, "좋아요 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private class UpdateUnHearCountTask extends SafeAsyncTask<JSONResult> {
            private PostVo post;

            UpdateUnHearCountTask(PostVo post) {
                this.post = post;
            }

            @Override
            public JSONResult call() throws Exception {
                return postService.updateUnHeart(mContext, post);
            }

            @Override
            protected void onSuccess(JSONResult s) throws Exception {
                super.onSuccess(s);
                if ("success".equals(s.getResult())) {
                    Toast.makeText(mContext, "안좋아요 합니다.", Toast.LENGTH_SHORT).show();
                    Long count = ((PostVo) s.getData()).getPost_heart_count();
                    viewHolder.mtv_heart.setText(String.format(mContext.getResources().getString(R.string.btn_heart),String.valueOf(count)));
                }
                if ("fail".equals(s.getResult())) {
                    Toast.makeText(mContext, "안좋아요 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public class DeletePostAsyncTask extends SafeAsyncTask<String> {
            PostVo post;
            int position;

            public DeletePostAsyncTask(PostVo mPost, int position) {
                this.post = mPost;
                this.position = position;
            }

            @Override
            public String call() throws Exception {
                return postService.deletePost(mContext, post);
            }

            @Override
            protected void onSuccess(String s) throws Exception {
                //1. null

                //2. "fail"
                if ("fail".equals(s)) {
                    Toast.makeText(mContext, " 삭제 실패", Toast.LENGTH_SHORT).show();
                }
                //3. "success"
                if ("success".equals(s)) {
                    Toast.makeText(mContext, " 삭제 성공", Toast.LENGTH_SHORT).show();
                    FragmentPagerAdapter fragmentPagerAdapter = ((pikiMainActivity) mContext).getFragmentPagerAdapter();
                    PostListFragment fgm = (PostListFragment) ((pikiMainActivity) mContext).getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:" + ((pikiMainActivity) mContext).getViewPager().getId() + ":"
                                    + fragmentPagerAdapter.getItemId(position));

                    fgm.updateList(position);
                }
            }
        }

        public class ModifyPermissionRequestAsyncTask extends SafeAsyncTask<Object>{

            String text;
            Long post_no;
            String user_id;

            public ModifyPermissionRequestAsyncTask(String text, Long post_no, String user_id){
                this.text = text;
                this.post_no = post_no;
                this.user_id = user_id;
            }
            @Override
            public Object call() throws Exception {
                postService.modifyPermissionRequest(text,post_no,user_id);
                System.out.println("---------간다");
                return null;
            }

            @Override
            protected void onSuccess(Object o) throws Exception {
                System.out.println("---------왔다");
                super.onSuccess(o);
            }
        }
    }
    private class PostPermissionCheckTask extends  SafeAsyncTask<Boolean>{
        private Long post_no;
        private Long user_no;


        PostPermissionCheckTask(Long post_no, Long user_no){
            this.post_no = post_no;
            this.user_no = user_no;
        }

        @Override
        public Boolean call() throws Exception {
            return postService.postPermissionCheck(post_no,user_no);
        }

        @Override
        protected void onSuccess(Boolean aBoolean) throws Exception {
            super.onSuccess(aBoolean);
            System.out.println("-------------asdfasd;lkja;sldkfja;lskdf========"+aBoolean);
            postPermissionExist = aBoolean;
        }
    }
}