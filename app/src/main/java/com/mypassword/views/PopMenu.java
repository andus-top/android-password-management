package com.mypassword.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * DESCRIPTION:自定义菜单控件
 * Created by yang on 2017/8/31 15:40
 */
public class PopMenu extends PopupWindow {

    /**
     * @param context
     * @param view  LayoutInflater.from(context).inflate(R.layout.bottom_menu, null);
     * @param width   ViewGroup.LayoutParams.MATCH_PARENT
     * @param height  ViewGroup.LayoutParams.WRAP_CONTENT
     * @param style  R.style.popwin_anim_style
     * @param dw    new ColorDrawable(context.getResources().getColor(R.color.ccc));   new ColorDrawable(0)->设置背景透明
     */
    public PopMenu(Context context,View view,int width, int height,int style,ColorDrawable dw){
        super(context);
        setContentView(view);
        //设置宽度
        setWidth(width);
        //设置高度
        setHeight(height);
        //默认获取焦点 弹出窗体可点击
        setFocusable(true);
        //设置显示隐藏动画
        //setAnimationStyle(R.style.AnimTools);//底部滚动菜单样式
        setAnimationStyle(style);//样式
        //设置背景
        setBackgroundDrawable(dw);

    }
//    //以某个控件的x和y的偏移量位置开始显示窗口
//    showAsDropDown(iv_suite_detail_menu, 0, 30);
//    //如果窗口存在，则更新
//    update();

    /**
     * 底部显示菜单
     */
    public void showAtBottom(Activity context){
        //得到当前activity的rootView
        View rootView=((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);
        showAtLocation(rootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);//Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL
    }
    /**
     * 已控件为原点显示
     */
    public void showAtView(View view,int offestX,int offestY){
        //以某个控件的x和y的偏移量位置开始显示窗口
        showAsDropDown(view, offestX, offestY);
    }

    public void close(){
        if (isShowing()) {
            dismiss();
        }
    }

}
