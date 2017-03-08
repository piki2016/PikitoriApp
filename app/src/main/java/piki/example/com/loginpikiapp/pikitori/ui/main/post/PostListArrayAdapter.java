package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.BaseVideoItem;

import static piki.example.com.loginpikiapp.R.id.btn_gallery;

public class PostListArrayAdapter extends BaseAdapter {

    private  VideoPlayerManager mVideoPlayerManager;
    private  List<BaseVideoItem> mList;
    private  List<View> mItem ;
    private  Context mContext;

    public PostListArrayAdapter(VideoPlayerManager videoPlayerManager, Context context, List<BaseVideoItem> list) {
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
        mItem = new ArrayList<View>();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder{
        public boolean mheartflg;
        public final VideoPlayerView mPlayer;
        public final ImageView mCover;
        public final ImageView mProfileImageView;
        public final TextView mTextview;
        public final ImageView mbtn_public;
        public final ImageView mbtn_private;
        public final Button mbtn_gallery;
        public final Button mbtn_setting;
        public final TextView mtv_content;
        public final Button mbtn_heart;
        public final Button mbtn_heart_selected;
        public final TextView mtv_heart;
        public final Button mbtn_reply;
        public final TextView mtv_reply;
        public final TextView mtv_date;
        public final ListView mCommentList;

        public ViewHolder(View view){
            super(view);
            mheartflg = false;
            mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
            mCover = (ImageView) view.findViewById(R.id.cover);
            //mtv_post_no = (TextView) view.findViewById(R.id.post_no);
            mProfileImageView = (ImageView) view.findViewById(R.id.profilepic);
            mTextview = (TextView)view.findViewById(R.id.name);
            mtv_content = (TextView)view.findViewById(R.id.post_content);
            mbtn_public = (ImageView) view.findViewById(R.id.is_public);
            mbtn_private = (ImageView) view.findViewById(R.id.is_private);
            mbtn_gallery = (Button)view.findViewById(btn_gallery);
            mbtn_setting = (Button)view.findViewById(R.id.btn_setting);
            mbtn_heart = (Button)view.findViewById(R.id.btn_heart);
            mbtn_heart_selected = (Button) view.findViewById(R.id.btn_heart_selected);
            mtv_heart = (TextView)view.findViewById(R.id.tv_heart);
            mbtn_reply= (Button)view.findViewById(R.id.btn_reply);
            mtv_reply=(TextView)view.findViewById(R.id.tv_reply);
            mtv_date= (TextView)view.findViewById(R.id.tv_date);
            mCommentList=(ListView)view.findViewById(R.id.post_list_reply);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public BaseVideoItem getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BaseVideoItem videoItem = mList.get(position);

        View resultView;
        if (convertView == null) {
            resultView = videoItem.createView(parent, mContext.getResources().getDisplayMetrics().widthPixels);
            mItem.add(resultView);
        } else {
            resultView = convertView;
        }

        videoItem.update(position, (ViewHolder)resultView.getTag(), mVideoPlayerManager);
        return resultView;
    }

}
