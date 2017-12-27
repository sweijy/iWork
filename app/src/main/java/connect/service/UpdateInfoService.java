package connect.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import connect.activity.contact.bean.ContactNotice;
import connect.activity.home.bean.HttpRecBean;
import connect.activity.login.bean.CaPubBean;
import connect.activity.login.bean.UserBean;
import connect.activity.set.bean.PaySetBean;
import connect.activity.set.bean.PrivateSetBean;
import connect.activity.set.bean.SystemSetBean;
import connect.activity.wallet.bean.RateBean;
import connect.activity.wallet.bean.WalletSetBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.DaoHelper.ParamHelper;
import connect.database.green.DaoHelper.ParamManager;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ProtoBufUtil;
import connect.utils.RegularUtil;
import connect.utils.StringUtil;
import connect.utils.UriUtil;
import connect.utils.cryption.DecryptionUtil;
import connect.utils.cryption.EncryptionUtil;
import connect.utils.cryption.SupportKeyUril;
import connect.utils.data.RateDataUtil;
import connect.utils.log.LogManager;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.system.SystemDataUtil;
import connect.utils.system.SystemUtil;
import instant.utils.manager.FailMsgsManager;
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
        updatePreferencesInfo();
        return super.onStartCommand(intent, flags, startId);
    }

    public void initSoundPool() {
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(service, R.raw.instant_message, 1);
    }

    public void updatePreferencesInfo() {
        //get private set
        if (ParamManager.getInstance().getPrivateSet() == null) {
            HttpRecBean.sendHttpRecMsg(HttpRecBean.HttpRecType.PrivateSet, "");
        }
        if(ParamManager.getInstance().getSystemSet() == null){
            SystemSetBean.initSystemSet();
        }
        HttpRecBean.sendHttpRecMsg(HttpRecBean.HttpRecType.BlackList, "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onEventMainThread(HttpRecBean httpRec) {
        Object[] objects = null;
        if (httpRec.obj != null) {
            objects = (Object[]) httpRec.obj;
        }
        switch (httpRec.httpRecType) {
            case PrivateSet://private set
                requestPrivateInfo();
                break;
            case BlackList://black list
                requestBlackList();
                break;
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

    public void requestPrivateInfo() {
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.SETTING_PRIVACY_INFO, ByteString.copyFrom(new byte[]{}),
                new ResultCall<Connect.HttpResponse>() {
                    @Override
                    public void onResponse(Connect.HttpResponse response) {
                        try {
                            Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                            Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(imResponse.getCipherData());
                            if (structData != null) {
                                Connect.Preferences preferences = Connect.Preferences.parseFrom(structData.getPlainData());
                                PrivateSetBean privateSetBean = new PrivateSetBean();
                                privateSetBean.setPhoneFind(preferences.getPhoneNum());
                                privateSetBean.setRecommend(preferences.getRecommend());
                                ParamManager.getInstance().putPrivateSet(privateSetBean);
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Connect.HttpResponse response) {}
                });
    }

    private void requestBlackList() {
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNEXT_V1_BLACKLIST_LIST, ByteString.copyFrom(new byte[]{}),
                new ResultCall<Connect.HttpResponse>() {
                    @Override
                    public void onResponse(Connect.HttpResponse response) {
                        try {
                            Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                            Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(imResponse.getCipherData());
                            if(structData == null || structData.getPlainData() == null){
                                return;
                            }
                            Connect.UsersInfo usersInfo = Connect.UsersInfo.parseFrom(structData.getPlainData());
                            List<Connect.UserInfo> list = usersInfo.getUsersList();
                            for (Connect.UserInfo info : list) {
                                ContactHelper.getInstance().updataFriendBlack(info.getUid(),true);
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Connect.HttpResponse response) {}
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
