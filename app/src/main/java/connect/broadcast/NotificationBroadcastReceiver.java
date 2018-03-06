package connect.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;

import connect.activity.base.BaseApplication;
import connect.activity.chat.ChatActivity;
import connect.activity.home.HomeActivity;
import connect.utils.log.LogManager;
import protos.Connect;

/**
 * Click on the notification bar to determine whether the private key is available
 * Created by pujin on 2017/4/17.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = "_NotificationBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int notifytype = intent.getIntExtra("NOTIFY_TYPE", 0);
        if (notifytype == 0) {
            LogManager.getLogger().d(TAG, "TO START ACTIVITY");
            intent = new Intent(context, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            if (BaseApplication.getInstance().isEmptyActivity()) {
                LogManager.getLogger().d(TAG, "TO START ACTIVITY");
                ARouter.getInstance().build("/iwork/login/StartPageActivity")
                        .navigation();
            } else {
                LogManager.getLogger().d(TAG, "TO CHAT ACTIVITY");
                int chattype = intent.getIntExtra("ROOM_TYPE", 0);
                String identify = intent.getStringExtra("ROOM_IDENTIFY");

                intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("CHAT_TYPE", Connect.ChatType.forNumber(chattype));
                intent.putExtra("CHAT_IDENTIFY", identify);
                context.startActivity(intent);
            }
        }
    }
}
