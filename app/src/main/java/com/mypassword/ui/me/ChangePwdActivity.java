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

public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_changepwd_resure;
    private EditText et_changepwd_newpwd;
    private EditText et_changepwd_oldpwd;
    private User user = LoginActivity.mUser;
    private Button btn_changepwd_sure;
    private AES aes = AES.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);

        initView();

        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_changepwd_sure://修改密码
                String oldpwd = et_changepwd_oldpwd.getText().toString();
                String newpwd = et_changepwd_newpwd.getText().toString();
                String repwd = et_changepwd_resure.getText().toString();
                if (TextUtils.isEmpty(oldpwd)) {
                    et_changepwd_oldpwd.setError("请输入旧密码");
                    et_changepwd_oldpwd.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(newpwd)) {
                    et_changepwd_newpwd.setError("新密码不能为空");
                    et_changepwd_newpwd.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(repwd)) {
                    et_changepwd_resure.setError("请确认新密码");
                    et_changepwd_resure.requestFocus();
                    return;
                }
                if(!repwd.equals(newpwd)){
                    et_changepwd_resure.setError("两次密码不一致");
                    et_changepwd_resure.requestFocus(repwd.length());
                    return;
                }
                oldpwd = aes.encrypt(oldpwd);
                if(!oldpwd.equals(user.getUserPwd())){
                    showToast("旧密码不正确");
                    return;
                }
                user.setUserPwd(repwd);
                if(user.update(user.getId()) > 0){
                    LoginActivity.mUser.setUserPwd(repwd);
                    showToast("修改成功");
                    finish();//必须finish 不然点返回键会回到主页
                }else{
                    showToast("修改失败");
                }
                break;
        }
    }

    private void initData() {

    }

    private void initView() {
        et_changepwd_oldpwd =  (EditText) findViewById(R.id.et_changepwd_oldpwd);
        et_changepwd_newpwd =  (EditText) findViewById(R.id.et_changepwd_newpwd);
        et_changepwd_resure =  (EditText) findViewById(R.id.et_changepwd_resure);
        btn_changepwd_sure = (Button) findViewById(R.id.btn_changepwd_sure);
        btn_changepwd_sure.setOnClickListener(this);
    }

    @Override
    public void backAction(View v) {
        onBackPressed();//解决侧滑页闪退的效果
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
