package instant.parser;

import java.nio.ByteBuffer;

import instant.parser.localreceiver.MessageLocalReceiver;
import protos.Connect;

/**
 * Created by pujin on 2017/4/19.
 */

public class ChatParser extends InterParse {

    private static String TAG = "_ChatParser";

    private byte ackByte;
    private Connect.MessagePost messagePost;

    public ChatParser(byte ackByte, Connect.MessagePost messagePost) {
        super(ackByte, ByteBuffer.wrap(messagePost.toByteArray()));
        this.ackByte = ackByte;
        this.messagePost = messagePost;
    }

    @Override
    public synchronized void msgParse() throws Exception {
        switch (ackByte) {
            case 0x01://private chat
            case 0x02://burn chat
                singleChat(messagePost);
                break;
            case 0x04://group chat
                groupChat(messagePost);
                break;
        }
    }

    public synchronized void singleChat(Connect.MessagePost msgpost) throws Exception {
        Connect.MessageData messageData = msgpost.getMsgData();
        MessageLocalReceiver.localReceiver.singleChat(messageData);
    }

    /**
     * group chat
     *
     * @param msgpost
     */
    protected synchronized void groupChat(Connect.MessagePost msgpost) {
        Connect.MessageData messageData = msgpost.getMsgData();
        Connect.ChatMessage chatMessage = messageData.getChatMsg();
        MessageLocalReceiver.localReceiver.groupChat(chatMessage);
    }
}
