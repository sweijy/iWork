package connect.activity.chat.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.OrganizerEntity;
import connect.ui.activity.R;
import connect.widget.DepartmentAvatar;

/**
 * Created by PuJin on 2018/1/10.
 */
public class GroupDepartSelectAdapter extends RecyclerView.Adapter<GroupDepartSelectAdapter.SelectHolder> {

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
    public SelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = null;
        SelectHolder selectHolder = null;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.item_groupdepart_workmate, parent, false);
            selectHolder = new WorkmateSelectHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_groupdepart_depart, parent, false);
            selectHolder = new DepartSelectHolder(view);
        }
        return selectHolder;
    }

    @Override
    public int getItemViewType(int position) {
        final OrganizerEntity department = departSelectBeens.get(position);
        return department.isDepartment() ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(SelectHolder holder, final int position) {
        final OrganizerEntity department = departSelectBeens.get(position);
        if (department.isDepartment()) {
            final DepartSelectHolder departSelectHolder = (DepartSelectHolder) holder;
            final String departmentKey = String.valueOf(department.getId());
            departSelectHolder.departSelectView.setSelected(departSelectListener.isContains(true, departmentKey));
            departSelectHolder.depaetCountTxt.setText("(" + department.getCount() + ")");
            departSelectHolder.depaetNameTxt.setText(department.getName());
            departSelectHolder.departmentSelectRelative.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    boolean isselect = departSelectHolder.departSelectView.isSelected();
                    isselect = !isselect;
                    departSelectListener.departmentClick(isselect, department);
                    departSelectHolder.departSelectView.setSelected(isselect);
                }
            });
            departSelectHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!departSelectListener.isContains(true, departmentKey)) {
                        departSelectListener.itemClick(department);
                    }
                }
            });
        } else {
            final WorkmateSelectHolder workmateSelectHolder = (WorkmateSelectHolder) holder;
            final String workmateKey = department.getUid();

            workmateSelectHolder.workmateSelectView.setSelected(departSelectListener.isContains(false, workmateKey));
            workmateSelectHolder.nickTxt.setText(department.getName());
            workmateSelectHolder.avatarImg.setAvatarName(department.getName(), department.getGender());
            workmateSelectHolder.workmateSelectRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(department.getUid())) {
                        if (departSelectListener.isMoveSelect(workmateKey)) {
                            boolean isselect = workmateSelectHolder.workmateSelectView.isSelected();
                            isselect = !isselect;
                            departSelectListener.workmateClick(isselect, department);
                            workmateSelectHolder.workmateSelectView.setSelected(isselect);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return departSelectBeens.size();
    }

    static class SelectHolder extends RecyclerView.ViewHolder {

        public SelectHolder(View itemView) {
            super(itemView);
        }
    }

    static class DepartSelectHolder extends SelectHolder {

        View itemView;
        RelativeLayout departmentSelectRelative;
        View departSelectView;
        TextView depaetNameTxt;
        TextView depaetCountTxt;

        public DepartSelectHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            departmentSelectRelative = (RelativeLayout) itemView.findViewById(R.id.relative_department_select);
            departSelectView = itemView.findViewById(R.id.view_department_select);
            depaetNameTxt = (TextView) itemView.findViewById(R.id.department_tv);
            depaetCountTxt = (TextView) itemView.findViewById(R.id.count_tv);
        }
    }

    static class WorkmateSelectHolder extends SelectHolder {

        View itemView;
        RelativeLayout workmateSelectRelative;
        View workmateSelectView;
        DepartmentAvatar avatarImg;
        TextView nickTxt;

        public WorkmateSelectHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            workmateSelectRelative = (RelativeLayout) itemView.findViewById(R.id.relative_workmate_select);
            workmateSelectView = itemView.findViewById(R.id.view_workmate_select);
            avatarImg = (DepartmentAvatar) itemView.findViewById(R.id.depart_avatar);
            nickTxt = (TextView) itemView.findViewById(R.id.nickname_tv);
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