package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.CommentVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;

public class CommentService {
    private static  String TAG = "CommentService";
    Gson gson = new Gson();

    public String addReplyInfoTask(Context context, CommentVo comment){
        TAG = "addReplyInfoTask";

        String url = BasicInfo.domain + "mecavo/comment/addreply";

        HttpRequest request = sendDataPost(url,gson.toJson(comment) , Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,"addReplyInfoTask");
        Log.d(TAG,""+jsonResult);

        return (String)jsonResult.getData();
    }

    public List<CommentVo> showReplyList(Context context, PostVo post){
        TAG = "showReplyList";

        String url = BasicInfo.domain + "mecavo/comment/showreplyList";

        HttpRequest request = sendDataPost(url,gson.toJson(post) , Utils.getStringPreferences(context,"sessionID"));

        JSONResult jsonResult = fromJSON(request,JSONResultUserReplyList.class,TAG);

        return (List<CommentVo>) jsonResult.getData();
    }

    public List<CommentVo> show5ReplyList(Context context, PostVo post){
        TAG = "show5ReplyList";

        String url = BasicInfo.domain + "mecavo/comment/show5replyList";

        HttpRequest request = sendDataPost(url,gson.toJson(post) , Utils.getStringPreferences(context,"sessionID"));

        JSONResult jsonResult = fromJSON(request,JSONResultUserReplyList.class,TAG);

        return (List<CommentVo>) jsonResult.getData();
    }

    public String deleteComment(Context context, CommentVo comment) {
        TAG = "deleteComment";

        String url = BasicInfo.domain + "mecavo/comment/deleteComment";

        HttpRequest request = sendDataPost(url,gson.toJson(comment) , Utils.getStringPreferences(context,"sessionID"));

        JSONResult jsonResult = fromJSON(request,JSONResultString.class,TAG);

        return (String) jsonResult.getData();
    }

    private class JSONResultString extends JSONResult<String>{}
    private class JSONResultUserReplyList extends JSONResult<List<CommentVo>> {};
}

