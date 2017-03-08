package piki.example.com.loginpikiapp.pikitori.ui.main.reply;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Comment;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.CommentService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.CommentVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes.UserReplyArrayAdapter;

public class ReplyListActivity extends AppCompatActivity {

    private static final String TAG = "ReplyListActivity";
    private CommentService commentService = new CommentService();
    PostVo postVo = new PostVo();

    ListView listView;
    EditText edit_reply_write;
    ImageView btn_create_reply;

    private CommentArrayAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_list);

        getSupportActionBar().setTitle("댓글");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_reply_write = (EditText) findViewById(R.id.edit_reply_write);
        edit_reply_write.requestFocus();
        btn_create_reply = (ImageView) findViewById(R.id.btn_create_reply);
        listView = (ListView) findViewById(R.id.reply_list);

        commentAdapter = new CommentArrayAdapter(getApplicationContext());
        listView.setAdapter(commentAdapter);

        final Long post_no = getIntent().getLongExtra("post_no", 0L);

        postVo.setUser_no(((UserVo) Utils.getUserPreferences(this, "PikiUser")).getUser_no());
        postVo.setPost_no(post_no);

        new showreplyList(postVo).execute();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        btn_create_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentVo commentVo = new CommentVo();
                commentVo.setUser_no(((UserVo) Utils.getUserPreferences(getApplicationContext(), "PikiUser")).getUser_no());
                commentVo.setPost_no(post_no);
                commentVo.setComment_content(edit_reply_write.getText().toString());
                new addReplyInfoTask(commentVo).execute();
            }
        });

    }

    public class CommentArrayAdapter extends ArrayAdapter<CommentVo> {
        private static final String TAG = "UserReplyArrayAdapter";
        private ImageView profileImageView;
        private LayoutInflater layoutInflater;
        private CommentService commentService = new CommentService();
        ReplyListActivity root = new ReplyListActivity();
        public List<CommentVo> list;

        private Context mContext;

        public CommentArrayAdapter(Context context) {
            super(context, R.layout.reply_item);
            mContext = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = layoutInflater.inflate(R.layout.reply_item, parent, false);
            }

            final CommentVo comment = getItem(position);
            final int mPosition = position;
            TextView user_name = (TextView) view.findViewById(R.id.reply_user_name);
            TextView reply = (TextView) view.findViewById(R.id.reply);
            TextView reply_regdate = (TextView) view.findViewById(R.id.reply_regdate);
            ImageView btn_delete = (ImageView) view.findViewById(R.id.btn_comment_delete);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DeleteCommentTask(mContext, comment, mPosition).execute();
                }
            });

            reply.setText(comment.getComment_content());
            user_name.setText(comment.getUser_name());
            reply_regdate.setText(comment.getComment_regdate());

            if (comment.getUser_no() == ((UserVo) Utils.getUserPreferences(getContext(), "PikiUser")).getUser_no()) {
                btn_delete.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "comment: " + comment);

            ImageLoader.getInstance().displayImage(comment.getUser_profile_url(), (ImageView) view.findViewById(R.id.reply_profile), BasicInfo.displayImageOption);
            return view;
        }

        public void add(List<CommentVo> list) {
            this.list = list;
            if (list == null) {
                return;
            }
            for (CommentVo comment : list) {
                add(comment);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class showreplyList extends SafeAsyncTask<List<CommentVo>> {
        PostVo post;

        public showreplyList(PostVo post) {
            this.post = post;
        }

        @Override
        public List<CommentVo> call() throws Exception {
            return commentService.showReplyList(getApplicationContext(), post);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(List<CommentVo> commentVos) throws Exception {
            commentAdapter.clear();
            commentAdapter.add(commentVos);
        }
    }

    private class addReplyInfoTask extends SafeAsyncTask<String> {
        CommentVo commentvo;

        public addReplyInfoTask(CommentVo commentvo) {
            this.commentvo = commentvo;
        }

        @Override
        public String call() throws Exception {
            return commentService.addReplyInfoTask(getApplicationContext(), commentvo);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            Log.d(TAG, "addReplyInfoTask onSuccess :" + result);
            //1. success
            if ("success".equals(result)) {
                new showreplyList(postVo).execute();
                edit_reply_write.setText("");
                Toast.makeText(getApplicationContext(), "댓글을 달았습니다.", Toast.LENGTH_SHORT).show();
            }
            //2. fail
            if ("fail".equals(result)) {
                Toast.makeText(getApplicationContext(), "comment 생성시 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class DeleteCommentTask extends SafeAsyncTask<String> {
        private Context mContext;
        private CommentVo comment;
        private int position;

        public DeleteCommentTask(Context mContext, CommentVo comment, int mPosition) {
            this.mContext = mContext;
            this.comment = comment;
            this.position = mPosition;
        }

        @Override
        public String call() throws Exception {
            return commentService.deleteComment(mContext, comment);
        }

        @Override
        protected void onSuccess(String s) throws Exception {
            super.onSuccess(s);
            if (s.equals("success")) {
                Toast.makeText(getApplicationContext(), "comment:" + comment, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "delete comment:" + comment);
                commentAdapter.remove(comment);
                commentAdapter.notifyDataSetChanged();
            }
        }
    }
}