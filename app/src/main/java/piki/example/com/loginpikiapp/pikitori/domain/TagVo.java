package piki.example.com.loginpikiapp.pikitori.domain;

import java.util.List;

/**
 * Created by joohan on 2017-01-19.
 */

public class TagVo {
    private Long tag_no;
    private String tag_name;
    private Long tag_search_count;
    private List<PostVo> postList;

    @Override
    public String toString() {
        return "TagVo{" +
                "tag_no=" + tag_no +
                ", tag_name='" + tag_name + '\'' +
                ", tag_search_count=" + tag_search_count +
                ", postList=" + postList +
                '}';
    }

    public List<PostVo> getPostList() {
        return postList;
    }

    public void setPostList(List<PostVo> postList) {
        this.postList = postList;
    }

    public Long getTag_no() {
        return tag_no;
    }

    public void setTag_no(Long tag_no) {
        this.tag_no = tag_no;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public Long getTag_search_count() {
        return tag_search_count;
    }

    public void setTag_search_count(Long tag_search_count) {
        this.tag_search_count = tag_search_count;
    }

}

