package piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.video_player_manager.manager.VideoItem;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.utils.Logger;
import com.volokh.danylo.visibility_utils.items.ListItem;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostListArrayAdapter.ViewHolder;

/**
 * Created by joohan on 2017-02-06.
 */

public abstract class BaseVideoItem implements VideoItem, ListItem {
    private static final boolean SHOW_LOGS = false;
    private static final String TAG = BaseVideoItem.class.getSimpleName();

    private final Rect mCurrentViewRect = new Rect();
    private final VideoPlayerManager<MetaData> mVideoPlayerManager;

    protected BaseVideoItem(VideoPlayerManager<MetaData>  videoPlayerManager) {
        mVideoPlayerManager = videoPlayerManager;
    }

    public abstract void update(int position, ViewHolder view, VideoPlayerManager videoPlayerManager);

    /*ListItem*/
    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        ViewHolder viewHolder = (ViewHolder) newActiveView.getTag();
        playNewVideo(new CurrentItemMetaData(newActiveViewPosition, newActiveView), viewHolder.mPlayer, mVideoPlayerManager);
    }

    /*ListItem*/
    @Override
    public void deactivate(View currentView, int position) {
        ViewHolder viewHolder = (ViewHolder) currentView.getTag();
        stopPlayback(mVideoPlayerManager);
    }

    public View createView(ViewGroup parent, int screenWidth) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        //ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        //layoutParams.height = screenWidth;

        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        viewHolder.mPlayer.addMediaPlayerListener(new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
            @Override
            public void onVideoSizeChangedMainThread(int width, int height) {
            }

            @Override
            public void onVideoPreparedMainThread() {
                // When video is prepared it's about to start playback. So we hide the cover
                viewHolder.mCover.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                viewHolder.mPlayer.start();
            }

            @Override
            public void onErrorMainThread(int what, int extra) {
            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {
            }

            @Override
            public void onVideoStoppedMainThread() {
                // Show the cover when video stopped
                viewHolder.mCover.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    /*ListItem*/
    @Override
    public int getVisibilityPercents(View currentView) {
        if(SHOW_LOGS) Logger.v(TAG, ">> getVisibilityPercents currentView " + currentView);

        int percents = 100;

        currentView.getLocalVisibleRect(mCurrentViewRect);
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents mCurrentViewRect top " + mCurrentViewRect.top + ", left " + mCurrentViewRect.left + ", bottom " + mCurrentViewRect.bottom + ", right " + mCurrentViewRect.right);

        int height = currentView.getHeight();
        if(SHOW_LOGS) Logger.v(TAG, "getVisibilityPercents height " + height);

        if(viewIsPartiallyHiddenTop()){
            // view is partially hidden behind the top edge
            percents = (height - mCurrentViewRect.top) * 100 / height;
        } else if(viewIsPartiallyHiddenBottom(height)){
            percents = mCurrentViewRect.bottom * 100 / height;
        }

        setVisibilityPercentsText(currentView, percents);
        if(SHOW_LOGS) Logger.v(TAG, "<< getVisibilityPercents, percents " + percents);

        return percents;
    }

    private void setVisibilityPercentsText(View currentView, int percents) {
        if(SHOW_LOGS) Logger.v(TAG, "setVisibilityPercentsText percents " + percents);
        ViewHolder videoViewHolder = (ViewHolder) currentView.getTag();
        String percentsText = "Visibility percents: " + String.valueOf(percents);

        //videoViewHolder.mVisibilityPercents.setText(percentsText);
    }

    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }

    /*VideoItem playNewVideo*/

    /*VideoItem stopPlayback*/


}