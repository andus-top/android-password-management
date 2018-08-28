package com.mypassword.ui.groupofpwd;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.GroupOfPwd;
import com.mypassword.ui.me.LoginActivity;
import com.mypassword.utils.AES;

import java.util.Date;

public class AddGroupOfPwdActivity extends BaseActivity implements View.OnClickListener,TextWatcher {

    private EditText et_addgroup_title;
    private Button btn_addgroup_next;
    private EditText et_addgroup_pwdtip;
    private EditText et_addgroup_pwd;
    private EditText et_addgroup_rmk;
    private AES aes = AES.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_of_pwd);

        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_addgroup_next://添加分组
                String title = et_addgroup_title.getText().toString().trim();
                String pwd = et_addgroup_pwd.getText().toString().trim();
                String pwdTip = et_addgroup_pwdtip.getText().toString().trim();
                String rmk = et_addgroup_rmk.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    et_addgroup_title.setError("请输入分组名称");
                    et_addgroup_title.requestFocus();
                    return;
                }
                //加密
                pwd = aes.encrypt(pwd);

                GroupOfPwd groupOfPwd = new GroupOfPwd();
                groupOfPwd.setGroupTitle(title);
                groupOfPwd.setGroupPwd(pwd);
                groupOfPwd.setGroupPwdTip(pwdTip);
                groupOfPwd.setGroupRmk(rmk);
                groupOfPwd.setUser(LoginActivity.mUser);
                groupOfPwd.setGroupTime(new Date());
                try {
                    groupOfPwd.saveThrows();
                    showToast("添加成功");
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                    showToast("添加失败");
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(et_addgroup_title.getText().length() > 0){
            btn_addgroup_next.setEnabled(true);
        }
        else{
            btn_addgroup_next.setEnabled(false);
        }
    }

    private void initView() {
        et_addgroup_title = (EditText) findViewById(R.id.et_addgroup_title);
        et_addgroup_title.addTextChangedListener(this);
        et_addgroup_pwd = (EditText) findViewById(R.id.et_addgroup_pwd);
        et_addgroup_pwdtip = (EditText) findViewById(R.id.et_addgroup_pwdtip);
        et_addgroup_rmk = (EditText) findViewById(R.id.et_addgroup_rmk);
        btn_addgroup_next = (Button) findViewById(R.id.btn_addgroup_next);
        btn_addgroup_next.setOnClickListener(this);
    }
}
