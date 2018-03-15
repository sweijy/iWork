package connect.activity.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragment;
import connect.activity.contact.adapter.DepartmentAdapter;
import connect.activity.home.view.ToolbarSearch;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.OrganizerHelper;
import connect.database.green.bean.OrganizerEntity;
import connect.ui.activity.R;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.NameLinear;
import protos.Connect;

@Route(path = "/iwork/home/fragment/DepartmentFragment")
public class DepartmentFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    ToolbarSearch toolbar;
    @Bind(R.id.name_linear)
    NameLinear nameLinear;
    @Bind(R.id.scrollview)
    HorizontalScrollView scrollview;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.group_layout)
    LinearLayout groupLayout;

    private FragmentActivity mActivity;
    private DepartmentAdapter adapter;
    private ArrayList<Connect.Department> nameList = new ArrayList<>();
    private UserBean userBean;

    public static DepartmentFragment startFragment() {
        DepartmentFragment departmentFragment = new DepartmentFragment();
        return departmentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_contact_department, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initView();
    }

    @Override
    public void initView() {
        userBean = SharedPreferenceUtil.getInstance().getUser();
        toolbar.setRightLayoutVisible(false);

        nameLinear.setItemClickListener(onItemClickListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        //recyclerview.addItemDecoration(new LineDecoration(mActivity));
        adapter = new DepartmentAdapter(mActivity);
        adapter.setItemClickListener(onItemListener);
        recyclerview.setAdapter(adapter);
        initData();
    }

    private void initData() {
        nameLinear.setVisibility(View.VISIBLE);
        nameList.clear();
        Connect.Department department = Connect.Department.newBuilder()
                .setId(2)
                .setName("比特大陆组织架构")
                .build();
        nameList.add(department);
        nameLinear.notifyAddView(nameList, scrollview);
        requestDepartment(2L);
    }

    @OnClick(R.id.linear_layout)
    void search(View view) {
        ARouter.getInstance().build("/contact/SearchContactActivity").
                navigation();
    }

    @OnClick(R.id.group_layout)
    void groupLayout(View view) {
        ARouter.getInstance().build("/iwork/contact/ContactGroupActivity")
                .navigation();
    }

    NameLinear.OnItemClickListener onItemClickListener = new NameLinear.OnItemClickListener() {
        @Override
        public void itemClick(int position) {
            for (int i = nameList.size() - 1; i > position; i--) {
                nameList.remove(i);
            }
            nameLinear.notifyAddView(nameList, scrollview);
            requestDepartment(nameList.get(position).getId());
        }
    };

    DepartmentAdapter.OnItemClickListener onItemListener = new DepartmentAdapter.OnItemClickListener() {
        @Override
        public void itemClick(OrganizerEntity departmentBean) {
            if (departmentBean.getId() != null) {
                Connect.Department department = Connect.Department.newBuilder()
                        .setId(departmentBean.getId())
                        .setName(departmentBean.getName())
                        .build();
                nameList.add(department);
                nameLinear.notifyAddView(nameList, scrollview);
                requestDepartment(departmentBean.getId());
            } else {
                if (userBean.getUid().equals(departmentBean.getUid())) {
                    ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                            navigation();
                } else {
                    ARouter.getInstance().build("/iwork/contact/ContactDepartmentActivity")
                            .withSerializable("workmate", OrganizerHelper.getInstance().getWorkmate(departmentBean))
                            .navigation();
                }
            }
        }
    };

    private void requestDepartment(final Long id) {
        List<OrganizerEntity> entities = OrganizerHelper.getInstance().loadParamEntityByUpperId(id);
        adapter.setNotify(entities);

        final Connect.Department department = Connect.Department.newBuilder()
                .setId(id)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_DEPARTMENT, department, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.SyncWorkmates syncWorkmates = Connect.SyncWorkmates.parseFrom(structData.getPlainData());
                    ArrayList<OrganizerEntity> list = new ArrayList<>();
                    for (Connect.Department department1 : syncWorkmates.getDepts().getListList()) {
                        OrganizerEntity departmentBean = new OrganizerEntity();
                        departmentBean.setUpperId(id);
                        departmentBean.setId(department1.getId());
                        departmentBean.setName(department1.getName());
                        departmentBean.setCount(department1.getCount());
                        list.add(departmentBean);
                    }
                    for (Connect.Workmate workmate : syncWorkmates.getWorkmates().getListList()) {
                        OrganizerEntity organizerEntity = OrganizerHelper.getInstance().getContactBean(workmate);
                        organizerEntity.setUpperId(id);
                        list.add(organizerEntity);
                    }
                    adapter.setNotify(list);

                    OrganizerHelper.getInstance().removeOrganizerEntityByUpperId(id);
                    OrganizerHelper.getInstance().insertOrganizerEntities(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {}
        });
    }

}
