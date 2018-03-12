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

import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.widget.DepartmentAvatar;

/**
 * Created by PuJin on 2018/1/11.
 */

public class BaseGroupSelectAdapter extends RecyclerView.Adapter<BaseGroupSelectAdapter.RecentHolder> {

    private List<ConversionEntity> contactEntities = new ArrayList<>();
    private Context context;

    public BaseGroupSelectAdapter() {

    }

    public void setData() {
        notifyDataSetChanged();
    }

    public void setData(List<ConversionEntity> contactEntities) {
        this.contactEntities = contactEntities;
        notifyDataSetChanged();
    }

    @Override
    public RecentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_basegroup_recent, parent, false);
        RecentHolder holder = new RecentHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecentHolder holder, int position) {
        final ConversionEntity entity = contactEntities.get(position);
        String avatar = entity.getAvatar();
        String name = entity.getName();

        holder.selectView.setVisibility(View.VISIBLE);
        holder.nameTv.setVisibility(View.VISIBLE);
        holder.avatarImg.setVisibility(View.VISIBLE);
        holder.departmentAvatar.setVisibility(View.GONE);

        holder.selectView.setSelected(groupSelectListener.isContains(entity.getIdentifier()));
        holder.nameTv.setText(name);
        GlideUtil.loadImage(holder.avatarImg, avatar);
        holder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = entity.getIdentifier();
                if (groupSelectListener.isMoveSelect(uid)) {
                    boolean isselect = holder.selectView.isSelected();
                    isselect = !isselect;
                    groupSelectListener.itemClick(isselect, entity);
                    holder.selectView.setSelected(isselect);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactEntities.size();
    }

    static class RecentHolder extends RecyclerView.ViewHolder {

        View contentLayout;
        View selectView;
        DepartmentAvatar departmentAvatar;
        ImageView avatarImg;
        TextView nameTv;

        RecentHolder(View view) {
            super(view);
            contentLayout = view;
            selectView = view.findViewById(R.id.select);
            departmentAvatar = (DepartmentAvatar) view.findViewById(R.id.avatar_lin);
            avatarImg = (ImageView) view.findViewById(R.id.avatar_rimg);
            nameTv = (TextView) view.findViewById(R.id.text_view);
        }
    }

    private BaseGroupSelectListener groupSelectListener;

    public interface BaseGroupSelectListener {

        boolean isContains(String selectKey);

        boolean isMoveSelect(String selectKey);

        void organizeClick();

        void itemClick(boolean isSelect, ConversionEntity contactEntity);
    }

    public void setGroupSelectListener(BaseGroupSelectListener groupSelectListener) {
        this.groupSelectListener = groupSelectListener;
    }
}
