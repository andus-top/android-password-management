package com.mypassword.ui.me;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mypassword.R;
import com.mypassword.app.BaseFragment;
import com.mypassword.model.User;

/**
 * Description:侧滑页
 * Change by:
 * Created by yang on 2017/12/14 20:19
 */

public class SlideFragment extends BaseFragment implements View.OnClickListener{

    private View mainView;
    private TextView tv_mine_nickNmae;
    private RelativeLayout rl_mine_logOut;
    private RelativeLayout rl_mine_changePwd;
    private RelativeLayout rl_mine_mine;
    private User user = LoginActivity.mUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_slide, container, false);
        initView();

        initData();

        initListener();
        return mainView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_mine_mine://个人资料
                Intent me = new Intent(getActivity(),MeInfoActivity.class);
                startActivity(me);
                break;
            case R.id.rl_mine_changePwd://更改密码
                Intent c = new Intent(getActivity(),ChangePwdActivity.class);
                startActivity(c);
                break;
            case R.id.rl_mine_logOut://登出
                new AlertDialog.Builder(getContext())
                        .setTitle("温馨提示").setMessage("是否退出登陆？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();//必须finish 不然点返回键会回到主页
                            }
                }).setNegativeButton("暂不", null).create().show();
                break;
        }
    }

    private void initListener() {

    }

    private void initData() {
        if(user != null){
            tv_mine_nickNmae.setText("昵称:"+user.getUserAccount());
        }
    }

    private void initView() {
        tv_mine_nickNmae = (TextView) mainView.findViewById(R.id.tv_mine_nickNmae);
        rl_mine_mine = (RelativeLayout) mainView.findViewById(R.id.rl_mine_mine);
        rl_mine_mine.setOnClickListener(this);
        rl_mine_changePwd = (RelativeLayout) mainView.findViewById(R.id.rl_mine_changePwd);
        rl_mine_changePwd.setOnClickListener(this);
        rl_mine_logOut = (RelativeLayout) mainView.findViewById(R.id.rl_mine_logOut);
        rl_mine_logOut.setOnClickListener(this);

    }

}
