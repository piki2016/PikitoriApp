package piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes;

import android.app.Activity;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import piki.example.com.loginpikiapp.pikitori.domain.PostVo;

/**
 * Created by joohan on 2017-02-06.
 */

public class ItemFactory {

    public static BaseVideoItem createItemFromHttp(PostVo post, int imageResource, Activity activity, VideoPlayerManager<MetaData> videoPlayerManager ){
        return new DirectLinkVideoItem(post, videoPlayerManager, Picasso.with(activity), imageResource, activity);
    }

}