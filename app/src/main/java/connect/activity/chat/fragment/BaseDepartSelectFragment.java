package connect.activity.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.chat.adapter.BaseGroupSelectAdapter;
import connect.activity.chat.set.GroupSelectActivity;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.UriUtil;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.AvatarSearchView;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * 选择联系人
 * Created by PuJin on 2018/2/22.
 */
public class BaseDepartSelectFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.view_avatar_search)
    AvatarSearchView viewAvatarSearch;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private View view;

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
        view = inflater.inflate(R.layout.fragment_departselect, container, false);
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
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setTitle(getResources().getString(R.string.Chat_Choose_contact));

        // 组织架构选择
        View organization = view.findViewById(R.id.include_organization);
        ImageView oganizationImg = (ImageView) organization.findViewById(R.id.roundimg);
        TextView organizationTxt = (TextView) organization.findViewById(R.id.name);
        GlideUtil.loadImage(oganizationImg, R.mipmap.icon_organize);
        organizationTxt.setText(getString(R.string.Chat_Organization_Choose));
        organization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.switchFragment(DetailDepartSelectFragment.startFragment());
            }
        });
        // 通讯录选择
//        View contacts = view.findViewById(R.id.include_contacts);
//        ImageView contactsImg = (ImageView) contacts.findViewById(R.id.roundimg);
//        TextView contactsTxt = (TextView) contacts.findViewById(R.id.name);
//        GlideUtil.loadImage(contactsImg, R.mipmap.icon_contacts);
//        contactsTxt.setText(getString(R.string.Chat_Contacts_Choose));
//        contacts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                activity.switchFragment(ContactsFragment.startFragment());
//            }
//        });

        requestUserInfo(activity.getUid());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        selectAdapter = new BaseGroupSelectAdapter();
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(selectAdapter);
        selectAdapter.setGroupSelectListener(groupSelectListener);
        viewAvatarSearch.setListener(new AvatarSearchView.AvatarListener() {

            @Override
            public void removeUid(String uid) {
                activity.removeWorkMate(uid);
            }
        });
        viewAvatarSearch.addTextWatch(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                searchContent(string);
            }
        });

        searchContent("");
        updateSelect();
    }

    public void updateSelect() {
        Map<String, Object> objectMap = activity.getSelectMembers();
        Iterator iterator = objectMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Connect.Workmate workmate = (Connect.Workmate) entry.getValue();
            viewAvatarSearch.addAvatar(workmate.getAvatar(), workmate.getUid());
        }
    }

    public void searchContent(String string) {
        List<ConversionEntity> conversionEntities = null;
        if (TextUtils.isEmpty(string)) {
            viewAvatarSearch.hideKeyboard();
            conversionEntities = ConversionHelper.getInstance().loadRecentConversations();
            if (conversionEntities == null) {
                conversionEntities = new ArrayList<>();
            }
        } else {
            conversionEntities = ConversionHelper.getInstance().loadRecentConversationsLike(string);
        }
        selectAdapter.setData(conversionEntities);
    }

    private class GroupSelectListener implements BaseGroupSelectAdapter.BaseGroupSelectListener {
        @Override
        public boolean isContains(String avatar, String selectKey) {
            boolean iscontains = activity.isContains(selectKey);
            return iscontains;
        }

        @Override
        public boolean isMoveSelect(String selectKey) {
            return activity.isSelected(selectKey);
        }

        @Override
        public void organizeClick() {
            activity.switchFragment(DetailDepartSelectFragment.startFragment());
        }

        @Override
        public void itemClick(boolean isSelect, ConversionEntity contactEntity) {
            String avatar = contactEntity.getAvatar();
            String uid = contactEntity.getIdentifier();
            if (isSelect) {
                Connect.Workmate workmate = Connect.Workmate.newBuilder()
                        .setAvatar(contactEntity.getAvatar())
                        .setName(contactEntity.getName())
                        .setUid(contactEntity.getIdentifier())
                        .build();

                activity.addWorkMate(workmate);
                viewAvatarSearch.addAvatar(avatar, uid);
            } else {
                activity.removeWorkMate(uid);
                viewAvatarSearch.removeAvatar(uid);
            }
        }
    }

    public void requestUserInfo(String value) {
        Connect.SearchUser searchUser = Connect.SearchUser.newBuilder()
                .setTyp(2)
                .setCriteria(value)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_WORKMATE_SEARCH, searchUser, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.Workmates userInfo = Connect.Workmates.parseFrom(structData.getPlainData());
                    Connect.Workmate userInfo1 = userInfo.getList(0);

                    Connect.Workmate workmate = Connect.Workmate.newBuilder()
                            .setAvatar(userInfo1.getAvatar())
                            .setUid(userInfo1.getUid())
                            .setName(userInfo1.getName())
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
