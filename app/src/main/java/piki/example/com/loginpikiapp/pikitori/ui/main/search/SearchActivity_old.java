package piki.example.com.loginpikiapp.pikitori.ui.main.search;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import piki.example.com.loginpikiapp.R;

public class SearchActivity_old extends AppCompatActivity {
    private SearchTabsAdapter searchTabsAdapter;
    private int indexDefaultTab = SearchTabsConfig.TABINDEX.FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().hide();

        indexDefaultTab = SearchTabsConfig.TABINDEX.FIRST;

        searchTabsAdapter = new SearchTabsAdapter( this, (TabHost)findViewById( android.R.id.tabhost ), (ViewPager)findViewById( R.id.pager) );
        if( indexDefaultTab != SearchTabsConfig.TABINDEX.FIRST ) {
            searchTabsAdapter.selectTab(indexDefaultTab);
        }
    }
}
