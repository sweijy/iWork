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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.base.compare.GroupComPara;
import connect.activity.chat.adapter.GroupMemberAdapter;
import connect.activity.chat.set.contract.GroupMemberContract;
import connect.activity.chat.set.presenter.GroupMemberPresenter;
import connect.activity.home.view.LineDecoration;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.dialog.DialogUtil;
import connect.utils.glide.GlideUtil;
import connect.widget.SideBar;
import connect.widget.TopToolBar;

/**
 * 群成员列表
 * Created by gtq on 2016/12/15.
 */
@Route(path = "/iwork/chat/set/GroupMemberActivity")
public class GroupMemberActivity extends BaseActivity implements GroupMemberContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.roundimg)
    ImageView roundimg;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.recordview)
    RecyclerView recordview;
    @Bind(R.id.siderbar)
    SideBar siderbar;

    @Autowired
    String groupIdentify;

    private static String TAG = "_GroupMemberActivity";
    private GroupMemberActivity activity;

    private LinearLayoutManager layoutManager;
    private List<GroupMemberEntity> memEntities = new ArrayList<>();
    private GroupMemberContract.Presenter presenter;
    private GroupMemberAdapter memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmember);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setRightImg(R.mipmap.icon_selectmore);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid = SharedPreferenceUtil.getInstance().getUser().getUid();
                GroupMemberEntity myMember = ContactHelper.getInstance().loadGroupMemberEntity(groupIdentify, myUid);

                final ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(getString(R.string.Chat_Member_Add));
                if (myMember.getRole() == 1) {
                    arrayList.add(getString(R.string.Chat_Member_Remove));
                }
                DialogUtil.showBottomView(activity, arrayList, new DialogUtil.DialogListItemClickListener() {
                    @Override
                    public void confirm(int position) {
                        String string = arrayList.get(position);
                        if (string.equals(getString(R.string.Chat_Member_Add))) {
                            ARouter.getInstance().build("/iwork/chat/set/GroupSelectActivity")
                                    .withBoolean("isCreateGroup", false)
                                    .withString("identify", groupIdentify)
                                    .navigation();
                        } else if (string.equals(getString(R.string.Chat_Member_Remove))) {
                            ARouter.getInstance().build("/iwork/chat/exts/GroupRemoveActivity")
                                    .withString("groupIdentify", groupIdentify)
                                    .navigation();
                        }
                    }
                });
            }
        });

        memEntities = ContactHelper.getInstance().loadGroupMemberEntities(groupIdentify);

        GroupMemberEntity manageEntity = memEntities.get(0);
        GlideUtil.loadAvatarRound(roundimg, manageEntity.getAvatar());
        name.setText(manageEntity.getUsername());

        toolbar.setTitle(getString(R.string.Chat_Member_All_Count, memEntities.size()));
        memEntities = memEntities.subList(1, memEntities.size());
        Collections.sort(memEntities, new GroupComPara());

        layoutManager = new LinearLayoutManager(activity);
        recordview.setLayoutManager(layoutManager);
        memberAdapter = new GroupMemberAdapter();
        recordview.setAdapter(memberAdapter);
        memberAdapter.setData(memEntities);
        recordview.addItemDecoration(new LineDecoration(activity));
        siderbar.setOnTouchingLetterChangedListener(new GroupMemberLetterChanged());

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
                searchContent(string);
            }
        });

        new GroupMemberPresenter(this).start();
    }

    public void searchContent(String string) {
        List<GroupMemberEntity> memberEntities = null;
        if (TextUtils.isEmpty(string)) {
            memberEntities = ContactHelper.getInstance().loadGroupMemberEntities(groupIdentify);
            if (memberEntities == null) {
                memberEntities = new ArrayList<>();
            }
        } else {
            memberEntities = ContactHelper.getInstance().loadGroupMemEntitiesLikeKey(groupIdentify, string);
        }
        memberAdapter.setData(memberEntities);
    }

    private class GroupMemberLetterChanged implements SideBar.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            int position = memberAdapter.getPositionForSection(s.charAt(0));

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

    @Override
    public String getRoomKey() {
        return groupIdentify;
    }

    @Override
    public void setPresenter(GroupMemberContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }
}
