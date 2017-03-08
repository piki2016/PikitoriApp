package piki.example.com.loginpikiapp.pikitori.core;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joohan on 2017-01-19.
 */

public class TagParse {
    public ArrayList<String> exportHashTag(String text){

        String strHashTag = null;
        ArrayList<String> list = new ArrayList<String>();

        //#뒤에 문자, 숫자 특수문자가 있는지 확인하는 정규식( space, #제외 )
        Pattern p = Pattern.compile("\\#([0-9a-zA-Z가-힣-_+=!@$%^&*()\\[\\]{}|\\;:'\"<>,.?/~`)]*)");

        //text와 정규식과 비교
        Matcher m = p.matcher(text);

        // find() : 패턴이 일치하는 경우 true를 반환하고, 그 위치로 이동(여러개가 매칭되는 경우 반복 실행가능)
        while(m.find()){

            // group() : 매칭된 부분을 반환
            strHashTag = hashTagReplace(m.group());

            if(!strHashTag.equals("")){
                list.add(strHashTag);
            }
        }
        return list;
    }

    // _ 를 제외한 특수문자를 제거하고 그 스트링을 return
    public String hashTagReplace(String str){
        str = StringUtils.replaceChars(str,"-+=!@#$%^&*()[]{}|\\;:'\"<>,.?/~`) ","");
        if(str==null){
            return null;
        }
        return str;
    }
}
