package piki.example.com.loginpikiapp.pikitori.ui.main.search.people;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;

import static android.R.id.list;
import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.displayImageOption;

/**
 * Created by joohan on 2017-01-31.
 */

public class SearchListFragment extends ListFragment {

    private EditText searchTextView;
    SearchListArrayAdapter searchListArrayAdapter;
    ListView searchListView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootview = inflater.inflate(R.layout.fragment_search_list, container, false);
        searchListView = (ListView) rootview.findViewById(list);
        searchListArrayAdapter = new SearchListArrayAdapter(getActivity());
        setListAdapter(searchListArrayAdapter);

        searchTextView = (EditText) getActivity().findViewById(R.id.serchTextView);

        //Name 유저 검색
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                if (s.length() >= 1) {
                    new FetchSearchListTask(s).execute();
                }
                searchListArrayAdapter.clear();
                searchListArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을 때
            }
        });
        return rootview;
    }

    //List item항목 클릭시 해당유저 화면전환
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        Intent intent = new Intent(getActivity(), SecondActivity.class);
//        startActivity(intent);
    }

    public class SearchListArrayAdapter extends ArrayAdapter<UserVo> {
        private LayoutInflater layoutInflater;


        Context mContext;

        public SearchListArrayAdapter(Context context) {
            super(context, R.layout.fragment_search_list);
            layoutInflater = LayoutInflater.from(context);
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.search_item, parent, false);
            }
            final UserVo user = getItem(position);
            //      1. 프로필
            ImageLoader.getInstance().displayImage(user.getUser_profile_url(), (ImageView) view.findViewById(R.id.searchUserProfile), displayImageOption);

            //      2. 아이디
            TextView tv_userId = (TextView) view.findViewById(R.id.userId);
            tv_userId.setText(user.getUser_id());
            tv_userId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), pikiMainActivity.class);
                    intent.putExtra("view_no", user.getUser_no());
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            //      3. 닉네임
            TextView tv_userName = (TextView) view.findViewById(R.id.userName);
            tv_userName.setText(user.getUser_name());

            return view;
        }

        public void add(List<UserVo> list) {
            if (list == null) {
                return;
            }

            for (UserVo user : list) {
                add(user);
            }
        }

    }

    //Asynctask
    private class FetchSearchListTask extends SafeAsyncTask<List<UserVo>> {
        String str;
        UserService userService = new UserService();

        FetchSearchListTask(CharSequence s) {
            this.str = s.toString();
        }

        @Override
        public List<UserVo> call() throws Exception {
            return userService.FetchSearchList(getContext(),str);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        protected void onSuccess(List<UserVo> user) throws Exception {
            searchListArrayAdapter.clear();
            searchListArrayAdapter.add(user);
        }
    }
}
