package com.common.inter;

import android.app.Activity;
import android.content.Context;

/**
 * 每个具体的推送平台实现
 * Created by PuJin on 2018/2/28.
 */

public interface InterRegister<T> {

    /**
     * 初始化
     * @param context
     * @return
     */
    public T registerPush(Context context, PushCallBack callBack);

    /**
     * 发起连接 获取token
     * @param activity
     */
    public void connectPush(Activity activity);

    /**
     * 关闭推送
     */
    public void deleteToken();
}
