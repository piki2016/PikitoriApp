package piki.example.com.loginpikiapp.pikitori.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.isValidEmail;

public class JoinUserActivity extends SNSBase implements View.OnClickListener {
    private static final String TAG = "JoinUserActivity";
    private UserService userService = new UserService();

    TextView warningWindow;
    EditText editText_email;
    EditText editText_name;
    EditText editText_password;
    EditText editText_password_confirm;
    Button btn_join_connect;
    TextView btn_goto_login;
    SignInButton btn_google_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_user);

        getSupportActionBar().setTitle("회원가입");

        warningWindow = (TextView) findViewById(R.id.warningWindow);
        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_email.addTextChangedListener(new ValidationWatcher(editText_email));
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_name.addTextChangedListener(new ValidationWatcher(editText_name));
        editText_password = (EditText) findViewById(R.id.editText_password);
        editText_password.addTextChangedListener(new ValidationWatcher(editText_password));
        editText_password_confirm = (EditText) findViewById(R.id.editText_password_confirm);
        editText_password_confirm.addTextChangedListener(new ValidationWatcher(editText_password_confirm));
        btn_join_connect = (Button) findViewById(R.id.btn_join_connect);
        btn_join_connect.setOnClickListener(this);

        editText_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean status) {
                 /*회원가입 이메일 중복 검사*/
                if (status == false) {
                    /*이메일 validation 검사*/
                    if(check_email){
                        new checkUserIdTask(((EditText) view).getText().toString(), view).execute();
                    }
                }
            }
        });

        btn_goto_login = (TextView) findViewById(R.id.btn_goto_login);
        btn_goto_login.setOnClickListener(this);

        /*Google 로그인 */
        btn_google_login = (SignInButton) findViewById(R.id.btn_google_login);
        btn_google_login.setOnClickListener(this);

        /*FACEBOOK 로그인*/
        LoginButton facebook_login = (LoginButton) findViewById(R.id.btn_facebook_login);
        facebook_login.setReadPermissions("public_profile");
        facebook_login.setReadPermissions("email");
        facebook_login.registerCallback(mFacebookcallbackManager, new CustomFaceBookCallback());

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*UI warning창 숨기기*/
        warningWindow.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_google_login: {
                /*Google 로그인 */
                super.onSignIn();
            }
            break;
            case R.id.btn_goto_login: {
                Intent i = new Intent(this, LoginUserActivity.class);
                startActivityForResult(i, BasicInfo.RESULT_CODE_LOGIN_USER);
                finish();
            }
            break;
            case R.id.btn_join_connect: {
                if (check_email && check_name && check_password && check_password_confirm) {
                    UserVo user = new UserVo();
                    user.setUser_id(editText_email.getText().toString());
                    user.setUser_name(editText_name.getText().toString());
                    user.setUser_password(editText_password.getText().toString());
                    super.showProgressDialog("Piki 회원가입을 시도하고 있습니다. 잠시 기다려 주세요.");
                    new CreateUserTask(user).execute();
                } else {
                    warningWindow.setText("형식이 맞지 않아 회원가입 할수 없습니다.");
                    warningWindow.setVisibility(View.VISIBLE);
                }
            }
            break;
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

    protected void hideProgressDialog(){
        super.hideProgressDialog();
    }

    private boolean check_email = false;
    private boolean check_name = false;
    private boolean check_password = false;
    private boolean check_password_confirm = false;
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

            switch (target_view.getId()) {

                case R.id.editText_email: {
                    EditText editText_email = (EditText) target_view;

                    if (charSequence.length() >= 1) {
                        if (!isValidEmail(charSequence.toString())) {
                            editText_email.setError("올바른 이메일 형식을 입력해주세요 ");
                            check_email = false;
                        } else {
                            editText_email.setError(null);
                            check_email = true;
                        }
                    }

                }
                break;
                case R.id.editText_name: {
                    EditText editText_name = (EditText) target_view;
                    if (charSequence.length() >= 1) {
                        if (!Utils.isLengthNickname(charSequence.toString())) {
                            editText_name.setError("3자리이상 20자이하로 닉네임을 설정해주세요 ");
                            check_name = false;
                        } else {
                            editText_name.setError(null);
                            check_name = true;
                        }
                    }

                }
                break;
                case R.id.editText_password: {

                    EditText editText_password = (EditText) target_view;
                    if (charSequence.length() >= 1) {
                        if (!Utils.isValidPassword(charSequence.toString())) {
                            editText_password.setError("7자리 이상 비밀번호를 입력해주세요 ");
                            check_password = false;
                        } else {
                            editText_password.setError(null);
                            check_password = true;
                        }
                    }

                }
                break;
                case R.id.editText_password_confirm: {
                    EditText editText_password_confirm = (EditText) target_view;
                    String before_password = ((EditText) findViewById(R.id.editText_password)).getText().toString();

                    if (charSequence.length() >= 1) {
                        if (!before_password.equals(charSequence.toString())) {
                            editText_password_confirm.setError("비밀번호가 일치하지 않습니다.");
                            check_password_confirm = false;
                        } else {
                            editText_password_confirm.setError(null);
                            check_password_confirm = true;
                        }
                    }

                }
                break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
    /*회원가입 이메일 중복 검사*/
    private class checkUserIdTask extends SafeAsyncTask<String> {
        public static final String TAG = "checkUserIdTask";
        String user_id;
        EditText targetView;

        public checkUserIdTask(String user_id, View view) {
            this.targetView = (EditText) view;
            this.user_id = user_id;
        }

        @Override
        public String call() throws Exception {
            return userService.checkUserId(user_id);
        }


        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d(TAG, "onException" + e.toString());
            super.onException(e);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);
            Log.d(TAG, "onSuccess: " + result );
            if ("not exist".equals(result)) {
                warningWindow.setText("사용가능한 이메일 입니다.");
                warningWindow.setVisibility(View.VISIBLE);
            } else {
                warningWindow.setText("사용할 수 없는 이메일 입니다.");
                warningWindow.setVisibility(View.VISIBLE);
            }
        }
    }

    private class CreateUserTask extends SafeAsyncTask<String> {
        UserVo user;

        public CreateUserTask(UserVo user) {
            this.user = user;
        }

        @Override
        public String call() throws Exception {
            return userService.createUser(user);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);
            hideProgressDialog ();
            Log.d(TAG, "onSuccess");
            if ("create".equals(result)) {
                warningWindow.setText("회원가입 되었습니다.");
                warningWindow.setVisibility(View.VISIBLE);
                Intent i = new Intent(getApplicationContext(), LoginUserActivity.class);
                i.putExtra("joinresult", "success");
                startActivityForResult(i, BasicInfo.RESULT_CODE_JOIN_USER);
                finish();
            } else {
                warningWindow.setText("회원가입에 실패하였습니다. 다시 시도 하여주세요.");
                warningWindow.setVisibility(View.VISIBLE);
            }
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
