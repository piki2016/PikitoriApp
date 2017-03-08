package piki.example.com.loginpikiapp.pikitori.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;

public class ProfileViewActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        imageView = (ImageView)findViewById(R.id.OriginalProfileImage);
        String url;
        Intent intent = getIntent();
        url = intent.getStringExtra("userProfileImage");

        ImageLoader.getInstance().displayImage( url, imageView, BasicInfo.displayImageOption );

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
