package connect.activity.chat.view.holder;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import connect.activity.chat.bean.MsgExtEntity;
import connect.activity.contact.FriendInfoActivity;
import connect.activity.contact.StrangerInfoActivity;
import connect.activity.contact.bean.SourceType;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.cryption.SupportKeyUril;
import connect.utils.glide.GlideUtil;
import connect.widget.roundedimageview.RoundedImageView;
import protos.Connect;

/**
 * Created by gtq on 2016/11/23.
 */
public class MsgCardHolder extends MsgChatHolder {

    private RoundedImageView cardHead;
    private TextView cardName;

    public MsgCardHolder(View itemView) {
        super(itemView);
        cardHead = (RoundedImageView) itemView.findViewById(R.id.roundimg_head_small);
        cardName = (TextView) itemView.findViewById(R.id.tvName);
    }

    @Override
    public void buildRowData(MsgBaseHolder msgBaseHolder, final MsgExtEntity msgExtEntity) throws Exception {
        super.buildRowData(msgBaseHolder, msgExtEntity);
        final Connect.CardMessage cardMessage = Connect.CardMessage.parseFrom(msgExtEntity.getContents());

        GlideUtil.loadAvater(cardHead, cardMessage.getAvatar());
        cardName.setText(cardMessage.getUsername());
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactEntity entity = ContactHelper.getInstance().loadFriendEntity(cardMessage.getUid());
                if (entity == null) {
                    String address = SupportKeyUril.getAddressFromPubkey(cardMessage.getUid());
                    StrangerInfoActivity.startActivity((Activity) context, address, SourceType.CARD);
                } else {
                    FriendInfoActivity.startActivity((Activity) context, cardMessage.getUid());
                }
            }
        });
    }
}