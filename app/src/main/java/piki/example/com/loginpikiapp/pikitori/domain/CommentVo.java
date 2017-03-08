package piki.example.com.loginpikiapp.pikitori.domain;

public class CommentVo {
    private Long comment_no;
    private String comment_content;
    private String comment_regdate;
    private Long user_no;
    private Long post_no;
    private String user_profile_url;
    private String user_name;
    public Long getComment_no() {
        return comment_no;
    }
    public void setComment_no(Long comment_no) {
        this.comment_no = comment_no;
    }
    public String getComment_content() {
        return comment_content;
    }
    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }
    public String getComment_regdate() {
        return comment_regdate;
    }
    public void setComment_regdate(String comment_regdate) {
        this.comment_regdate = comment_regdate;
    }
    public Long getUser_no() {
        return user_no;
    }
    public void setUser_no(Long user_no) {
        this.user_no = user_no;
    }
    public Long getPost_no() {
        return post_no;
    }
    public void setPost_no(Long post_no) {
        this.post_no = post_no;
    }
    public String getUser_profile_url() {
        return user_profile_url;
    }
    public void setUser_profile_url(String user_profile_url) {
        this.user_profile_url = user_profile_url;
    }
    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    @Override
    public String toString() {
        return "CommentVo [comment_no=" + comment_no + ", comment_content=" + comment_content + ", comment_regdate="
                + comment_regdate + ", user_no=" + user_no + ", post_no=" + post_no + ", user_profile_url="
                + user_profile_url + ", user_name=" + user_name + "]";
    }



}