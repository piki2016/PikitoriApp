package piki.example.com.loginpikiapp.pikitori.Service;

import piki.example.com.loginpikiapp.pikitori.Exception.FailException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.saveSession;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;

public class UserService {

    private static String TAG = "UserService";
    Gson gson = new Gson();

    public String checkUserId(String user_id) {
        TAG = "checkUserId";

        String url = BasicInfo.domain+"/mecavo/user/checkemail?user_id="+ user_id;

        HttpRequest request = Utils.sendGet(TAG,url);

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,TAG);

        return ((String)jsonResult.getData() != null)? (String)jsonResult.getData() : (String) jsonResult.getMessage();

    }

    public String createUser(UserVo user) {
        TAG = "createUser";

        Log.d(TAG, "createUser user:" + user);
        String url = BasicInfo.domain+"/mecavo/user/createuser";

        HttpRequest request = sendDataPost(url,gson.toJson(user));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,TAG);

        return ((String)jsonResult.getData() != null)? (String)jsonResult.getData() : (String) jsonResult.getMessage();
    }

    public UserVo Login(Context context, UserVo user) {
        TAG = "Login";

        String url = BasicInfo.domain+"/mecavo/user/login";

        Log.d(TAG,"-------------"+url);

        HttpRequest request = sendDataPost(url,gson.toJson(user));
        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUser.class,TAG);
        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (UserVo) jsonResult.getData();
    }

    public String LoginF_User(Context context, UserVo user) {
        TAG = "LoginF_User";

        String url = BasicInfo.domain+"/mecavo/user/createf_user";
        HttpRequest request = sendDataPost(url,gson.toJson(user));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultString.class,TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        if("fail".equals(jsonResult.getResult())){

        }
        return (String) jsonResult.getData();
    }

    //1. 프로필 정보 가져오기
    public JSONResult getUserProfileInfo(Context context) {
        TAG = "getUserProfileInfo";

        String url = BasicInfo.domain+"/mecavo/user/get_user";

        UserVo user = new UserVo();
        user.setUser_no(Long.parseLong(Utils.getStringPreferences(context,"PostUserNo")));

        Log.d(TAG,"-----------"+url);

        HttpRequest request = sendDataPost(url,gson.toJson(user) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUser.class,TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return jsonResult;
    }

    /* 프로필 수정 사용x */
    public JSONResult updateProfile(Context context, UserVo update_user ) {
        TAG = "updateProfile";

        String url = BasicInfo.domain+"/mecavo/user/update_profile";
        Log.d(TAG, "updateUserProfile: " + update_user);
        Log.d(TAG, Utils.getStringPreferences(context,"sessionID"));
        HttpRequest request = sendDataPost(url,gson.toJson(update_user),Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUser.class,TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return jsonResult;
    }

    /* 프로필 수정 */
    public JSONResult updateProfile(Context context, UserVo update_user,File updateImg ) {
        TAG = "updateProfile2";

        String url = BasicInfo.domain+"/mecavo/user/update_profile_img";

        HttpRequest request = HttpRequest.post(url);
        request.header("Cookie", "JSESSIONID=" + Utils.getStringPreferences(context,"sessionID"));
        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
        request.accept( HttpRequest.CONTENT_TYPE_JSON );
        request.connectTimeout( 2000 );
        request.readTimeout( 30000 );

        request.part("file", updateImg.getName(), updateImg);
        request.part("user_no",update_user.getUser_no());
        request.part("user_id",update_user.getUser_id());
        request.part("user_name", update_user.getUser_name());
        request.part("user_profile_msg", update_user.getUser_profile_msg());

        request.code();

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultUser.class,TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return jsonResult;
    }

    //사람검색 List
//    public List<UserVo> FetchSearchList(String keyword) {
//        TAG = "FetchSearchList";
//
//        //HTTP는 서버와 통신하는데 가장 정통한 방식.
//        String url = BasicInfo.domain+"/mecavo/user/searchname?str=" + keyword;
//        HttpRequest request = HttpRequest.get( url );
//        request.contentType( HttpRequest.CONTENT_TYPE_JSON );
//        request.accept( HttpRequest.CONTENT_TYPE_JSON );
//        request.connectTimeout( 1000 );
//        request.readTimeout( 30000 );
//        int responseCode = request.code();//200이 저장되어있다.
//        //200이면 서버와 제대로 통신 할 준비가 되어있다.
//        if( responseCode != HttpURLConnection.HTTP_OK ) {
//            Log.e( "UserService", "fetchUserList() error : Not 200 OK" );
//            return null;
//        }
//        JSONResultSearchUserList jsonResult = fromJSON( request,JSONResultSearchUserList.class, TAG);
//        return jsonResult.getData();
//    }

    public List<UserVo> FetchSearchList(Context context, String keyword) {
        TAG = "FetchSearchList";

        Long auth_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();

        String url = BasicInfo.domain+"/mecavo/user/searchname";

        HttpRequest request = HttpRequest.post(url);
        request.part("str",keyword);
        request.part("user_no", auth_no);

        request.code();

        JSONResultSearchUserList jsonResult = fromJSON( request,JSONResultSearchUserList.class, TAG);
        return jsonResult.getData();
    }

    public String tokenCheck(Long user_no, String token){
        TAG = "tokenCheck";

        String url = BasicInfo.domain+"/mecavo/user/tokenCheck";
        HttpRequest request = HttpRequest.post(url);
        request.part("user_no",user_no);
        request.part("token", token);

        request.code();

        JSONResultString jsonResult = fromJSON( request, JSONResultString.class,TAG);
        return jsonResult.getData();
    }

    private class JSONResultString extends JSONResult<String>{};
    private class JSONResultUser extends  JSONResult<UserVo>{};
    private class JSONResultSearchUserList extends  JSONResult<List<UserVo>>{};

}
