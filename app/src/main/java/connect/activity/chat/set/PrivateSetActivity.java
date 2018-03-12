package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
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

    private static String TAG = "_PrivateSetActivity";
    private PrivateSetActivity activity;

    private PrivateSetContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleset);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
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
    public String getUid() {
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
    public void switchTop(String name, boolean state) {
        View view = findViewById(R.id.top);
        TextView txt = (TextView) view.findViewById(R.id.txt);
        txt.setText(name);

        Switch topToggle = (Switch) view.findViewById(R.id.toggle);
        topToggle.setSelected(state);
        topToggle.setTag(name);topToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int top = b ? 1 : 0;
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

        Switch topToggle = (Switch) view.findViewById(R.id.toggle);
        topToggle.setSelected(state);
        topToggle.setTag(name);
        topToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int disturb = b ? 1 : 0;

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
    public void showContactInfo(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
        layout.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                if (TextUtils.isEmpty(tag)) {
                    ARouter.getInstance().build("/iwork/chat/set/GroupSelectActivity")
                            .withBoolean("isCreate", true)
                            .withSerializable("idnetify", uid)
                            .navigation();
                } else if (SharedPreferenceUtil.getInstance().getUser().getUid().equals(tag)) {
                    ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                            navigation();
                } else {
                    ARouter.getInstance().build("/iwork/contact/ContactInfoActivity")
                            .withString("uid",tag)
                            .navigation();
                }
            }
        });
    }
}
