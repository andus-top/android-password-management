package com.mypassword.ui.index;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.mypassword.R;
import com.mypassword.app.BaseFragment;
import com.mypassword.model.GroupOfPwd;
import com.mypassword.model.Password;
import com.mypassword.model.User;
import com.mypassword.ui.groupofpwd.GroupDetailActivity;
import com.mypassword.ui.index.adapter.ExpandableListViewAdapter;
import com.mypassword.ui.me.LoginActivity;
import com.mypassword.ui.password.PwdDetailActivity;
import com.mypassword.utils.AES;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static org.litepal.crud.DataSupport.update;

/**
 * Description:首页
 * Change by:
 * Created by yang on 2017/12/14 20:19
 */

public class IndexFragment extends BaseFragment implements View.OnClickListener{
    private View mainView;
    private ExpandableListView elv_groupofpwd;
    private List<GroupOfPwd> pData = new ArrayList<GroupOfPwd>();//父元素data
    private List<List<Password>> cData = new ArrayList<List<Password>>();//子元素data
    private ExpandableListViewAdapter elvAdapter;
    private User user = LoginActivity.mUser;
    private View mNoDataView;
    private AES aes = AES.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_index, container, false);
        initView();

        initData();

        initListener();
        return mainView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    private void initListener() {
        //为列表设置点击事件
        elv_groupofpwd.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //cData.get(groupPosition).get(childPosition).getiName()
                Intent intent = new Intent(getActivity(),PwdDetailActivity.class);
                intent.putExtra("pwd",cData.get(groupPosition).get(childPosition));
                intent.putExtra("groupId",""+pData.get(groupPosition).getId());
                startActivity(intent);
                return true;//返回值的作用 ？
            }
        });
        //为父元素添加点击事件
        elv_groupofpwd.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){
            @Override
            public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {
                if( parent.isGroupExpanded( groupPosition ) ){//展开
                    parent.collapseGroup( groupPosition );
                }else{//关闭
                    if(pData.get(groupPosition) != null && !TextUtils.isEmpty(pData.get(groupPosition).getGroupPwd())){//该分组有密码
                        final EditText editText = new EditText(getActivity());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                        builder.setTitle("请输入密码");
                        builder.setIcon(R.drawable.ico_pwd);
                        builder.setView(editText);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pass = editText.getText().toString().trim();
                                pass = aes.encrypt(pass);
                                if (!pData.get(groupPosition).getGroupPwd().equals(pass)) {//密码不相等不展开
                                    if(!TextUtils.isEmpty(pData.get(groupPosition).getGroupPwdTip())){
                                        Toast.makeText(getActivity(), "密码提示："+pData.get(groupPosition).getGroupPwdTip(),
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "密码不正确！", Toast.LENGTH_SHORT).show();
                                    }
                                    parent.collapseGroup(groupPosition);
                                } else {//密码正确
                                    Toast.makeText(getActivity(), "密码正确", Toast.LENGTH_SHORT).show();
                                    parent.expandGroup(groupPosition);
                                }
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                parent.collapseGroup(groupPosition);
                            }
                        });
                        builder.create().show();
                    }else{//没有密码
                        parent.expandGroup( groupPosition );
                    }

                }
                return true;//expandlistview 点击事件的返回值 ?
            }
        });
        elv_groupofpwd.setOnItemLongClickListener(new LongPressListener());
    }

    private void initData() {

        getGroupAndPwd();

    }

    private void initView() {
        elv_groupofpwd = (ExpandableListView) mainView.findViewById(R.id.elv_groupofpwd);
        mNoDataView = mainView.findViewById(R.id.fragment_password_noData);
        mNoDataView.setOnClickListener(this);
        elvAdapter = new ExpandableListViewAdapter(pData, cData,getActivity(),elv_groupofpwd);
        elv_groupofpwd.setAdapter(elvAdapter);
    }


    //expandlistview长按事件的监听
    private class LongPressListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                       int pos, long id) {
            final int groupPos = (Integer)view.getTag(R.id.tv_group_pname); //参数值是在setTag时使用的对应资源id号
            final int childPos = (Integer)view.getTag(R.id.tv_group_cname);
            if(childPos == -1){//长按的是父项
                //根据groupPos判断你长按的是哪个父项，做相应处理（弹框等）
                dialogList(groupPos);
            } else {
                //根据groupPos及childPos判断你长按的是哪个父项下的哪个子项，然后做相应处理。
                new AlertDialog
                        .Builder(getActivity())
                        .setTitle("删除后不可恢复,是否删除?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (groupPos < pData.size() && groupPos < cData.size()
                                        && childPos < cData.get(groupPos).size()) {
                                    //修改  用对象更新没用
                                    ContentValues values = new ContentValues();
                                    values.put("isdelete", 1);
                                    if(update(Password.class,values,cData.get(groupPos).get(childPos).getId()) > 0){
                                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                    cData.get(groupPos).remove(childPos);
                                    //重新加载数据
                                    getGroupAndPwd();
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
            //true:长按事件被消耗掉了  false:长按时间会继续传递给点击事件
            return true;
        }
    }

    //长按事件选择dialog
    private void dialogList(final int selectgroupPosition) {
        final String items[] = {"查看", "删除"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),3);
        builder.setTitle("请选择");
        // builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.drawable.ico_pwd);
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which){
                    case 0://查看分组
                        if(pData.get(selectgroupPosition) != null && !TextUtils.isEmpty(pData.get(selectgroupPosition).getGroupPwd())){//该分组有密码
                            final EditText editText = new EditText(getActivity());
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                            builder.setTitle("请输入密码");
                            builder.setIcon(R.drawable.ico_pwd);
                            builder.setView(editText);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String pass = editText.getText().toString().trim();
                                    pass = aes.encrypt(pass);
                                    if (!pData.get(selectgroupPosition).getGroupPwd().equals(pass)) {//密码不相等不展开
                                        if(!TextUtils.isEmpty(pData.get(selectgroupPosition).getGroupPwdTip())){
                                            Toast.makeText(getActivity(), "密码提示："+pData.get(selectgroupPosition).getGroupPwdTip(),
                                                    Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(), "密码不正确！", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {//密码正确
                                        Toast.makeText(getActivity(), "密码正确", Toast.LENGTH_SHORT).show();
                                        Intent g = new Intent(getActivity(), GroupDetailActivity.class);
                                        g.putExtra("group",pData.get(selectgroupPosition));
                                        startActivity(g);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }else{//没有密码
                            Intent g = new Intent(getActivity(), GroupDetailActivity.class);
                            g.putExtra("group",pData.get(selectgroupPosition));
                            startActivity(g);
                        }

                        break;
                    case 1://删除分组
                        if(pData.get(selectgroupPosition) != null && !TextUtils.isEmpty(pData.get(selectgroupPosition).getGroupPwd())){//该分组有密码
                            final EditText editText = new EditText(getActivity());
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                            builder.setTitle("请输入密码");
                            builder.setIcon(R.drawable.ico_pwd);
                            builder.setView(editText);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String pass = editText.getText().toString().trim();
                                    pass = aes.encrypt(pass);
                                    if (!pData.get(selectgroupPosition).getGroupPwd().equals(pass)) {//密码不相等
                                        dialog.dismiss();
                                        if(!TextUtils.isEmpty(pData.get(selectgroupPosition).getGroupPwdTip())){
                                            Toast.makeText(getActivity(), "密码提示："+pData.get(selectgroupPosition).getGroupPwdTip(),
                                                    Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getActivity(), "密码不正确！", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {//密码正确
                                        dialog.dismiss();
                                        deleteGroupDialog(selectgroupPosition);
                                    }
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.create().show();
                        }else{//没有密码
                            deleteGroupDialog(selectgroupPosition);
                        }

                        break;
                }

            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //删除分组的dialog
    public void deleteGroupDialog(final int selectgroupPosition){
        new AlertDialog
                .Builder(getActivity())
                .setTitle("删除后不可恢复,是否删除?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectgroupPosition < pData.size()) {
                            if(cData != null && !cData.isEmpty() && selectgroupPosition < cData.size()){
                                //将该组的密码全部删除
                                //修改  用对象更新没用
                                ContentValues values = new ContentValues();
                                values.put("isdelete", 1);
                                for (Password p:cData.get(selectgroupPosition)) {
                                    int c = DataSupport.update(Password.class,values,p.getId());
                                    Log.d(TAG, "onClick: ->"+c);
                                }
                            }
                            //删除该分组
                            int pn = DataSupport.delete(GroupOfPwd.class,pData.get(selectgroupPosition).getId());
                            Log.d(TAG, "onClick: ->"+pn);

                            //重新加载数据
                            getGroupAndPwd();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    //从数据库获取分组及密码数据
    public void getGroupAndPwd(){
        if(user != null){
            pData.clear();
            cData.clear();

            showWhat();

            pData.addAll(DataSupport.where("user_id = ? and isdelete = ?", String.valueOf(user.getId()),"0").find(GroupOfPwd.class));
        }else Log.e(TAG, "initData: 从数据查询数据失败->用户空");

        if(pData != null && !pData.isEmpty()){
            for (int i=0;i<pData.size();i++) {
                if(pData.get(i).getPwdList() != null){//list没有元素也要添加
                    cData.add(pData.get(i).getPwdList());
                }
            }
        }
        //数据刷新
        elvAdapter.refresh();

        showWhat();
    }

    //显示那个view（有数据时显示 expandlistview，无数据显示没有数据的布局）
    private void showWhat(){
        if (pData != null && pData.isEmpty()) {
            mNoDataView.setVisibility(View.VISIBLE);
            elv_groupofpwd.setVisibility(View.GONE);
        } else {
            mNoDataView.setVisibility(View.GONE);
            elv_groupofpwd.setVisibility(View.VISIBLE);

            //关闭所有的父元素
            elvAdapter.closeAllParent();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //重新获取分组及密码，并刷新
        getGroupAndPwd();
        //回到此页面，保存上次输入密码时间

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    //当前Fragment是否可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        getGroupAndPwd();
    }
}
