package com.mypassword.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import static com.mypassword.utils.Utils.StringToBytes;

/**
 * SharedPreferences工具类
 * change by:
 * Created by yang on 2017/11/8 18:53
 */
public class PrefUtils {
    public static final String PRE_NAME = "com.mypassword";
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存字符串
     * @param context
     * @param key
     * @param val
     */
    public static boolean saveString(Context context, String key, String val) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, val);
        return editor.commit();
    }

    //获取字符串
    public static String getString(Context context,String key) {
        return getSharedPreferences(context).getString(key, null);
    }

    /**
     * 保存boolan值
     * @param context
     * @param key
     * @param val
     */
    public static boolean saveBoolean(Context context, String key, boolean val) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, val);
        return editor.commit();
    }

    //获取boolean
    public static boolean getBoolean(Context context,String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    /**
     * 保存Float值
     * @param context
     * @param key
     * @param val
     */
    public static boolean saveFloat(Context context, String key, float val) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, val);
        return editor.commit();
    }

    //获取float
    public static float getFloat(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getFloat(key, 0);
    }

    /**
     * 保存Int值
     * @param context
     * @param key
     * @param val
     */
    public static boolean saveInt(Context context, String key, int val) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, val);
        return editor.commit();
    }

    //获取int
    public static int getIntValue(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(key, -1);
    }

    /**
     * 保存Long值
     * @param context
     * @param key
     * @param val
     */
    public static boolean saveLong(Context context, String key, Long val) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, val);
        return editor.commit();
    }

    //获取Long
    public static Long getLongValue(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getLong(key,-1);
    }

    /**
     * 删除 SharedPreferences 的某个 key
     * @param key
     */
    public static void removeFromPrefs(Context context, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * desc:保存对象
     * @param context
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     * modified:
     */
    public static boolean saveObject(Context context,String key,Object obj){
        try {
            // 保存对象
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream os=new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = Utils.bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            editor.putString(key, bytesToHexString);
            return editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrefUtils", "保存obj失败"+e.getMessage());
            return false;
        }
    }

    /**
     * desc:获取保存的Object对象
     * @param context
     * @return
     * modified:
     */
    public static Object getObject(Context context,String key){
        //返回反序列化得到的对象
        Object readObject = null;
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if (sharedPreferences.contains(key)) {
                String string = sharedPreferences.getString(key, "");
                if(TextUtils.isEmpty(string)){
                    return null;
                }else{
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis=new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is=new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    readObject = is.readObject();
                    return readObject;
                }
            }else{
                return null;
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //所有异常返回null
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //所有异常返回null
            return null;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //所有异常返回null
            return null;
        }
    }
}