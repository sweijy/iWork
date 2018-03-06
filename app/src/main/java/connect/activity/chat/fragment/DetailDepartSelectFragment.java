package connect.activity.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragment;
import connect.activity.base.BaseListener;
import connect.activity.chat.adapter.GroupDepartSelectAdapter;
import connect.activity.chat.set.GroupSelectActivity;
import connect.activity.home.view.LineDecoration;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.OrganizerHelper;
import connect.database.green.bean.OrganizerEntity;
import connect.ui.activity.R;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.system.SystemUtil;
import connect.widget.NameLinear;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public class DetailDepartSelectFragment extends BaseFragment {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.edittext_search_user)
    EditText edittextSearchUser;
    @Bind(R.id.imageview_clear)
    ImageView imageviewClear;
    @Bind(R.id.name_linear)
    NameLinear nameLinear;
    @Bind(R.id.scrollview)
    HorizontalScrollView scrollview;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private GroupSelectActivity activity;
    private GroupDepartSelectAdapter departSelectAdapter;
    private ArrayList<Connect.Department> nameList = new ArrayList<>();
    private List<String> selectDeparts = new ArrayList<>();

    public static DetailDepartSelectFragment startFragment() {
        DetailDepartSelectFragment fragment = new DetailDepartSelectFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_detaildepart_select, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (GroupSelectActivity) getActivity();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void initView() {
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideKeyBoard(getContext(), edittextSearchUser);
                activity.popBackLastFragment();
            }
        });

        if (activity.isCreateGroup()) {
            toolbarTop.setTitle(getResources().getString(R.string.Link_Group_Create));
        } else {
            toolbarTop.setTitle(getResources().getString(R.string.Link_Group_Invite));
        }

        nameLinear.setVisibility(View.VISIBLE);
        nameList.clear();
        Connect.Department department = Connect.Department.newBuilder()
                .setId(2)
                .setName("组织架构")
                .build();
        nameList.add(department);
        nameLinear.notifyAddView(nameList, scrollview);
        nameLinear.setItemClickListener(onItemClickListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.addItemDecoration(new LineDecoration(activity));
        departSelectAdapter = new GroupDepartSelectAdapter(activity);
        recyclerview.setAdapter(departSelectAdapter);
        departSelectAdapter.setItemClickListener(new GroupDepartSelectAdapter.GroupDepartSelectListener() {

            UserBean userBean = SharedPreferenceUtil.getInstance().getUser();

            @Override
            public boolean isMoveSelect(String selectKey) {
                return activity.isRemoveSelect(selectKey);
            }

            @Override
            public boolean isContains(boolean isDepart, String selectKey) {
                if (isDepart) {
                    return selectDeparts.contains(selectKey);
                } else {
                    return activity.isContains(selectKey);
                }
            }

            @Override
            public void itemClick(OrganizerEntity department) {
                if (department != null || department.getId() != null) {
                    requestDepartmentInfoShow(department.getId());

                    Connect.Department department1 = Connect.Department.newBuilder()
                            .setId(department.getId())
                            .setCount(department.getCount())
                            .setName(department.getName())
                            .build();
                    nameList.add(department1);
                    nameLinear.notifyAddView(nameList, scrollview);
                }
            }

            @Override
            public void departmentClick(boolean isSelect, OrganizerEntity department) {
                final long departmentId = department.getId();
                final String departmentKey = String.valueOf(departmentId);
                if (isSelect) {
                    requestDepartmentWorksById(departmentId, new BaseListener<Connect.Workmates>() {
                        @Override
                        public void Success(Connect.Workmates workmates) {
                            selectDeparts.add(departmentKey);

                            for (Connect.Workmate workmate : workmates.getListList()) {
                                if (workmate.getRegisted()) {
                                    activity.addWorkMate(workmate);
                                }
                            }
                        }

                        @Override
                        public void fail(Object... objects) {

                        }
                    });
                } else {
                    requestDepartmentWorksById(departmentId, new BaseListener<Connect.Workmates>() {
                        @Override
                        public void Success(Connect.Workmates workmates) {
                            selectDeparts.remove(departmentKey);

                            for (Connect.Workmate workmate : workmates.getListList()) {
                                String workMateUid = workmate.getUid();
                                activity.removeWorkMate(workMateUid);
                            }
                        }

                        @Override
                        public void fail(Object... objects) {

                        }
                    });
                }
            }

            @Override
            public void workmateClick(boolean isSelect, OrganizerEntity workmate) {
                String workmateKey = workmate.getUid();
                if (isSelect) {
                    if (workmate.getRegisted()) {
                        Connect.Workmate workmate1 = Connect.Workmate.newBuilder()
                                .setName(workmate.getName())
                                .setAvatar(workmate.getAvatar())
                                .setGender(workmate.getGender())
                                .setPubKey(workmate.getPub_key())
                                .setUid(workmate.getUid())
                                .build();
                        activity.addWorkMate(workmate1);
                    }
                } else {
                    activity.removeWorkMate(workmateKey);
                }
            }
        });

        edittextSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchTxt = editable.toString();
                if (TextUtils.isEmpty(searchTxt)) {
                    imageviewClear.setVisibility(View.GONE);
                    nameLinear.setVisibility(View.VISIBLE);
                } else {
                    imageviewClear.setVisibility(View.VISIBLE);
                }
            }
        });
        edittextSearchUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String mateName = edittextSearchUser.getText().toString();
                    requestWorkmateSearch(mateName);
                    return true;
                }
                return false;
            }
        });

        requestDepartmentInfoShow(2);
    }

    /**
     * 查询部门信息
     *
     * @param id
     */
    private void requestDepartmentInfo(long id, final BaseListener<Connect.SyncWorkmates> baseListener) {
        Connect.Department department = Connect.Department.newBuilder()
                .setId(id)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_DEPARTMENT, department, new ResultCall<Connect.HttpNotSignResponse>() {

            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.SyncWorkmates syncWorkmates = Connect.SyncWorkmates.parseFrom(structData.getPlainData());
                    baseListener.Success(syncWorkmates);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                baseListener.fail();
            }
        });
    }

    /**
     * 查询该部门所有成员
     *
     * @param id
     */
    private void requestDepartmentWorksById(long id, final BaseListener<Connect.Workmates> baseListener) {
        Connect.Department department = Connect.Department.newBuilder()
                .setId(id)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_DEPAERTMENT_WORKMATES, department, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.Workmates workmates = Connect.Workmates.parseFrom(structData.getPlainData());
                    baseListener.Success(workmates);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                baseListener.fail();
            }
        });
    }


    NameLinear.OnItemClickListener onItemClickListener = new NameLinear.OnItemClickListener() {
        @Override
        public void itemClick(int position) {
            for (int i = nameList.size() - 1; i > position; i--) {
                nameList.remove(i);
            }
            nameLinear.notifyAddView(nameList, scrollview);
            Connect.Department department = nameList.get(position);
            if (department != null) {
                long id = department.getId();
                requestDepartmentInfoShow(id);
            }
        }
    };

    protected void requestDepartmentInfoShow(final long id) {
        List<OrganizerEntity> entities = OrganizerHelper.getInstance().loadParamEntityByUpperId(id);
        departSelectAdapter.notifyData(entities);

        requestDepartmentInfo(id, new BaseListener<Connect.SyncWorkmates>() {
            @Override
            public void Success(Connect.SyncWorkmates syncWorkmates) {
                List<Connect.Department> departments = syncWorkmates.getDepts().getListList();
                List<Connect.Workmate> workmates = syncWorkmates.getWorkmates().getListList();

                List<OrganizerEntity> departSelectBeanList = new ArrayList();
                for (Connect.Department department1 : departments) {
                    OrganizerEntity organizerEntity = new OrganizerEntity();
                    organizerEntity.setUpperId(id);
                    organizerEntity.setId(department1.getId());
                    organizerEntity.setName(department1.getName());
                    organizerEntity.setCount(department1.getCount());
                    departSelectBeanList.add(organizerEntity);
                }

                for (Connect.Workmate workmate : workmates) {
                    if (workmate.getRegisted()) {
                        OrganizerEntity organizerEntity = getOrganizerEntity(workmate);
                        organizerEntity.setUpperId(id);
                        departSelectBeanList.add(organizerEntity);
                    }
                }
                departSelectAdapter.notifyData(departSelectBeanList);

                OrganizerHelper.getInstance().removeOrganizerEntityByUpperId(id);
                OrganizerHelper.getInstance().insertOrganizerEntities(departSelectBeanList);
            }

            @Override
            public void fail(Object... objects) {

            }
        });
    }

    private OrganizerEntity getOrganizerEntity(Connect.Workmate workmate) {
        OrganizerEntity entity = new OrganizerEntity();
        entity.setUid(workmate.getUid());
        entity.setName(workmate.getName());
        entity.setAvatar(workmate.getAvatar());
        entity.setO_u(workmate.getOU());
        entity.setPub_key(workmate.getPubKey());
        entity.setRegisted(workmate.getRegisted());
        entity.setEmpNo(workmate.getEmpNo());
        entity.setMobile(workmate.getMobile());
        entity.setGender(workmate.getGender());
        entity.setTips(workmate.getTips());
        return entity;
    }

    private void requestWorkmateSearch(String username) {
        if (TextUtils.isEmpty(username)) {
            return;
        }
        Connect.SearchUser searchUser = Connect.SearchUser.newBuilder()
                .setCriteria(username)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_WORKMATE_SEARCH, searchUser, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.Workmates workmates = Connect.Workmates.parseFrom(structData.getPlainData());
                    if (workmates.getListList().size() == 0) {
                        ToastEUtil.makeText(activity, R.string.Wallet_No_match_user).show();
                        return;
                    }
                    ArrayList<OrganizerEntity> list = new ArrayList<>();
                    for (Connect.Workmate workmate : workmates.getListList()) {
                        OrganizerEntity organizerEntity = getOrganizerEntity(workmate);
                        list.add(organizerEntity);
                    }
                    departSelectAdapter.notifyData(list);
                    nameLinear.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
            }
        });
    }

    @OnClick({R.id.imageview_clear})
    void OnClickListener(View view) {
        switch (view.getId()) {
            case R.id.imageview_clear:
                edittextSearchUser.setText("");
                onItemClickListener.itemClick(0);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
