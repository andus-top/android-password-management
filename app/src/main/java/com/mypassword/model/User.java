package com.mypassword.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:用户
 * 父类：DataSupport 使实体类有一个统一的父类，方便litepal管理
 *          boolean save(); 存储数据 不会抛出异常的
 *          saveThrows();   存储失败会抛出一个异常
 *          saveAll(Collection c);      集合数据的存储
 *
 *          int delete(Class<?> modelClass, long id);  数据的删除  会级联删除
 *          deleteAll(Class<?> modelClass, String... conditions);  带条件批量删除数据的方
 *              DataSupport.deleteAll(User.class, "userAccount = ? and userPwd = ?", "用户名是这个的删除", "0");
 *              DataSupport.deleteAll(User.class);   表中所有的数据全部删除掉
 *              除了DataSupport类中提供的静态删除方法之外，还有一个删除方法是作用于对象上的，
 *              即任何一个继承自DataSupport类的实例都可以通过调用delete()这个实例方法来删除数据。
 *              但前提是这个对象一定是要持久化之后的（即使用了save()方法，查询方法从数据库中查出来的对象也是经过持久化的），
 *              一个非持久化的对象如果调用了delete()方法则不会产生任何效果。
 *          boolean isSaved(); 对象是否持久化
 *
 *          int update(Class<?> modelClass, ContentValues values, long id);  更新数据
 *              ContentValues values = new ContentValues();
 *              values.put("userAccount", "今日iPhone6发布");
 *              DataSupport.update(User.class, values, id字段);
 *              //通过对象直接更新
 *              User user = new User();
 *              user.setUserAccount("要更新的值");
 *              user.update(id);
 *          int updateAll(Class<?> modelClass, ContentValues values, String... conditions); 根据条件修改 无条件会将该字段修改为values中值
 *              ContentValues values = new ContentValues();
 *              values.put("userAccount", "今日iPhone6 Plus发布");
 *              DataSupport.updateAll(User.class, values, "userAccount = ?", "今日iPhone6发布");
 *              DataSupport.updateAll(User.class, values)  修改所有行的数据
 *              //通过对象直接更新
 *              User user = new User();
 *              user.setUserAccount("要更新的值");
 *              user.updateAll("userAccount = ? and userPwd = ?", "今日iPhone6发布", "0");
 *              //对象方法
 *          void setToDefault(要设置为默认值的列名);  把某一列的数据修改成默认值
 *
 *          T find(Class<T> modelClass, long id); //查询主键为id的数据
 *          T findFirst(Class<T> modelClass); //获取表中第一条数据
 *          T findLast(Class<T> modelClass);   //获取表中最后一条数据
 *          List<T> findAll(Class<T> modelClass, long... ids);  //查询多个记录
 *          List<T> findAll(Class<T> modelClass);  //查询表中所有记录
 *          List<T> t = DataSupport.where("commentcount > ?", "0").find(Class<T> modelClass);  //条件查询，将条件放入 wher方法中
 *          //连缀查询
 *          List<T> t = DataSupport
 *              .select("userAccount", "userPwd")
 *              .where("userPwd = ?", "0")
 *              .order("registerTm desc")
 *              .limit(10)
 *              .offset(10) //分页查询，页面数量大小
 *              .find(Class<T> modelClass);
 *          //激进查询
 *          //一旦关联表中的数据很多，查询速度可能就会非常慢
 *          List<T> t = DataSupport.find(Class<T> modelClass, long id, true);//将和modelClass表关联的所有表中的数据也一起查出来
 *          //其他方法也一样
 *          Cursor cursor = DataSupport.findBySQL("select * from user where id=?", "0");
 * Change by:
 * Created by yang on 2017/12/14 9:58
 */
//tb:user  ;  id,registertm,useraccount,userpwd//该实体类对应的表结构
public class User extends DataSupport implements Serializable {
    private long id;//litepal规定每个实体类中都定义了一个id字段
    private String userAccount;
    private String userPwd;
    private Date registerTm;//注册时间
    //为了实现litepal中 一个用户多个分组的对应关系，放入该属性
    private List<GroupOfPwd> groupList = new ArrayList<GroupOfPwd>();

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public Date getRegisterTm() {
        return registerTm;
    }

    public void setRegisterTm(Date registerTm) {
        this.registerTm = registerTm;
    }

    public List<GroupOfPwd> getGroupList() {
        //return groupList;
        //返回id为当前用户，且未被删除的分组信息
        this.groupList = DataSupport.where("user_id = ? and isdelete = ?",String.valueOf(id),"0").find(GroupOfPwd.class);//user_id 根据 User首字母小写+ _id 拼接而来
        return this.groupList;
    }

    public void setGroupList(List<GroupOfPwd> groupList) {
        this.groupList = groupList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    private void saveExample(){
//        //一对多关系数据存储
//        GroupOfPwd groupOfPwd1 = new GroupOfPwd();
//        groupOfPwd1.setGroupTitle("biaoti");
//        groupOfPwd1.save();
//        GroupOfPwd groupOfPwd2 = new GroupOfPwd();
//        groupOfPwd2.setGroupTitle("biaoti");
//        groupOfPwd2.save();
//        User user = new User();
//        user.getGroupList().add(groupOfPwd1);
//        user.getGroupList().add(groupOfPwd2);
//        user.setUserAccount("123");
//        user.save();
//    }

//    public void updateExample(){
          //根据主键删除
//        ContentValues values = new ContentValues();
//        values.put("userAccount", "今日iPhone6发布");
//        DataSupport.update(User.class, values, id字段);
          //根据条件删除
//        ContentValues values = new ContentValues();
//        values.put("userAccount", "今日iPhone6 Plus发布");
//        DataSupport.updateAll(User.class, values, "userAccount = ?", "今日iPhone6发布");
//    }
}
