package com.jpush;

import android.app.Activity;
import android.content.Context;

import com.common.inter.InterRegister;
import com.common.inter.PushCallBack;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by PuJin on 2018/2/28.
 */

public class JpushRegisger implements InterRegister {

    private static String TAG = "_JpushRegisger";

    private static JpushRegisger regisger;

    public static JpushRegisger getInstance() {
        if (regisger == null) {
            regisger = new JpushRegisger();
        }
        return regisger;
    }

    private Context context;
    private PushCallBack callBack;

    @Override
    public JpushRegisger registerPush(Context context, PushCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
        String registerId = JPushInterface.getRegistrationID(context);
        callBack.platformToken(registerId);
        return this;
    }

    @Override
    public void connectPush(Activity activity) {

    }

    @Override
    public void deleteToken() {
        JPushInterface.stopPush(context);
    }

    public PushCallBack getCallBack() {
        return callBack;
    }

    public static boolean isSurvival() {
        return regisger != null;
    }
}
