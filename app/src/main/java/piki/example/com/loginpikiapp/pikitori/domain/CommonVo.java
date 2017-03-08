package piki.example.com.loginpikiapp.pikitori.domain;

/**
 * Created by admin on 2017-02-26.
 */

public class CommonVo {

    private Long auth_user_no;
    private Long user_no;
    private Long post_no;
    private boolean post_ispublic;

    public Long getAuth_user_no() {
        return auth_user_no;
    }

    public void setAuth_user_no(Long auth_user_no) {
        this.auth_user_no = auth_user_no;
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

    public boolean isPost_ispublic() {
        return post_ispublic;
    }

    public void setPost_ispublic(boolean post_ispublic) {
        this.post_ispublic = post_ispublic;
    }

    @Override
    public String toString() {
        return "CommonVo{" +
                "auth_user_no=" + auth_user_no +
                ", user_no=" + user_no +
                ", post_no=" + post_no +
                ", post_ispublic=" + post_ispublic +
                '}';
    }
}
