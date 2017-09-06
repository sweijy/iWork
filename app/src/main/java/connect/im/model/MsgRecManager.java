package connect.im.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import connect.activity.base.BaseApplication;
import connect.activity.login.StartPageActivity;
import connect.database.MemoryDataManager;
import connect.im.inter.InterParse;
import connect.im.parser.CommandBean;
import connect.im.parser.ExceptionBean;
import connect.im.parser.MsgParseBean;
import connect.im.parser.ReceiptBean;
import connect.im.parser.ShakeHandBean;
import connect.service.bean.PushMessage;
import connect.service.bean.ServiceAck;
import connect.utils.log.LogManager;
import connect.utils.system.SystemUtil;

/**
 * Created by gtq on 2016/11/30.
 */
public class MsgRecManager {

    private String Tag = "_MsgRecManager";
    private static MsgRecManager receiverManager;

    public static MsgRecManager getInstance() {
        if (receiverManager == null) {
            synchronized (MsgRecManager.class) {
                if (receiverManager == null) {
                    receiverManager = new MsgRecManager();
                }
            }
        }
        return receiverManager;
    }

    private static ExecutorService threadPoolExecutor = Executors.newCachedThreadPool();

    public void sendMessage(ByteBuffer ack, ByteBuffer body) {
        ReceiveRun receiveRun = new ReceiveRun(ack, body);
        threadPoolExecutor.submit(receiveRun);
    }

    private class ReceiveRun implements Runnable {

        private ByteBuffer ack;
        private ByteBuffer body;

        public ReceiveRun(ByteBuffer ack, ByteBuffer body) {
            this.ack = ack;
            this.body = body;
        }

        @Override
        public synchronized void run() {
            if (!isKeyAvaliable()) {
                return;
            }

            try {
                byte type = ack.get(0);
                byte ext = ack.get(1);

                LogManager.getLogger().i(Tag, "receive order: [" + type + "][" + ext + "]");
                InterParse interParse = null;

                switch (type) {
                    case 0x01:
                        interParse = new ShakeHandBean(ext, body);
                        break;
                    case 0x03:
                        interParse = new ReceiptBean(ext, body);
                        break;
                    case 0x04://command order
                        interParse = new CommandBean(ext, body);
                        break;
                    case 0x05://chat order
                        interParse = new MsgParseBean(ext, body);
                        break;
                    case 0x06://Be offline
                        interParse = new ExceptionBean(ext, body);
                        break;
                }

                if (interParse != null) {
                    interParse.msgParse();
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errInfo = e.getMessage();
                if (TextUtils.isEmpty(errInfo)) {
                    errInfo = "";
                }
                LogManager.getLogger().d(Tag, "exception order info: [" + ack.get(0) + "][" + ack.get(1) + "]" + errInfo);
            }
        }

        /** private key is available
         *
         * @return
         */
        public synchronized boolean isKeyAvaliable() {
            boolean isAvailable = MemoryDataManager.getInstance().isAvailableKey();
            if (!isAvailable) {
                PushMessage.pushMessage(ServiceAck.EXIT_ACCOUNT,new byte[0], ByteBuffer.allocate(0));//close socket
                if (SystemUtil.isRunBackGround()) {// run in front
                    Context context = BaseApplication.getInstance().getBaseContext();
                    Intent intent = new Intent(context, StartPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//keep a single instance
                    context.startActivity(intent);
                }
            }
            return isAvailable;
        }
    }
}