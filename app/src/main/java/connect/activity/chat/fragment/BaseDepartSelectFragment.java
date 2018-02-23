package connect.activity.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragment;
import connect.activity.chat.adapter.BaseGroupSelectAdapter;
import connect.activity.chat.set.GroupSelectActivity;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */
public class BaseDepartSelectFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.text_search_user)
    TextView textSearchUser;
    @Bind(R.id.layout_edittext)
    RelativeLayout layoutEdittext;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private GroupSelectActivity activity;
    private BaseGroupSelectAdapter selectAdapter = null;
    private GroupSelectListener groupSelectListener = new GroupSelectListener();

    public static BaseDepartSelectFragment startFragment() {
        BaseDepartSelectFragment fragment = new BaseDepartSelectFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_departselect, container, false);
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
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        if (activity.isCreateGroup()) {
            toolbar.setTitle(getResources().getString(R.string.Link_Group_Create));
        } else {
            toolbar.setTitle(getResources().getString(R.string.Link_Group_Invite));
        }

        List<ContactEntity> contactEntities = ContactHelper.getInstance().loadFriend();
        requestUserInfo(activity.getUid());

        //添加组织架构
        ContactEntity originEntity = new ContactEntity();
        originEntity.setName(getString(R.string.Chat_Organizational_structure));
        originEntity.setGender(3);
        contactEntities.add(0, originEntity);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        selectAdapter = new BaseGroupSelectAdapter();
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(selectAdapter);
        selectAdapter.setData(contactEntities);
        selectAdapter.setGroupSelectListener(groupSelectListener);
    }

    private class GroupSelectListener implements BaseGroupSelectAdapter.BaseGroupSelectListener {
        @Override
        public boolean isContains(String selectKey) {
            return activity.isContains(selectKey);
        }

        @Override
        public boolean isMoveSelect(String selectKey) {
            return activity.isRemoveSelect(selectKey);
        }

        @Override
        public void organizeClick() {
            activity.switchFragment(DetailDepartSelectFragment.startFragment());
        }

        @Override
        public void itemClick(boolean isSelect, ContactEntity contactEntity) {
            String uid = contactEntity.getUid();
            if (isSelect) {
                Connect.Workmate workmate = Connect.Workmate.newBuilder()
                        .setPubKey(contactEntity.getPublicKey())
                        .setAvatar(contactEntity.getAvatar())
                        .setName(contactEntity.getName())
                        .setUid(contactEntity.getUid())
                        .build();

                activity.addWorkMate(workmate);
            } else {
                activity.removeWorkMate(uid);
            }
        }
    }

    @OnClick({R.id.layout_edittext})
    void OnclickListener(View view) {
        switch (view.getId()) {
            case R.id.layout_edittext:
                startDepartSearch();
                break;
        }
    }

    private void startDepartSearch() {
        activity.switchFragment(DepartSearchFragment.startFragment());
    }

    public void requestUserInfo(String value) {
        final Connect.SearchUser searchUser = Connect.SearchUser.newBuilder()
                .setTyp(1)
                .setCriteria(value)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V1_USER_SEARCH, searchUser, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.UsersInfo userInfo = Connect.UsersInfo.parseFrom(structData.getPlainData());
                    Connect.UserInfo userInfo1 = userInfo.getUsersList().get(0);

                    Connect.Workmate workmate = Connect.Workmate.newBuilder()
                            .setAvatar(userInfo1.getAvatar())
                            .setUid(userInfo1.getUid())
                            .setName(userInfo1.getName())
                            .setPubKey(userInfo1.getCaPub())
                            .build();
                    activity.addWorkMate(workmate);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
