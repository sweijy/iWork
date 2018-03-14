package connect.activity.chat.view.holder;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import connect.database.green.DaoHelper.OrganizerHelper;
import connect.ui.activity.R;
import instant.bean.ChatMsgEntity;
import protos.Connect;

/**
 * Created by gtq on 2016/12/19.
 */
public class MsgNoticeHolder extends MsgBaseHolder {

    private LinearLayout noticeLayout;
    private TextView notice;

    public MsgNoticeHolder(View itemView) {
        super(itemView);
        noticeLayout = (LinearLayout) itemView.findViewById(R.id.content_notice);
        notice = (TextView) itemView.findViewById(R.id.notify);
    }

    @Override
    public void buildRowData(MsgBaseHolder msgBaseHolder, final ChatMsgEntity msgExtEntity) throws Exception {
        super.buildRowData(msgBaseHolder, msgExtEntity);
        SpannableStringBuilder builder = null;
        SpannableStringBuilder imageBuilder = null;
        SpannableStringBuilder colorBuilder = null;
        SpannableStringBuilder stringBuilder = null;
        ForegroundColorSpan colorSpan = null;

        Drawable drawable = null;
        ImageSpan imageSpan = null;

        try {
            final Connect.NotifyMessage notifyMessage = Connect.NotifyMessage.parseFrom(msgExtEntity.getContents());
            final int notifyType = notifyMessage.getNotifyType();

            switch (notifyType) {
                case 0://normal text
                    notice.setText(notifyMessage.getContent());
                    break;
                case 4://stranger
                    builder = new SpannableStringBuilder();
                    stringBuilder = new SpannableStringBuilder(context.getString(R.string.Chat_Add_as_a_friend_to_chat));
                    builder.append(stringBuilder);
                    colorBuilder = new SpannableStringBuilder(context.getString(R.string.Link_Add_as_a_friend));
                    colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.color_blue));
                    colorBuilder.setSpan(colorSpan, 0, colorBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(colorBuilder);
                    notice.setText(builder);
                    noticeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String friendUid = notifyMessage.getExtion();
                                ARouter.getInstance().build("/iwork/contact/ContactDepartmentActivity")
                                        .withString("userName",friendUid)
                                        .navigation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case 5:// 审核信息查询
                    builder = new SpannableStringBuilder(notifyMessage.getContent());
                    colorBuilder = new SpannableStringBuilder(context.getString(R.string.Wallet_Detail));
                    colorSpan = new ForegroundColorSpan(Color.BLUE);
                    colorBuilder.setSpan(colorSpan, 0, colorBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(colorBuilder);

                    notice.setText(builder);
                    noticeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ARouter.getInstance().build("/iwork/workbench/VisitorsActivity").
                                    navigation();
                        }
                    });
                    break;
                case 6://访客信息
                    builder = new SpannableStringBuilder(notifyMessage.getContent());
                    colorBuilder = new SpannableStringBuilder(context.getString(R.string.Wallet_Detail));
                    colorSpan = new ForegroundColorSpan(Color.BLUE);
                    colorBuilder.setSpan(colorSpan, 0, colorBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(colorBuilder);

                    notice.setText(builder);
                    noticeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Activity activity = (Activity) context;
                            long id = Long.parseLong(notifyMessage.getExtion());

                            ARouter.getInstance().build("/iwork/workbench/WarehouseDetailActivity")
                                    .withLong("id", id)
                                    .navigation();
                        }
                    });
                    break;
                default:
                    String showTxt = context.getString(R.string.Chat_Msg_Undefine);
                    Connect.NotifyMessage defaultNotify = Connect.NotifyMessage.parseFrom(msgExtEntity.getContents());
                    if (!TextUtils.isEmpty(defaultNotify.getContent())) {
                        showTxt = defaultNotify.getContent();
                    }
                    notice.setText(showTxt);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
