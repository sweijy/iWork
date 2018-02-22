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

import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.widget.DepartmentAvatar;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public class DepartSelectShowAdapter extends RecyclerView.Adapter<DepartSelectShowAdapter.DepartSelectShowHolder> {

    private List<Connect.Workmate> workmates = new ArrayList<>();
    private Context context;

    public void setData(List<Connect.Workmate> workmates) {
        this.workmates = workmates;
        notifyDataSetChanged();
    }

    @Override
    public DepartSelectShowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_departselect_show, parent, false);
        DepartSelectShowHolder holder = new DepartSelectShowHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DepartSelectShowHolder holder, final int position) {
        final Connect.Workmate workmate = workmates.get(position);

        String avatar = workmate.getAvatar();
        String name = workmate.getName();

        if (TextUtils.isEmpty(avatar)) {
            holder.nameTv.setVisibility(View.VISIBLE);
            holder.avatarImg.setVisibility(View.GONE);
            holder.departmentAvatar.setVisibility(View.VISIBLE);

            holder.nameTv.setText(name);
            holder.departmentAvatar.setAvatarName(name, false, workmate.getGender());
        } else {
            holder.nameTv.setVisibility(View.VISIBLE);
            holder.avatarImg.setVisibility(View.VISIBLE);
            holder.departmentAvatar.setVisibility(View.GONE);

            holder.nameTv.setText(name);
            GlideUtil.loadAvatarRound(holder.avatarImg, workmate.getAvatar());
        }

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workmates.remove(workmate);
                notifyItemRemoved(position);
                departRemoveListener.removeWorkMate(workmate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    static class DepartSelectShowHolder extends RecyclerView.ViewHolder {

        DepartmentAvatar departmentAvatar;
        ImageView avatarImg;
        TextView nameTv;
        ImageView deleteImg;

        DepartSelectShowHolder(View view) {
            super(view);
            departmentAvatar = (DepartmentAvatar) view.findViewById(R.id.avatar_lin);
            avatarImg = (ImageView) view.findViewById(R.id.avatar_rimg);
            nameTv = (TextView) view.findViewById(R.id.text_view);
            deleteImg = (ImageView) view.findViewById(R.id.image_delete);
        }
    }


    private DepartRemoveListener departRemoveListener;

    public interface DepartRemoveListener {
        void removeWorkMate(Connect.Workmate workmate);
    }

    public void setDepartRemoveListener(DepartRemoveListener departRemoveListener) {
        this.departRemoveListener = departRemoveListener;
    }
}
