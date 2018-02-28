package com.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.common.inter.PushCallBack;
import com.common.inter.PushListener;
import com.huawei.android.hms.agent.HuaweiRegister;
import com.jpush.JpushRegisger;
import com.mi.push.XiaomiRegister;
import com.utils.PlatformUtil;

/**
 * Created by PuJin on 2018/2/28.
 */

public class PushSDK {

    private static PushSDK pushSDK;

    private static String TAG = "_PushSDK";
    private Context context;
    private PushListener pushListener;

    private static int PLATFORM_HUAWEI = 0;
    private static int PLATFORM_XIAOMI = 1;
    private static int PLATFORM_J = 2;

    public static PushSDK getInstance() {
        if (pushSDK == null) {
            pushSDK = new PushSDK();
        }
        return pushSDK;
    }

    /**
     * Application onCreate() 调用
     *
     * @param context
     */
    public PushSDK register(Context context) {
        switch (PlatformUtil.getSystem()) {
            case 100:
                HuaweiRegister.getInstance().registerPush(context, new PushCallBack() {

                    @Override
                    public void platformToken(String token) {
                        Log.d(TAG, "huaweiDeveiToken: " + token);
                        pushListener.registerToken(PLATFORM_HUAWEI, token);
                    }

                    @Override
                    public void receiveMessage(String title, String content) {
                        pushListener.receiveMessage(title, content);
                    }
                });
                break;
            case 101:
                XiaomiRegister.getInstance().registerPush(context, new PushCallBack() {

                    @Override
                    public void platformToken(String registerid) {
                        Log.d(TAG, "xiaomiRisterId: " + registerid);
                        pushListener.registerToken(PLATFORM_XIAOMI, registerid);
                    }

                    @Override
                    public void receiveMessage(String title, String content) {
                        pushListener.receiveMessage(title, content);
                    }
                });
                break;
            case 102:
                JpushRegisger.getInstance().registerPush(context, new PushCallBack() {
                    @Override
                    public void platformToken(String registerid) {
                        Log.d(TAG, "jpushRegisterId: " + registerid);
                        pushListener.registerToken(PLATFORM_J, registerid);
                    }

                    @Override
                    public void receiveMessage(String title, String content) {
                        pushListener.receiveMessage(title, content);
                    }
                });
                break;
        }
        return this;
    }

    /**
     * 用户登陆成功后 调用
     *
     * @param activity
     */
    public void connect(Activity activity) {
        switch (PlatformUtil.getSystem()) {
            case 100:
                HuaweiRegister.getInstance().connectPush(activity);
                break;
            case 101:
                XiaomiRegister.getInstance().connectPush(activity);
                break;
            case 102:
                JpushRegisger.getInstance().connectPush(activity);
                break;
        }
    }

    /**
     * 用户退出账号 调用
     */
    public void deleteToken() {
        switch (PlatformUtil.getSystem()) {
            case 100:
                if (HuaweiRegister.isSurvival()) {
                    HuaweiRegister.getInstance().deleteToken();
                }
                break;
            case 101:
                if (XiaomiRegister.isSurvival()) {
                    XiaomiRegister.getInstance().deleteToken();
                }
                break;
            case 102:
                if (JpushRegisger.isSurvival()) {
                    JpushRegisger.getInstance().deleteToken();
                }
                break;
        }
    }

    public Context getContext() {
        return context;
    }

    public PushSDK setPushListener(PushListener pushListener) {
        this.pushListener = pushListener;
        return this;
    }
}
