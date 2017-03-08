package piki.example.com.loginpikiapp.pikitori.ui.main.search;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import piki.example.com.loginpikiapp.R;

public class SearchActivity extends AppCompatActivity {
    private SearchTabsAdapter searchTabsAdapter;
    private int indexDefaultTab = SearchTabsConfig.TABINDEX.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        indexDefaultTab = SearchTabsConfig.TABINDEX.FIRST;

        searchTabsAdapter = new SearchTabsAdapter( this, (TabHost)findViewById( android.R.id.tabhost ), (ViewPager)findViewById( R.id.pager) );
        if( indexDefaultTab != SearchTabsConfig.TABINDEX.FIRST ) {
            searchTabsAdapter.selectTab(indexDefaultTab);
        }
    }
}
