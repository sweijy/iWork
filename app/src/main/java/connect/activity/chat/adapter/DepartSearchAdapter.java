package connect.activity.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.widget.DepartmentAvatar;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public class DepartSearchAdapter extends RecyclerView.Adapter<DepartSearchAdapter.DepartSearchHolder> {

    private List<Connect.Workmate> workmates = new ArrayList<>();
    private Context context;
    private String friendUid = "";

    public void setData(List<Connect.Workmate> workmates) {
        this.workmates = workmates;
        notifyDataSetChanged();
    }

    @Override
    public DepartSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_departsearch, parent, false);
        DepartSearchHolder holder = new DepartSearchHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DepartSearchHolder holder, int position) {
        final Connect.Workmate workmate = workmates.get(position);

        String avatar = workmate.getAvatar();
        String name = workmate.getName();

        if (TextUtils.isEmpty(avatar)) {
            holder.selectView.setVisibility(View.VISIBLE);
            holder.nameTv.setVisibility(View.VISIBLE);
            holder.avatarImg.setVisibility(View.GONE);
            holder.departmentAvatar.setVisibility(View.VISIBLE);

            holder.selectView.setSelected(departSearchListener.isContains(workmate.getUid()));
            holder.nameTv.setText(name);
            holder.departmentAvatar.setAvatarName(name, false, workmate.getGender());
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = workmate.getUid();
                    if (!friendUid.equals(uid)) {
                        boolean isselect = holder.selectView.isSelected();
                        isselect = !isselect;
                        departSearchListener.itemClick(isselect, workmate);
                        holder.selectView.setSelected(isselect);
                    }
                }
            });
        } else {
            holder.selectView.setVisibility(View.VISIBLE);
            holder.nameTv.setVisibility(View.VISIBLE);
            holder.avatarImg.setVisibility(View.VISIBLE);
            holder.departmentAvatar.setVisibility(View.GONE);

            holder.selectView.setSelected(departSearchListener.isContains(workmate.getUid()));
            holder.nameTv.setText(name);
            GlideUtil.loadAvatarRound(holder.avatarImg, workmate.getAvatar());
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uid = workmate.getUid();
                    if (!friendUid.equals(uid)) {
                        boolean isselect = holder.selectView.isSelected();
                        isselect = !isselect;
                        departSearchListener.itemClick(isselect, workmate);
                        holder.selectView.setSelected(isselect);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    static class DepartSearchHolder extends RecyclerView.ViewHolder {

        LinearLayout contentLayout;
        TextView topTv;
        View selectView;
        DepartmentAvatar departmentAvatar;
        ImageView avatarImg;
        TextView nameTv;

        DepartSearchHolder(View view) {
            super(view);
            topTv = (TextView) view.findViewById(R.id.top_tv);
            contentLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
            selectView = view.findViewById(R.id.select);
            departmentAvatar = (DepartmentAvatar) view.findViewById(R.id.avatar_lin);
            avatarImg = (ImageView) view.findViewById(R.id.avatar_rimg);
            nameTv = (TextView) view.findViewById(R.id.text_view);
        }
    }

    private DepartSearchListener departSearchListener;

    public interface DepartSearchListener {

        boolean isContains(String selectKey);

        void itemClick(boolean isSelect, Connect.Workmate workmate);
    }

    public void setDepartSearchListener(DepartSearchListener departSearchListener) {
        this.departSearchListener = departSearchListener;
    }
}