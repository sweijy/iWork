package connect.activity.contact.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.GroupEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by Administrator on 2018/3/8 0008.
 */

public class ContactGroupAdapter extends RecyclerView.Adapter<ContactGroupAdapter.ViewHolder> {

    private Activity activity;
    private OnItemClickListener itemClickListener;
    private ArrayList<GroupEntity> listData = new ArrayList<>();

    public ContactGroupAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ContactGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_contact_group, parent, false);
        ContactGroupAdapter.ViewHolder holder = new ContactGroupAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ContactGroupAdapter.ViewHolder holder, final int position) {
        final GroupEntity groupEntity = listData.get(position);
        GlideUtil.loadAvatarRound(holder.avatarImage, groupEntity.getAvatar());
        holder.nameText.setText(groupEntity.getName());
        holder.contentRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClick(position, groupEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout contentRelative;
        ImageView avatarImage;
        TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);
            contentRelative = (RelativeLayout)itemView.findViewById(R.id.content_relative);
            avatarImage = (ImageView)itemView.findViewById(R.id.avatar_image);
            nameText = (TextView)itemView.findViewById(R.id.name_text);
        }
    }

    public void setNotify(List<GroupEntity> list){
        listData.clear();
        if(list!= null && list.size() > 0){
            listData.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setItemClickListener(ContactGroupAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void itemClick(int position, GroupEntity groupEntity);
    }

}
