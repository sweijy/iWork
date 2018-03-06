package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.chat.SearchContentActivity;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.set.contract.PrivateSetContract;
import connect.activity.chat.set.presenter.PrivateSetPresenter;
import connect.activity.home.bean.ConversationAction;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.dialog.DialogUtil;
import connect.widget.TopToolBar;

/**
 * private chat setting
 * Created by gtq on 2016/11/22.
 */
@Route(path = "/iwork/chat/set/PrivateSetActivity")
public class PrivateSetActivity extends BaseActivity implements PrivateSetContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.linearlayout)
    LinearLayout linearlayout;

    @Autowired
    String uid;
    @Autowired
    String avatar;
    @Autowired
    String userName;

    private PrivateSetActivity activity;
    private static String TAG = "_PrivateSetActivity";

    private PrivateSetContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleset);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Chat_Private_Setting));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        new PrivateSetPresenter(this).start();
    }

    @Override
    public void setPresenter(PrivateSetContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public String getRoomKey() {
        return uid;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getName() {
        return userName;
    }

    @Override
    public void searchHistoryTxt() {
        View view = findViewById(R.id.privateset_searchhistory);

        TextView searchTxt = (TextView) view.findViewById(R.id.txt1);
        ImageView imageView = (ImageView) view.findViewById(R.id.img1);

        searchTxt.setText(getResources().getString(R.string.Chat_Search_Txt));
        imageView.setImageResource(R.mipmap.group_item_arrow);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchContentActivity.lunchActivity(activity, 3);
            }
        });
    }

    @Override
    public void switchTop(String name, boolean state) {
        View view = findViewById(R.id.top);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(name);

        View topToggle = view.findViewById(R.id.toggle);
        topToggle.setSelected(state);
        topToggle.setTag(name);
        topToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                int top = v.isSelected() ? 1 : 0;

                ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(uid);
                if (conversionEntity == null) {
                    conversionEntity = new ConversionEntity();
                    conversionEntity.setIdentifier(uid);
                }
                conversionEntity.setTop(top);
                ConversionHelper.getInstance().insertRoomEntity(conversionEntity);

                ConversationAction.conversationAction.sendEvent(ConversationAction.ConverType.LOAD_MESSAGE);
            }
        });
    }

    @Override
    public void switchDisturb(String name, boolean state) {
        View view = findViewById(R.id.mute);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(name);

        View topToggle = view.findViewById(R.id.toggle);
        topToggle.setSelected(state);
        topToggle.setTag(name);
        topToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                int disturb = v.isSelected() ? 1 : 0;

                ConversionSettingEntity settingEntity = ConversionSettingHelper.getInstance().loadSetEntity(uid);
                if (settingEntity == null) {
                    settingEntity = new ConversionSettingEntity();
                    settingEntity.setIdentifier(uid);
                }
                settingEntity.setDisturb(disturb);
                ConversionSettingHelper.getInstance().insertSetEntity(settingEntity);

                ConversationAction.conversationAction.sendEvent(ConversationAction.ConverType.LOAD_MESSAGE);
            }
        });
    }

    @Override
    public void clearMessage() {
        View view = findViewById(R.id.clear);
        String str = getResources().getString(R.string.Link_Clear_Chat_History);
        TextView txt = (TextView) view.findViewById(R.id.private_clear_history);
        txt.setText(str);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> strings = new ArrayList();
                strings.add(getString(R.string.Link_Clear_Chat_History));
                DialogUtil.showBottomView(activity, strings, new DialogUtil.DialogListItemClickListener() {
                    @Override
                    public void confirm(int position) {
                        ConversionHelper.getInstance().deleteRoom(uid);
                        RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.CLEAR_HISTORY, uid);
                    }
                });
            }
        });
    }

    @Override
    public void showContactInfo(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
        layout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = (String) v.getTag();
                if (TextUtils.isEmpty(uid)) {
                    ARouter.getInstance().build("/iwork/chat/set/GroupSelectActivity")
                            .withBoolean("isCreate", true)
                            .withSerializable("groupIdentify", uid)
                            .navigation();
                } else if (SharedPreferenceUtil.getInstance().getUser().getUid().equals(uid)) {
                    ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                            navigation();
                } else {
                    ARouter.getInstance().build("/iwork/contact/ContactInfoActivity")
                            .withString("uid",uid)
                            .navigation();
                }
            }
        });
    }
}
