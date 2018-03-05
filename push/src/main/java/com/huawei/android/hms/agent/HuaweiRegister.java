package com.huawei.android.hms.agent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.common.inter.InterRegister;
import com.common.inter.PushCallBack;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.huawei.hms.support.api.push.TokenResult;

/**
 * Created by PuJin on 2018/2/28.
 */
public class HuaweiRegister implements InterRegister {

    private static String TAG = "_HuaweiRegister";

    private static HuaweiRegister register;
    private String token;

    public static HuaweiRegister getInstance(){
        if(register ==null){
            register=new HuaweiRegister();
        }
        return register;
    }

    private Context context;
    private PushCallBack callBack;

    @Override
    public HuaweiRegister registerPush(Context context,PushCallBack callBack) {
        this.context = context;
        this.callBack =  callBack;
        HMSAgent.init((Application) context);
        return this;
    }

    @Override
    public void connectPush(Activity activity) {
        // 调用华为Connect
        HMSAgent.connect(activity, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Log.d(TAG, "HMS connect end:" + rst);
            }
        });

        // 申请token
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rtnCode, TokenResult tokenResult) {
                Log.d(TAG, "HMS Push getToken:");
            }
        });
    }

    @Override
    public void deleteToken() {
        HMSAgent.Push.deleteToken(token);
    }

    public PushCallBack getCallBack() {
        return callBack;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static boolean isSurvival() {
        return register != null;
    }
}
