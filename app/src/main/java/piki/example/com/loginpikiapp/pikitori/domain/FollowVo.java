package piki.example.com.loginpikiapp.pikitori.domain;

public class FollowVo {

    private Long user_no;
    private String user_id;
    private String user_name;
    private Long user_no_friend;
    private String user_id_friend;
    private String user_name_friend;
    private String user_profile_url_friend;
    private String user_profile_msg_friend;
    private Long user_post_msg;
	public Long getUser_no() {
		return user_no;
	}
	public void setUser_no(Long user_no) {
		this.user_no = user_no;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Long getUser_no_friend() {
		return user_no_friend;
	}
	public void setUser_no_friend(Long user_no_friend) {
		this.user_no_friend = user_no_friend;
	}
	public String getUser_id_friend() {
		return user_id_friend;
	}
	public void setUser_id_friend(String user_id_friend) {
		this.user_id_friend = user_id_friend;
	}
	public String getUser_name_friend() {
		return user_name_friend;
	}
	public void setUser_name_friend(String user_name_friend) {
		this.user_name_friend = user_name_friend;
	}
	public String getUser_profile_url_friend() {
		return user_profile_url_friend;
	}
	public void setUser_profile_url_friend(String user_profile_url_friend) {
		this.user_profile_url_friend = user_profile_url_friend;
	}
	public String getUser_profile_msg_friend() {
		return user_profile_msg_friend;
	}
	public void setUser_profile_msg_friend(String user_profile_msg_friend) {
		this.user_profile_msg_friend = user_profile_msg_friend;
	}
	public Long getUser_post_msg() {
		return user_post_msg;
	}
	public void setUser_post_msg(Long user_post_msg) {
		this.user_post_msg = user_post_msg;
	}
	
	@Override
	public String toString() {
		return "FollowVo [user_no=" + user_no + ", user_id=" + user_id + ", user_name=" + user_name
				+ ", user_no_friend=" + user_no_friend + ", user_id_friend=" + user_id_friend + ", user_name_friend="
				+ user_name_friend + ", user_profile_url_friend=" + user_profile_url_friend
				+ ", user_profile_msg_friend=" + user_profile_msg_friend + ", user_post_msg=" + user_post_msg + "]";
	}
    
}