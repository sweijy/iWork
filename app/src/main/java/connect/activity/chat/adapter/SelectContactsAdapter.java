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

import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.PinyinUtil;
import connect.utils.glide.GlideUtil;

/**
 * Created by PuJin on 2018/3/9.
 */

public class SelectContactsAdapter extends RecyclerView.Adapter<SelectContactsAdapter.SelectHolder> {

    private List<ContactEntity> contactEntities = new ArrayList<>();

    public void setData(List<ContactEntity> entities) {
        this.contactEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactEntities.size();
    }

    @Override
    public SelectHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        View view = inflater.inflate(R.layout.item_group_contactselect, arg0, false);
        SelectHolder holder = new SelectHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SelectHolder holder, final int position) {
        final ContactEntity contactEntity = contactEntities.get(position);

        holder.selectView.setSelected(itemClick.isContains(contactEntity.getUid()));
        String name = TextUtils.isEmpty(contactEntity.getName()) ? "" : contactEntity.getName();
        holder.nameTxt.setText(name);
        GlideUtil.loadAvatarRound(holder.headImg, contactEntity.getAvatar());

        if (TextUtils.isEmpty(name)) name = "#";
        String curFirst = PinyinUtil.chatToPinyin(name.charAt(0));

        if (position == 0) {
            holder.txt.setVisibility(View.VISIBLE);
            holder.txt.setText(curFirst);
        } else {
            ContactEntity lastEntity = contactEntities.get(position - 1);
            String lastName = TextUtils.isEmpty(lastEntity.getName()) ? "" : lastEntity.getName();
            if (TextUtils.isEmpty(lastName)) lastName = "#";
            String lastFirst = PinyinUtil.chatToPinyin(lastName.charAt(0));

            if (lastFirst.equals(curFirst)) {
                holder.txt.setVisibility(View.GONE);
            } else {
                holder.txt.setVisibility(View.VISIBLE);
                holder.txt.setText(curFirst);
            }
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isselect = holder.selectView.isSelected();
                isselect = !isselect;
                holder.selectView.setSelected(isselect);
                itemClick.itemClick(isselect,contactEntity);
            }
        });
    }

    public int getPositionForSection(char selectchar) {
        for (int i = 0; i < contactEntities.size(); i++) {
            ContactEntity entity = contactEntities.get(i);
            String showName = entity.getName();
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

    class SelectHolder extends RecyclerView.ViewHolder {

        private View view;
        private View selectView;
        private ImageView headImg;
        private TextView txt;
        private TextView nameTxt;
        private TextView departmentTxt;

        public SelectHolder(View itemView) {
            super(itemView);
            view = itemView;
            txt = (TextView) itemView.findViewById(R.id.txt);
            selectView = itemView.findViewById(R.id.view_workmate_select);
            headImg = (ImageView) itemView.findViewById(R.id.roundimg);
            nameTxt = (TextView) itemView.findViewById(R.id.name);
            departmentTxt = (TextView) itemView.findViewById(R.id.department);
        }
    }

    private ItemClick itemClick;

    public interface ItemClick{

        boolean isContains(String selectKey);

        void itemClick(boolean isselect, ContactEntity contactEntity);
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }
}