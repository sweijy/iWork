package connect.activity.contact.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.chat.fragment.bean.SearchBean;
import connect.activity.contact.adapter.SearchAdapter;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.OrganizerHelper;
import connect.database.green.DaoHelper.ParamManager;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.ProgressUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;


public class SearchContentFragment extends BaseFragment {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.no_data_lin)
    LinearLayout noDataLin;

    private FragmentActivity mActivity;
    private SearchAdapter adapter;
    private Connect.Workmates workmates;
    private UserBean userBean;

    public static SearchContentFragment startFragment() {
        SearchContentFragment searchContentFragment = new SearchContentFragment();
        return searchContentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_chat_content, container, false);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        adapter = new SearchAdapter(mActivity);
        adapter.setOnItemChildListence(onItemChildClickListener);
        recyclerview.setAdapter(adapter);
    }

    private void updateView(ArrayList<SearchBean> list) {
        if (list.size() > 0) {
            noDataLin.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
            adapter.setDataNotify(list);
        } else {
            noDataLin.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        }
    }

    SearchAdapter.OnItemChildClickListener onItemChildClickListener = new SearchAdapter.OnItemChildClickListener() {
        @Override
        public void itemClick(int position, SearchBean searchBean) {
            if (searchBean.getStyle() == 1) {
                if (workmates.getListList().size() > position) {
                    Connect.Workmate workmate = workmates.getListList().get(position);
                    if (userBean.getUid().equals(workmate.getUid())) {
                        ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                                navigation();
                    } else {
                        ARouter.getInstance().build("/iwork/contact/ContactDepartmentActivity")
                                .withSerializable("workmate", workmate)
                                .navigation();
                    }
                }
            } else if (searchBean.getStyle() == 2) {
                ARouter.getInstance().build("/chat/ChatActivity")
                        .withSerializable("CHAT_TYPE", Connect.ChatType.GROUP)
                        .withString("CHAT_IDENTIFY", searchBean.getUid())
                        .navigation();
            } else if (searchBean.getStyle() == 3) {
                ARouter.getInstance().build("/chat/ChatActivity")
                        .withSerializable("CHAT_TYPE", searchBean.getStatus() == 1 ?
                                Connect.ChatType.PRIVATE :
                                Connect.ChatType.GROUP)
                        .withString("CHAT_IDENTIFY", searchBean.getUid())
                        .withString("CHAT_SEARCH_TXT", searchBean.getSearchStr())
                        .navigation();
            }
        }
    };

    public void updateView(String value, int status) {
        ParamManager.getInstance().putCommonlyString(value);
        ArrayList<SearchBean> list = new ArrayList<>();
        if (status == 0 || status == 1) {
            requestSearch(value, status);
        } else if (status == 2) {
            requestGroup(value, list);
        }
    }

    private void requestSearch(final String value, final int status) {
        ProgressUtil.getInstance().showProgress(mActivity);
        Connect.SearchUser searchUser = Connect.SearchUser.newBuilder()
                .setCriteria(value)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_WORKMATE_SEARCH, searchUser, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    workmates = Connect.Workmates.parseFrom(structData.getPlainData());
                    ArrayList<SearchBean> list = new ArrayList<>();
                    for (Connect.Workmate workmate : workmates.getListList()) {
                        SearchBean searchBean = new SearchBean();
                        searchBean.setStyle(1);
                        searchBean.setUid(workmate.getUid());
                        searchBean.setName(workmate.getName());
                        searchBean.setAvatar(workmate.getAvatar());
                        searchBean.setGender(workmate.getGender());
                        list.add(searchBean);
                    }
                    if (status == 0) {
                        requestGroup(value, list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                ArrayList<SearchBean> list = new ArrayList<>();
                if (status == 0) {
                    requestGroup(value, list);
                }
                updateView(list);
            }
        });
    }

    private void requestGroup(final String value, final ArrayList<SearchBean> list){
        Connect.SearchGroup searchGroup = Connect.SearchGroup.newBuilder()
                .setName(value)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_GROUP_SEARCH, searchGroup, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    ProgressUtil.getInstance().dismissProgress();
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.SearchGroupResult searchGroupResult = Connect.SearchGroupResult.parseFrom(structData.getPlainData());
                    List<Connect.Group> listGroup = searchGroupResult.getGroupsList();
                    for(Connect.Group group : listGroup){
                        SearchBean searchBean = new SearchBean();
                        searchBean.setUid(group.getIdentifier());
                        searchBean.setName(group.getName());
                        searchBean.setAvatar(group.getName());
                        searchBean.setSearchStr(value);
                        searchBean.setStyle(2);
                        searchBean.setStatus(1);
                        list.add(searchBean);
                    }
                    List<Connect.GroupMemberMatch> listMatch = searchGroupResult.getMembersList();
                    for(Connect.GroupMemberMatch groupMemberMatch : listMatch){
                        SearchBean searchBean = new SearchBean();
                        searchBean.setUid(groupMemberMatch.getGroup().getIdentifier());
                        searchBean.setName(groupMemberMatch.getGroup().getName());
                        searchBean.setAvatar(groupMemberMatch.getGroup().getName());
                        searchBean.setSearchStr(value);
                        searchBean.setHinit(groupMemberMatch.getMember().getName());
                        searchBean.setStyle(2);
                        searchBean.setStatus(2);
                        list.add(searchBean);
                    }
                    updateView(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                updateView(list);
            }
        });

    }

    private ArrayList<SearchBean> getFriendData(String value) {
        ArrayList<SearchBean> list = new ArrayList<>();
        List<ContactEntity> listFriend = ContactHelper.getInstance().loadFriendEntityFromText(value);
        for (ContactEntity contactEntity : listFriend) {
            if (!TextUtils.isEmpty(contactEntity.getPublicKey())) {
                SearchBean searchBean = new SearchBean();
                searchBean.setStyle(1);
                searchBean.setUid(contactEntity.getUid());
                searchBean.setName(contactEntity.getName());
                searchBean.setAvatar(contactEntity.getAvatar());
                list.add(searchBean);
            }
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}