package connect.activity.chat.bean;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

/**
 * Send chat message
 * Created by gtq on 2016/11/26.
 */
public class MsgSend implements Serializable {

    private MsgSendType msgSendType;
    private Object obj;

    public MsgSend(MsgSendType msgSendType, Object obj) {
        this.msgSendType = msgSendType;
        this.obj = obj;
    }

    public enum MsgSendType {
        NOTICE,
        Text,
        Voice,
        Photo,
        Video,
        Emotion,
        Location,
        Name_Card,
        OUTER_WEBSITE,
        SYSTEM_AD,
    }

    public static void sendOuterMsg(MsgSendType type, Object... objs) {
        EventBus.getDefault().post(new MsgSend(type, objs));
    }

    public MsgSendType getMsgType() {
        return msgSendType;
    }

    public Object getObj() {
        return obj;
    }
}