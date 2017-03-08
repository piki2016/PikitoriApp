package piki.example.com.loginpikiapp.pikitori.ui.main.post;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AlertDialog;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewConfiguration;
        import android.view.ViewGroup;
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
        import piki.example.com.loginpikiapp.android.JSONResult;
        import piki.example.com.loginpikiapp.android.SafeAsyncTask;
        import piki.example.com.loginpikiapp.pikitori.Service.PostService;
        import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
        import piki.example.com.loginpikiapp.pikitori.core.Utils;
        import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
        import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
        import piki.example.com.loginpikiapp.pikitori.ui.main.ProfileViewActivity;
        import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;
        import piki.example.com.loginpikiapp.pikitori.ui.main.post.relay.PostRelayActivity;
        import piki.example.com.loginpikiapp.pikitori.ui.main.post.update.PostUpdateActivity;
        import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.BaseVideoItem;
        import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.ItemFactory;
        import piki.example.com.loginpikiapp.pikitori.ui.main.reply.ReplyListActivity;

/**
 * Created by admin on 2017-01-22.
 */
public class PostListFragment extends Fragment {

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = "PostListFragment";

    public final ArrayList<BaseVideoItem> mList = new ArrayList<>();

    private final ListItemsVisibilityCalculator mListItemVisibilityCalculator = new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    public ItemsPositionGetter mItemsPositionGetter;
    public PostListArrayAdapter videoListViewAdapter;

    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {
            if (SHOW_LOGS) Logger.v(TAG, "onPlayerItemChanged " + metaData);

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    boolean ispublic_post = true;

    ImageView no_item_icon;
    TextView no_item_message;
    TextView no_item_message2;

    private ListView mListView;

    private TextView textViewNew;
    private Switch switchPostOrder;
    private boolean postOrder = true;
    private List<PostVo> postList1 = new ArrayList<>();
    private PostService postService = new PostService();
    private View rootView = null;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_list, container, false);

        //no_itme
        no_item_icon = (ImageView) rootView.findViewById(R.id.no_item_icon);
        no_item_message = (TextView) rootView.findViewById(R.id.no_item_message);
        no_item_message2 = (TextView) rootView.findViewById(R.id.no_item_message2);
        no_item_icon.setVisibility(View.VISIBLE);
        no_item_message.setVisibility(View.VISIBLE);
        no_item_message2.setVisibility(View.VISIBLE);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        videoListViewAdapter = new PostListArrayAdapter(mVideoPlayerManager, getActivity(), mList);
        mListView.setAdapter(videoListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
//                Toast.makeText(getActivity(),"setonclickitemlistener",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textViewNew = (TextView) rootView.findViewById(R.id.post_new_order);
        switchPostOrder = (Switch) rootView.findViewById(R.id.post_order);
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
                        mList.add(ItemFactory.createItemFromHttp(postList1.get(i), R.drawable.background, getActivity(), mVideoPlayerManager));

                    }

                } else {
                    postOrder = state;
                    textViewNew.setTextColor(Color.parseColor("#828282"));
                    for (int i = postList1.size() - 1; i >= 0; i--) {
                        mList.add(ItemFactory.createItemFromHttp(postList1.get(i), R.drawable.background, getActivity(), mVideoPlayerManager));
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
    public void onStart() {
        super.onStart();

        // view_no  == pikiuser check
        Long user_no = ((UserVo) Utils.getUserPreferences(getContext(),"PikiUser")).getUser_no();
        String view_no = Utils.getStringPreferences(getContext(),"PostUserNo");
        if(!view_no.equals(String.valueOf(user_no))){
            ispublic_post = false;
        }
//        Toast.makeText(getContext(),"PostUserNo:" + view_no + "// PikiUser " +  ((UserVo)Utils.getUserPreferences(getContext(),"PikiUser")), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "PostUserNo: " + view_no + "// PikiUser" + ((UserVo)Utils.getUserPreferences(getContext(),"PikiUser")));

        getActivity().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        new FetchPostListAsyncTask(getActivity()).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity(),"PostFragment scroll!",Toast.LENGTH_SHORT).show();
        scroll();
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
                    try {
                        mListItemVisibilityCalculator.onScrollStateIdle(mItemsPositionGetter, view.getFirstVisiblePosition(), view.getLastVisiblePosition());
                    }catch(IllegalStateException e) {
                        Toast.makeText(getActivity(), "스크롤이 너무 빨라서 에러가 납니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    if (!mList.isEmpty()) {
                        mListItemVisibilityCalculator.onScroll(mItemsPositionGetter, firstVisibleItem, visibleItemCount, mScrollState);
                    }
                }catch(IllegalStateException e){
                    Toast.makeText(getActivity(),"스크롤이 너무 빨라서 에러가 납니다.",Toast.LENGTH_SHORT).show();

                }catch(NullPointerException e){
                    Log.d(TAG,"null point exception" + e);
                }
            }
        });

        mListView.setFriction(ViewConfiguration.getScrollFriction() * 30);
    }

    public void onPause(){
        super.onPause();
//        Toast.makeText(getActivity(),"map 멈춰",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }

    public void show_item() {
        mListView.setVisibility(View.VISIBLE);
        no_item_icon.setVisibility(View.GONE);
        no_item_message.setVisibility(View.GONE);
        no_item_message2.setVisibility(View.GONE);
    }

    private class FetchPostListAsyncTask extends SafeAsyncTask<List<PostVo>> {
        Context context;

        public FetchPostListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        public List<PostVo> call() throws Exception {
            getActivity().findViewById(R.id.progress).setVisibility(View.VISIBLE);
            return postService.fetchPostList(context);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            //throw new RuntimeException();
        }

        @Override
        protected void onSuccess(List<PostVo> postList) throws Exception {
            super.onSuccess(postList);
            //게시글 재 정렬을 위한 저장.
            postList1 = postList;
            mList.clear();
            for (int i = 0; i < postList.size(); i++) {
                mList.add(ItemFactory.createItemFromHttp(postList.get(i), R.drawable.background, getActivity(), mVideoPlayerManager));
                Log.d("FetchPostListAsyncTask", "" + postList.get(i));
            }

            if (postList.size() > 0) {
                show_item();
            }

            getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
            videoListViewAdapter.notifyDataSetChanged();
            scroll();
        }
    }

}