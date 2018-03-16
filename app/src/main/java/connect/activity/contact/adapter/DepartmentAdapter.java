package connect.activity.contact.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.OrganizerEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.widget.DepartmentAvatar;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<OrganizerEntity> list = new ArrayList<>();
    private OnItemClickListener itemClickListener;

    public DepartmentAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public DepartmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_company_contact, parent, false);
        DepartmentAdapter.ViewHolder holder = new DepartmentAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DepartmentAdapter.ViewHolder holder, final int position) {
        final OrganizerEntity departmentBean = list.get(position);
        if(position == 0){
            holder.bottomLine.setVisibility(View.GONE);
            holder.topLine.setVisibility(View.VISIBLE);
        }else{
            if(TextUtils.isEmpty(list.get(position-1).getUsername()) && !TextUtils.isEmpty(departmentBean.getUsername())){
                holder.bottomLine.setVisibility(View.GONE);
                holder.topLine.setVisibility(View.VISIBLE);
            }else{
                holder.bottomLine.setVisibility(View.VISIBLE);
                holder.topLine.setVisibility(View.GONE);
            }
        }

        if(departmentBean.getId() != null){
            holder.departmentLinear.setVisibility(View.VISIBLE);
            holder.contentLin.setVisibility(View.GONE);

            holder.departmentTv.setText(departmentBean.getName());
            holder.countTv.setText("( " + departmentBean.getCount() + " )");
        }else{
            holder.departmentLinear.setVisibility(View.GONE);
            holder.contentLin.setVisibility(View.VISIBLE);

            holder.nameTvS.setText(departmentBean.getName());
            holder.avatarImage.setVisibility(View.VISIBLE);
            GlideUtil.loadAvatarRound(holder.avatarImage, departmentBean.getAvatar(), 8);
        }
        holder.contentLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClick(departmentBean);
            }
        });
        holder.departmentLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClick(departmentBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;
        RelativeLayout contentLin;
        TextView nameTvS;
        Button addBtn;

        LinearLayout departmentLinear;
        TextView departmentTv;
        TextView countTv;
        View topLine;
        View bottomLine;

        public ViewHolder(View itemView) {
            super(itemView);
            contentLin = (RelativeLayout)itemView.findViewById(R.id.content_layout);
            nameTvS = (TextView)itemView.findViewById(R.id.nickname_tv);
            addBtn = (Button) itemView.findViewById(R.id.status_btn);
            avatarImage = (ImageView)itemView.findViewById(R.id.avatar_image);

            departmentLinear = (LinearLayout)itemView.findViewById(R.id.department_linear);
            departmentTv = (TextView)itemView.findViewById(R.id.department_tv);
            countTv = (TextView)itemView.findViewById(R.id.count_tv);
            topLine = itemView.findViewById(R.id.top_Line);
            bottomLine = itemView.findViewById(R.id.bottom_line);
        }
    }

    public void setNotify(List<OrganizerEntity> list){
        this.list.clear();
        if(list.size() > 0){
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void itemClick(OrganizerEntity departmentBean);
    }

}
