package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.set.contract.GroupSetContract;
import connect.activity.chat.set.presenter.GroupSetPresenter;
import connect.activity.contact.FriendInfoActivity;
import connect.activity.contact.StrangerInfoActivity;
import connect.activity.contact.bean.SourceType;
import connect.activity.set.ModifyInfoActivity;
import connect.database.MemoryDataManager;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.DialogUtil;
import connect.widget.TopToolBar;

/**
 * group setting
 * Created by gtq on 2016/12/15.
 */
public class GroupSetActivity extends BaseActivity implements GroupSetContract.BView{

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.count)
    TextView count;
    @Bind(R.id.linearlayout)
    LinearLayout linearlayout;
    @Bind(R.id.relativelayout_1)
    RelativeLayout relativelayout1;

    private static String GROUP_KEY = "GROUP_KEY";
    private final String TAG_ADD = "TAG_ADD";

    private String groupKey;
    private GroupSetActivity activity;
    private GroupSetContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupset);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity, String groupkey) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_KEY, groupkey);
        ActivityUtil.next(activity, GroupSetActivity.class, bundle);
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Link_Group));
        toolbar.setLeftListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        groupKey = getIntent().getStringExtra(GROUP_KEY);
        new GroupSetPresenter(this).start();
        presenter.syncGroupInfo();
    }

    @OnClick(R.id.relativelayout_1)
    public void memberLayoutClickListener() {
        GroupMemberActivity.startActivity(activity, groupKey);
    }

    @Override
    public void setPresenter(GroupSetContract.Presenter presenter) {
        this.presenter=presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public String getRoomKey() {
        return groupKey;
    }

    @Override
    public void countMember(String members) {
        count.setText(members);
    }

    @Override
    public void memberList(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
        layout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = (String) v.getTag();
                if (TAG_ADD.equals(address)) {
                    GroupInviteActivity.startActivity(activity, groupKey);
                } else {
                    if (MemoryDataManager.getInstance().getAddress().equals(address)) {
                        ModifyInfoActivity.startActivity(activity);
                    } else {
                        ContactEntity entity = ContactHelper.getInstance().loadFriendEntity(address);
                        if (entity == null) {
                            StrangerInfoActivity.startActivity(activity, address, SourceType.CARD);
                        } else {
                            FriendInfoActivity.startActivity(activity, entity.getPub_key());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void groupName(String groupname) {
        View view = findViewById(R.id.groupset_groupname);
        ImageView img1 = (ImageView) view.findViewById(R.id.img1);
        TextView txt1 = (TextView) view.findViewById(R.id.txt1);
        TextView txt2 = (TextView) view.findViewById(R.id.txt2);
        ImageView img2 = (ImageView) view.findViewById(R.id.img2);

        img1.setBackgroundResource(R.mipmap.message_groupchat_name2x);
        txt1.setText(getString(R.string.Link_Group_Name));
        if (!TextUtils.isEmpty(groupname)) {
            if (groupname.length() > 10) {
                groupname = groupname.substring(0, 10) + "...";
            }
            txt2.setText(groupname);
        }

        img2.setImageResource(R.mipmap.group_item_arrow);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupNameActivity.startActivity(activity, groupKey);
            }
        });
    }

    @Override
    public void groupNameClickable(boolean clickable) {
        View view = findViewById(R.id.groupset_myname);
        view.setEnabled(false);
    }

    @Override
    public void groupMyAlias(String alias) {
        View view = findViewById(R.id.groupset_myname);
        view.setVisibility(TextUtils.isEmpty(alias) ? View.GONE : View.VISIBLE);
        ImageView img1 = (ImageView) view.findViewById(R.id.img1);
        TextView txt1 = (TextView) view.findViewById(R.id.txt1);
        TextView txt2 = (TextView) view.findViewById(R.id.txt2);
        ImageView img2 = (ImageView) view.findViewById(R.id.img2);

        img1.setBackgroundResource(R.mipmap.message_groupchat_myname2x);
        txt1.setText(getString(R.string.Link_My_Alias_in_Group));
        if (!TextUtils.isEmpty(alias)) {
            if (alias.length() > 10) {
                alias = alias.substring(0, 10) + "...";
            }
            txt2.setText(alias);
        }

        img2.setImageResource(R.mipmap.group_item_arrow);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupMyNameActivity.startActivity(activity, groupKey);
            }
        });
    }

    @Override
    public void groupQRCode() {
        View view = findViewById(R.id.groupset_qrcode);
        ImageView img1 = (ImageView) view.findViewById(R.id.img1);
        TextView txt1 = (TextView) view.findViewById(R.id.txt1);
        TextView txt2 = (TextView) view.findViewById(R.id.txt2);
        ImageView img2 = (ImageView) view.findViewById(R.id.img2);

        img1.setBackgroundResource(R.mipmap.message_groupchat_qrcode2x);
        txt1.setText(getString(R.string.Link_Group_is_QR_Code));
        txt2.setText("");

        img2.setImageResource(R.mipmap.group_item_arrow);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupQRActivity.startActivity(activity, groupKey);
            }
        });
    }

    @Override
    public void groupManager(boolean visiable) {
        View view = findViewById(R.id.groupset_manage);
        view.setVisibility(visiable ? View.VISIBLE : View.GONE);
        ImageView img1 = (ImageView) view.findViewById(R.id.img1);
        TextView txt1 = (TextView) view.findViewById(R.id.txt1);
        TextView txt2 = (TextView) view.findViewById(R.id.txt2);
        ImageView img2 = (ImageView) view.findViewById(R.id.img2);

        img1.setBackgroundResource(R.mipmap.message_groupchat_setting);
        txt1.setText(getString(R.string.Link_ManageGroup));
        txt2.setText("");

        img2.setImageResource(R.mipmap.group_item_arrow);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupManageActivity.startActivity(activity, groupKey);
            }
        });

    }

    @Override
    public void topSwitch(boolean top) {
        View view = findViewById(R.id.top);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Chat_Sticky_on_Top_chat));

        View topToggle = view.findViewById(R.id.toggle);
        topToggle.setSelected(top);
        topToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                int top = v.isSelected() ? 1 : 0;
                ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(groupKey);
                if (conversionEntity == null) {
                    conversionEntity = new ConversionEntity();
                    conversionEntity.setIdentifier(groupKey);
                }
                conversionEntity.setTop(top);
                ConversionHelper.getInstance().insertRoomEntity(conversionEntity);
            }
        });
    }

    @Override
    public void noticeSwitch(boolean notice) {
        View view = findViewById(R.id.mute);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Chat_Mute_Notification));

        View topToggle = view.findViewById(R.id.toggle);
        topToggle.setSelected(notice);
        topToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                boolean mutestate = v.isSelected();
                presenter.updateGroupMute(mutestate);
            }
        });
    }

    @Override
    public void commonSwtich(boolean common) {
        View view=findViewById(R.id.save);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Link_Save_to_Contacts));

        View topToggle = view.findViewById(R.id.toggle);
        topToggle.setSelected(common);
        topToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                boolean common = v.isSelected();
                presenter.updateGroupCommon(common);
            }
        });
    }

    @Override
    public void clearHistory() {
        View view=findViewById(R.id.clear);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setBackgroundResource(R.mipmap.message_clear_history2x);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Link_Clear_Chat_History));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> strings = new ArrayList();
                strings.add(getString(R.string.Link_Clear_Chat_History));
                DialogUtil.showBottomView(activity, strings, new DialogUtil.DialogListItemClickListener() {
                    @Override
                    public void confirm(int position) {
                        ConversionHelper.getInstance().deleteRoom(groupKey);
                        RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.CLEAR_HISTORY);
                    }
                });
            }
        });
    }

    @Override
    public void exitGroup() {
        View view=findViewById(R.id.delete);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setBackgroundResource( R.mipmap.message_group_leave2x);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(getResources().getString(R.string.Link_Delete_and_Leave));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.requestExitGroup();
            }
        });
    }
}