package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.base.BaseListener;
import connect.activity.base.compare.GroupComPara;
import connect.activity.chat.adapter.GroupRemoveAdapter;
import connect.activity.chat.set.contract.GroupRemoveContract;
import connect.activity.chat.set.presenter.GroupRemovePresenter;
import connect.activity.home.view.LineDecoration;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.widget.SideBar;
import connect.widget.TopToolBar;

/**
 * 移除群成员
 */
@Route(path = "/iwork/chat/exts/GroupRemoveActivity")
public class GroupRemoveActivity extends BaseActivity implements GroupRemoveContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.recordview)
    RecyclerView recordview;
    @Bind(R.id.siderbar)
    SideBar siderbar;

    @Autowired
    String groupIdentify;

    private static String TAG = "_GroupRemoveActivity";
    @Bind(R.id.roundimg)
    ImageView roundimg;
    @Bind(R.id.name)
    TextView name;

    private GroupRemoveActivity activity;
    private Map<String, GroupMemberEntity> removeMap = new HashMap<>();

    private LinearLayoutManager layoutManager;
    private GroupRemoveContract.Presenter removePresenter;
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
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getString(R.string.Chat_Member_Remove));
        toolbar.setRightText(getString(R.string.Chat_Confirm));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        toolbar.setRightListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List<GroupMemberEntity> memberEntities = new ArrayList();
                Iterator iterator = removeMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, GroupMemberEntity> entry = (Map.Entry) iterator.next();
                    memberEntities.add(entry.getValue());
                }

                removePresenter.removeMembers(memberEntities, new BaseListener<Boolean>() {
                    @Override
                    public void Success(Boolean ts) {
                        ARouter.getInstance().build("/iwork/chat/set/GroupSetActivity")
                                .withString("groupIdentify", groupIdentify)
                                .navigation();
                    }

                    @Override
                    public void fail(Object... objects) {

                    }
                });
            }
        });
        recordview.addItemDecoration(new LineDecoration(activity));
        removeAdapter = new GroupRemoveAdapter();
        layoutManager = new LinearLayoutManager(activity);
        recordview.setLayoutManager(layoutManager);
        recordview.setAdapter(removeAdapter);
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

        List<GroupMemberEntity> groupMemEntities = ContactHelper.getInstance().loadGroupMemEntities(groupIdentify);

        GroupMemberEntity manageEntity = groupMemEntities.get(0);
        GlideUtil.loadAvatarRound(roundimg, manageEntity.getAvatar());
        name.setText(manageEntity.getUsername());

        groupMemEntities = groupMemEntities.subList(1, groupMemEntities.size());
        Collections.sort(groupMemEntities, new GroupComPara());
        removeAdapter.setData(groupMemEntities);

        new GroupRemovePresenter(this).start();
        totalRemove();
    }

    @Override
    public void setPresenter(GroupRemoveContract.Presenter presenter) {
        this.removePresenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public String getidentify() {
        return groupIdentify;
    }

    public void totalRemove() {
        int count = removeMap.size();
        if (count == 0) {
            toolbar.setRightText(getString(R.string.Chat_Confirm));
        } else {
            toolbar.setRightText(getString(R.string.Chat_Confirm_Number, count));
        }
        toolbar.setRightTextEnable(count > 0);
    }

    public void searchGroupMember(String memberKey) {
        List<GroupMemberEntity> groupMemEntities = null;
        if (TextUtils.isEmpty(memberKey)) {
            groupMemEntities = ContactHelper.getInstance().loadGroupMemEntities(groupIdentify);
        } else {
            groupMemEntities = ContactHelper.getInstance().loadGroupMemEntitiesLikeKey(groupIdentify, memberKey);
        }

        groupMemEntities = groupMemEntities.subList(1, groupMemEntities.size());
        Collections.sort(groupMemEntities, new GroupComPara());
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
