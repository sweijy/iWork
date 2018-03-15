package connect.activity.contact;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.base.compare.FriendCompara;
import connect.activity.base.compare.SearchCompare;
import connect.activity.chat.fragment.bean.SearchBean;
import connect.activity.contact.adapter.SearchAdapter;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ProgressUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

@Route(path = "/contact/SearchContactActivity")
public class SearchContactActivity extends BaseActivity {

    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.clear_image)
    ImageView clearImage;
    @Bind(R.id.right_lin)
    RelativeLayout rightLin;
    @Bind(R.id.no_result_text)
    TextView noResultText;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private SearchContactActivity mActivity;
    private UserBean userBean;
    private SearchAdapter adapter;
    private Connect.Workmates workmates;
    private SearchCompare searchCompare = new SearchCompare();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        userBean = SharedPreferenceUtil.getInstance().getUser();

        searchEdit.setOnKeyListener(keyListener);
        searchEdit.addTextChangedListener(textWatcher);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        adapter = new SearchAdapter(mActivity);
        adapter.setOnItemChildListence(onItemChildClickListener);
        recyclerview.setAdapter(adapter);

        noResultText.setVisibility(View.VISIBLE);
        recyclerview.setVisibility(View.GONE);
    }

    private void updateView(ArrayList<SearchBean> list) {
        if (list.size() > 0) {
            noResultText.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
            adapter.setDataNotify(list);
        } else {
            noResultText.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.right_lin)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.clear_image)
    void delEdit(View view) {
        searchEdit.setText("");
    }

    //防止第三方输入 监听ENTER
    private View.OnKeyListener keyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                // 先隐藏键盘
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(SearchContactActivity.this.getCurrentFocus()
                                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                requestSearch();
            }
            return false;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(TextUtils.isEmpty(s.toString())){
                clearImage.setVisibility(View.GONE);
            }else{
                clearImage.setVisibility(View.VISIBLE);
            }
        }
    };

    SearchAdapter.OnItemChildClickListener onItemChildClickListener = new SearchAdapter.OnItemChildClickListener() {
        @Override
        public void itemClick(int position, SearchBean searchBean) {
            if (searchBean.getStyle() == 1) {
                if (userBean.getUserName().equals(searchBean.getUserName())){
                    ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                            navigation();
                }else if(searchBean.getStatus() == 1){
                    ARouter.getInstance().build("/chat/ChatActivity")
                            .withSerializable("chatType", Connect.ChatType.GROUP)
                            .withString("chatIdentify", searchBean.getUid())
                            .navigation();
                }else if(searchBean.getStatus() == 2){
                    ARouter.getInstance().build("/contact/SearchContactUserActivity")
                            .withString("value", searchBean.getSearchStr())
                            .navigation();
                }
            } else if (searchBean.getStyle() == 2) {
                ARouter.getInstance().build("/chat/ChatActivity")
                        .withSerializable("chatType", Connect.ChatType.GROUP)
                        .withString("chatIdentify", searchBean.getUid())
                        .navigation();
            }
        }
    };

    private void requestSearch() {
        final String value = searchEdit.getText().toString();
        if(TextUtils.isEmpty(value)){
            return;
        }
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
                        searchBean.setUserName(workmate.getUsername());
                        searchBean.setName(workmate.getName());
                        searchBean.setAvatar(workmate.getAvatar());
                        searchBean.setGender(workmate.getGender());
                        searchBean.setSearchStr(value);
                        searchBean.setHinit(workmate.getOrganizational());
                        list.add(searchBean);
                        if(list.size() == 3){
                            break;
                        }
                    }
                    Collections.sort(list, searchCompare);
                    if(list.size() == 3 && workmates.getListList().size() > 3){
                        SearchBean searchBean = new SearchBean();
                        searchBean.setStyle(1);
                        searchBean.setStatus(2);
                        searchBean.setSearchStr(value);
                        list.add(searchBean);
                    }
                    requestGroup(value, list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                ArrayList<SearchBean> list = new ArrayList<>();
                requestGroup(value, list);
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

                    ArrayList<SearchBean> listData = new ArrayList<>();
                    List<Connect.Group> listGroup = searchGroupResult.getGroupsList();
                    for(Connect.Group group : listGroup){
                        SearchBean searchBean = new SearchBean();
                        searchBean.setUid(group.getIdentifier());
                        searchBean.setName(group.getName());
                        searchBean.setAvatar(group.getAvatar());
                        searchBean.setSearchStr(value);
                        searchBean.setCountMember(group.getCount());
                        searchBean.setStyle(2);
                        searchBean.setStatus(1);
                        listData.add(searchBean);
                    }
                    List<Connect.GroupMemberMatch> listMatch = searchGroupResult.getMembersList();
                    for(Connect.GroupMemberMatch groupMemberMatch : listMatch){
                        SearchBean searchBean = new SearchBean();
                        searchBean.setUid(groupMemberMatch.getGroup().getIdentifier());
                        searchBean.setName(groupMemberMatch.getGroup().getName());
                        searchBean.setAvatar(groupMemberMatch.getGroup().getAvatar());
                        searchBean.setSearchStr(value);
                        searchBean.setCountMember(groupMemberMatch.getGroup().getCount());
                        searchBean.setStyle(2);
                        searchBean.setStatus(2);
                        String names = "";
                        for(Connect.GroupMember groupMember : groupMemberMatch.getMemberList()){
                            if(TextUtils.isEmpty(names)){
                                names = names + groupMember.getName();
                            }else{
                                names = names + "," +groupMember.getName();
                            }
                        }
                        searchBean.setHinit(names);
                        listData.add(searchBean);
                    }
                    Collections.sort(listData, searchCompare);
                    list.addAll(listData);

                    updateView(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                ProgressUtil.getInstance().dismissProgress();
                updateView(list);
            }
        });
    }

}
