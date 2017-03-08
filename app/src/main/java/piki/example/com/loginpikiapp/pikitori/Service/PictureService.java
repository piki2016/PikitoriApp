package piki.example.com.loginpikiapp.pikitori.Service;

import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.saveSession;
import static piki.example.com.loginpikiapp.pikitori.core.Utils.sendDataPost;

/**
 * Created by joohan on 2017-01-19.
 */

public class PictureService {


    private static  String TAG = "PictureService";
    Gson gson = new Gson();

    public String makeMovie(Context context,List<File> fileList, PostVo post, double speed){
        TAG = "makeMovie";

        String url = BasicInfo.domain+"/mecavo/picture/makemovie";
        Long user_no = ((UserVo)Utils.getUserPreferences(context,"PikiUser")).getUser_no();
        HttpRequest request = HttpRequest.post(url);

        for (File f : fileList) {
            request.part("fileList", f.getName(), f);
        }

        for(int i =  0; i< post.getPictureList().size(); i++){
            request.part("pictureNoList",post.getPictureList().get(i).getPicture_no());
            request.part("pictureType",post.getPictureList().get(i).getPicture_local_url());
            System.out.println(post.getPictureList().get(i).getPicture_no());
        }
        request.part("postNo",post.getPost_no());
        request.part("userNo",user_no);
        request.part("speed",speed);
        int response = request.code();

        JSONResult jsonResult = fromJSON(request, JSONResulString.class,TAG);
        return (String) jsonResult.getData();
    }

    public List<PictureVo> getPicture(Long user_no){
        TAG = "getPicture";

        String url = BasicInfo.domain+"/mecavo/picture/getpictureuseingmap";
        HttpRequest request = HttpRequest.post(url);
        request.part("user_no",user_no);
        int response = request.code();
        PictureService.JSONResultPictureList jsonResult = fromJSON(request, PictureService.JSONResultPictureList.class,TAG);
        return jsonResult.getData();
    }

    public List<PictureVo> getPictureGallery(Context context, PostVo post){
        TAG = "getPictureGallery";

        Log.d(TAG, "post:" + post);

        String url = BasicInfo.domain+"/mecavo/picture/getpicturegallery";

        HttpRequest request = sendDataPost(url,gson.toJson(post) ,Utils.getStringPreferences(context,"sessionID"));

        //1.  결과값
        JSONResult jsonResult = fromJSON(request, JSONResultPictureList.class,TAG);

        //2.  세션관리
        saveSession(jsonResult,request,context);

        return (List<PictureVo>) jsonResult.getData();
    }

    private class JSONResultPictureList extends JSONResult<List<PictureVo>>{};

    private class JSONResulString extends JSONResult<String>{};
}
