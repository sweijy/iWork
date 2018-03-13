package instant.parser;

import android.text.TextUtils;

import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import instant.bean.Session;
import instant.parser.localreceiver.CommandLocalReceiver;
import instant.parser.localreceiver.ConnectLocalReceiver;
import instant.utils.SharedUtil;
import instant.utils.log.LogManager;
import protos.Connect;

/**
 * order message
 * Created by pujin on 2017/4/18.
 */
public class CommandParser extends InterParse {

    private static String TAG = "_CommandParser";

    public CommandParser(byte ackByte, ByteBuffer byteBuffer) {
        super(ackByte, byteBuffer);
    }

    @Override
    public synchronized void msgParse() throws Exception {
        if (ackByte == 0x02) {
            receiveOffLineMsgs(byteBuffer);
        } else {
            Connect.Command command = imTransferToCommand(byteBuffer);
            String msgid = command.getMsgId();

            switch (ackByte) {
                case 0x01:
                case 0x03:
                case 0x04:
                case 0x05:
                case 0x06:
                case 0x0c:
                    break;
                default:
                    backOnLineAck(4, msgid);
                    break;
            }

            switch (ackByte) {
                case 0x01://contact list
                    syncContacts(command.getDetail());
                    break;
                case 0x06://bind servicetoken
                    break;
                case 0x0c://conversation mute notify
                    conversationMute(command.getDetail());
                    break;
                case 0x0d://modify group information
                    updateGroupInfo(command.getDetail(), msgid, command.getErrNo());
                    break;
            }
        }
    }

    /**
     * Batch processing offline messages
     *
     * @param buffer
     * @throws Exception
     */
    private void receiveOffLineMsgs(ByteBuffer buffer) throws Exception {
        Connect.StructData structData = imTransferToStructData(buffer);
        byte[] unGzip = unGZip(structData.getPlainData().toByteArray());
        //Whether offline news has been exhausted
        boolean offComplete = false;
        if (unGzip.length == 0 || unGzip.length < 20) {
            offComplete = true;
        } else {
            List<Connect.Ack> ackList = new ArrayList<>();
            try {
                Connect.OfflineMsgs offlineMsgs = Connect.OfflineMsgs.parseFrom(unGzip);
                List<Connect.OfflineMsg> msgList = offlineMsgs.getOfflineMsgsList();

                for (Connect.OfflineMsg offlineMsg : msgList) {

                    String messageId = offlineMsg.getMsgId();
                    Connect.ProducerMsgDetail msgDetail = offlineMsg.getBody();
                    int extension = msgDetail.getExt();

                    LogManager.getLogger().d(TAG, "messageId:" + messageId);
                    Connect.Ack ack = Connect.Ack.newBuilder()
                            .setType(msgDetail.getType())
                            .setMsgId(messageId)
                            .build();
                    ackList.add(ack);

                    switch ((byte) msgDetail.getType()) {
                        case 0x04://Offline command processing
                            Connect.Command command = Connect.Command.parseFrom(msgDetail.getData());
                            ByteString transferDataByte = command.getDetail();

                            int errorNumber = command.getErrNo();
                            switch (extension) {
                                case 0x01://contact list
                                    syncContacts(transferDataByte);
                                    break;
                                case 0x0d://modify group information
                                    updateGroupInfo(transferDataByte, messageId, errorNumber);
                                    break;
                            }
                            break;
                        case 0x05://Offline notification
                            InterParse interParse = new MessageParser((byte) extension, ByteBuffer.wrap(msgDetail.getData().toByteArray()), 0);
                            interParse.msgParse();
                            break;
                    }
                }

                offComplete = offlineMsgs.getCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                offComplete = true;
            } finally {
                backOffLineAcks(ackList);
            }
        }

        if (offComplete) {
            ConnectLocalReceiver.receiver.connectSuccess();

            String pubKey = Session.getInstance().getConnectCookie().getUid();
            Session.getInstance().setUpFailTime(pubKey, 0);
        }
    }

    /**
     * GZip decompression
     *
     * @param data
     * @return
     */
    private byte[] unGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * Sync contacts list
     *
     * @param buffer
     * @throws Exception
     */
    private void syncContacts(ByteString buffer) throws Exception {
        String version = SharedUtil.getInstance().getStringValue(SharedUtil.CONTACTS_VERSION);
        if (TextUtils.isEmpty(version)) {
            Connect.WorkmatesVersion  workmatesVersion = Connect.WorkmatesVersion.parseFrom(buffer);
            version = workmatesVersion.getVersion();
            CommandLocalReceiver.receiver.loadAllContacts(workmatesVersion.getListList());
        } else {
            Connect.WorkmateChangeRecords changeRecords = Connect.WorkmateChangeRecords.parseFrom(buffer);
            version = changeRecords.getVersion();
            CommandLocalReceiver.receiver.contactChanges(changeRecords);
        }
        SharedUtil.getInstance().putValue(SharedUtil.CONTACTS_VERSION, version);
    }

    /**
     * Conversation mute
     *
     * @param buffer
     */
    private void conversationMute(ByteString buffer) throws Exception {
        Connect.ManageSession manageSession = Connect.ManageSession.parseFrom(buffer);
        CommandLocalReceiver.receiver.conversationMute(manageSession);
    }

    /**
     * Group of information change
     *
     * @param buffer
     */
    private void updateGroupInfo(ByteString buffer, Object... objs) throws Exception {
        Connect.GroupChange groupChange = Connect.GroupChange.parseFrom(buffer);
        CommandLocalReceiver.receiver.updateGroupChange(groupChange);
    }
}
