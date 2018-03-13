package connect.instant.receiver;

import connect.activity.home.bean.HomeAction;
import connect.database.green.DaoHelper.MessageHelper;
import connect.instant.bean.ConnectState;
import connect.instant.model.CRobotChat;
import instant.bean.ChatMsgEntity;
import instant.bean.Session;
import instant.parser.inter.ConnectListener;
import instant.sender.model.RobotChat;
import instant.ui.InstantSdk;

/**
 * Created by Administrator on 2017/10/18.
 */

public class ConnectReceiver implements ConnectListener {

    private static String TAG = "_ConnectReceiver";

    private static ConnectReceiver receiver;

    public synchronized static ConnectReceiver getInstance() {
        if (receiver == null) {
            receiver = new ConnectReceiver();
        }
        return receiver;
    }

    @Override
    public void disConnect() {
        ConnectState.getInstance().sendEvent(ConnectState.ConnectType.DISCONN);
    }

    @Override
    public void connectSuccess() {
        ConnectState.getInstance().sendEvent(ConnectState.ConnectType.CONNECT);
    }

    @Override
    public void welcome() {
        String mypublickey = Session.getInstance().getConnectCookie().getUid();
        ChatMsgEntity msgEntity = RobotChat.getInstance().txtMsg(InstantSdk.getInstance().getBaseContext().getString(instant.R.string.Login_Welcome));
        msgEntity.setMessage_from(RobotChat.getInstance().nickName());
        msgEntity.setMessage_to(mypublickey);

        MessageHelper.getInstance().insertMsgExtEntity(msgEntity);
        CRobotChat.getInstance().updateRoomMsg(null, msgEntity.showContent(), msgEntity.getCreatetime(), -1, 1);
    }

    @Override
    public void exceptionConnect() {
        HomeAction.getInstance().sendEvent(HomeAction.HomeType.DELAY_EXIT);
    }
}
