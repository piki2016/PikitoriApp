package piki.example.com.loginpikiapp.pikitori.ui.main.search;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import piki.example.com.loginpikiapp.R;

/**
 * Created by joohan on 2017-01-31.
 */

public class SearchTabsAdapter extends FragmentStatePagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener{
    private final Context context;
    private final TabHost tabHost;
    private final ViewPager viewPager;

    private View currentTabView;
    private int currentIndex;

    public SearchTabsAdapter(Context context, TabHost tabHost, ViewPager viewPager ) {

        super(((FragmentActivity) context).getSupportFragmentManager() );

        currentTabView = null;
        currentIndex = -1;

        this.context = context;
        this.tabHost = tabHost;
        this.viewPager = viewPager;

        this.tabHost.setup();
        this.tabHost.getTabWidget().setDividerDrawable( null );

        // 페이지 사이의 간격
        // this.viewPager.setPageMargin(20);
        // viewPager.setPageMarginDrawable( R.color.bg_color_fragment_page );

        this.viewPager.setAdapter( this );

        this.tabHost.setOnTabChangedListener( this );
        this.viewPager.setOnPageChangeListener( this );

        // init
        int count = SearchTabsConfig.COUNT_TABS();
        for( int i = 0; i < count; i++ ) {
            SearchTabsConfig.TabInfo tabInfo = SearchTabsConfig.TABINFO( i );

            View view = LayoutInflater.from( this.context ).inflate( R.layout.tab_background, null );
            TabHost.TabSpec tabSpec = tabHost.newTabSpec( tabInfo.tag ).setIndicator( view );
            tabSpec.setContent( new DummyTabFactory( this.context ) );

            this.tabHost.addTab( tabSpec );

            if( tabInfo.drawableNormal != 0  && tabInfo.drawableSelected != 0 ) {
                ImageView imageView = ( ImageView ) view.findViewById( R.id.tab_image );
                imageView.setVisibility( View.VISIBLE );
                imageView.setImageDrawable(  this.context.getResources().getDrawable( tabInfo.drawableNormal ) );
            } else {
                TextView textView = ( TextView ) view.findViewById( R.id.tab_text );
                textView.setVisibility( View.VISIBLE );
                textView.setText( tabInfo.tag );
            }

            notifyDataSetChanged();
        }
    }

    public void selectTab( int position ) {
        tabHost.setCurrentTab( position );
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return SearchTabsConfig.COUNT_TABS();
    }

    @Override
    public Fragment getItem(int index ) {
        SearchTabsConfig.TabInfo tabInfo = SearchTabsConfig.TABINFO( index );
        return Fragment.instantiate( context, tabInfo.klass.getName(), tabInfo.bundle );
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object ) {
        // onCreate의 반복적 호출 조절
        // super.destroyItem( container, position, object );
    }

    @Override
    public void onTabChanged( String tabId ) {
        SearchTabsConfig.TabInfo tabInfo;

        // previous tab
        if( currentTabView != null && currentIndex >= 0 ) {
            tabInfo = SearchTabsConfig.TABINFO( currentIndex );
            if( tabInfo.drawableNormal != 0  && tabInfo.drawableSelected != 0 ) {
                ( ( ImageView ) currentTabView.findViewById( R.id.tab_image ) ).setImageDrawable(  context.getResources().getDrawable( tabInfo.drawableNormal ) );
            }
        }

        // currentTab
        currentIndex = tabHost.getCurrentTab();
        currentTabView = tabHost.getCurrentTabView();

        tabInfo = SearchTabsConfig.TABINFO( currentIndex );
        if( tabInfo.drawableNormal != 0  && tabInfo.drawableSelected != 0 ) {
            ( ( ImageView ) currentTabView.findViewById( R.id.tab_image ) ).setImageDrawable(  context.getResources().getDrawable( tabInfo.drawableSelected ) );
        }

        // pager and title
        viewPager.setCurrentItem( currentIndex );
        ( (Activity) context ).setTitle( context.getResources().getString( R.string.app_name ) + " - " + tabInfo.tag );
    }

    @Override
    public void onPageSelected( int position ) {
        TabWidget widget = tabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();

        widget.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );
        tabHost.setCurrentTab( position );
        widget.setDescendantFocusability( oldFocusability );

        if( position == 0 ) {
        }
    }

    @Override
    public void onPageScrollStateChanged( int arg0 ) {
    }

    @Override
    public void onPageScrolled( int arg0, float arg1, int arg2 ) {
    }

    private static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context context;

        public DummyTabFactory( Context context ) {
            this.context = context;
        }

        @Override
        public View createTabContent( String tag ) {
            View v = new View( context );
            v.setMinimumWidth( 0 );
            v.setMinimumHeight( 0 );
            return v;
        }
    };
}
