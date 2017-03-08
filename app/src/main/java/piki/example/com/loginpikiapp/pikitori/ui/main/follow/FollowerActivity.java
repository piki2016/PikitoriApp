package piki.example.com.loginpikiapp.pikitori.ui.main.follow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.FollowService;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.FollowVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.displayImageOption;

/**
 * Created by joohan on 2017-01-31.
 */

public class FollowerActivity extends AppCompatActivity{
    FollowService followService =  new FollowService();
    FollowVo follow = new FollowVo();
    ListView followerListView;
    FollowAdapter adapter;
    Long no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        getSupportActionBar().setTitle("팔로워");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        followerListView = (ListView) findViewById(R.id.pikiFollowerListView);
        adapter = new FollowAdapter(getApplicationContext());
        followerListView.setAdapter(adapter);

        no = getIntent().getLongExtra("view_no",0L);
        if(no == 0){
            no = ((UserVo)Utils.getUserPreferences(getApplicationContext(),"PikiUser")).getUser_no();
        }

        new FetchFollowerListTask(getApplicationContext(),no).execute();

        followerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FollowVo follow = (FollowVo) parent.getAdapter().getItem(position);
                Intent intent = new Intent(FollowerActivity.this, pikiMainActivity.class);
                intent.putExtra("view_no", follow.getUser_no_friend());
                startActivity(intent);

            }
        });
    }

    private class FollowAdapter extends ArrayAdapter<FollowVo> {

        private LayoutInflater layoutInflater;
        Context mcContext;
        public FollowAdapter(Context context) {
            super(context, R.layout.follow_item);
            layoutInflater = LayoutInflater.from(context);
            this.mcContext  = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = layoutInflater.inflate(R.layout.follow_item, parent, false);
            }

            FollowVo followVo = getItem(position);

//          1. 프로필
            ImageLoader.getInstance().displayImage(followVo.getUser_profile_url_friend(), (ImageView) view.findViewById(R.id.userProfile), displayImageOption);
//          2. 아이디
            TextView tv_userId = (TextView) view.findViewById(R.id.userId);
            tv_userId.setText(followVo.getUser_id_friend());
//          3. 닉네임
            TextView tv_userName = (TextView)view.findViewById(R.id.userName);
            tv_userName.setText(followVo.getUser_name_friend());
//          4. 상태 메세지
            TextView tv_userMsg = (TextView)view.findViewById(R.id.userMsg);
            tv_userMsg.setText(followVo.getUser_profile_msg_friend());

            return view;
        }

        public void add(List<FollowVo> list) {
            if (list == null) {
                return;
            }

            for (FollowVo followVo : list) {
                add(followVo);
            }
        }

    }

    private class FetchFollowerListTask extends SafeAsyncTask<List<FollowVo>> {

        Context context;
        FetchFollowerListTask(Context context, Long no) {
            this.context = context;
        }

        @Override
        public List<FollowVo> call() throws Exception {
            return followService.fetchFollowerList(context, no);
         }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<FollowVo> followList) throws Exception {
            adapter.clear();
            adapter.add(followList);
        }
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

}
