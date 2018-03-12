package connect.activity.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import connect.activity.home.bean.ConversationAction;
import connect.activity.home.bean.RoomAttrBean;
import connect.activity.home.view.ShowTextView;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.MessageHelper;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.FileUtil;
import connect.utils.TimeUtil;
import connect.utils.dialog.DialogUtil;
import connect.utils.glide.GlideUtil;
import connect.widget.RightTrangleView;
import connect.widget.badge.BadgeView;
import connect.widget.popuprecycler.RecyclerPopupWindow;
import protos.Connect;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationHolder> {

    private Context context;
    private ItemListener itemListener;
    private List<RoomAttrBean> roomAttrBeanList = new ArrayList<>();

    private RecyclerPopupWindow recyclerPopupWindow;
    private ItemClickListener itemClickListener = new ItemClickListener();

    public void setData(List<RoomAttrBean> entities) {
        this.roomAttrBeanList.clear();
        this.roomAttrBeanList.addAll(entities);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return roomAttrBeanList.size();
    }

    @Override
    public ConversationHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_fm_chatlist, parent, false);
        ConversationHolder holder = new ConversationHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ConversationHolder holder, final int position) {
        final RoomAttrBean roomAttr = roomAttrBeanList.get(position);
        holder.directTxt.showText(roomAttr.getUnreadAt(), roomAttr.getUnreadAttention(), roomAttr.getDraft(), TextUtils.isEmpty(roomAttr.getContent()) ? "" : roomAttr.getContent());
        try {
            long sendtime = roomAttr.getTimestamp();
            holder.timeTxt.setText(0 == sendtime ? "" : TimeUtil.getMsgTime(TimeUtil.getCurrentTimeInLong(), sendtime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Integer.valueOf(1).equals(roomAttr.getDisturb())) {
            holder.notifyImg.setVisibility(View.VISIBLE);
        } else {
            holder.notifyImg.setVisibility(View.GONE);
        }
        if (roomAttr.getRoomtype() == Connect.ChatType.CONNECT_SYSTEM_VALUE) {
            holder.nameTxt.setText(context.getString(R.string.app_name));
            GlideUtil.loadAvatarRound(holder.headImg, R.mipmap.connect_logo);
            holder.badgeTxt.setBadgeCount(roomAttr.getDisturb(), roomAttr.getUnread());
        } else if (roomAttr.getRoomtype() == Connect.ChatType.PRIVATE_VALUE ||
                roomAttr.getRoomtype() == Connect.ChatType.GROUP_VALUE) {
            String showName = TextUtils.isEmpty(roomAttr.getName()) ? "" : roomAttr.getName();
            if (showName.length() > 15) {
                showName = showName.subSequence(0, 15) + "...";
            }
            String showAvatar = TextUtils.isEmpty(roomAttr.getAvatar()) ? "" : roomAttr.getAvatar();

            holder.nameTxt.setText(showName);
            GlideUtil.loadAvatarRound(holder.headImg, showAvatar);
            holder.badgeTxt.setBadgeCount(roomAttr.getDisturb(), roomAttr.getUnread());
        }

        if (roomAttr.getTop() == 1) {
            holder.topImg.setVisibility(View.VISIBLE);
            holder.itemRelative.setBackgroundResource(R.color.color_F5F5F5);
        } else {
            holder.topImg.setVisibility(View.GONE);
            holder.itemRelative.setBackgroundResource(R.color.color_WHITE);
        }

        holder.itemRelative.setTag(R.id.position, position);
        holder.itemRelative.setTag(R.id.roomid, roomAttr.getRoomid());
        holder.itemRelative.setTag(R.id.roomtype, roomAttr.getRoomtype());
        holder.itemRelative.setTag(R.id.roomtop, roomAttr.getTop());
        holder.itemRelative.setOnClickListener(itemClickListener);

        String[] strings = new String[]{
                (roomAttr.getTop() <= 0 ? context.getResources().getString(R.string.Chat_Message_Top) :
                        context.getResources().getString(R.string.Chat_Message_Top_Remove)),
                context.getResources().getString(R.string.Chat_Conversation_Del)
        };
        recyclerPopupWindow = new RecyclerPopupWindow(context);
        recyclerPopupWindow.popupWindow(holder.itemRelative, Arrays.asList(strings), new RecyclerPopupWindow.RecyclerPopupListener() {

            boolean isDeleteAble = true;

            @Override
            public void longPress(View view) {
                view.setBackgroundColor(context.getResources().getColor(R.color.color_F5F5F5));
            }

            @Override
            public void pressCancle(View view) {
                if (roomAttr.getTop() == 1) {

                } else {
                    view.setBackgroundColor(context.getResources().getColor(R.color.color_WHITE));
                }
            }

            @Override
            public void onItemClick(int position) {
                String roomid = roomAttr.getRoomid();
                switch (position) {
                    case 0:
                        topMessage(roomid);
                        break;
                    case 1:
                        deleteMessage(roomid);
                        break;
                }
            }

            protected void topMessage(String roomId) {
                ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(roomId);
                if (conversionEntity == null) {
                    conversionEntity = new ConversionEntity();
                    conversionEntity.setIdentifier(roomId);
                }
                int top = (null == conversionEntity.getTop() || conversionEntity.getTop() == 0) ? 1 : 0;
                conversionEntity.setTop(top);
                ConversionHelper.getInstance().insertRoomEntity(conversionEntity);
                ConversationAction.conversationAction.sendEvent(ConversationAction.ConverType.LOAD_MESSAGE);
            }

            protected void deleteMessage(final String roomId) {
                String content = context.getString(R.string.Chat_Remove_Conversation);
                String leftCancle = context.getString(R.string.Chat_Member_Cancel);
                String rightConfirm = context.getString(R.string.Common_OK);
                DialogUtil.showAlertTextView(context, "", content, leftCancle, rightConfirm, true, new DialogUtil.OnItemClickListener() {

                    @Override
                    public void confirm(String value) {
                        ConversionHelper.getInstance().deleteRoom(roomId);
                        MessageHelper.getInstance().deleteRoomMsg(roomId);
                        FileUtil.deleteContactFile(roomId);
                        ConversationAction.conversationAction.sendEvent(ConversationAction.ConverType.LOAD_UNREAD);

                        if (isDeleteAble) {
                            isDeleteAble = false;
                            roomAttrBeanList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(500);
                                        isDeleteAble = true;//可点击按钮
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });
    }

    private class ItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String roomIdentify = (String) view.getTag(R.id.roomid);
            int roomType = (int) view.getTag(R.id.roomtype);
            itemListener.itemClick(Connect.ChatType.forNumber(roomType), roomIdentify);
        }
    }

    class ConversationHolder extends RecyclerView.ViewHolder {

        private RelativeLayout itemRelative;

        private BadgeView badgeTxt;
        private ImageView headImg;

        private TextView nameTxt;
        private ShowTextView directTxt;

        private RightTrangleView topImg;
        private ImageView notifyImg;
        private TextView timeTxt;

        public ConversationHolder(View itemView) {
            super(itemView);
            itemRelative = (RelativeLayout) itemView.findViewById(R.id.relative_item);
            headImg = (ImageView) itemView.findViewById(R.id.roundimg_head);
            nameTxt = (TextView) itemView.findViewById(R.id.usernameText);
            directTxt = (ShowTextView) itemView.findViewById(R.id.directTxtView);

            timeTxt = (TextView) itemView.findViewById(R.id.txt1);
            topImg = (RightTrangleView) itemView.findViewById(R.id.right_trangle);
            notifyImg = (ImageView) itemView.findViewById(R.id.image_notify);

            badgeTxt = (BadgeView) itemView.findViewById(R.id.badgetv);
        }
    }

    public void setConversationListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface ItemListener {
        void itemClick(Connect.ChatType chatType, String identify);
    }
}

