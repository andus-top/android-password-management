package com.mypassword.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.User;
import com.mypassword.ui.MainActivity;
import com.mypassword.utils.AES;
import com.mypassword.utils.LocalInfoUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_login_register;
    private Button btn_login;
    private TextView et_login_username;
    private TextView et_login_userpwd;
    private CheckBox cb_login_checkBox;

    private AES aes = AES.getInstance();

    public static User mUser = new User();
    private String username;
    private String userpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //通知栏透明
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }

        setContentView(R.layout.activity_login);

        initView();

        initData();

        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login://登录
                username = et_login_username.getText().toString();
                userpwd = et_login_userpwd.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    et_login_username.setError("请输入用户名");
                    et_login_username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(userpwd)) {
                    et_login_userpwd.setError("请输入密码");
                    et_login_userpwd.requestFocus();
                    return;
                }
                //加密
                //得到加密后的字符串
                userpwd = aes.encrypt(userpwd);
                List<User> u = DataSupport.where(new String[]{"useraccount = ? and userpwd = ?", username, userpwd}).find(User.class);
                if(u == null || u.isEmpty()){
                    showToast("用户名或密码错误");
                }else{
                    if(u.size() == 1){//登录成功
                        mUser = u.get(0);

                        //登录成功就记住用户名
                        LocalInfoUtils.saveLocalUserName(LoginActivity.this,username);
                        //保存密码
                        savePwd();
                        //保存登录用户
                        if(!LocalInfoUtils.saveLocalUser(LoginActivity.this,mUser)){
                            Log.e("LoginActivity", "保存本地用户失败 " );
                        }

                        showToast("登录成功");
                        Intent i = new Intent(this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                       showToast("数据格式错误");
                    }

                }
                break;
            case R.id.tv_login_register://注册
                Intent i = new Intent(this,RegisterActivity.class);
                startActivity(i);
                break;
        }
    }

    //保存密码
    public void savePwd(){
        if(cb_login_checkBox.isChecked() && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(userpwd)){
            LocalInfoUtils.saveLocalUserPwd(LoginActivity.this, userpwd);
        }else{
            //不记住密码
            LocalInfoUtils.removeLocalUserpwd(LoginActivity.this);
        }
    }


    private void initListener() {
        cb_login_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    savePwd();
                }else {
                    //不记住密码
                    LocalInfoUtils.removeLocalUserpwd(LoginActivity.this);
                }
            }
        });
    }

    private void initData() {

        String name = LocalInfoUtils.getLocalUsername(LoginActivity.this);
        String pwd = LocalInfoUtils.getLocalUserPwd(LoginActivity.this);
        //解密
        pwd = aes.decrypt(pwd);
        et_login_username.setText(TextUtils.isEmpty(name) ? "" : name);
        et_login_userpwd.setText(TextUtils.isEmpty(pwd) ? "" : pwd);
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)){
            cb_login_checkBox.setChecked(true);
            //设置光标
            et_login_userpwd.requestFocus(pwd.length());
        }
        if(TextUtils.isEmpty(pwd)){
            et_login_userpwd.requestFocus();
        }
        if(TextUtils.isEmpty(name)){
            et_login_username.requestFocus();
        }
    }

    private void initView() {

        et_login_username = (TextView) findViewById(R.id.et_login_username);
        et_login_userpwd = (TextView) findViewById(R.id.et_login_userpwd);
        tv_login_register = (TextView) findViewById(R.id.tv_login_register);
        tv_login_register.setOnClickListener(this);
        cb_login_checkBox = (CheckBox) findViewById(R.id.cb_login_checkBox);
        cb_login_checkBox.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }
}
