package piki.example.com.loginpikiapp.pikitori.domain;

public class PictureVo {
    private Long picture_no;
    private String picture_url;
    private String picture_ext;
    private String picture_location;
    private Double picture_lat;
    private Double picture_lng;
    private String picture_local_url;
    private String picture_url_thumb;
    private Long user_no;

    public Long getPicture_no() {
        return picture_no;
    }

    public void setPicture_no(Long picture_no) {
        this.picture_no = picture_no;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getPicture_ext() {
        return picture_ext;
    }

    public void setPicture_ext(String picture_ext) {
        this.picture_ext = picture_ext;
    }

    public String getPicture_location() {
        return picture_location;
    }

    public void setPicture_location(String picture_location) {
        this.picture_location = picture_location;
    }

    public Double getPicture_lat() {
        return picture_lat;
    }

    public void setPicture_lat(Double picture_lat) {
        this.picture_lat = picture_lat;
    }

    public Double getPicture_lng() {
        return picture_lng;
    }

    public void setPicture_lng(Double picture_lng) {
        this.picture_lng = picture_lng;
    }

    public String getPicture_local_url() {
        return picture_local_url;
    }

    public void setPicture_local_url(String picture_local_url) {
        this.picture_local_url = picture_local_url;
    }

    public String getPicture_url_thumb() {
        return picture_url_thumb;
    }

    public void setPicture_url_thumb(String picture_url_thumb) {
        this.picture_url_thumb = picture_url_thumb;
    }

    public Long getUser_no() {
        return user_no;
    }

    public void setUser_no(Long user_no) {
        this.user_no = user_no;
    }

    @Override
    public String toString() {
        return "PictureVo{" +
                "picture_no=" + picture_no +
                ", picture_url='" + picture_url + '\'' +
                ", picture_ext='" + picture_ext + '\'' +
                ", picture_location='" + picture_location + '\'' +
                ", picture_lat=" + picture_lat +
                ", picture_lng=" + picture_lng +
                ", picture_local_url='" + picture_local_url + '\'' +
                ", picture_url_thumb='" + picture_url_thumb + '\'' +
                ", user_no=" + user_no +
                '}';
    }
}