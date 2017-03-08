package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.BaseVideoItem;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.displayImageOption;

public class PostTagListArrayAdapter extends BaseAdapter {

    private static final String TAG = "PostTagListArrayAdapter";

    private VideoPlayerManager mVideoPlayerManager;
    private List<BaseVideoItem> mList;
    private List<View> mItem;
    private Context mContext;

    public PostTagListArrayAdapter(VideoPlayerManager videoPlayerManager, Context context, List<BaseVideoItem> list) {
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
        mItem = new ArrayList<View>();
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

        videoItem.update(position, (PostListArrayAdapter.ViewHolder)resultView.getTag(), mVideoPlayerManager);
        return resultView;
    }

}
