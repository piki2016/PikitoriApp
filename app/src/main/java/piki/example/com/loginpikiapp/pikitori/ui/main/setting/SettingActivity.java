package piki.example.com.loginpikiapp.pikitori.ui.main.setting;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.ui.Login.LoginUserActivity;

public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener {

    private static final String TAG = "settingActivity";

    private Preference developer_info;
    private Preference logout_preference;
    private Preference facebook_logout_preference;
    private Preference google_logout_preference;
    private Preference password_change;
    private SwitchPreference gps_use_setting;

    GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
        

        developer_info = findPreference("developerInfo");
        logout_preference = findPreference("logout");
        facebook_logout_preference = findPreference("facebook_logout");
        google_logout_preference = findPreference("google_logout");
        password_change = findPreference("password_change");

        developer_info.setOnPreferenceClickListener(this);
        logout_preference.setOnPreferenceClickListener(this);
        facebook_logout_preference.setOnPreferenceClickListener(this);
        google_logout_preference.setOnPreferenceClickListener(this);
        password_change.setOnPreferenceClickListener(this);

        if((Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_social_index() == BasicInfo.PIKIUSER){
            logout_preference.setSummary((Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_id());
        }
        else if((Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_social_index() == BasicInfo.FACEUSER){
            facebook_logout_preference.setSummary((Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_id());
        }
        else if((Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_social_index() == BasicInfo.GOOGLEUSER){
            google_logout_preference.setSummary((Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_id());
        }

    }



    @Override
    public boolean onPreferenceClick(Preference preference) {
        String getKey = preference.getKey();

        if("developerInfo".equals(getKey)){
            Intent intent = new Intent(this,IntroduceActivity.class);
            startActivity(intent);
        } else if("logout".equals(getKey) && (Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_social_index() == BasicInfo.PIKIUSER){
           logout();
        } else if("facebook_logout".equals(getKey) && (Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_social_index() == BasicInfo.FACEUSER){
            LoginManager.getInstance().logOut();
            logout();
        } else if("google_logout".equals(getKey) && (Utils.getUserPreferences(SettingActivity.this,"PikiUser")).getUser_social_index() == BasicInfo.GOOGLEUSER){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            logout();
                        }
                    });
        } else if("password_change".equals(getKey)){
            Intent intent = new Intent(this,PasswordChangeActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void logout(){
        Utils.removePreferences(SettingActivity.this,"user_login_status");
        Utils.removePreferences(SettingActivity.this,"user_type");
        Utils.removePreferences(SettingActivity.this,"PikiUser");
        Utils.removePreferences(SettingActivity.this,"sessionID");
        Intent i = new Intent(SettingActivity.this, LoginUserActivity.class);
        startActivity(i);
        finish();
    }

}
