package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.base.compare.GroupComPara;
import connect.activity.base.compare.GroupMemberCompara;
import connect.activity.chat.adapter.GroupRemoveAdapter;
import connect.activity.chat.set.contract.GroupRemoveContract;
import connect.activity.chat.set.presenter.GroupRemovePresenter;
import connect.activity.home.view.LineDecoration;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.dialog.DialogUtil;
import connect.widget.SideBar;
import connect.widget.TopToolBar;

/**
 * 移除群成员
 */
@Route(path = "/iwork/chat/exts/GroupRemoveActivity")
public class GroupRemoveActivity extends BaseActivity implements GroupRemoveContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.line_view)
    View lineView;
    @Bind(R.id.txt_selected)
    TextView txtSelected;
    @Bind(R.id.btn_selected)
    Button btnSelected;
    @Bind(R.id.layout_selected)
    RelativeLayout layoutSelected;
    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.recordview)
    RecyclerView recordview;
    @Bind(R.id.siderbar)
    SideBar siderbar;

    @Autowired
    String groupIdentify;

    private static String TAG = "_GroupRemoveActivity";
    private GroupRemoveActivity activity;
    private Map<String, GroupMemberEntity> removeMap = new HashMap<>();

    private LinearLayoutManager layoutManager;
    private List<GroupMemberEntity> memberEntities;
    private GroupRemoveAdapter removeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_remove);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getString(R.string.Chat_Member_Remove));
        toolbar.setRightText(getString(R.string.Chat_Member_Cancel));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMap.clear();
                totalRemove();
                removeAdapter.setData(memberEntities);
            }
        });
        memberEntities = ContactHelper.getInstance().loadGroupMemberEntities(groupIdentify);
        Collections.sort(memberEntities, new GroupComPara());

        recordview.addItemDecoration(new LineDecoration(activity));
        removeAdapter = new GroupRemoveAdapter();
        layoutManager = new LinearLayoutManager(activity);
        recordview.setLayoutManager(layoutManager);
        recordview.setAdapter(removeAdapter);
        removeAdapter.setData(memberEntities);
        siderbar.setOnTouchingLetterChangedListener(new GroupRemoveLetterChanged());
        removeAdapter.setRemoveListener(new GroupRemoveAdapter.RemoveListener() {

            @Override
            public void addRemove(GroupMemberEntity entity) {
                removeMap.put(entity.getUid(), entity);
                totalRemove();
            }

            @Override
            public boolean isCheckOn(String uid) {
                return removeMap.containsKey(uid);
            }

            @Override
            public void remove(String uid) {
                removeMap.remove(uid);
                totalRemove();
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();
                searchGroupMember(string);
            }
        });
        totalRemove();
        searchGroupMember("");

        new GroupRemovePresenter(this).start();
    }

    @OnClick({R.id.btn_selected})
    void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_selected:
                String title = getString(R.string.Chat_Set_Remove_Members);

                StringBuffer buffer = new StringBuffer();
                Iterator iterator = removeMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    GroupMemberEntity entity = (GroupMemberEntity) entry.getValue();
                    buffer.append(entity.getUsername());
                    buffer.append(",");
                }
                String content = buffer.substring(0, buffer.length() - 1);
                String leftCancle = getString(R.string.Chat_Member_Cancel);
                String rightConfirm = getString(R.string.Common_OK);
                DialogUtil.showAlertTextView(activity, title, content, leftCancle, rightConfirm, true, new DialogUtil.OnItemClickListener() {

                    @Override
                    public void confirm(String value) {

                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
        }
    }

    @Override
    public void setPresenter(GroupRemoveContract.Presenter presenter) {

    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public String getGroupIdentify() {
        return groupIdentify;
    }

    public void totalRemove() {
        int count = removeMap.size();
        txtSelected.setText(getString(R.string.Chat_Selected_Member, count));
        btnSelected.setText(getString(R.string.Chat_Determine_Count, count));
        btnSelected.setEnabled(count > 0);
    }

    public void searchGroupMember(String memberKey) {
        List<GroupMemberEntity> groupMemEntities = null;
        if (TextUtils.isEmpty(memberKey)) {
            groupMemEntities = ContactHelper.getInstance().loadGroupMemEntities(groupIdentify);
        } else {
            groupMemEntities = ContactHelper.getInstance().loadGroupMemEntitiesLikeKey(groupIdentify, memberKey);
        }

        String myPublicKey = SharedPreferenceUtil.getInstance().getUser().getUid();
        Iterator<GroupMemberEntity> iterator = groupMemEntities.iterator();
        while (iterator.hasNext()) {
            GroupMemberEntity memberEntity = iterator.next();
            if (memberEntity.getUid().equals(myPublicKey)) {
                iterator.remove();
            }
        }

        Collections.sort(groupMemEntities, new GroupMemberCompara());
        removeAdapter.setData(groupMemEntities);
    }

    private class GroupRemoveLetterChanged implements SideBar.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            int position = removeAdapter.getPositionForSection(s.charAt(0));

            int firstItem = layoutManager.findFirstVisibleItemPosition();
            int lastItem = layoutManager.findLastVisibleItemPosition();
            if (position <= firstItem) {
                recordview.scrollToPosition(position);
            } else if (position <= lastItem) {
                int top = recordview.getChildAt(position - firstItem).getTop();
                recordview.scrollBy(0, top);
            } else {
                recordview.scrollToPosition(position);
            }
        }
    }
}
