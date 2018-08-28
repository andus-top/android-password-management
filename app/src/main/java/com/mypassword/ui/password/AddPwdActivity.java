package com.mypassword.ui.password;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.model.GroupOfPwd;
import com.mypassword.model.Password;
import com.mypassword.model.User;
import com.mypassword.ui.me.LoginActivity;
import com.mypassword.utils.AES;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class AddPwdActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_password_title;
    private Spinner spn_addpwd_group;
    private EditText et_addpwd_account;
    private EditText et_addpwd_pwd;
    private EditText et_addpwd_rmk;
    private Button btn_addpwd_next;
    private List<GroupOfPwd> pData = new ArrayList<GroupOfPwd>();
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<Long> groupIds = new ArrayList<>();
    private User user = LoginActivity.mUser;
    private ArrayAdapter<String> groupAdapter;
    private Long selectGroup = null;
    private AES aes = AES.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pwd);

        initView();

        initData();

        initListener();
    }

    private void initListener() {

        spn_addpwd_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectGroup = groupIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectGroup = (long)-1;
            }
        });
    }

    private void initData() {
        groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,groups);
        spn_addpwd_group.setAdapter(groupAdapter);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getGroups();
        getGroupsName();
    }

    private void initView() {
        et_password_title = (EditText)findViewById(R.id.et_password_title);
        spn_addpwd_group = (Spinner) findViewById(R.id.spn_addpwd_group);
        et_addpwd_account = (EditText)findViewById(R.id.et_addpwd_account);
        et_addpwd_pwd = (EditText)findViewById(R.id.et_addpwd_pwd);
        et_addpwd_rmk = (EditText)findViewById(R.id.et_addpwd_rmk);
        btn_addpwd_next = (Button)findViewById(R.id.btn_addpwd_next);
        btn_addpwd_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_addpwd_next://添加密码
                String title = et_password_title.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    et_password_title.setError("标题项不能为空");
                    et_password_title.requestFocus();
                    return;
                }
                if(selectGroup == -1){
                    showToast("请选择分组");
                    return;
                }

                String account = et_addpwd_account.getText().toString().trim();
                String pwd = et_addpwd_pwd.getText().toString().trim();
                String rmk = et_addpwd_rmk.getText().toString().trim();

                if (TextUtils.isEmpty(account)) {
                    et_addpwd_account.setError("账号不能为空");
                    et_addpwd_account.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    et_addpwd_pwd.setError("密码不能为空");
                    et_addpwd_pwd.requestFocus();
                    return;
                }
                GroupOfPwd g = getGroupbyId(selectGroup);
                if(g == null){
                    showToast("该分组不存在或已被删除");
                    return;
                }
                //加密
                //得到加密后的字符串
                pwd = aes.encrypt(pwd);
                Password password = new Password();
                password.setGroupOfPwd(g);
                password.setTitle(title);
                password.setAccount(account);
                password.setPwd(pwd);
                password.setRmk(rmk);
                try {
                    password.saveThrows();
//                    Cursor cursor = DataSupport.findBySQL("select * from password");
//                    List<Password> list = null;
//                    if(cursor.getCount() >= 0) {
//                        list = new ArrayList<>();
//                        while(cursor.moveToNext()) {
//                            Password password1 = new Password();
//                            password1.setId(cursor.getLong(cursor.getColumnIndex("id")));
//                            password1.setAccount(cursor.getString(cursor.getColumnIndex("account")));
//                            password1.setPwd(cursor.getString(cursor.getColumnIndex("pwd")));
//                            password1.setTitle(cursor.getString(cursor.getColumnIndex("title")));
//                            long gid  = cursor.getLong(cursor.getColumnIndex("groupofpwd_id"));
//                            list.add(password1);
//                        }
//                    }
                    showToast("添加成功");
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                    showToast("添加失败");
                    finish();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getGroups();

    }

    public void getGroups(){
        if(user != null){
            pData.clear();
            pData.addAll(DataSupport.where("user_id = ? and isdelete = ?", String.valueOf(user.getId()),"0").find(GroupOfPwd.class));
        }else Log.e("getGroup", "getGroups: 从数据查询数据失败->用户空");

    }

    private void getGroupsName(){
        if(pData != null && !pData.isEmpty()){
            for (GroupOfPwd g:pData) {
                groups.add(g.getGroupTitle());
                groupIds.add(g.getId());
            }
        }
        groups.add(0,"请选择");
        groupIds.add(0,(long)-1);
        groupAdapter.notifyDataSetChanged();
    }

    private GroupOfPwd getGroupbyId(long id){
        GroupOfPwd g = null;
        if(pData != null && !pData.isEmpty() && id > 0){
            for (GroupOfPwd g1:pData) {
                if(id == g1.getId()){
                    g = g1;
                    break;
                }
            }
            return g;
        }else {
            return null;
        }
    }
}
