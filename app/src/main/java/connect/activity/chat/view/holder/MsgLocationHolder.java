package connect.activity.chat.view.holder;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import connect.activity.chat.view.BubbleImg;
import connect.ui.activity.R;
import connect.utils.StringUtil;
import instant.bean.ChatMsgEntity;
import protos.Connect;

/**
 * Created by gtq on 2016/11/23.
 */
public class MsgLocationHolder extends MsgChatHolder {
    private BubbleImg imgmsg;
    private TextView textView;

    public MsgLocationHolder(View itemView) {
        super(itemView);
        imgmsg = (BubbleImg) itemView.findViewById(R.id.imgmsg);
        textView = (TextView) itemView.findViewById(R.id.txt);
    }

    @Override
    public void buildRowData(MsgBaseHolder msgBaseHolder, final ChatMsgEntity msgExtEntity) throws Exception {
        super.buildRowData(msgBaseHolder, msgExtEntity);
        final Connect.LocationMessage locationMessage = Connect.LocationMessage.parseFrom(msgExtEntity.getContents());

        textView.setText(locationMessage.getAddress());
        String url = locationMessage.getScreenShot();
        Connect.ChatType chatType = Connect.ChatType.forNumber(msgExtEntity.getChatType());
        String hexString = StringUtil.bytesToHexString(locationMessage.getFileKey().toByteArray());
        imgmsg.loadUri(msgExtEntity.parseDirect(), chatType, msgExtEntity.getMessage_ower(), msgExtEntity.getMessage_id(),hexString,url, locationMessage.getImageWidth(), locationMessage.getImageHeight());
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/iwork/chat/exts/GoogleMapActivity")
                        .withDouble("LATITUDE",locationMessage.getLatitude())
                        .withDouble("LONGITUDE",locationMessage.getLongitude())
                        .navigation();
            }
        });
    }
}
