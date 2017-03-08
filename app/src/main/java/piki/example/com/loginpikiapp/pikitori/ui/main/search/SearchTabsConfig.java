package piki.example.com.loginpikiapp.pikitori.ui.main.search;

import android.os.Bundle;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.ui.main.search.people.SearchListFragment;
import piki.example.com.loginpikiapp.pikitori.ui.main.search.tag.TagListFragment;

/**
 * Created by joohan on 2017-01-31.
 */

public final class SearchTabsConfig {
    private static final TabInfo[] TABINFOS = {
            new TabInfo( "사람 검색", R.drawable.ic_tab_profile, R.drawable.ic_tab_profile_selected, SearchListFragment.class, null ),
           new TabInfo( "태그", R.drawable.ic_tab_tag, R.drawable.ic_tab_tag_selected, TagListFragment.class, null ),
    };

    public static final class TABINDEX {
        public static final int USERLIST = 0;
        public static final int CHANNELLIST = 1;
        public static final int SETTINGS = 2;

        public static final int FIRST = 0;
        public static final int LAST = TABINFOS.length;
    };

    public static final int COUNT_TABS() {
        return TABINFOS.length;
    }

    public static final TabInfo TABINFO( int index ) {
        return ( index < 0 || index >= COUNT_TABS() )  ? null : TABINFOS[ index ];
    }

    public static final class TabInfo {
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
