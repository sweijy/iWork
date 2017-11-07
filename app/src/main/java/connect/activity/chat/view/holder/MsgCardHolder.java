package connect.activity.chat.view.holder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import connect.activity.contact.FriendInfoActivity;
import connect.activity.contact.StrangerInfoActivity;
import connect.activity.contact.bean.SourceType;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import instant.bean.ChatMsgEntity;
import protos.Connect;

/**
 * Created by gtq on 2016/11/23.
 */
public class MsgCardHolder extends MsgChatHolder {

    private ImageView cardHead;
    private TextView cardName;

    public MsgCardHolder(View itemView) {
        super(itemView);
        cardHead = (ImageView) itemView.findViewById(R.id.roundimg_head_small);
        cardName = (TextView) itemView.findViewById(R.id.tvName);
    }

    @Override
    public void buildRowData(MsgBaseHolder msgBaseHolder, final ChatMsgEntity msgExtEntity) throws Exception {
        super.buildRowData(msgBaseHolder, msgExtEntity);
        final Connect.CardMessage cardMessage = Connect.CardMessage.parseFrom(msgExtEntity.getContents());

        GlideUtil.loadAvatarRound(cardHead, cardMessage.getAvatar());
        cardName.setText(cardMessage.getUsername());
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = cardMessage.getUid();
                ContactEntity entity = ContactHelper.getInstance().loadFriendEntity(cardMessage.getUid());
                if (entity == null) {
                    StrangerInfoActivity.startActivity((Activity) context, uid, SourceType.CARD);
                } else {
                    FriendInfoActivity.startActivity((Activity) context, uid);
                }
            }
        });
    }
}
