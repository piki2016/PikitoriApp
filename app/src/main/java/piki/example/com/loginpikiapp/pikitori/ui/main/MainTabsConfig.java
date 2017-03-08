package piki.example.com.loginpikiapp.pikitori.ui.main;

import android.os.Bundle;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.ui.main.map.GoogleMapFragment;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostListFragment;

public final class MainTabsConfig {

    private static final TabInfo[] TABINFOS = {
            new TabInfo( "게시글", R.drawable.ic_post, R.drawable.ic_post_selected, PostListFragment.class, null ),
            new TabInfo( "맵", R.drawable.ic_map, R.drawable.ic_map_selected, GoogleMapFragment.class, null )
    };

    public static final class TABINDEX {
        public static final int TIMELINE = 0;
        public static final int MAP = 1;

        public static final int FIRST = 0;
        public static final int LAST = TABINFOS.length;
    };

    public static final int COUNT_TABS() {
        return TABINFOS.length;
    }

    public static final TabInfo TABINFO( int index ) {
        return ( index < 0 || index >= COUNT_TABS() )  ? null : TABINFOS[ index ];
    }

    public static final class TabInfo{
        public final String tag;
        public final int drawableNormal;
        public final int drawableSelected;
        public final Class<?> klass;
        public final Bundle bundle;
        TabInfo( String tag, int drawableNormal, int drawableSelected, Class<?> klass, Bundle bundle ) {
            this.tag = tag;
            this.drawableNormal = drawableNormal;
            this.drawableSelected = drawableSelected;
            this.klass = klass;
            this.bundle = bundle;
        }
    }
}