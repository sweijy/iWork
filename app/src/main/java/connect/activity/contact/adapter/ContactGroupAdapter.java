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

public class ContactGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private OnItemClickListener itemClickListener;
    private ArrayList<GroupEntity> listData = new ArrayList<>();
    private final int STATUS_GROUP = 101;
    private final int STATUS_GROUP_COUNT = 102;
    private int count;

    public ContactGroupAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view;
        RecyclerView.ViewHolder holder = null;
        if(viewType == STATUS_GROUP){
            view = inflater.inflate(R.layout.item_contact_group, parent, false);
            holder = new ContactGroupAdapter.ViewHolder(view);
        } else if (viewType == STATUS_GROUP_COUNT) {
            view = inflater.inflate(R.layout.item_contact_list_count, parent, false);
            holder = new ContactGroupAdapter.CountHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GroupEntity groupEntity = listData.get(position);
        int type = getItemViewType(position);
        switch (type){
            case STATUS_GROUP:
                GlideUtil.loadAvatarRound(((ViewHolder)holder).avatarImage, groupEntity.getAvatar());
                ((ViewHolder)holder).nameText.setText(groupEntity.getName());
                ((ViewHolder)holder).contentRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.itemClick(position, groupEntity);
                    }
                });
                break;
            case STATUS_GROUP_COUNT:
                ((CountHolder) holder).bottomCount.setText(activity.getString(R.string.Link_group_count, count));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public int getItemViewType(int position) {
        String identifier = listData.get(position).getIdentifier();
        if(identifier.equals("count")){
            return STATUS_GROUP_COUNT;
        }else{
            return STATUS_GROUP;
        }
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

    class CountHolder extends RecyclerView.ViewHolder{

        TextView bottomCount;

        public CountHolder(View itemView) {
            super(itemView);
            bottomCount = (TextView)itemView.findViewById(R.id.friend_count_tv);
        }
    }

    public void setNotify(List<GroupEntity> list, int count){
        this.count = count;
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
