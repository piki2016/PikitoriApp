package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.FollowVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.saveSession;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;

/**
 * Created by joohan on 2017-01-31.
 */

public class FollowService {
    private static final String TAG = "FollowService";
    Gson gson = new Gson();

    public String followCheck(Context context){
        String url = BasicInfo.domain+"/mecavo/follow/followcheck";

        Long user_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();
        Long user_no_friend = Long.parseLong((String)Utils.getStringPreferences(context,"PostUserNo"));

        FollowVo followVo = new FollowVo();
        followVo.setUser_no(user_no);
        followVo.setUser_no_friend( user_no_friend );

        if(user_no.equals(user_no_friend)){
            return null;
        }

        HttpRequest request = sendDataPost(url,gson.toJson(followVo),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,"followCheck");

        return (String)jsonResult.getData();
    }

    public String following(Context context){

        String url = BasicInfo.domain+"/mecavo/follow/following" ;

        Long user_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();
        Long user_no_friend = Long.parseLong((String)Utils.getStringPreferences(context,"PostUserNo"));

        FollowVo follow = new FollowVo();
        follow.setUser_no(user_no);
        follow.setUser_no_friend( user_no_friend );

        HttpRequest request = sendDataPost(url,gson.toJson(follow) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,"following");

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (String)jsonResult.getData();
    }

    public String unFollowing(Context context){

        String url = BasicInfo.domain+"/mecavo/follow/unfollowing";

        Long user_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();
        Long user_no_friend = Long.parseLong((String)Utils.getStringPreferences(context,"PostUserNo"));

        FollowVo followVo = new FollowVo();
        followVo.setUser_no(user_no);
        followVo.setUser_no_friend( user_no_friend );

        if(user_no.equals(user_no_friend)){
            return null;
        }

        HttpRequest request = sendDataPost(url,gson.toJson(followVo),Utils.getStringPreferences(context,"sessionID"));

        JSONResult jsonResult = fromJSON(request, JSONResultString.class,"unFollowing");

        return (String)jsonResult.getData();
    }

    public List<FollowVo> fetchFollowList(Context context,Long no) {

        String url = BasicInfo.domain+"/mecavo/follow/followlist";

        UserVo user = new UserVo();
        user.setUser_no(no);

        HttpRequest request = sendDataPost(url,gson.toJson(user) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUserList.class,TAG);

        for(FollowVo v: (List<FollowVo>)jsonResult.getData()){
            Log.d(TAG,"success: " + v);
        }
        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<FollowVo>)jsonResult.getData();
    }

    public List<FollowVo> fetchFollowerList(Context context, Long no) {
        //HTTP는 서버와 통신하는데 가장 정통한 방식.

        String url = BasicInfo.domain+"/mecavo/follow/followerlist";

        UserVo user = new UserVo();
        user.setUser_no(no);

        HttpRequest request = sendDataPost(url,gson.toJson(user) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUserList.class,TAG);

        System.out.println("fetchFollowList " + jsonResult.getResult());
        for(FollowVo v: (List<FollowVo>)jsonResult.getData()){
            Log.d(TAG,"success: " + v);
        }
        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<FollowVo>)jsonResult.getData();
    }

    private class JSONResultUserList extends JSONResult<List<FollowVo>> {};
    private class JSONResultString extends JSONResult<String>{}
    private class JSONResultFollowing extends JSONResult<FollowVo>{};

}
