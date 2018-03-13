package instant.bean;

/**
 * Created by gtq on 2016/11/30.
 */
public enum SocketACK {
    VERSION_CHANGE(new byte[]{0x00, (byte) 0x90}),//Version changes

    HAND_SHAKE_FIRST(new byte[]{0x01, 0x01}),//The first handshake messages
    HAND_SHAKE_SECOND(new byte[]{0x01, 0x02}),//The second handshake messages

    HEART_BREAK(new byte[]{0x02, 0x00}),//The heartbeat

    ACK_BACK_ONLINE(new byte[]{0x03, 0x01}),//Online message receipt
    ACK_BACK_OFFLINE(new byte[]{0x03, 0x02}),//Offline message receipt
    ACK_BACK_OFFLINEBATCH(new byte[]{0x03, 0x04}),//Batch receipt

    CONTACT_SYNC(new byte[]{0x04, 0x01}),//Sync contacts
    PULL_OFFLINE(new byte[]{0x04, 0x02}),//Pull the offline messages
    CONTACT_LOGIN(new byte[]{0x04, 0x04}),//The login
    CONTACT_LOGOUT(new byte[]{0x04, 0x05}),//exit
    UPLOAD_APPINFO(new byte[]{0x04, 0x06}),//Report the device version information
    DIFFERENT_DEVICE(new byte[]{0x04, 0x19}),//different devive login in

    ROBOT_CHAT(new byte[]{0x05, 0x00}),//Robot news
    SINGLE_CHAT(new byte[]{0x05, 0x01}),//The private chat
    GROUP_INVITE(new byte[]{0x05, 0x03}),//Invited into the group of
    GROUP_CHAT(new byte[]{0x05, 0x04}),//Group chat
    MSG_UNTOUCH(new byte[]{0x05, 0x05}),//Message inaccessible
    CHAT_SUBSCRIBE(new byte[]{0x05, 0x06}),//notice
    CHAT_NOTICE(new byte[]{0x05, 0x09});//notice

    byte[] order;

    SocketACK(byte[] order) {
        this.order = order;
    }

    public byte[] getOrder() {
        return order;
    }

    public boolean equals(byte[] temp) {
        return order[0] == temp[0] && order[1] == temp[1];
    }
}
