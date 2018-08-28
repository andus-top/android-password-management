package com.mypassword.ui.me;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.User;
import com.mypassword.utils.AES;

import java.util.Date;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_register_username;
    private EditText et_register_userpwd;
    private EditText et_register_reuserpwd;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register://注册
                String username = et_register_username.getText().toString();
                String userpwd = et_register_userpwd.getText().toString();
                String reuserpwd = et_register_reuserpwd.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    et_register_username.setError("用户名不能为空");
                    et_register_username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(userpwd)) {
                    et_register_userpwd.setError("密码不能为空");
                    et_register_userpwd.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(reuserpwd)) {
                    et_register_reuserpwd.setError("请确认密码");
                    et_register_reuserpwd.requestFocus();
                    return;
                }
                if(!userpwd.equals(reuserpwd)){
                    et_register_reuserpwd.setError("两次密码不一致");
                    et_register_reuserpwd.requestFocus(reuserpwd.length());
                    return;
                }
                //加密
                AES aes = AES.getInstance();
                //得到加密后的字符串
                userpwd = aes.encrypt(userpwd);

                User user = new User();
                user.setUserAccount(username);
                user.setUserPwd(userpwd);
                user.setRegisterTm(new Date());
                try {
                    user.saveThrows();
                    showToast("注册成功");
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                    showToast("注册失败");
                }

                break;
        }
    }

    private void initView() {
        et_register_username = (EditText) findViewById(R.id.et_register_username);
        et_register_userpwd = (EditText) findViewById(R.id.et_register_userpwd);
        et_register_reuserpwd = (EditText) findViewById(R.id.et_register_reuserpwd);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
    }

}
