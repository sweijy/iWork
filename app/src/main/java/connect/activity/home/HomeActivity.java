package connect.activity.home;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.PushSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseApplication;
import connect.activity.base.BaseFragmentActivity;
import connect.activity.base.BaseListener;
import connect.activity.contact.bean.MsgSendBean;
import connect.activity.home.bean.HomeAction;
import connect.activity.home.bean.MsgNoticeBean;
import connect.activity.home.fragment.ConversationFragment;
import connect.activity.home.fragment.DepartmentFragment;
import connect.activity.home.fragment.SetFragment;
import connect.activity.home.fragment.WorkbenchFragment;
import connect.activity.home.view.CheckUpdate;
import connect.activity.set.bean.SystemSetBean;
import connect.database.green.DaoHelper.ParamManager;
import connect.database.green.DaoManager;
import connect.instant.bean.ConnectState;
import connect.service.GroupService;
import connect.service.UpdateInfoService;
import connect.ui.activity.R;
import connect.utils.log.LogManager;
import connect.utils.permission.PermissionUtil;
import connect.utils.scan.ResolveUrlUtil;
import connect.widget.badge.BadgeView;
import instant.bean.UserOrderBean;
import instant.utils.manager.FailMsgsManager;
import protos.Connect;

@Route(path = "/iwork/HomeActivity")
public class HomeActivity extends BaseFragmentActivity {

    @Bind(R.id.msg)
    ImageView msg;
    @Bind(R.id.contact)
    ImageView contact;
    @Bind(R.id.set)
    ImageView set;
    @Bind(R.id.workbench)
    ImageView workbench;
    @Bind(R.id.msg_rela)
    RelativeLayout msgRela;
    @Bind(R.id.contact_rela)
    RelativeLayout contactRela;
    @Bind(R.id.set_rela)
    RelativeLayout setRela;
    @Bind(R.id.workbench_rela)
    RelativeLayout workbenchRela;
    @Bind(R.id.badgetv)
    BadgeView badgetv;
    @Bind(R.id.contact_badgetv)
    BadgeView contactBadgetv;

    private static String TAG = "Tag_HomeActivity";
    @Bind(R.id.msg_text)
    TextView msgText;
    @Bind(R.id.contact_text)
    TextView contactText;
    @Bind(R.id.workbench_text)
    TextView workbenchText;
    @Bind(R.id.set_text)
    TextView setText;

    @Autowired
    int category;
    @Autowired
    Object[] objects;

    private HomeActivity activity;

    private ConversationFragment chatListFragment;
    private DepartmentFragment departmentFragment;
    private WorkbenchFragment workbenchFragment;
    private SetFragment setFragment;
    private ResolveUrlUtil resolveUrlUtil;
    private CheckUpdate checkUpdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_home);
        ButterKnife.bind(this);
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        activity = this;
        setDefaultFragment();
        PushSDK.getInstance().connect(activity);

        DaoManager.getInstance().testDaoMaster(new BaseListener<String>() {
            @Override
            public void Success(String ts) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        BaseApplication.getInstance().initRegisterAccount();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        LogManager.getLogger().d(TAG, "onPostExecute");
                        UpdateInfoService.startService(activity);
                        GroupService.startService(activity);

                        ConnectState.getInstance().sendEvent(ConnectState.ConnectType.CONNECT);
                        requestAppUpdata();
                    }
                }.execute();
            }

            @Override
            public void fail(Object... objects) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int category = bundle.getInt("CATEGORY");
            Object[] objs = (Object[]) bundle.getSerializable("SERIALIZE");
            if (category == 100) {
                int talType = (int) objs[0];
                String talkKey = (String) objs[1];
                ARouter.getInstance().build("/chat/ChatActivity")
                        .withSerializable("CHAT_TYPE",  Connect.ChatType.forNumber(talType))
                        .withString("CHAT_IDENTIFY", talkKey)
                        .navigation();
            }
        }
    }

    private static final int TIMEOUT_DELAYEXIT = 120;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMEOUT_DELAYEXIT:
                    HomeAction.getInstance().sendEvent(HomeAction.HomeType.EXIT);
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onEventMainThread(HomeAction action) {
        Object[] objects = null;
        if (action.getObject() != null) {
            objects = (Object[]) action.getObject();
        }

        switch (action.getType()) {
            case DELAY_EXIT://Timeout logged out
                FailMsgsManager.getInstance().removeAllFailMsg();
                UserOrderBean userOrderBean = new UserOrderBean();
                userOrderBean.connectLogout();

                mHandler.sendEmptyMessageDelayed(TIMEOUT_DELAYEXIT, 1000);
                break;
            case EXIT:
                UserOrderBean orderBean = new UserOrderBean();
                orderBean.connectLogout();

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1001);
                PushSDK.getInstance().deleteToken();

                mHandler.removeMessages(TIMEOUT_DELAYEXIT);
                BaseApplication.getInstance().exitRegisterAccount();
                List<Activity> list = BaseApplication.getInstance().getActivityList();
                for (Activity activity1 : list) {
                    if (!activity1.getClass().getName().equals(activity.getClass().getName())) {
                        activity1.finish();
                    }
                }
                ARouter.getInstance().build("/iwork/login/LoginUserActivity")
                        .navigation(activity, new NavCallback() {
                            @Override
                            public void onArrival(Postcard postcard) {
                                activity.finish();
                            }
                        });
                break;
            case SWITCHFRAGMENT:
                int fragmentCode = (Integer) objects[0];
                switchFragment(fragmentCode);
                break;
            case GROUP_NEWCHAT:
                int position = (int) (objects[0]);
                if(position == 1){
                    ARouter.getInstance().build("/iwork/chat/set/GroupSelectActivity")
                            .withBoolean("isCreate", true)
                            .withSerializable("groupIdentify", "")
                            .navigation();
                }else if(position == 2){
                    ARouter.getInstance().build("/iwork/contact/ScanAddFriendActivity")
                            .navigation();
                }else if(position == 3){
                    SystemSetBean systemSetBean = ParamManager.getInstance().getSystemSet();
                    boolean isVibrate = systemSetBean.isVibrate();
                    boolean isRing = systemSetBean.isRing();
                    if (isVibrate && isRing) {
                        systemSetBean.setVibrate(false);
                        systemSetBean.setRing(false);
                    } else {
                        systemSetBean.setVibrate(true);
                        systemSetBean.setRing(true);
                    }
                    ParamManager.getInstance().putSystemSet(systemSetBean);
                }else if(position == 4){
                    ARouter.getInstance().build("/iwork/set/SupportFeedbackActivity").
                            navigation();
                }
                break;
            case REMOTE_LOGIN:
                String deviceName = (String) objects[0];
                orderBean = new UserOrderBean();
                orderBean.connectLogout();

                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1001);

                mHandler.removeMessages(TIMEOUT_DELAYEXIT);
                BaseApplication.getInstance().exitRegisterAccount();
                list = BaseApplication.getInstance().getActivityList();
                for (Activity activity1 : list) {
                    if (!activity1.getClass().getName().equals(activity.getClass().getName())) {
                        activity1.finish();
                    }
                }

                ARouter.getInstance().build("/iwork/login/LoginUserActivity")
                        .withString("value",deviceName)
                        .navigation();
                finish();
                break;
            case TO_CHAT:
                Connect.ChatType chatType = (Connect.ChatType) objects[0];
                String groupKey = (String) objects[1];
                ARouter.getInstance().build("/chat/ChatActivity")
                        .withSerializable("CHAT_TYPE", chatType)
                        .withString("CHAT_IDENTIFY", groupKey)
                        .navigation();
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgNoticeBean notice) {
        Object[] objs = null;
        if (notice.object != null) {
            objs = (Object[]) notice.object;
        }
        if (objs[0] instanceof MsgSendBean) {
            resolveUrlUtil.showMsgTip(notice, ResolveUrlUtil.TYPE_OPEN_WEB, false);
        }
    }

    @OnClick({R.id.msg_rela, R.id.contact_rela, R.id.workbench_rela, R.id.set_rela})
    public void OnClickListener(View view) {
        initBottomTab();
        switch (view.getId()) {
            case R.id.msg_rela:
                switchFragment(0);
                msg.setSelected(true);
                msgText.setSelected(true);
                break;
            case R.id.contact_rela:
                switchFragment(1);
                contact.setSelected(true);
                contactText.setSelected(true);
                break;
            case R.id.workbench_rela:
                switchFragment(2);
                workbench.setSelected(true);
                workbenchText.setSelected(true);
                break;
            case R.id.set_rela:
                switchFragment(3);
                set.setSelected(true);
                setText.setSelected(true);
                break;
        }
    }

    private void initBottomTab() {
        msg.setSelected(false);
        contact.setSelected(false);
        workbench.setSelected(false);
        set.setSelected(false);

        msgText.setSelected(false);
        contactText.setSelected(false);
        workbenchText.setSelected(false);
        setText.setSelected(false);
    }

    public void setDefaultFragment() {
        chatListFragment = ConversationFragment.startFragment();
        departmentFragment = DepartmentFragment.startFragment();
        workbenchFragment = WorkbenchFragment.startFragment();
        setFragment = SetFragment.startFragment();

        switchFragment(0);
        msg.setSelected(true);
    }

    public void switchFragment(int code) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment.isVisible()) {
                    fragmentTransaction.hide(fragment);
                }
            }
        }

        switch (code) {
            case 0:
                if (!chatListFragment.isAdded()) {
                    fragmentTransaction.add(R.id.home_content, chatListFragment);
                } else {
                    fragmentTransaction.show(chatListFragment);
                }
                break;
            case 1:
                if (!departmentFragment.isAdded()) {
                    fragmentTransaction.add(R.id.home_content, departmentFragment);
                } else {
                    fragmentTransaction.show(departmentFragment);
                }
                break;
            case 2:
                if (!workbenchFragment.isAdded()) {
                    fragmentTransaction.add(R.id.home_content, workbenchFragment);
                } else {
                    fragmentTransaction.show(workbenchFragment);
                }
                break;
            case 3:
                if (!setFragment.isAdded()) {
                    fragmentTransaction.add(R.id.home_content, setFragment);
                } else {
                    fragmentTransaction.show(setFragment);
                }
                break;
        }

        //commit :IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void setFragmentDot(int pager, int count) {
        switch (pager) {
            case 0:
                if (badgetv != null) {
                    badgetv.setBadgeCount(0, count);
                }
                break;
            case 1:
                if (contactBadgetv != null) {
                    contactBadgetv.setBadgeCount(0, count);
                }
                break;
        }
    }

    private void requestAppUpdata() {
        checkUpdata = new CheckUpdate(activity);
        checkUpdata.check();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK://点击back  实现Home键功能
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(activity, requestCode, permissions, grantResults, checkUpdata.permissomCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
