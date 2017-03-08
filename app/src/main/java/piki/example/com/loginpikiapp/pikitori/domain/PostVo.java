package piki.example.com.loginpikiapp.pikitori.domain;

import java.util.List;


public class PostVo {

    private Long post_no;
    private String post_content;
    private String post_movie;
    private String post_regdate;
    private Long post_ispublic;
    private Long post_comment_count;
    private boolean heartflag;
    private Long post_heart_count;
    private Long user_no;
    private String user_name;
    private String user_profile_url;
    private List<CommentVo> commentList;
    private List<PictureVo> pictureList;
    private List<TagVo> tagList;

    public Long getPost_no() {
        return post_no;
    }

    public void setPost_no(Long post_no) {
        this.post_no = post_no;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_movie() {
        return post_movie;
    }

    public void setPost_movie(String post_movie) {
        this.post_movie = post_movie;
    }

    public String getPost_regdate() {
        return post_regdate;
    }

    public void setPost_regdate(String post_regdate) {
        this.post_regdate = post_regdate;
    }

    public Long getPost_ispublic() {
        return post_ispublic;
    }

    public void setPost_ispublic(Long post_ispublic) {
        this.post_ispublic = post_ispublic;
    }

    public Long getPost_comment_count() {
        return post_comment_count;
    }

    public void setPost_comment_count(Long post_comment_count) {
        this.post_comment_count = post_comment_count;
    }

    public boolean isHeartflag() {
        return heartflag;
    }

    public void setHeartflag(boolean heartflag) {
        this.heartflag = heartflag;
    }

    public Long getPost_heart_count() {
        return post_heart_count;
    }

    public void setPost_heart_count(Long post_heart_count) {
        this.post_heart_count = post_heart_count;
    }

    public Long getUser_no() {
        return user_no;
    }

    public void setUser_no(Long user_no) {
        this.user_no = user_no;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_url() {
        return user_profile_url;
    }

    public void setUser_profile_url(String user_profile_url) {
        this.user_profile_url = user_profile_url;
    }

    public List<CommentVo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentVo> commentList) {
        this.commentList = commentList;
    }

    public List<PictureVo> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<PictureVo> pictureList) {
        this.pictureList = pictureList;
    }

    public List<TagVo> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagVo> tagList) {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return "PostVo{" +
                "post_no=" + post_no +
                ", post_content='" + post_content + '\'' +
                ", post_movie='" + post_movie + '\'' +
                ", post_regdate='" + post_regdate + '\'' +
                ", post_ispublic=" + post_ispublic +
                ", post_comment_count=" + post_comment_count +
                ", heartflag=" + heartflag +
                ", post_heart_count=" + post_heart_count +
                ", user_no=" + user_no +
                ", user_name='" + user_name + '\'' +
                ", user_profile_url='" + user_profile_url + '\'' +
                ", commentList=" + commentList +
                ", pictureList=" + pictureList +
                ", tagList=" + tagList +
                '}';
    }
}
