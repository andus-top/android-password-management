package com.mypassword.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:密码分组
 * DataSupport 使实体类有一个统一的父类，方便litepal管理
 * Change by:
 * Created by yang on 2017/12/14 10:12
 */
//TableModel tableMode = DBUtility.findPragmaTableInfo("groupofpwd", Connector.getDatabase());//litepal获取表结构代码
//tb:groupofpwd ; user_id,isdelete,grouptitle,grouptime,grouprmk,grouppwdtip,grouppwd,id//该实体类对应的表结构
public class GroupOfPwd extends DataSupport implements Serializable {
    private long id;//litepal规定每个实体类中都定义了一个id字段
    private String groupTitle;
    private String groupPwd;//该分组的密码
    private String groupPwdTip;//该分组密码的提示
    private Date groupTime;//添加时间
    private String groupRmk;//备注
    private User user;//为了实现litepal中 一个分组属于一个用户的关系放入该属性，与User 中的List<GroupOfPwd>对象 形成一对多关系
    private List<Password> pwdList = new ArrayList<Password>();//为了实现litepal中 一个分组多个密码的对应关系，放入该属性
    private int isDelete = 0;////是否删除 默认为0 未删除

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupPwd() {
        return groupPwd;
    }

    public void setGroupPwd(String groupPwd) {
        this.groupPwd = groupPwd;
    }

    public String getGroupPwdTip() {
        return groupPwdTip;
    }

    public void setGroupPwdTip(String groupPwdTip) {
        this.groupPwdTip = groupPwdTip;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Password> getPwdList() {
        //return pwdList;
        this.pwdList = DataSupport.where("groupofpwd_id = ? and isdelete = ?",String.valueOf(id),"0").find(Password.class);
        return pwdList;
    }

    public void setPwdList(List<Password> pwdList) {
        this.pwdList = pwdList;
    }

    public Date getGroupTime() {
        return groupTime;
    }

    public void setGroupTime(Date groupTime) {
        this.groupTime = groupTime;
    }

    public String getGroupRmk() {
        return groupRmk;
    }

    public void setGroupRmk(String groupRmk) {
        this.groupRmk = groupRmk;
    }

}
