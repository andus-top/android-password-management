package com.mypassword.ui.index.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mypassword.R;
import com.mypassword.model.GroupOfPwd;
import com.mypassword.model.Password;

import java.util.List;

/**
 * DESCRIPTION:分组类型适配器
 * Created by yang on 2017/9/2 10:35
 */
public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    private List<GroupOfPwd> pData;
    private List<List<Password>> cData;
    private Context mContext;
    private ExpandableListView expandableListView;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();//更新数据
            super.handleMessage(msg);
        }
    };

    public ExpandableListViewAdapter(List<GroupOfPwd> pData, List<List<Password>> cData, Context mContext,ExpandableListView expandableListView) {
        this.pData = pData;
        this.cData = cData;
        this.mContext = mContext;
        this.expandableListView = expandableListView;
    }

    @Override
    public int getGroupCount() {
        if(pData != null && !pData.isEmpty()){
            int n = pData.size();
            return n;
        }else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(cData != null && !cData.isEmpty() && groupPosition < cData.size()){
            return cData.get(groupPosition).size();
        }else {
            return 0;
        }

    }

    @Override
    public GroupOfPwd getGroup(int groupPosition) {
        return pData.get(groupPosition);
    }

    @Override
    public Password getChild(int groupPosition, int childPosition) {
        return cData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //关闭
    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);

    }
    //展开
    @Override
    public void onGroupExpanded(final int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderParent parentHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_exlist_parent, parent, false);
            parentHolder = new ViewHolderParent();
            parentHolder.tv_group_pname = (TextView) convertView.findViewById(R.id.tv_group_pname);
            convertView.setTag(parentHolder);
            // 标记位置
            // 必须使用资源Id当key（不是资源id会出现运行时异常），android本意应该是想用tag来保存资源id对应组件。
            // 将groupPosition，childPosition通过setTag保存,在onItemLongClick方法中就可以通过view参数直接拿到了！
            convertView.setTag(R.id.tv_group_pname, groupPosition);
            convertView.setTag(R.id.tv_group_cname, -1);
        }else{
            parentHolder = (ViewHolderParent) convertView.getTag();
        }
        if(groupPosition < pData.size()){
            String titile = pData.get(groupPosition).getGroupTitle();
            parentHolder.tv_group_pname.setText(titile);
        }
        return convertView;
    }

    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild childHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_exlist_child, parent, false);
            childHolder = new ViewHolderChild();
           // childHolder.tv_operation_icon = (ImageView) convertView.findViewById(R.id.tv_operation_icon);
            childHolder.tv_group_cname = (TextView) convertView.findViewById(R.id.tv_group_cname);
            childHolder.iv_pwd_icon = (ImageView) convertView.findViewById(R.id.iv_pwd_icon);
            convertView.setTag(childHolder);
            // 标记位置
            // 必须使用资源Id当key（不是资源id会出现运行时异常），android本意应该是想用tag来保存资源id对应组件。
            // 将groupPosition，childPosition通过setTag保存,在onItemLongClick方法中就可以通过view参数直接拿到了！
            convertView.setTag(R.id.tv_group_pname, groupPosition);
            convertView.setTag(R.id.tv_group_cname, childPosition);
        }else{
            childHolder = (ViewHolderChild) convertView.getTag();
        }
        //childHolder.tv_operation_icon.setImageResource(cData.get(groupPosition).get(childPosition).getcId());
        if (groupPosition < pData.size() && groupPosition < cData.size()
                && childPosition < cData.get(groupPosition).size()) {
            String title = cData.get(groupPosition).get(childPosition).getTitle();
            childHolder.tv_group_cname.setText(title);
            //设置随机图片
            int color = ColorGenerator.MATERIAL.getRandomColor();
            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(title.substring(0,1),color);
            childHolder.iv_pwd_icon.setImageDrawable(myDrawable);
        }
        return convertView;
    }

    //设置子列表是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class ViewHolderParent {
        private TextView tv_group_pname;
    }

    private static class ViewHolderChild {
        private ImageView iv_pwd_icon;
        private TextView tv_group_cname;
    }

    /*供外界更新数据的方法*/
    public void refresh(){
        //必须重新伸缩之后才能更新数据
        if(pData != null && !pData.isEmpty()){
            for (int i = 0; i < pData.size(); i++) {
                expandableListView.collapseGroup(i);
                expandableListView.expandGroup(i);
                handler.sendMessage(new Message());
            }
        }else{
            handler.sendMessage(new Message());//当分组被删除完时，也需要刷新
        }
    }

    public void closeAllParent(){
        if(pData != null && !pData.isEmpty()){
            for (int i = 0; i < pData.size(); i++) {
                expandableListView.collapseGroup(i);
            }
        }
    }

}
