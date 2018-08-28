package com.mypassword.app;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Description:自定义页面基类
 * Change by:
 * Created by yang on 2017/12/14 15:48
 */

public class BaseActivity extends AppCompatActivity {

    private MyApplication getApp(){
        return (MyApplication)getApplication();
    }

    public void showToast(int id) {
        showToast(id, Toast.LENGTH_SHORT);
    }

    public void showToast(int id, int duration) {
        Toast.makeText(this, id, duration).show();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected BaseActivity getActivity() {
        return this;
    }

    public void backAction(View v) {
        finish();
    }
}
