package connect.activity.chat.adapter;

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

/**
 * Created by PuJin on 2018/1/10.
 */

public class GroupDepartSelectAdapter extends RecyclerView.Adapter<GroupDepartSelectAdapter.ViewHolder> {

    private Activity activity;
    private List<OrganizerEntity> departSelectBeens = new ArrayList<>();

    public GroupDepartSelectAdapter(Activity activity) {
        this.activity = activity;
    }

    public void notifyData(List<OrganizerEntity> departSelectBeens) {
        this.departSelectBeens = departSelectBeens;
        notifyDataSetChanged();
    }

    @Override
    public GroupDepartSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_group_departselect, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupDepartSelectAdapter.ViewHolder holder, final int position) {
        final OrganizerEntity department = departSelectBeens.get(position);
        if (department.isDepartment()) {
            holder.departmentLinear.setVisibility(View.VISIBLE);
            holder.contentLin.setVisibility(View.GONE);

            final String departmentKey = String.valueOf(department.getId());
            holder.departmentSelectView.setSelected(departSelectListener.isContains(true, departmentKey));
            holder.countTv.setText("(" + department.getCount() + ")");
            holder.departmentTv.setText(department.getName());
            holder.departmentSelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isselect = holder.departmentSelectView.isSelected();
                    isselect = !isselect;
                    departSelectListener.departmentClick(isselect, department);
                    holder.departmentSelectView.setSelected(isselect);
                }
            });
            holder.departmentLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!departSelectListener.isContains(true,departmentKey)) {
                        departSelectListener.itemClick(department);
                    }
                }
            });
        } else {
            holder.departmentLinear.setVisibility(View.GONE);
            holder.contentLin.setVisibility(View.VISIBLE);

            final String workmateKey = department.getUid();
            holder.workmateSelectView.setSelected(departSelectListener.isContains(false,workmateKey));
            holder.nameTvS.setText(department.getName());
            if (TextUtils.isEmpty(department.getO_u())) {
                holder.nicName.setVisibility(View.GONE);
            } else {
                holder.nicName.setVisibility(View.VISIBLE);
                holder.nicName.setText(department.getO_u());
            }
            if (department.getRegisted()) {
                holder.avater.setVisibility(View.GONE);
                holder.registerAvatar.setVisibility(View.VISIBLE);
                GlideUtil.loadAvatarRound(holder.registerAvatar, department.getAvatar());
            } else {
                holder.avater.setVisibility(View.VISIBLE);
                holder.registerAvatar.setVisibility(View.GONE);
                holder.avater.setAvatarName(department.getName(), department.getGender());
            }
            holder.workmateSelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if (departSelectListener.isMoveSelect(workmateKey)) {
                        boolean isselect = holder.workmateSelectView.isSelected();
                        isselect = !isselect;
                        departSelectListener.workmateClick(isselect, department);
                        holder.workmateSelectView.setSelected(isselect);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return departSelectBeens.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout contentLin;
        DepartmentAvatar avater;
        ImageView registerAvatar;
        TextView nameTvS;
        TextView nicName;
        Button addBtn;

        LinearLayout departmentLinear;
        TextView departmentTv;
        TextView countTv;

        private View departmentSelectView;
        private View workmateSelectView;

        public ViewHolder(View itemView) {
            super(itemView);
            contentLin = (RelativeLayout) itemView.findViewById(R.id.content_layout);
            avater = (DepartmentAvatar) itemView.findViewById(R.id.avatar_rimg);
            registerAvatar = (ImageView) itemView.findViewById(R.id.imageview_department_avatar);
            nameTvS = (TextView) itemView.findViewById(R.id.nickname_tv);
            nicName = (TextView) itemView.findViewById(R.id.hint_tv);
            addBtn = (Button) itemView.findViewById(R.id.status_btn);

            departmentLinear = (LinearLayout) itemView.findViewById(R.id.department_linear);
            departmentTv = (TextView) itemView.findViewById(R.id.department_tv);
            countTv = (TextView) itemView.findViewById(R.id.count_tv);

            departmentSelectView = itemView.findViewById(R.id.department_select);
            workmateSelectView = itemView.findViewById(R.id.workmate_select);
        }
    }


    private GroupDepartSelectListener departSelectListener;

    public void setItemClickListener(GroupDepartSelectListener selectListener) {
        this.departSelectListener = selectListener;
    }

    public interface GroupDepartSelectListener {

        void departmentClick(boolean isSelect, OrganizerEntity department);

        void workmateClick(boolean isSelect, OrganizerEntity workmate);

        boolean isContains(boolean isDepart, String selectKey);

        boolean isMoveSelect(String selectKey);

        void itemClick(OrganizerEntity department);
    }
}