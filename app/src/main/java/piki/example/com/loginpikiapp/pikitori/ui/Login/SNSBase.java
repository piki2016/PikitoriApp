package piki.example.com.loginpikiapp.pikitori.ui.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import org.json.JSONObject;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;

import static piki.example.com.loginpikiapp.pikitori.core.BasicInfo.RC_SIGN_IN;

public class SNSBase extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SNSBase";
    protected GoogleApiClient mGoogleApiClient;
    protected CallbackManager mFacebookcallbackManager;

    private UserService userService = new UserService();
    private Gson gson = new Gson();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*뒤로 가기 버튼 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Google 로그인 초기화*/
        mGoogleApiClient = buildGoogleApiClient();
         /*FaceBook 로그인 콜백함수*/
        mFacebookcallbackManager = CallbackManager.Factory.create();

    }

    private GoogleApiClient buildGoogleApiClient() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                          .requestIdToken(getString(R.string.default_web_client_id))
                                                          .requestEmail()
                                                          .build();

        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //silentSignIn .
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if(opr.isDone()){
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        }else{
//            showProgressDialog("Google 로그인을 시도하고 있습니다. 잠시 기다려 주세요.");
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
//        mGoogleApiClient.connect();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        GoogleSignInAccount acct = null;
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            UpdateUI(acct, true);
        } else {
            UpdateUI(acct, false);
        }
    }

    /* 구글 회원가입 */
    protected void UpdateUI(GoogleSignInAccount account, boolean success) {
        if (success) {
            UserVo user = new UserVo();
            user.setUser_social_id(account.getId());
            user.setUser_social_index(BasicInfo.GOOGLEUSER);
            user.setUser_name(account.getDisplayName());
            user.setUser_id(account.getEmail());
            user.setUser_profile_url(account.getPhotoUrl().toString());

            showProgressDialog("구글 회원을 등록하고 있습니다. 잠시 기다려 주세요.");
            new LoginFaceTask(user).execute();
        } else {
            warning(null);
        }
    }

    protected void warning(String message) {
        if(message == null){
            message = "회원이 존재 하지 않습니다. 회원가입 해주세요.";
        }
    }

    //OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void onSignedOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    protected void onSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*Facebook Login Callback */
        mFacebookcallbackManager.onActivityResult(requestCode, resultCode, data);
        /*Goolgle Login Callback */
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*   SNS 회원의 회원가입. */
    protected class LoginFaceTask extends SafeAsyncTask<String> {
        UserVo user;

        LoginFaceTask(UserVo userVo) {
            this.user = userVo;
        }

        @Override
        public String call() throws Exception {
            return userService.LoginF_User(getApplicationContext(), user);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            hideProgressDialog();
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);
            hideProgressDialog();

            if (result.startsWith("error")) {
                warning(result);
                return;
            }

            Long user_no = Long.parseLong(result);
            user.setUser_no(user_no);
            if (user == null) {
                warning("user null");
            }

            //1. 로그인 여부 저장
            Utils.setBooleanPreferences(getApplicationContext(),"user_login_status", BasicInfo.PIKIUSER_SIGNED_IN);
            //2. 로그인 타입 저장 **
            if(user.getUser_social_index() == BasicInfo.FACEUSER){
                Utils.setStringPreferences(getApplicationContext(),"user_type", String.valueOf(BasicInfo.FACEUSER));
            }else if(user.getUser_social_index() == BasicInfo.GOOGLEUSER){
                Utils.setStringPreferences(getApplicationContext(),"user_type", String.valueOf(BasicInfo.GOOGLEUSER));
            }else{
                Toast.makeText(getApplicationContext(),"올바른 SNS 유저가 아닙니다.", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(),"LoginFaceTask" + user , Toast.LENGTH_SHORT).show();
            //3. 로그인 유저 정보 저장
            Utils.setUserPreferences(getApplicationContext(),"PikiUser", user);

            Intent i = new Intent(getApplicationContext(), pikiMainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(i, BasicInfo.RESULT_CODE_PIKIMAIN);
            activity_finish();
        }

    }

    protected class CustomFaceBookCallback implements FacebookCallback<LoginResult> {
        private final String TAG="CustomFaceBookCallback";
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "onSuccess: " + loginResult);
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject f_user, GraphResponse response) {

                    Log.d(TAG,f_user.optString("url"));
                    Log.d(TAG, response.toString());
                    UserVo user = new UserVo();
                    user.setUser_social_id(f_user.optString("id"));
                    user.setUser_social_index(BasicInfo.FACEUSER);
                    user.setUser_id(f_user.optString("email"));
                    if(f_user.optString("email").equals("")){
                        Toast.makeText(getApplicationContext(),"이메일 주소를 가져오지 못함. JoInUserActivity",Toast.LENGTH_SHORT).show();
                    }
                    user.setUser_name(f_user.optString("name"));
                    user.setUser_profile_url(f_user.optString("url"));
                    Log.d(TAG, user.toString());
                    showProgressDialog("페이스북으로 로그인을 시도하고 있습니다. 잠시 기다려 주세요.");
                    new LoginFaceTask(user).execute();
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, link, email, picture");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.d(TAG,"registerCallback onCancel");
            Toast.makeText(getApplicationContext(),"FACEBOOK 로그인에 실패하였습니다.",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG,"registerCallback onError");
            Toast.makeText(getApplicationContext(), "FaceBook 로그인에 중대한 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();

        }
    }

    protected void activity_finish() {
        finish();
    }

}
