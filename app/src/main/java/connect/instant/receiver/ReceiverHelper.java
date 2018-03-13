package connect.instant.receiver;

import android.content.Context;

import connect.activity.base.BaseApplication;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import instant.parser.localreceiver.CommandLocalReceiver;
import instant.parser.localreceiver.ConnectLocalReceiver;
import instant.parser.localreceiver.ExceptionLocalReceiver;
import instant.parser.localreceiver.MessageLocalReceiver;
import instant.parser.localreceiver.RobotLocalReceiver;
import instant.parser.localreceiver.UnreachableLocalReceiver;
import instant.ui.InstantSdk;
import instant.utils.log.LogManager;

/**
 * Created by Administrator on 2017/10/25.
 */

public class ReceiverHelper {

    private static String TAG = "_ReceiverHelper";

    public void initInstantSDK() {
        Context context = BaseApplication.getInstance().getBaseContext();

        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        int userLogin = SharedPreferenceUtil.getInstance().getUserLogin();
        InstantSdk.getInstance().registerUserInfo(
                context,
                userBean.getUid(),
                userBean.getToken(),
                userBean.getName(),
                userBean.getAvatar(),
                userLogin);

        SharedPreferenceUtil.getInstance().setUserLogin(1);

        try {
            ConnectLocalReceiver.receiver.registerConnect(ConnectReceiver.getInstance());
            CommandLocalReceiver.receiver.registerCommand(CommandReceiver.getInstance());
            RobotLocalReceiver.localReceiver.registerRobotListener(RobotReceiver.getInstance());
            UnreachableLocalReceiver.localReceiver.registerUnreachableListener(UnreachableReceiver.getInstance());
            MessageLocalReceiver.localReceiver.registerMessageListener(MessageReceiver.getInstance());
            ExceptionLocalReceiver.localReceiver.registerConnect(ExceptionReceiver.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.getLogger().d(TAG, e.getMessage());
        }
    }
}
