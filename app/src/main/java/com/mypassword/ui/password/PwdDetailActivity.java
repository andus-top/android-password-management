package com.mypassword.ui.password;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.GroupOfPwd;
import com.mypassword.model.Password;
import com.mypassword.model.User;
import com.mypassword.ui.me.LoginActivity;
import com.mypassword.utils.AES;
import com.mypassword.utils.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class PwdDetailActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_pwddetail_title;
    private TextView tv_pwddetail_group;
    private EditText et_pwddetail_account;
    private EditText et_pwddetail_pwd;
    private EditText et_pwddetail_rmk;
    private Password password;
    private TextView tv_pwddetail_edit;
    private int i = 0;
    private int j = 0;
    private LinearLayout ll_pwddetail_sure;
    private Button btn_pwddetail_sure;
    private Spinner spn_pwddetail_group;
    private Long groupId;//spinner中选择的分组id
    private Long getGroupId;//页面传过来的分组id
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<Long> groupIds = new ArrayList<>();
    private List<GroupOfPwd> groupOfPwds = new ArrayList<GroupOfPwd>();
    private ArrayAdapter<String> groupAdapter;
    private User user = LoginActivity.mUser;
    private AES aes = AES.getInstance();
    private TextView tv_pwddetail_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_detail);

        initView();

        initData();

        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_pwddetail_edit://编辑
                if(i % 2 == 0){
                    tv_pwddetail_edit.setText("取消");
                    setEditEnable();
                    //恢复显示的值
                    if(password != null){
                        et_pwddetail_pwd.setText(password.getPwd() != null?aes.decrypt(password.getPwd()):"");
                    }
                    i++;
                }else{
                    tv_pwddetail_edit.setText("编辑");
                    setTxt();
                    if(Utils.isSoftInputShow(this)){
                        //关闭软键盘
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    //禁用edittext 必须在关闭键盘后，否则就会认为关闭了键盘
                    setEditDisable();
                    //恢复显示的值
                    if(password != null){
                        tv_pwddetail_pwd.setText(password.getPwd() != null?password.getPwd():"");
                    }

                    i++;
                }
                break;
            case R.id.btn_pwddetail_sure://确定
                String title = et_pwddetail_title.getText().toString().trim();
                String account = et_pwddetail_account.getText().toString().trim();
                String pwd = et_pwddetail_pwd.getText().toString().trim();
                String rmk = et_pwddetail_rmk.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    et_pwddetail_title.setError("标题不能为空");
                    et_pwddetail_title.requestFocus();
                    return;
                }
                if(groupId == null || groupId < 0){
                    showToast("请选择分组");
                    return;
                }
                if(password.getTitle() != null && password.getTitle().equals(title)
                        && password.getAccount() != null && password.getAccount().equals(account)
                        && password.getPwd() != null && password.getPwd().equals(pwd)
                        && password.getRmk() != null && password.getRmk().equals(rmk)
                        && getGroupId == groupId){
                    showToast("你未做任何修改！");
                    return;
                }
                //加密=
                //得到加密后的字符串
                pwd = aes.encrypt(pwd);

                //修改  用对象更新没用
                ContentValues values = new ContentValues();
                values.put("account", account);
                values.put("isdelete", 0);
                values.put("pwd", pwd);
                values.put("rmk", rmk);
                values.put("title", title);
                values.put("groupofpwd_id", groupId);
                if(DataSupport.update(Password.class,values,password.getId()) > 0){
                    showToast("修改成功");
                    finish();
                }else{
                    showToast("修改失败");
                }
                break;
            case R.id.tv_pwddetail_pwd://解密
                if(j % 2 == 0){
                    tv_pwddetail_pwd.setText(password.getPwd() != null?aes.decrypt(password.getPwd()):"");
                    j++;
                }else {
                    tv_pwddetail_pwd.setText(password.getPwd() != null ?password.getPwd():"");
                    j++;
                }

                break;
        }
    }

    private void initListener() {
        spn_pwddetail_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupId = groupIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                groupId = null;
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        password = (Password)intent.getSerializableExtra("pwd");
        getGroupId = Long.valueOf(intent.getStringExtra("groupId"));

        groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,groups);
        spn_pwddetail_group.setAdapter(groupAdapter);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setTxt();

        if(user != null){
            groupOfPwds.clear();
            groupOfPwds.addAll(DataSupport.where("user_id = ? and isdelete = ?", String.valueOf(user.getId()),"0").find(GroupOfPwd.class));
        }else Log.e("getGroup", "getGroups: 从数据查询数据失败->用户空");

        getGroupsName();

    }

    private void initView() {
        tv_pwddetail_edit = (TextView) findViewById(R.id.tv_pwddetail_edit);
        tv_pwddetail_edit.setOnClickListener(this);
        et_pwddetail_title = (EditText) findViewById(R.id.et_pwddetail_title);
        tv_pwddetail_group = (TextView) findViewById(R.id.tv_pwddetail_group);
        spn_pwddetail_group = (Spinner) findViewById(R.id.spn_pwddetail_group);
        et_pwddetail_account = (EditText) findViewById(R.id.et_pwddetail_account);
        et_pwddetail_pwd = (EditText) findViewById(R.id.et_pwddetail_pwd);
        tv_pwddetail_pwd = (TextView) findViewById(R.id.tv_pwddetail_pwd);
        tv_pwddetail_pwd.setOnClickListener(this);
        et_pwddetail_rmk = (EditText) findViewById(R.id.et_pwddetail_rmk);
        ll_pwddetail_sure = (LinearLayout) findViewById(R.id.ll_pwddetail_sure);
        btn_pwddetail_sure = (Button) findViewById(R.id.btn_pwddetail_sure);
        btn_pwddetail_sure.setOnClickListener(this);

        setEditDisable();
    }

    private void setTxt(){
        if(password != null){

            et_pwddetail_title.setText(password.getTitle());
            et_pwddetail_account.setText(password.getAccount());
            tv_pwddetail_pwd.setText(password.getPwd());
            et_pwddetail_rmk.setText(password.getRmk());
            if(getGroupId != null && getGroupId > 0){
                GroupOfPwd groupOfPwd = getGroupbyId(Long.valueOf(getGroupId));
                if(groupOfPwd != null){
                    tv_pwddetail_group.setText(groupOfPwd.getGroupTitle() );
                }else{
                    tv_pwddetail_group.setText("查询失败");
                }
            }else {
                tv_pwddetail_group.setText("查询失败");
            }

        }

    }

    //禁用editView
    public void setEditDisable(){
        et_pwddetail_title.setCursorVisible(false);//光标隐藏
        et_pwddetail_title.setFocusable(false);//丢失焦点
        et_pwddetail_title.setFocusableInTouchMode(false);//不可获得焦点

        et_pwddetail_account.setCursorVisible(false);//光标隐藏
        et_pwddetail_account.setFocusable(false);//丢失焦点
        et_pwddetail_account.setFocusableInTouchMode(false);//不可获得焦点

        et_pwddetail_pwd.setCursorVisible(false);//光标隐藏
        et_pwddetail_pwd.setFocusable(false);//丢失焦点
        et_pwddetail_pwd.setFocusableInTouchMode(false);//不可获得焦点

        et_pwddetail_rmk.setCursorVisible(false);//光标隐藏
        et_pwddetail_rmk.setFocusable(false);//丢失焦点
        et_pwddetail_rmk.setFocusableInTouchMode(false);//不可获得焦点

        tv_pwddetail_pwd.setVisibility(View.VISIBLE);
        et_pwddetail_pwd.setVisibility(View.GONE);
        spn_pwddetail_group.setVisibility(View.GONE);
        tv_pwddetail_group.setVisibility(View.VISIBLE);
        ll_pwddetail_sure.setVisibility(View.GONE);
        btn_pwddetail_sure.setVisibility(View.GONE);

    }
    //启用editView
    public void setEditEnable(){

        tv_pwddetail_group.setVisibility(View.GONE);
        spn_pwddetail_group.setVisibility(View.VISIBLE);

        et_pwddetail_title.setCursorVisible(true);
        et_pwddetail_title.setFocusableInTouchMode(true);
        et_pwddetail_title.setSelection(et_pwddetail_title.getText() != null?et_pwddetail_title.getText().length():0);

        et_pwddetail_account.setCursorVisible(true);
        et_pwddetail_account.setFocusableInTouchMode(true);

        et_pwddetail_pwd.setCursorVisible(true);
        et_pwddetail_pwd.setFocusableInTouchMode(true);

        et_pwddetail_rmk.setCursorVisible(true);
        et_pwddetail_rmk.setFocusableInTouchMode(true);

        tv_pwddetail_pwd.setVisibility(View.GONE);
        et_pwddetail_pwd.setVisibility(View.VISIBLE);
        ll_pwddetail_sure.setVisibility(View.VISIBLE);
        btn_pwddetail_sure.setVisibility(View.VISIBLE);
    }

    private void getGroupsName(){
        groups.clear();
        groupIds.clear();
        if(groupOfPwds != null && !groupOfPwds.isEmpty()){
            for (GroupOfPwd g:groupOfPwds) {
                groups.add(g.getGroupTitle());
                groupIds.add(g.getId());
            }
        }
        groups.add(0,"请选择");
        groupIds.add(0,(long)-1);
        groupAdapter.notifyDataSetChanged();
    }

    private GroupOfPwd getGroupbyId(long id){
        if(id < 0 ){
            return null;
        }
        List<GroupOfPwd> groupOfPwds =
                DataSupport.where("id = ? and isdelete = ?",String.valueOf(id),"0").find(GroupOfPwd.class);
        if(groupOfPwds != null && !groupOfPwds.isEmpty() && groupOfPwds.size() == 1){
            return groupOfPwds.get(0);
        }else{
           return null;
        }
    }

}
