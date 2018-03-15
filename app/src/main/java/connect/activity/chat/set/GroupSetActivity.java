package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.base.BaseListener;
import connect.activity.chat.set.contract.GroupSetContract;
import connect.activity.chat.set.presenter.GroupSetPresenter;
import connect.activity.contact.bean.ContactNotice;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.GroupEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;

/**
 * 群设置
 * Created by gtq on 2016/12/15.
 */
@Route(path = "/iwork/chat/set/GroupSetActivity")
public class GroupSetActivity extends BaseActivity implements GroupSetContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.linearlayout)
    LinearLayout linearlayout;
    @Bind(R.id.groupset_membercount)
    TextView groupsetMembercount;
    @Bind(R.id.linearlayout_groupmember)
    LinearLayout linearlayoutGroupmember;

    @Autowired
    String groupIdentify;

    private static String TAG = "_GroupSetActivity";
    private GroupSetActivity activity;
    private GroupSetContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupset);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        new GroupSetPresenter(this).start();
        presenter.syncGroupInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ContactNotice contactNotice) {
        switch (contactNotice.getNotice()) {
            case RecGroup:
                initView();
                break;
        }
    }

    @OnClick({R.id.linearlayout_groupmember, R.id.scrollview})
    public void memberLayoutClick() {
        ARouter.getInstance().build("/iwork/chat/set/GroupMemberActivity")
                .withString("groupIdentify", groupIdentify)
                .navigation();
    }

    @Override
    public void setPresenter(GroupSetContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    public String getUid() {
        return groupIdentify;
    }

    @Override
    public void countMember(int members) {
        groupsetMembercount.setText(getResources().getString(R.string.Chat_GroupMember_Count, members));
    }

    @Override
    public void memberList(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
        layout.addView(view);
    }

    @Override
    public void groupName(String groupname) {
        toolbar.setTitle(groupname);

        View view = findViewById(R.id.groupset_groupname);
        TextView txt1 = (TextView) view.findViewById(R.id.txt1);
        TextView txt2 = (TextView) view.findViewById(R.id.txt2);

        txt1.setText(getString(R.string.Link_Group_Name));
        if (!TextUtils.isEmpty(groupname)) {
            if (groupname.length() > 20) {
                groupname = groupname.substring(0, 20) + "...";
            }
            txt2.setText(groupname);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/iwork/chat/set/GroupNameActivity")
                        .withSerializable("identify", groupIdentify)
                        .navigation();
            }
        });
    }

    @Override
    public void addNewMember() {
        View view = findViewById(R.id.groupset_add_newmember);
        TextView textView = (TextView) view.findViewById(R.id.txt1);
        textView.setText(getResources().getString(R.string.Chat_GroupMember_Add));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ARouter.getInstance().build("/iwork/chat/set/GroupSelectActivity")
                        .withBoolean("isCreateGroup", false)
                        .withString("identify", groupIdentify)
                        .navigation();
            }
        });
    }

    @Override
    public void groupNameClickable(boolean clickable) {
        View view = findViewById(R.id.groupset_groupname);
        view.setEnabled(clickable);
    }

    @Override
    public void topSwitch(boolean top) {
        View view = findViewById(R.id.top);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Chat_Sticky_on_Top_chat));

        final View toggle = view.findViewById(R.id.toggle);
        toggle.setSelected(top);
        toggle.setOnClickListener(new View.OnClickListener() {

            boolean isSelect;

            @Override
            public void onClick(View view) {
                isSelect = view.isSelected();
                isSelect = !isSelect;
                presenter.groupTop(isSelect, new BaseListener<Boolean>() {

                    @Override
                    public void Success(Boolean ts) {
                        toggle.setSelected(isSelect);

                        ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(groupIdentify);
                        if (conversionEntity == null) {
                            conversionEntity = new ConversionEntity();
                            conversionEntity.setIdentifier(groupIdentify);
                        }
                        int top = isSelect ? 1 : 0;
                        conversionEntity.setTop(top);
                        ConversionHelper.getInstance().insertRoomEntity(conversionEntity);
                    }

                    @Override
                    public void fail(Object... objects) {
                    }
                });
            }
        });
    }

    @Override
    public void noticeSwitch(boolean notice) {
        View view = findViewById(R.id.mute);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Chat_Mute_Notification));

        final View toggle = view.findViewById(R.id.toggle);
        toggle.setSelected(notice);
        toggle.setOnClickListener(new View.OnClickListener() {
            boolean isSelect;

            @Override
            public void onClick(View view) {
                isSelect = view.isSelected();
                isSelect = !isSelect;
                presenter.groupMute(isSelect, new BaseListener<Boolean>() {
                    @Override
                    public void Success(Boolean ts) {
                        toggle.setSelected(isSelect);
                        ConversionSettingEntity setEntity = ConversionSettingHelper.getInstance().loadSetEntity(groupIdentify);
                        if (setEntity == null) {
                            setEntity = new ConversionSettingEntity();
                            setEntity.setIdentifier(groupIdentify);
                        }
                        int disturb = isSelect ? 1 : 0;
                        setEntity.setDisturb(disturb);
                        ConversionSettingHelper.getInstance().insertSetEntity(setEntity);
                    }

                    @Override
                    public void fail(Object... objects) {
                    }
                });
            }
        });


    }

    @Override
    public void commonSwtich(boolean common) {
        View view = findViewById(R.id.save);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Link_Save_to_Contacts));

        final View toggle = view.findViewById(R.id.toggle);
        toggle.setSelected(common);
        toggle.setOnClickListener(new View.OnClickListener() {
            boolean isSelect;

            @Override
            public void onClick(final View view) {
                isSelect = view.isSelected();
                isSelect = !isSelect;
                presenter.groupCommon(isSelect, new BaseListener<Boolean>() {
                    @Override
                    public void Success(Boolean ts) {
                        toggle.setSelected(isSelect);

                        GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(groupIdentify);
                        if (!(groupEntity == null || TextUtils.isEmpty(groupEntity.getName()))) {
                            int common = isSelect ? 1 : 0;
                            groupEntity.setCommon(common);

                            String groupName = groupEntity.getName();
                            if (TextUtils.isEmpty(groupName)) {
                                groupName = "groupname8";
                            }
                            groupEntity.setName(groupName);
                            ContactHelper.getInstance().inserGroupEntity(groupEntity);
                        }
                        ContactNotice.receiverGroup();
                    }

                    @Override
                    public void fail(Object... objects) {
                    }
                });
            }
        });
    }

    @Override
    public void quitExitGroup(final boolean isquit) {
        Button view = (Button) findViewById(R.id.groupset_exit_group);
        String string = isquit ?
                getString(R.string.Link_Delete_and_Leave) :
                getString(R.string.Chat_Group_Disband);
        view.setText(string);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isquit) {
                    presenter.exitGroup(new BaseListener<Boolean>() {
                        @Override
                        public void Success(Boolean ts) {
                            ARouter.getInstance().build("/iwork/HomeActivity").navigation();
                        }

                        @Override
                        public void fail(Object... objects) {

                        }
                    });
                } else {
                    presenter.disbandGroup(new BaseListener<Boolean>() {
                        @Override
                        public void Success(Boolean ts) {
                            ARouter.getInstance().build("/iwork/HomeActivity").navigation();
                        }

                        @Override
                        public void fail(Object... objects) {

                        }
                    });
                }
            }
        });
    }
}