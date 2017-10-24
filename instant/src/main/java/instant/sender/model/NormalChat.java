package instant.sender.model;
import instant.R;
import instant.bean.ChatMsgEntity;
import instant.bean.MessageType;
import instant.bean.Session;
import instant.bean.UserCookie;
import instant.ui.InstantSdk;
import instant.utils.cryption.SupportKeyUril;
import protos.Connect;

/**
 * public methods to extract
 * Created by gtq on 2016/12/19.
 */
public abstract class NormalChat extends BaseChat {

    @Override
    public ChatMsgEntity txtMsg(String string) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Text);
        Connect.TextMessage.Builder builder = Connect.TextMessage.newBuilder()
                .setContent(string);

        if (chatType() == Connect.ChatType.PRIVATE_VALUE) {
            long destructtime = destructReceipt();
            if (destructtime > 0) {
                builder.setSnapTime(destructtime);
            }
        }
        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity photoMsg(String thum, String url, String filesize, int width, int height) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Photo);
        Connect.PhotoMessage.Builder builder = Connect.PhotoMessage.newBuilder()
                .setThum(thum)
                .setUrl(url)
                .setSize(filesize)
                .setImageWidth(width)
                .setImageHeight(height);

        if (chatType() == Connect.ChatType.PRIVATE_VALUE) {
            long destructtime = destructReceipt();
            if (destructtime > 0) {
                builder.setSnapTime(destructtime);
            }
        }
        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity voiceMsg(String string, int length) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Voice);
        Connect.VoiceMessage.Builder builder = Connect.VoiceMessage.newBuilder()
                .setUrl(string)
                .setTimeLength(length);

        if (chatType() == Connect.ChatType.PRIVATE_VALUE) {
            long destructtime = destructReceipt();
            if (destructtime > 0) {
                builder.setSnapTime(destructtime);
            }
        }
        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity videoMsg(String thum, String url, int length, int filesize, int width, int height) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Video);
        Connect.VideoMessage.Builder builder = Connect.VideoMessage.newBuilder()
                .setCover(thum)
                .setUrl(url)
                .setTimeLength(length)
                .setSize(filesize)
                .setImageWidth(width)
                .setImageHeight(height);

        if (chatType() == Connect.ChatType.PRIVATE_VALUE) {
            long destructtime = destructReceipt();
            if (destructtime > 0) {
                builder.setSnapTime(destructtime);
            }
        }
        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity emotionMsg(String string) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Emotion);
        Connect.EmotionMessage.Builder builder = Connect.EmotionMessage.newBuilder()
                .setContent(string);

        if (chatType() == Connect.ChatType.PRIVATE_VALUE) {
            long destructtime = destructReceipt();
            if (destructtime > 0) {
                builder.setSnapTime(destructtime);
            }
        }
        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity cardMsg(String pubkey, String name, String avatar) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Name_Card);
        Connect.CardMessage.Builder builder = Connect.CardMessage.newBuilder()
                .setUid(pubkey)
                .setUsername(name)
                .setAvatar(avatar);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity destructMsg(int time) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Self_destruct_Notice);
        Connect.DestructMessage.Builder builder = Connect.DestructMessage.newBuilder()
                .setTime(time);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity receiptMsg(String messageid) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Self_destruct_Receipt);
        Connect.ReadReceiptMessage.Builder builder = Connect.ReadReceiptMessage.newBuilder()
                .setMessageId(messageid);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity paymentMsg(int paymenttype, String hashid, long amount, int membersize, String tips) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Request_Payment);
        Connect.PaymentMessage.Builder builder = Connect.PaymentMessage.newBuilder()
                .setPaymentType(paymenttype)
                .setHashId(hashid)
                .setAmount(amount)
                .setMemberSize(membersize)
                .setTips(tips);

        msgExtEntity.setContents(builder.build().toByteArray());
        msgExtEntity.setCrowdCount(membersize);
        msgExtEntity.setPayCount(0);
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity transferMsg(int type, String hashid, long amout, String tips) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Transfer);
        Connect.TransferMessage.Builder builder = Connect.TransferMessage.newBuilder()
                .setTransferType(type)
                .setHashId(hashid)
                .setAmount(amout)
                .setTips(tips);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity locationMsg(float latitude, float longitude, String address, String thum, int width, int height) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Location);
        Connect.LocationMessage.Builder builder = Connect.LocationMessage.newBuilder()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setAddress(address)
                .setScreenShot(thum)
                .setImageWidth(width)
                .setImageHeight(height);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity luckPacketMsg(int type, String hashid, long amount, String tips) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.Lucky_Packet);
        Connect.LuckPacketMessage.Builder builder = Connect.LuckPacketMessage.newBuilder()
                .setLuckyType(type)
                .setHashId(hashid)
                .setAmount(amount)
                .setTips(tips);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity noticeMsg(int noticeType, String content, String ext) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.NOTICE);
        Connect.NotifyMessage notifyMessage = Connect.NotifyMessage.newBuilder()
                .setNotifyType(noticeType)
                .setContent(content)
                .setExtion(ext)
                .build();

        msgExtEntity.setContents(notifyMessage.toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity outerWebsiteMsg(String url, String title, String subtitle, String img) {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.OUTER_WEBSITE);
        Connect.WebsiteMessage.Builder builder = Connect.WebsiteMessage.newBuilder()
                .setUrl(url)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setImg(img);

        msgExtEntity.setContents(builder.build().toByteArray());
        return msgExtEntity;
    }

    @Override
    public ChatMsgEntity encryptChatMsg() {
        ChatMsgEntity msgExtEntity = (ChatMsgEntity) createBaseChat(MessageType.NOTICE_ENCRYPTCHAT);
        Connect.NotifyMessage notifyMessage = Connect.NotifyMessage.newBuilder()
                .setNotifyType(0)
                .setContent(InstantSdk.instantSdk.getBaseContext().getString(R.string.Chat_You_are_start_encrypt_chat_Messages_encrypted))
                .build();

        msgExtEntity.setContents(notifyMessage.toByteArray());
        return msgExtEntity;
    }

    public Connect.MessagePost normalChatMessage(Connect.MessageData data) {
        UserCookie connectCookie = Session.getInstance().getUserCookie(Session.CONNECT_USER);
        String publicKey=connectCookie.getPubKey();
        String priKey = connectCookie.getPriKey();

        //messagePost
        String postsign = SupportKeyUril.signHash(priKey, data.toByteArray());
        return Connect.MessagePost.newBuilder().
                setMsgData(data).setSign(postsign).
                setPubKey(publicKey).
                build();
    }

    public abstract long destructReceipt();
}