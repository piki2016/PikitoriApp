package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.TagVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;

/**
 * Created by joohan on 2017-01-31.
 */

public class TagService {

    private static String TAG = "TagService";

    public List<TagVo> FetchTagList(Context context, String str){
        TAG = "FetchTagList";

        Long auth_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();

//        String query = null;
//        try {
//            query = URLEncoder.encode(str,"utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        String url = BasicInfo.domain+"tag/searchtag?str=" + query+"&user_no="+auth_no;
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
//        HttpRequest request =  Utils.sendGet(TAG, url);
        String url = BasicInfo.domain+"tag/searchtag";
        HttpRequest request  = HttpRequest.post(url);
        request.part("str",str);
        request.part("user_no",auth_no);

        request.code();

        JSONResultTagList jsonResult = fromJSON( request, JSONResultTagList.class,TAG );
        return jsonResult.getData();
    }

    private class JSONResultTagList extends JSONResult<List<TagVo>> {};

}
