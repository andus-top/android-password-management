package com.mypassword.utils;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Description:Fragment管理帮助类
 * Change by:
 * Created by yang on 2017/10/13 19:51
 */

public class FragmentManagerHelper {

    // 管理类FragmentManager
    private FragmentManager mFragmentManager;
    // 容器布局id containerViewId
    private int mContainerViewId;

    /**
     * 构造函数
     * @param fragmentManager 管理类FragmentManager
     * @param containerViewId 容器布局id containerViewId
     */
    public FragmentManagerHelper(@Nullable FragmentManager fragmentManager, @IdRes int containerViewId) {
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
    }

    /**
     * 添加Fragment
     * @param fragment 目标Fragment
     */
    public void addFg(Fragment fragment){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //通过add()连续两次添加Fragment的时候，每个Fragment生命周期中的onAttach()-onResume()都会被各调用一次，而且两个Fragment的View会被同时attach到containerView。
        //通过replace添加Fragment的时候，第二次添加会导致第一个Fragment被销毁，即执行第二个Fragment的onAttach()方法之前会先执行第一个Fragment的onPause()-onDetach()方法，同时containerView会detach第一个Fragment的View。
        //传入 Fragment的容器id，需要添加的Fragment
        fragmentTransaction.add(mContainerViewId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 切换显示的Fragment
     * @param fragment
     */
    public void switchFg(Fragment fragment){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        // 先隐藏当前所有的Fragment
        List<Fragment> childFragments = mFragmentManager.getFragments();
        for (Fragment childFragment : childFragments) {
            fragmentTransaction.hide(childFragment);
        }

        // 如果容器里面没有我们就添加，否则显示
        if(!childFragments.contains(fragment)){
            fragmentTransaction.add(mContainerViewId,fragment);
        }else{
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commit();
    }

}
