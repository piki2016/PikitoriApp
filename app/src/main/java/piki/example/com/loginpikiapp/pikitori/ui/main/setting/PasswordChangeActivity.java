package piki.example.com.loginpikiapp.pikitori.ui.main.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.Utils;

public class PasswordChangeActivity extends AppCompatActivity {

    private EditText editText_present_password;
    private EditText editText_new_password;
    private EditText editText_new_password_confirm;
    private boolean check_password = false;
    private boolean check_password_confirm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        getSupportActionBar().setTitle("비밀번호 변경");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        editText_present_password = (EditText)findViewById(R.id.present_password);
        editText_new_password = (EditText)findViewById(R.id.new_password);
        editText_new_password.addTextChangedListener(new ValidationWatcher(editText_new_password));
        editText_new_password_confirm = (EditText)findViewById(R.id.new_password_confirm);
        editText_new_password_confirm.addTextChangedListener(new ValidationWatcher(editText_new_password_confirm));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return true;
    }

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
                case R.id.new_password: {

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
                case R.id.new_password_confirm: {
                    EditText editText_password_confirm = (EditText) target_view;
                    String before_password = ((EditText) findViewById(R.id.new_password)).getText().toString();

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
}
