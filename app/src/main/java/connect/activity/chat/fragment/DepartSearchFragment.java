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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragment;
import connect.activity.chat.adapter.DepartSearchAdapter;
import connect.activity.chat.set.GroupSelectActivity;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public class DepartSearchFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.edittext_search_user)
    EditText edittextSearchUser;
    @Bind(R.id.imageview_clear)
    ImageView imageviewClear;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private GroupSelectActivity activity;
    private DepartSearchAdapter searchAdapter;
    private boolean isCreateGroup = true;
    private Map<String, Object> selectedDeparts = new HashMap<>();

    public static DepartSearchFragment startFragment() {
        DepartSearchFragment fragment = new DepartSearchFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_departsearch, container, false);
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
    public void initView() {
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
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

        isCreateGroup = activity.isCreateGroup();
        selectedDeparts = activity.getSelectMembers();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        searchAdapter = new DepartSearchAdapter();
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(searchAdapter);
        searchAdapter.setDepartSearchListener(new DepartSearchAdapter.DepartSearchListener() {

            UserBean userBean = SharedPreferenceUtil.getInstance().getUser();

            @Override
            public boolean isContains(String selectKey) {
                return selectedDeparts.containsKey(selectKey) || selectKey.equals(userBean.getUid());
            }

            @Override
            public void itemClick(boolean isSelect, Connect.Workmate workmate) {
                String uid = workmate.getUid();
                if (isSelect) {
                    selectedDeparts.put(uid, workmate);
                    activity.addWorkMate(workmate);
                } else {
                    selectedDeparts.remove(uid);
                    activity.removeWorkMate(uid);
                }
            }
        });

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
                    } else {
                        searchAdapter.setData(workmates.getListList());
                    }
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
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
