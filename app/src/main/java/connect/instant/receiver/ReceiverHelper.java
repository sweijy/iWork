package connect.instant.receiver;

import android.content.Context;

import connect.activity.base.BaseApplication;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import instant.parser.localreceiver.CommandLocalReceiver;
import instant.parser.localreceiver.ConnectLocalReceiver;
import instant.parser.localreceiver.MessageLocalReceiver;
import instant.parser.localreceiver.RobotLocalReceiver;
import instant.parser.localreceiver.TransactionLocalReceiver;
import instant.parser.localreceiver.UnreachableLocalReceiver;
import instant.ui.InstantSdk;

/**
 * Created by Administrator on 2017/10/25.
 */

public class ReceiverHelper {

    public void initInstantSDK() {
        Context context = BaseApplication.getInstance().getBaseContext();

        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        InstantSdk.instantSdk.registerUserInfo(context, userBean.getPubKey(), userBean.getPriKey());

        ConnectLocalReceiver.receiver.registerConnect(ConnectReceiver.receiver);
        CommandLocalReceiver.receiver.registerCommand(CommandReceiver.receiver);
        TransactionLocalReceiver.localReceiver.registerTransactionListener(TransactionReceiver.receiver);
        RobotLocalReceiver.localReceiver.registerRobotListener(RobotReceiver.receiver);
        UnreachableLocalReceiver.localReceiver.registerUnreachableListener(UnreachableReceiver.receiver);
        MessageLocalReceiver.localReceiver.registerMessageListener(MessageReceiver.receiver);
    }
}