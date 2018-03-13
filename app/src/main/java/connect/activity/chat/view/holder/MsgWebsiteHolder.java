package connect.activity.chat.view.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import connect.ui.activity.R;
import connect.utils.RegularUtil;
import connect.utils.glide.GlideUtil;
import instant.bean.ChatMsgEntity;
import protos.Connect;

/**
 * Created by pujin on 2017/2/20.
 */
public class MsgWebsiteHolder extends MsgChatHolder {

    private TextView titleTxt;
    private TextView contentTxt;
    private ImageView typeImg;

    public MsgWebsiteHolder(View itemView) {
        super(itemView);
        titleTxt = (TextView) itemView.findViewById(R.id.txt2);
        contentTxt = (TextView) itemView.findViewById(R.id.txt3);
        typeImg = (ImageView) itemView.findViewById(R.id.roundimg1);
    }

    @Override
    public void buildRowData(MsgBaseHolder msgBaseHolder, final ChatMsgEntity msgExtEntity) throws Exception {
        super.buildRowData(msgBaseHolder, msgExtEntity);
        Connect.WebsiteMessage websiteMessage = Connect.WebsiteMessage.parseFrom(msgExtEntity.getContents());

        titleTxt.setText(websiteMessage.getTitle());
        contentTxt.setText(websiteMessage.getSubtitle());

        String content = websiteMessage.getUrl();
        if (RegularUtil.matches(content, RegularUtil.OUTER_BITWEBSITE)) {
            if (RegularUtil.matches(content, RegularUtil.OUTER_BITWEBSITE_TRANSFER)) {//outer transfer
                GlideUtil.loadAvatarRound(typeImg, R.mipmap.message_send_bitcoin2x);
            } else if (RegularUtil.matches(content, RegularUtil.OUTER_BITWEBSITE_PACKET)) {//outer lucky packet
                GlideUtil.loadAvatarRound(typeImg, R.mipmap.luckybag3x);
            } else if (RegularUtil.matches(content, RegularUtil.OUTER_BITWEBSITE_PAY)) {//outer gather
                GlideUtil.loadAvatarRound(typeImg, R.mipmap.message_send_payment2x);
            }
        } else {//outer link
            String imgUrl = TextUtils.isEmpty(websiteMessage.getImg()) ? "" : websiteMessage.getImg();
            if (TextUtils.isEmpty(imgUrl)) {
                GlideUtil.loadAvatarRound(typeImg, R.mipmap.message_link2x);
            } else {
                GlideUtil.loadImage(typeImg, imgUrl, R.mipmap.message_link2x);
            }
        }

        contentLayout.setTag(content);
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = (String) v.getTag();
                ARouter.getInstance().build("/iwork/chat/exts/OuterWebsiteActivity")
                        .withString("URL", content)
                        .navigation();
            }
        });
    }
}