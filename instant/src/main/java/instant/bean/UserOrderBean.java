package instant.bean;

import com.google.protobuf.ByteString;

import instant.parser.InterParse;
import instant.utils.TimeUtil;

/**
 * Created by pujin on 2017/5/16.
 */

public class UserOrderBean extends InterParse {

    @Override
    public void msgParse() throws Exception {

    }

    /**
     * login out
     */
    public void connectLogout() {
        String msgid = TimeUtil.timestampToMsgid();
        commandToIMTransfer(msgid, SocketACK.CONTACT_LOGOUT, ByteString.copyFrom(new byte[]{}));
    }
}
