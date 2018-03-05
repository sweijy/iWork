package com.huawei.android.hms.agent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by PuJin on 2018/2/27.
 */

public class HuaweiPushReceiver extends PushReceiver {

    private static String TAG = "_HuaweiPushReceiver";

    /**
     * 接收token返回
     */
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        Log.d(TAG, "onToken: " + token);
        HuaweiRegister.getInstance().setToken(token);
        HuaweiRegister.getInstance().getCallBack().platformToken(token);
    }

    /**
     * 透传消息
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        Log.d(TAG, "onPushMsg: ");
        try {
            String message = new String(msg, "UTF-8");// {"title":"DOLOR IMPEDIT SOLUTA","content":"odit unde"}
            JSONObject object = new JSONObject(message);
            String title = object.getString("title");
            String content = object.getString("content");
            HuaweiRegister.getInstance().getCallBack().receiveMessage(title, content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 通知栏点击事件处理
     */
    public void onEvent(Context context, Event event, Bundle extras) {
        Log.d(TAG, "onEvent: ");
    }

    /**
     * push连接状态
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        Log.d(TAG, "onPushState: ");
    }
}
