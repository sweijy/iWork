package com.common.inter;

/**
 * SDK 对外提供的访问接口
 * Created by PuJin on 2018/2/28.
 */
public interface PushListener {

    /**
     * 当前设备平台
     * @param deviceplatform
     * @param registerid
     */
    void registerToken(int deviceplatform,String registerid);

    /**
     * 接受到的推送消息
     * @param title
     * @param content
     */
    void receiveMessage(String title ,String content);
}
