package com.mi.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.common.inter.InterRegister;
import com.common.inter.PushCallBack;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by PuJin on 2018/2/27.
 */
public class XiaomiRegister implements InterRegister {

    private String TAG = "_XiaomiRegister";
    private String APP_ID = "2882303761517721816";
    private String APP_KEY = "5531772137816";

    private Context context;
    private static XiaomiRegister register;
    private PushCallBack callBack;

    public synchronized static XiaomiRegister getInstance() {
        if (register == null) {
            register = new XiaomiRegister();
        }
        return register;
    }

    private boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public XiaomiRegister registerPush(Context context, PushCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        //初始化push推送服务
        if (shouldInit(context)) {
            MiPushClient.registerPush(context, APP_ID, APP_KEY);
        }

        //打开Log
        Logger.setLogger(context, new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        });
        return this;
    }

    @Override
    public void connectPush(Activity activity) {

    }

    @Override
    public void deleteToken() {
        MiPushClient.disablePush(context);
    }

    public PushCallBack getCallBack() {
        return callBack;
    }

    public static boolean isSurvival() {
        return register != null;
    }
}
