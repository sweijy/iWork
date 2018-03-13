package connect.activity.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.PinyinUtil;
import connect.utils.glide.GlideUtil;

/**
 * Created by gtq on 2016/12/15.
 */
public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.MemberReHolder> {

    private List<GroupMemberEntity> groupMemEntities = new ArrayList<>();

    public void setData(List<GroupMemberEntity> entities) {
        this.groupMemEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupMemEntities.size();
    }

    @Override
    public MemberReHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        View view = inflater.inflate(R.layout.item_group_member, arg0, false);
        MemberReHolder holder = new MemberReHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MemberReHolder holder, final int position) {
        GroupMemberEntity memEntity = groupMemEntities.get(position);
        String name = TextUtils.isEmpty(memEntity.getUsername()) ? "" : memEntity.getUsername();
        holder.nameTxt.setText(name);
        GlideUtil.loadAvatarRound(holder.headImg, memEntity.getAvatar());

        if (TextUtils.isEmpty(name)) name = "#";
        String curFirst = PinyinUtil.chatToPinyin(name.charAt(0));

        if (position == 0) {
            holder.txt.setVisibility(View.VISIBLE);
            holder.txt.setText(curFirst);
        } else {
            GroupMemberEntity lastEntity = groupMemEntities.get(position - 1);
            String lastName = TextUtils.isEmpty(lastEntity.getUsername()) ? "" : lastEntity.getUsername();
            if (TextUtils.isEmpty(lastName)) lastName = "#";
            String lastFirst = PinyinUtil.chatToPinyin(lastName.charAt(0));

            if (lastFirst.equals(curFirst)) {
                holder.txt.setVisibility(View.GONE);
            } else {
                holder.txt.setVisibility(View.VISIBLE);
                holder.txt.setText(curFirst);
            }
        }
    }

    public int getPositionForSection(char selectchar) {
        for (int i = 0; i < groupMemEntities.size(); i++) {
            GroupMemberEntity entity = groupMemEntities.get(i);
            String showName = entity.getUsername();
            if (TextUtils.isEmpty(showName)) {
                showName = "#";
            }
            String firstChar = PinyinUtil.chatToPinyin(showName.charAt(0));
            if (firstChar.charAt(0) >= selectchar) {
                return i;
            }
        }
        return -1;
    }

    class MemberReHolder extends RecyclerView.ViewHolder {

        private ImageView headImg;
        private TextView txt;
        private TextView nameTxt;
        private TextView departmentTxt;

        public MemberReHolder(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(R.id.txt);
            headImg = (ImageView) itemView.findViewById(R.id.roundimg);
            nameTxt = (TextView) itemView.findViewById(R.id.name);
            departmentTxt = (TextView) itemView.findViewById(R.id.department);
        }
    }
}
