package connect.activity.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by Administrator on 2017/11/20.
 */

public class GroupCreateAdapter extends RecyclerView.Adapter<GroupCreateAdapter.MemberHolder>{

    private List<ContactEntity> contactEntities=new ArrayList<>();

    public void setData(List<ContactEntity> contactEntities){
        this.contactEntities=contactEntities;
        notifyDataSetChanged();
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_groupcreate_list, parent, false);
        MemberHolder memberHolder = new MemberHolder(view);
        return memberHolder;
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position) {
        ContactEntity entity = contactEntities.get(position);

        GlideUtil.loadAvatarRound(holder.avatar, entity.getAvatar());
        String curName = TextUtils.isEmpty(entity.getRemark()) ? entity.getUsername() : entity.getRemark();
        holder.name.setText(curName);
    }

    @Override
    public int getItemCount() {
        return contactEntities.size();
    }

    class MemberHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView name;

        MemberHolder(View view) {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.roundimg);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}
