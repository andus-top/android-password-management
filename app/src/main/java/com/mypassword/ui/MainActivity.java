package com.mypassword.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mypassword.R;
import com.mypassword.app.BaseActivity;
import com.mypassword.ui.groupofpwd.AddGroupOfPwdActivity;
import com.mypassword.ui.index.IndexFragment;
import com.mypassword.ui.me.SlideFragment;
import com.mypassword.ui.menu.AboutActivity;
import com.mypassword.ui.password.AddPwdActivity;
import com.mypassword.views.RollingChoiceView;

import java.util.ArrayList;
import java.util.List;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.mypassword.R.style.dialog;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;

    private View mDrawerView;

    private ActionBarDrawerToggle mDrawerToggle;

    private IndexFragment mIndexFragment;//首页Fragment
    private SlideFragment mSlideFragment;//侧滑页Fragment
    private Dialog datePickerDialog;
    private TextView tv_choice_cancle;
    private TextView tv_choice_select;
    private RollingChoiceView rcv_choice_add_item;
    private List<String> opterations;
    final Select select = new Select();//底部滚动菜单才使用
    private Button btn_bottommenu_addGroup;
    private Button btn_bottommenu_addPwd;
    private Button btn_bottommenu_cancel;
    // 保存用户按返回键的时间
    private long mExitTime = 0;

    //导入导出
    private static final int CODE_OUT = 0;
    private static final int CODE_IN = 1;
    private String mFullPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initDrawer();

        initFragment();

        initListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//加载右上角菜单布局
        if (!mDrawerLayout.isDrawerOpen(mDrawerView)) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//右上角菜单控件点击事件
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.item_add://添加
                //显示底部菜单
                datePickerDialog.show();
                break;
            case R.id.item_about://关于作者
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //主页面返回键的监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK)) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Snackbar.make(mDrawerLayout, "再按一次退出程序哦~", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);//强制退出应用   改为用Application退出
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    //dialog弹出时注入返回键监听
    class BottomOnkeyPress implements DialogInterface.OnKeyListener{

        @Override
        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                if (datePickerDialog != null && datePickerDialog.isShowing()){
                    datePickerDialog.dismiss();
                }
            }
            return false;
        }
    }


    //底部按钮菜单监听类
    class BottomListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(datePickerDialog != null){
                datePickerDialog.dismiss();
            }
            switch (view.getId()){
                case R.id.btn_bottommenu_addGroup://添加分组
                    Intent i = new Intent(MainActivity.this, AddGroupOfPwdActivity.class);
                    startActivity(i);
                    break;
                case R.id.btn_bottommenu_addPwd://添加密码
                    Intent p = new Intent(MainActivity.this, AddPwdActivity.class);
                    startActivity(p);
                    break;
                case R.id.btn_bottommenu_cancel://取消
                    break;
            }
        }
    }

    private void initListener() {
//        //初始化底部滚动菜单
//        initRollingListener();
        //初始化底部按钮菜单监听
        BottomListener bottomListener = new BottomListener();
        btn_bottommenu_addGroup.setOnClickListener(bottomListener);
        btn_bottommenu_addPwd.setOnClickListener(bottomListener);
        btn_bottommenu_cancel.setOnClickListener(bottomListener);
    }


    private void initData() {
//        //初始化底部滚动菜单
//        initRollingChoiceData();
//        loadDataRollingView();
    }

    //弹窗
    private void initDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = new Dialog(this, dialog);
            datePickerDialog.setCancelable(false);
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //加载底部滚动菜单
            //datePickerDialog.setContentView(R.layout.custom_dialog);
            //加载底部按钮菜单
            datePickerDialog.setContentView(R.layout.bottom_menu);
            //添加按键的监听
            datePickerDialog.setOnKeyListener(new BottomOnkeyPress());
            //点击空白处消失
            datePickerDialog.setCanceledOnTouchOutside(true);
            Window window = datePickerDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    private void initDialogView() {
        //初始化底部滚动菜单控件
//        tv_choice_cancle = (TextView) datePickerDialog.findViewById(R.id.tv_choice_cancle);
//        tv_choice_select = (TextView) datePickerDialog.findViewById(R.id.tv_choice_select);
//        rcv_choice_add_item = (RollingChoiceView) datePickerDialog.findViewById(R.id.rcv_choice_add_item);
//
//        tv_choice_cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                datePickerDialog.dismiss();
//            }
//        });
//
//        tv_choice_select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                datePickerDialog.dismiss();
//            }
//        });
//
//        setIsLoop(false); // 不允许循环滚动
        //初始化底部按钮菜单控件
        btn_bottommenu_addGroup = (Button) datePickerDialog.findViewById(R.id.btn_bottommenu_addGroup);
        btn_bottommenu_addPwd = (Button) datePickerDialog.findViewById(R.id.btn_bottommenu_addPwd);
        btn_bottommenu_cancel = (Button) datePickerDialog.findViewById(R.id.btn_bottommenu_cancel);

    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        mIndexFragment = (IndexFragment) fragmentManager.findFragmentByTag("indexFragment");
        if (mIndexFragment == null)
            mIndexFragment = new IndexFragment();

        mSlideFragment = (SlideFragment) fragmentManager.findFragmentByTag("slideFragment");
        if (mSlideFragment == null)
            mSlideFragment = new SlideFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_main_navigation_drawer, mSlideFragment, "slideFragment");
        fragmentTransaction.replace(R.id.fl_main_container, mIndexFragment, "indexFragment");
        fragmentTransaction.commitAllowingStateLoss();
    }

    //region init
    private void initDrawer() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }


    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
        mDrawerView = findViewById(R.id.fl_main_navigation_drawer);
//        //初始化底部菜单
        initDialog();
        initDialogView();
    }


    /**
     * 设置控件是否可以循环滚动
     */
    public void setIsLoop(boolean isLoop) {
        this.rcv_choice_add_item.setIsLoop(isLoop);
    }

    //初始化连动控件数据
    private void initRollingChoiceData() {
        opterations = new ArrayList<>();
        opterations.add("添加分组");
        opterations.add("添加密码");
    }

    //为控件绑定数据
    private void loadDataRollingView() {

        rcv_choice_add_item.setData(opterations);
        if (opterations.size() > 0) {
            //初始显示的值
            rcv_choice_add_item.setSelected(0);
            select.operation = opterations.get(0);//保存当前选择的数据
        }
        executeScroll();
    }

    //执行滚动
    private void executeScroll() {
        rcv_choice_add_item.setCanScroll(opterations.size() > 1);
    }

    //滑动监听
    private void initRollingListener() {

        rcv_choice_add_item.setOnSelectListener(new RollingChoiceView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                select.operation = text;//保存选中的值
            }
        });
    }

    //保存当前选择的数据类
    private class Select {
        public String operation;
    }
}
