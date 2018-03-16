package connect.activity.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.PinyinUtil;
import connect.utils.glide.GlideUtil;

/**
 * Created by PuJin on 2018/3/9.
 */
public class GroupRemoveAdapter extends RecyclerView.Adapter<GroupRemoveAdapter.RemoveHolder> {
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
    public RemoveHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        View view = inflater.inflate(R.layout.item_group_remove, arg0, false);
        RemoveHolder holder = new RemoveHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RemoveHolder holder, final int position) {
        final GroupMemberEntity memEntity = groupMemEntities.get(position);
        final String uid = memEntity.getUid();
        String name = TextUtils.isEmpty(memEntity.getUsername()) ? "" : memEntity.getUsername();
        holder.nameTxt.setText(name);
        GlideUtil.loadAvatarRound(holder.headImg, memEntity.getAvatar());

        if (memEntity.getRole() == null || memEntity.getRole() == 0) {
            holder.checkImg.setVisibility(View.VISIBLE);
            holder.managerTxt.setVisibility(View.GONE);
        } else {
            holder.checkImg.setVisibility(View.INVISIBLE);
            holder.managerTxt.setVisibility(View.VISIBLE);
        }

        holder.checkImg.setSelected(removeListener.isCheckOn(uid));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memEntity.getRole() == null || memEntity.getRole() == 0) {
                    boolean isremove = removeListener.isCheckOn(uid);
                    holder.checkImg.setSelected(!isremove);
                    if (isremove) {
                        removeListener.remove(uid);
                    } else {
                        removeListener.addRemove(memEntity);
                    }
                }
            }
        });

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

    class RemoveHolder extends RecyclerView.ViewHolder {

        private ImageView checkImg;
        private ImageView headImg;
        private TextView txt;
        private TextView nameTxt;
        private TextView managerTxt;
        RelativeLayout itemLayout;

        public RemoveHolder(View itemView) {
            super(itemView);
            checkImg = (ImageView) itemView.findViewById(R.id.checkstate);
            txt = (TextView) itemView.findViewById(R.id.txt);
            headImg = (ImageView) itemView.findViewById(R.id.roundimg);
            nameTxt = (TextView) itemView.findViewById(R.id.name);
            managerTxt = (TextView) itemView.findViewById(R.id.manager_tv);
            itemLayout = (RelativeLayout) itemView.findViewById(R.id.relative_item);
        }
    }

    private RemoveListener removeListener;

    public interface RemoveListener {

        void addRemove(GroupMemberEntity entity);

        boolean isCheckOn(String uid);

        void remove(String uid);
    }

    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }
}
