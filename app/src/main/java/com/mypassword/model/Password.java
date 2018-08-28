package com.mypassword.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Description:密码
 * 父类：DataSupport 使实体类有一个统一的父类，方便litepal管理
 *          boolean save(); 存储数据 不会抛出异常的
 *          saveThrows();   存储失败会抛出一个异常
 * Change by:
 * Created by yang on 2017/12/14 10:10
 */
//tb:password  ; id,account,isdelete,pwd,rmk,title,groupofpwd_id//该实体类对应的表结构

public class Password extends DataSupport implements Serializable {
    private long id;//litepal规定每个实体类中都定义了一个id字段
    private String account;
    private String pwd;
    private String title;
    private String rmk;//备注
    private GroupOfPwd groupOfPwd;//为了实现litepal中 一个密码属于一个分组的关系放入该属性，与GroupOfPwd 中的List<Password>对象 形成一对多关系
    private int isDelete = 0;//是否删除 默认为0 未删除

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public GroupOfPwd getGroupOfPwd() {
        return this.groupOfPwd;
    }

    public void setGroupOfPwd(GroupOfPwd groupOfPwd) {
        this.groupOfPwd = groupOfPwd;
    }

    //    Cursor cursor = DataSupport.findBySQL("select * from password");//查找数据库数据代码
//    List<Password> list = null;
//    if(cursor.getCount() >= 0) {
//        list = new ArrayList<>();
//        while(cursor.moveToNext()) {
//        Password password1 = new Password();
//        password1.setId(cursor.getLong(cursor.getColumnIndex("id")));
//        password1.setAccount(cursor.getString(cursor.getColumnIndex("account")));
//        password1.setPwd(cursor.getString(cursor.getColumnIndex("pwd")));
//        password1.setTitle(cursor.getString(cursor.getColumnIndex("title")));
//        long gid  = cursor.getLong(cursor.getColumnIndex("groupofpwd_id"));
//        list.add(password1);
//        }
//    }

}
