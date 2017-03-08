package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.CommonVo;
import piki.example.com.loginpikiapp.pikitori.domain.HeartVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.saveSession;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendGet;

/**
 * Created by joohan on 2017-01-19.
 */

public class PostService {

    private static String TAG = "PostService";
    Gson gson = new Gson();

    public PostVo addPost(Context context, PostVo post){
        TAG = "addPost";

        String url = BasicInfo.domain+"/mecavo/post/add";

        HttpRequest request = sendDataPost(url,gson.toJson(post) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult= fromJSON(request, PostService.JSONResultPost.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (PostVo)jsonResult.getData();
    }

    public List<PostVo> fetchPostList(Context context){
        TAG = "fetchPostList";

        Long auth_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();
        Long view_user_no = Long.parseLong(Utils.getStringPreferences(context,"PostUserNo"));

        String   url = BasicInfo.domain + "/mecavo/post/showpostList";

        Log.d(TAG,"FetchPostListAsyncTask :" + "auth_no " + auth_no + " view_user_no " + view_user_no );

        CommonVo user = new CommonVo();
        user.setAuth_user_no(auth_no);
        user.setUser_no(view_user_no);
        if( auth_no == view_user_no){
            user.setPost_ispublic(true);
        }else{
            user.setPost_ispublic(false);
        }

        HttpRequest request = sendDataPost(url,gson.toJson(user),Utils.getStringPreferences(context,"sessionID"));
        Log.d(TAG,"FetchPostListAsyncTask 5 ");
        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultPostList.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<PostVo>)jsonResult.getData();
    }

    public JSONResult updateHeart(Context context, PostVo post) {
        TAG = "updateHeart";

        String url = BasicInfo.domain+"/mecavo/post/updateHeart";

        HeartVo heartVo = new HeartVo();
        heartVo.setPost_no(post.getPost_no());
        heartVo.setAuth_user_no(((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no());
        heartVo.setUser_no(Long.parseLong( Utils.getStringPreferences(context,"PostUserNo")));

        HttpRequest request = sendDataPost(url,gson.toJson(heartVo),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultPost.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return jsonResult;
    }

    public JSONResult updateUnHeart(Context context, PostVo post) {
        TAG = "updateUnHeart";

        String url = BasicInfo.domain+"/mecavo/post/updateUnHeart";

        HeartVo heartVo = new HeartVo();
        heartVo.setPost_no(post.getPost_no());
        heartVo.setAuth_user_no(((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no());
        heartVo.setUser_no(Long.parseLong( Utils.getStringPreferences(context,"PostUserNo")));

        HttpRequest request = sendDataPost(url,gson.toJson(heartVo),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultPost.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return jsonResult;
    }

    public PostVo getPostInfo(Context context, PostVo post) {
        TAG = "getPostInfo";

        String url = BasicInfo.domain+"/mecavo/post/getPost";

        HttpRequest request = sendDataPost(url,gson.toJson(post),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultPost.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (PostVo) jsonResult.getData();
    }

    public String updatePost(Context context, PostVo post) {
        TAG = "updatePost";

        String url = BasicInfo.domain+"/mecavo/post/updatePost";

        HttpRequest request = sendDataPost(url,gson.toJson(post),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultString.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (String) jsonResult.getData();
    }

    public List<PostVo> FetchPostTagList(Context context, Long tagNo){
        TAG = "FetchPostTagList";

        Long auth_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();
        String url = BasicInfo.domain + "/mecavo/post/postTag";
//        String url = BasicInfo.domain + "/mecavo/post/postTag?tag_no="+ tagNo+"&user_no="+auth_no;

//        HttpRequest request = sendGet("FetchPostTagList",url);
        HttpRequest request = HttpRequest.post(url);
        request.part("tag_no",tagNo);
        request.part("user_no",auth_no);

        JSONResult jsonResult= fromJSON(request, JSONResultPostList.class, "FetchPostTagList");

        saveSession(jsonResult, request, context);

        return (List<PostVo>)jsonResult.getData();
    }

    public String deletePost(Context context, PostVo post) {
        TAG = "deletePost";

        String url = BasicInfo.domain+"/mecavo/post/deletePost";

        HttpRequest request = sendDataPost(url,gson.toJson(post),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request,PostService.JSONResultString.class, TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (String) jsonResult.getData();
    }

    public String modifyPermissionRequest(String text, Long post_no, String user_id){
        TAG = "modifyPermissionRequest";

        String url = BasicInfo.domain+"/mecavo/post/modifyPermissionRequest";

        HttpRequest request = HttpRequest.post(url);
        request.part("text",text);
        request.part("post_no",post_no);
        request.part("user_id",user_id);
        int response = request.code();
        return null;
    }

    public String postPermissionRequestAccept(int status, Long post_no, String request_user_id){
        TAG = "postPermissionRequestAccept";

        String url = BasicInfo.domain+"/mecavo/post/postPermissionRequestAccept";

        HttpRequest request = HttpRequest.post(url);
        request.part("status",status);
        request.part("post_no",post_no);
        request.part("request_user_id",request_user_id);
        int response = request.code();
        return null;
    }

    public Boolean postPermissionCheck(Long post_no, Long user_no){
        TAG = "postPermissionCheck";

        String url = BasicInfo.domain+"/mecavo/post/postPermissionCheck";
        HttpRequest request = HttpRequest.post(url);
        request.part("post_no",post_no);
        request.part("user_no",user_no);
        request.code();

        JSONResult jsonResult = fromJSON(request, JSONResultBoolean.class, TAG);

        return (Boolean)jsonResult.getData();
    }

    private class JSONResultPost extends JSONResult<PostVo> {};
    private class JSONResultPostList extends JSONResult<List<PostVo>>{};
    private class JSONResultString extends JSONResult<String>{};
    private class JSONResultBoolean extends JSONResult<Boolean>{};

}
