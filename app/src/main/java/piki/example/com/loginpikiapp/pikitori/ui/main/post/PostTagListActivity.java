package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.utils.Logger;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.ListViewItemPositionGetter;

import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.BaseVideoItem;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.ItemFactory;

public class PostTagListActivity extends AppCompatActivity {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = "PostTagListActivity";

    public final ArrayList<BaseVideoItem> mList = new ArrayList<>();

    private final ListItemsVisibilityCalculator mListItemVisibilityCalculator = new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    public ItemsPositionGetter mItemsPositionGetter;
    public PostTagListArrayAdapter videoListViewAdapter;

    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {
            if (SHOW_LOGS) Logger.v(TAG, "onPlayerItemChanged " + metaData);

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    ImageView no_item_icon;
    TextView no_item_message;
    TextView no_item_message2;

    private ListView mListView;

    private TextView textViewNew;
    private Switch switchPostOrder;
    private boolean postOrder = true;
    private List<PostVo> postList1 = new ArrayList<>();
    private PostService postService = new PostService();

    Long tagNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tag_list);

        no_item_icon = (ImageView)findViewById(R.id.no_item_icon);
        no_item_message = (TextView)findViewById(R.id.no_item_message);
        no_item_message2 = (TextView)findViewById(R.id.no_item_message2);

        mListView = (ListView) findViewById(R.id.list_view);
        videoListViewAdapter = new PostTagListArrayAdapter(mVideoPlayerManager, getApplicationContext()  , mList);
        mListView.setAdapter(videoListViewAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),"setonclickitemlistener",Toast.LENGTH_SHORT).show();
//            }
//        });

        textViewNew = (TextView) findViewById(R.id.post_new_order);
        switchPostOrder = (Switch) findViewById(R.id.post_order);
        switchPostOrder.setChecked(true);

        switchPostOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                mList.clear();
                onStop();
                if (state) {
                    postOrder = state;
                    textViewNew.setTextColor(Color.parseColor("#FA3F7E"));
                    for (int i = 0; i < postList1.size(); i++) {
                        mList.add(ItemFactory.createItemFromHttp(postList1.get(i), R.drawable.background, PostTagListActivity.this, mVideoPlayerManager));

                    }

                } else {
                    postOrder = state;
                    textViewNew.setTextColor(Color.parseColor("#828282"));
                    for (int i = postList1.size() - 1; i >= 0; i--) {
                        mList.add(ItemFactory.createItemFromHttp(postList1.get(i), R.drawable.background, PostTagListActivity.this, mVideoPlayerManager));
                    }
                }
                mListView.setAdapter(videoListViewAdapter);
                videoListViewAdapter.notifyDataSetChanged();
                scroll();
            }
        });
    }

    public  void updateList(int position) {
        mList.remove(position);
        videoListViewAdapter.notifyDataSetChanged();
        scroll();
    }

    @Override
    protected void onStart() {
        super.onStart();

        tagNo  = getIntent().getLongExtra("tag_no", 0L);

        getSupportActionBar().setTitle("#"+getIntent().getStringExtra("keyword"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.progress).setVisibility(View.VISIBLE);

        new FetchPostTagListTask(getApplicationContext(),tagNo).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void scroll(){
        if (!mList.isEmpty()) {
            // need to call this method from list view handler in order to have list filled previously

            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    mListItemVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mListView.getFirstVisiblePosition(),
                            mListView.getLastVisiblePosition());

                }
            }, 1000);
        }

        mItemsPositionGetter = new ListViewItemPositionGetter(mListView);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
                if (scrollState == SCROLL_STATE_IDLE && !mList.isEmpty()) {
                    mListItemVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    if (!mList.isEmpty()) {
                        mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, firstVisibleItem, visibleItemCount, mScrollState);
                    }
                }catch(NullPointerException e){
                    Log.d(TAG,"null point exception" + e);
                }
            }
        });
    }

    public void onPause(){
        super.onPause();
        Toast.makeText(this,"map 멈춰",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            break;
        }
        return true;
    }

    public void show_item() {
        mListView.setVisibility(View.VISIBLE);
        no_item_icon.setVisibility(View.GONE);
        no_item_message.setVisibility(View.GONE);
        no_item_message2.setVisibility(View.GONE);
    }

    private class FetchPostTagListTask extends SafeAsyncTask<List<PostVo>> {
        Context context;
        Long tagNo;
        FetchPostTagListTask(Context context, Long no){
            this.context = context;
            this.tagNo = no;
        }

        @Override
        public List<PostVo> call() throws Exception{
            return postService.FetchPostTagList(context, tagNo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d(TAG,"Error: " + e);
        }

        @Override
        protected void onSuccess(List<PostVo> postTagList) throws Exception{
            postList1=postTagList;
            for(int i = 0; i<postTagList.size();i++){
                mList.add(ItemFactory.createItemFromHttp(postTagList.get(i),R.drawable.background,PostTagListActivity.this,mVideoPlayerManager));
                Log.d("FetchPostTagListTask",""+postTagList.get(i));
            }

            if(postTagList.size() > 0){
                show_item();
            }
            findViewById(R.id.progress).setVisibility(View.GONE);

            scroll();
        }
    }
}
