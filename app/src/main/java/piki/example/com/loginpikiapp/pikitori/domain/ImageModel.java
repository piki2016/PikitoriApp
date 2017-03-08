package piki.example.com.loginpikiapp.pikitori.domain;

public class ImageModel {
    String id;
    String uri;
    String bucket;
    String Day;
    String lat;
    String lng;
    boolean selected;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public ImageModel(String id, String uri, String bucket, boolean selected) {
        this.id = id;
        this.uri = uri;
        this.bucket = bucket;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "id='" + id + '\'' +
                ", uri='" + uri + '\'' +
                ", bucket='" + bucket + '\'' +
                ", Day='" + Day + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", selected=" + selected +
                '}';
    }
}
