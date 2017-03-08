package piki.example.com.loginpikiapp.pikitori.ui.main.post.videoitmes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.CommentService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.CommentVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

/**
 * Created by admin on 2017-02-27.
 */

public class UserReplyArrayAdapter extends ArrayAdapter<CommentVo> {
    private static final String TAG = "UserReplyArrayAdapter" ;
    private ImageView profileImageView;
    private LayoutInflater layoutInflater;
    private CommentService commentService = new CommentService();
    public List<CommentVo> list ;

    private Context mContext;
    public UserReplyArrayAdapter(Context context){
        super(context, R.layout.reply_item);
        mContext = context;
        layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;

        if(view==null){
            view=layoutInflater.inflate(R.layout.reply_item,parent,false);
        }

        final CommentVo comment = getItem(position);
        final int mPosition = position;
        TextView user_name=(TextView)view.findViewById(R.id.reply_user_name);
        TextView reply=(TextView)view.findViewById(R.id.reply);
        TextView reply_regdate=(TextView)view.findViewById(R.id.reply_regdate);
        ImageView btn_delete = (ImageView) view.findViewById(R.id.btn_comment_delete);
        btn_delete.setVisibility(View.GONE);

        reply.setText(comment.getComment_content());
        user_name.setText(comment.getUser_name());
        reply_regdate.setText(comment.getComment_regdate());

        Log.d(TAG, "comment: " + comment);

        ImageLoader.getInstance().displayImage( comment.getUser_profile_url(), (ImageView)view.findViewById( R.id.reply_profile ), BasicInfo.displayImageOption );
        return view;
    }

    public void add(List<CommentVo> list){
        this.list = list;
        if(list==null){
            return ;
        }
        for(CommentVo comment :list){
            add(comment);
        }
    }
}
