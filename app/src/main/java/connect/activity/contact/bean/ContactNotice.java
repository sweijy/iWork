package connect.activity.contact.bean;

import org.greenrobot.eventbus.EventBus;

public class ContactNotice {

    public enum ConNotice{
        RecContact,
        RecGroup,
    }

    private ConNotice notice;

    public ContactNotice(ConNotice notice) {
        this.notice = notice;
    }

    public ConNotice getNotice(){
        return this.notice;
    }

    public static void receiverGroup() {
        EventBus.getDefault().post(new ContactNotice(ConNotice.RecGroup));
    }

    public static void receiverContact() {
        EventBus.getDefault().post(new ContactNotice(ConNotice.RecContact));
    }

}
