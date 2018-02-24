package connect.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import connect.activity.home.bean.HttpRecBean;
import connect.activity.set.bean.SystemSetBean;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ParamManager;
import connect.ui.activity.R;
import connect.utils.UriUtil;
import connect.utils.log.LogManager;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.system.SystemUtil;
import protos.Connect;

/**
 * Background processing HTTP requests
 */
public class UpdateInfoService extends Service {

    private String Tag = "HttpService";
    private UpdateInfoService service;
    private SoundPool soundPool = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        EventBus.getDefault().register(this);
    }

    public static void startService(Activity activity) {
        Intent intent = new Intent(activity, UpdateInfoService.class);
        activity.startService(intent);
    }

    public static void stopServer(Context context) {
        Intent intent = new Intent(context, UpdateInfoService.class);
        context.stopService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogManager.getLogger().d(Tag, "***  onStartCommand start  ***");
        initSoundPool();
        if(ParamManager.getInstance().getSystemSet() == null){
            SystemSetBean.initSystemSet();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void initSoundPool() {
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(service, R.raw.instant_message, 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onEventMainThread(HttpRecBean httpRec) {
        Object[] objects = null;
        if (httpRec.obj != null) {
            objects = (Object[]) httpRec.obj;
        }
        switch (httpRec.httpRecType) {
            case SOUNDPOOL:
                if ((Integer) objects[0] == 0) {
                    SystemUtil.noticeVoice(service);
                } else {
                    if (soundPool != null) {
                        soundPool.play(1, 7, 7, 0, 0, 1);
                    }
                }
                break;
            case SYSTEM_VIBRATION:
                SystemUtil.noticeVibrate(service);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
