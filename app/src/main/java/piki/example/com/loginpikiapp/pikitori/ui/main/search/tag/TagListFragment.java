package piki.example.com.loginpikiapp.pikitori.ui.main.search.tag;

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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.TagService;
import piki.example.com.loginpikiapp.pikitori.domain.TagVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostTagListActivity;

import static android.R.id.list;

/**
 * Created by joohan on 2017-01-31.
 */

public class TagListFragment extends ListFragment {
    private EditText searchTextView;
    TagListArrayAdapter tagListArrayAdapter;
    ListView tagListview;
    private TagService tagService = new TagService();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_tag_list, container, false);
        tagListview = (ListView) rootView.findViewById(list);
        tagListArrayAdapter = new TagListArrayAdapter(getActivity());
        setListAdapter(tagListArrayAdapter);

        searchTextView = (EditText)getActivity().findViewById(R.id.serchTextView);

        //HashTag 검색
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=1){
                    new FetchTagListTask(s).execute();
                }
                tagListArrayAdapter.clear();
                tagListArrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id){
        super.onListItemClick(parent, v, position, id);
        TagVo tagvo = (TagVo) getListView().getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), PostTagListActivity.class);
        intent.putExtra("keyword", tagvo.getTag_name());
        intent.putExtra("tag_no", tagvo.getTag_no());
        startActivity(intent);
    }

    public class TagListArrayAdapter extends ArrayAdapter<TagVo> {
        private LayoutInflater layoutInflater;

        Context mContext;
        public TagListArrayAdapter(Context context ) {
            super( context, R.layout.fragment_tag_list );
            layoutInflater = LayoutInflater.from( context );
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if( view == null ) {
                view = layoutInflater.inflate( R.layout.tag_item, parent, false );
            }

            TagVo tagVo = getItem(position);

            String tag_format = getString(R.string.tag);
            TextView tv_hashTag = (TextView)view.findViewById(R.id.hashTag);
            tv_hashTag.setText(String.format(tag_format,tagVo.getTag_name()));
            return view;
        }

        public void add( List<TagVo> list ) {
            if( list == null ) {
                return;
            }

            for( TagVo tagVo : list ) {
                add( tagVo );
            }
        }
    }

    private class FetchTagListTask extends SafeAsyncTask<List<TagVo>> {
        String str;

        FetchTagListTask(CharSequence s) {
            this.str = s.toString();
        }

        @Override
        public List<TagVo> call() throws Exception {
            return tagService.FetchTagList(getContext(),str);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        protected void onSuccess(List<TagVo> tag) throws Exception {
            for (TagVo t : tag) {
                System.out.println(t);
            }
            tagListArrayAdapter.clear();
            tagListArrayAdapter.add(tag);
        }
    }
}
