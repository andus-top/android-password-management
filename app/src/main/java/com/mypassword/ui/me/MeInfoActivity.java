package com.mypassword.ui.me;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.User;

import com.mypassword.utils.Utils;

public class MeInfoActivity extends BaseActivity implements View.OnClickListener {

    private User user = LoginActivity.mUser;
    private TextView tv_meinfo_name;
    private TextView tv_meinfo_regtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_info);
        initView();

        initData();
    }

    private void initData() {
        if(user != null){
            tv_meinfo_name.setText(user.getUserAccount());
            user.getRegisterTm();
            tv_meinfo_regtime.setText(Utils.getTime(user.getRegisterTm()));
        }
    }

    private void initView() {
        tv_meinfo_name = (TextView) findViewById(R.id.tv_meinfo_name);
        tv_meinfo_regtime = (TextView) findViewById(R.id.tv_meinfo_regtime);
    }

    @Override
    public void onClick(View view) {

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
