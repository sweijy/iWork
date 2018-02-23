package connect.activity.home.bean;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

public class HttpRecBean implements Serializable {

    public enum HttpRecType {
        SALTEXPIRE,//salt timeout
        SOUNDPOOL,//system voice
        SYSTEM_VIBRATION,//system vibrate
    }

    public HttpRecType httpRecType;
    public Object obj;


    public HttpRecBean(HttpRecType httpRecType, Object obj1) {
        this.httpRecType = httpRecType;
        this.obj = obj1;
    }

    public static void sendHttpRecMsg(HttpRecType recType, Object... objs) {
        EventBus.getDefault().post(new HttpRecBean(recType, objs));
    }
}
