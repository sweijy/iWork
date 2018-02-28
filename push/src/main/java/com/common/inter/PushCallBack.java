package com.common.inter;

/**
 * Created by PuJin on 2018/2/27.
 */

public interface PushCallBack {

    void platformToken(String token);

    void receiveMessage(String title ,String content);
}
