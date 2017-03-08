package piki.example.com.loginpikiapp.pikitori.ui.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostWriteActivity;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.isValidEmail;

public class LoginUserActivity extends SNSBase implements View.OnClickListener {
    private static final String TAG = "LoginUserActivity";
    private UserService userService = new UserService();

    private TextView warningWindow;
    private EditText editText_email;
    private EditText editText_password;
    private SignInButton btn_google_login;

    public ProgressDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        getSupportActionBar().setTitle("로그인");

        warningWindow = (TextView) findViewById(R.id.warningWindow);
        editText_email = (EditText)findViewById(R.id.editText_email);
        editText_email.addTextChangedListener(new ValidationWatcher(editText_email));
        editText_password = (EditText)findViewById(R.id.editText_password);
        editText_password.addTextChangedListener(new ValidationWatcher(editText_password));

        Button btn_login_connect = (Button) findViewById(R.id.btn_login_connect);
        btn_login_connect.setOnClickListener(this);
        TextView btn_create_account = (TextView) findViewById(R.id.btn_create_account);
        btn_create_account.setOnClickListener(this);

        /*Google*/
        btn_google_login = (SignInButton) findViewById(R.id.btn_google_login);
        btn_google_login.setOnClickListener(this);
        /*FACEBOOK*/
        LoginButton facebook_login = (LoginButton) findViewById(R.id.btn_facebook_login);
        facebook_login.setReadPermissions("public_profile");
        facebook_login.setReadPermissions("email");
        facebook_login.registerCallback(mFacebookcallbackManager, new CustomFaceBookCallback());

    }

    @Override
    protected void onStart() {
        super.onStart();
        warningWindow.setVisibility(View.INVISIBLE);

        /*자동로그인*/
        if (Utils.getBooleanPreferences(this, "user_login_status")) {
            Log.d(TAG,"로그인 가능?" + (UserVo) Utils.getUserPreferences(this,"PikiUser"));
            loginDialog = new ProgressDialog(this);
            loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loginDialog.setMessage("로그인을 시도하고 있습니다..");
            loginDialog.show();
            new LoginTask((UserVo) Utils.getUserPreferences(this,"PikiUser")).execute();
        }

        /*회원가입 화면에서 오는 경우 메시지 출력*/
        if( !TextUtils.isEmpty( getIntent().getStringExtra("joinresult") ) ){
            if(getIntent().getStringExtra("joinresult").equals("success")){
                Toast.makeText(getApplicationContext(),"회원가입이 되었습니다. 로그인하여 주세요.",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (!super.mGoogleApiClient.isConnecting()) {
            switch (view.getId()) {
                case R.id.btn_google_login: {
                    super.onSignIn();
                }
                break;
                case R.id.btn_create_account: {
                    Intent i = new Intent(getApplicationContext(), JoinUserActivity.class);
                    startActivityForResult(i, BasicInfo.RESULT_CODE_JOIN_USER);
                }
                break;
                case R.id.btn_login_connect:{
                    if(check_email && check_password) {
                        UserVo user = new UserVo();
                        user.setUser_id(editText_email.getText().toString());
                        user.setUser_password(editText_password.getText().toString());
                        new LoginTask(user).execute();
                    }else{
                        warningWindow.setText("형식이 맞지 않아 로그인 할수 없습니다.");
                        warningWindow.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
        }

    }

    @Override
    protected void activity_finish() {
        finish();
    }

    @Override
    protected void warning(String message) {
        super.warning(message);
        warningWindow.setText(message);
        warningWindow.setVisibility(View.VISIBLE);
    }

    private boolean check_email = false;
    private boolean check_password = false;
    private class ValidationWatcher implements TextWatcher {

        private View target_view;
        public ValidationWatcher(View view) {
            target_view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            switch (target_view.getId()){
                case R.id.editText_email:{
                    EditText editText_email = (EditText)target_view;

                    if(charSequence.length() >=1){
                        if(!isValidEmail(charSequence.toString())){
                            editText_email.setError("올바른 이메일 형식을 입력해주세요 ");
                            check_email = false;
                        }else{
                            editText_email.setError(null);
                            check_email = true;
                        }
                    }
                }
                break;
                case R.id.editText_password:{

                    EditText editText_password = (EditText)target_view;
                    if(charSequence.length() >=1){
                        if (!Utils.isValidPassword(charSequence.toString())) {
                            editText_password.setError("7자리 이상 비밀번호를 입력해주세요 ");
                            check_password = false;
                        } else {
                            editText_password.setError(null);
                            check_password = true;
                        }
                    }

                }break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class LoginTask extends SafeAsyncTask<UserVo> {

        UserVo user;

        LoginTask(UserVo user) {
            this.user = user;
        }

        @Override
        public UserVo call() throws Exception {
            return userService.Login(getApplicationContext(),user);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            Log.d(TAG, "onException: " + e);
            warning(e.toString());
        }

        @Override
        protected void onSuccess(UserVo result) throws Exception {
            super.onSuccess(result);

            if(loginDialog!=null)loginDialog.dismiss();

            if(result==null){
                Toast.makeText(getApplicationContext(),"로그인 에러났음.",Toast.LENGTH_SHORT).show();
                String error = "로그인이 제대로 이루어 지지 못하였습니다.";
                warningWindow.setText(error);
                warningWindow.setVisibility(View.VISIBLE);
                return;
            }

            Log.d(TAG, "login success" + user + "/" + result);

            //1. 로그인 여부 저장
            Utils.setBooleanPreferences(getApplicationContext(),"user_login_status", BasicInfo.PIKIUSER_SIGNED_IN);
            //2. 로그인 타입 저장
            Utils.setStringPreferences(getApplicationContext(),"user_type", String.valueOf(BasicInfo.PIKIUSER));
            //3. 로그인 유저 정보 저장
            Utils.setUserPreferences(getApplicationContext(),"PikiUser", result);

            Intent i = new Intent(getApplicationContext(),pikiMainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(i, BasicInfo.RESULT_CODE_PIKIMAIN);
            finish();
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
}

