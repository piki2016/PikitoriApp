package piki.example.com.loginpikiapp.pikitori.ui.main.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by joohan on 2016-12-27.
 */

public class Picture implements ClusterItem {
    public final String name;
    public final String filePath;
    private final LatLng position;
    private final int bitmapNo;

    public Picture(LatLng position, String name, String filePath, int bitmapNo) {
        this.name = name;
        this.filePath = filePath;
        this.position = position;
        this.bitmapNo = bitmapNo;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getName() {
        return name;
    }

    public int getBitmapNo() {
        return bitmapNo;
    }


    @Override
    public String toString() {
        return "Picture{" +
                "name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", position=" + position +
                ", bitmapNo=" + bitmapNo +
                '}';
    }
}