package com.mypassword.ui.groupofpwd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.GroupOfPwd;

import com.mypassword.utils.AES;
import com.mypassword.utils.Utils;

public class GroupDetailActivity extends BaseActivity implements View.OnClickListener {

    private int i=0;
    private TextView tv_groupdetail_edit;
    private EditText et_groupdetail_title;
    private EditText et_groupdetail_pwd;
    private EditText et_groupdetail_tip;
    private EditText et_groupdetail_rmk;
    private TextView tv_groupdetail_regtm;
    Intent intent = null;
    private GroupOfPwd oldGroupOfPwd = null;
    private LinearLayout ll_groupdetail_sure;
    private Button btn_groupdetail_sure;
    private double j = 0;
    private AES aes = AES.getInstance();
    private TextView tv_groupdetail_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();

        initData();

        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_groupdetail_edit://编辑
                if(i % 2 == 0){
                    tv_groupdetail_edit.setText("取消");
                    setEditEnable();
                    //恢复显示的值
                    if(oldGroupOfPwd != null){
                        et_groupdetail_pwd.setText(oldGroupOfPwd.getGroupPwd() != null?aes.decrypt(oldGroupOfPwd.getGroupPwd()):"");
                    }

                    i++;
                }else{
                    tv_groupdetail_edit.setText("编辑");
                    setTxt();
                    if(Utils.isSoftInputShow(getActivity())){
                        //关闭软键盘
                        //无效
                        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    //禁用edittext 必须在关闭键盘后，否则就会认为关闭了键盘
                    setEditDisable();
                    //恢复显示的值
                    if(oldGroupOfPwd != null){
                        tv_groupdetail_pwd.setText(oldGroupOfPwd.getGroupPwd() != null? oldGroupOfPwd.getGroupPwd():"");
                    }

                    i++;
                }
                break;
            case R.id.btn_groupdetail_sure://确定
                String title = et_groupdetail_title.getText().toString().trim();
                String pwd = et_groupdetail_pwd.getText().toString().trim();
                String tip = et_groupdetail_tip.getText().toString().trim();
                String rmk = et_groupdetail_rmk.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    et_groupdetail_title.setError("标题不能为空");
                    et_groupdetail_title.requestFocus();
                    return;
                }
                if(oldGroupOfPwd.getGroupTitle() != null && oldGroupOfPwd.getGroupTitle().equals(title)
                        && oldGroupOfPwd.getGroupPwd() != null && oldGroupOfPwd.getGroupPwd().equals(pwd)
                        && oldGroupOfPwd.getGroupPwdTip() != null && oldGroupOfPwd.getGroupPwdTip().equals(tip)
                        && oldGroupOfPwd.getGroupRmk() != null && oldGroupOfPwd.getGroupRmk().equals(tip)){
                    showToast("你未做任何修改！");
                    return;
                }
                //加密
                pwd = aes.encrypt(pwd);
                GroupOfPwd newGroupOfpwd = new GroupOfPwd();
                newGroupOfpwd.setGroupTitle(title);
                newGroupOfpwd.setGroupPwd(pwd);
                newGroupOfpwd.setGroupPwdTip(tip);
                newGroupOfpwd.setGroupRmk(rmk);
                newGroupOfpwd.setGroupTime(oldGroupOfPwd.getGroupTime());
                if(newGroupOfpwd.update(oldGroupOfPwd.getId()) > 0){
                    showToast("修改成功");
                    finish();
                }else{
                    showToast("修改失败");
                }
                break;
            case R.id.tv_groupdetail_pwd://解密
                if(j % 2 == 0){
                    tv_groupdetail_pwd.setText(aes.decrypt(oldGroupOfPwd.getGroupPwd()));
                    j++;
                }else {
                    tv_groupdetail_pwd.setText(oldGroupOfPwd.getGroupPwd());
                    j++;
                }

                break;
        }
    }

    private void initListener() {

    }

    private void initData() {
        intent = getIntent();
        oldGroupOfPwd =(GroupOfPwd) intent.getSerializableExtra("group");
        setTxt();
    }

    private void setTxt(){
        if(oldGroupOfPwd != null){
            et_groupdetail_title.setText(oldGroupOfPwd.getGroupTitle());
            tv_groupdetail_pwd.setText(oldGroupOfPwd.getGroupPwd());
            et_groupdetail_tip.setText(oldGroupOfPwd.getGroupPwdTip());
            et_groupdetail_rmk.setText(oldGroupOfPwd.getGroupRmk());
            tv_groupdetail_regtm.setText(Utils.getTime(oldGroupOfPwd.getGroupTime()));
        }
    }

    private void initView() {
        tv_groupdetail_edit =(TextView) findViewById(R.id.tv_groupdetail_edit);
        tv_groupdetail_edit.setOnClickListener(this);
        et_groupdetail_title = (EditText) findViewById(R.id.et_groupdetail_title);
        et_groupdetail_pwd = (EditText) findViewById(R.id.et_groupdetail_pwd);
        tv_groupdetail_pwd =(TextView) findViewById(R.id.tv_groupdetail_pwd);
        tv_groupdetail_pwd.setOnClickListener(this);
        et_groupdetail_tip = (EditText) findViewById(R.id.et_groupdetail_tip);
        et_groupdetail_rmk = (EditText) findViewById(R.id.et_groupdetail_rmk);
        tv_groupdetail_regtm =(TextView) findViewById(R.id.tv_groupdetail_regtm);
        ll_groupdetail_sure = (LinearLayout) findViewById(R.id.ll_groupdetail_sure);
        btn_groupdetail_sure = (Button) findViewById(R.id.btn_groupdetail_sure);
        btn_groupdetail_sure.setOnClickListener(this);
        setEditDisable();
    }

    //禁用editView
    public void setEditDisable(){
        et_groupdetail_title.setCursorVisible(false);//光标隐藏
        et_groupdetail_title.setFocusable(false);//丢失焦点
        et_groupdetail_title.setFocusableInTouchMode(false);//不可获得焦点

        et_groupdetail_pwd.setCursorVisible(false);//光标隐藏
        et_groupdetail_pwd.setFocusable(false);//丢失焦点
        et_groupdetail_pwd.setFocusableInTouchMode(false);//不可获得焦点

        et_groupdetail_tip.setCursorVisible(false);//光标隐藏
        et_groupdetail_tip.setFocusable(false);//丢失焦点
        et_groupdetail_tip.setFocusableInTouchMode(false);//不可获得焦点

        et_groupdetail_rmk.setCursorVisible(false);//光标隐藏
        et_groupdetail_rmk.setFocusable(false);//丢失焦点
        et_groupdetail_rmk.setFocusableInTouchMode(false);//不可获得焦点

        tv_groupdetail_pwd.setVisibility(View.VISIBLE);

        et_groupdetail_pwd.setVisibility(View.GONE);
        ll_groupdetail_sure.setVisibility(View.GONE);
        btn_groupdetail_sure.setVisibility(View.GONE);

    }
    //启用editView
    public void setEditEnable(){

        et_groupdetail_title.setCursorVisible(true);
        et_groupdetail_title.setFocusableInTouchMode(true);
        et_groupdetail_title.setSelection(et_groupdetail_title.getText() != null?et_groupdetail_title.getText().length():0);

        et_groupdetail_pwd.setCursorVisible(true);
        et_groupdetail_pwd.setFocusableInTouchMode(true);

        et_groupdetail_tip.setCursorVisible(true);
        et_groupdetail_tip.setFocusableInTouchMode(true);

        et_groupdetail_rmk.setCursorVisible(true);
        et_groupdetail_rmk.setFocusableInTouchMode(true);

        tv_groupdetail_pwd.setVisibility(View.GONE);

        et_groupdetail_pwd.setVisibility(View.VISIBLE);
        ll_groupdetail_sure.setVisibility(View.VISIBLE);
        btn_groupdetail_sure.setVisibility(View.VISIBLE);
    }

}
