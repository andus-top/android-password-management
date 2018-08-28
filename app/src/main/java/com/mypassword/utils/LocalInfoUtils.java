package com.mypassword.utils;

import android.content.Context;

import com.mypassword.model.User;

import static com.mypassword.utils.PrefUtils.saveString;

/**
 * Description:本地信息保存获取工具类
 * Change by:
 * Created by yang on 2017/11/8 19:04
 */

public class LocalInfoUtils {
    //本地用户名key
    public static final String LOCALUSERNAME = "localusername";
    //本地用户密码key
    public static final String LOCALUSERPWD = "localuserpwd";
    //是否自动登录
    public static final String ISREMBERPWD = "isremberpwd";
    //登录用户
    public static final String LOCALUSER = "localuser";

    /**
     * 保存本地用户名
     * @param context
     * @param val 用户名
     */
    public static boolean saveLocalUserName(Context context, String val){
        return saveString(context,LOCALUSERNAME,val);
    }
    //获取本地用户名
    public static String getLocalUsername(Context context){
        return PrefUtils.getString(context,LOCALUSERNAME);
    }

    //删除本地用户名
    public static void removeLocalUsername(Context context){
        PrefUtils.removeFromPrefs(context,LOCALUSERNAME);
    }

    /**
     * 保存本地用户密码
     * @param context
     * @param val 密码
     */
    public static boolean saveLocalUserPwd(Context context, String val){
        return saveString(context,LOCALUSERPWD,val);
    }

    //获取本地用户密码
    public static String getLocalUserPwd(Context context){
        return PrefUtils.getString(context,LOCALUSERPWD);
    }

    //删除本地用户密码
    public static void removeLocalUserpwd(Context context){
        PrefUtils.removeFromPrefs(context,LOCALUSERPWD);
    }

    /**
     * 是否记住密码
     * @param context
     * @param val 密码
     */
    public static boolean saveRemberPwd(Context context, boolean val){
        return PrefUtils.saveBoolean(context,ISREMBERPWD,val);
    }

    //获取是否记住密码 true false
    public static boolean getIsRemberPwd(Context context){
        return PrefUtils.getBoolean(context,ISREMBERPWD);
    }

    //删除记住密码
    public static void removeIsRemberPwd(Context context){
        PrefUtils.removeFromPrefs(context,ISREMBERPWD);
    }

    /**
     * desc:保存本地登录对象
     * @param context
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     * modified:
     */
    public static boolean saveLocalUser(Context context,User obj){
        return PrefUtils.saveObject(context,LOCALUSER,obj);
    }

    //获取本地登录对象
    public static User getLocalUser(Context context){
        try {
            User luser = (User) PrefUtils.getObject(context, LOCALUSER);
            return luser;
        }catch (ClassCastException e){
            e.printStackTrace();
            return null;
        }
    }

    //删除本地登录对象
    public static void removeLocalUser(Context context){
        PrefUtils.removeFromPrefs(context,LOCALUSER);
    }

}
