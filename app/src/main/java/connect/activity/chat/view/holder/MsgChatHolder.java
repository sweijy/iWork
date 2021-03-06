package connect.activity.chat.view.holder;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.Arrays;

import connect.activity.base.BaseListener;
import connect.activity.chat.bean.GroupMemberUtil;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.bean.RoomSession;
import connect.activity.chat.view.MsgStateView;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.MessageHelper;
import connect.database.green.DaoHelper.OrganizerHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ToastEUtil;
import connect.utils.glide.GlideUtil;
import connect.widget.ChatHeadImg;
import connect.widget.popuprecycler.RecyclerPopupHelper;
import instant.bean.ChatMsgEntity;
import instant.bean.MsgDirect;
import protos.Connect;

/**
 * Created by gtq on 2016/11/23.
 */
public abstract class MsgChatHolder extends MsgBaseHolder {

    protected ChatHeadImg headImg;
    protected TextView memberTxt;
    protected MsgStateView msgStateView;
    protected RelativeLayout contentLayout;

    private String[] strings = null;
    private RecyclerPopupHelper popupHelper;

    @TargetApi(Build.VERSION_CODES.M)
    public MsgChatHolder(View itemView) {
        super(itemView);
        headImg = (ChatHeadImg) itemView.findViewById(R.id.roundimg_head);
        memberTxt = (TextView) itemView.findViewById(R.id.usernameText);
        msgStateView = (MsgStateView) itemView.findViewById(R.id.msgstateview);
        contentLayout = (RelativeLayout) itemView.findViewById(R.id.content_layout);
    }

    @Override
    public void buildRowData(MsgBaseHolder msgBaseHolder, final ChatMsgEntity msgExtEntity) throws Exception {
        super.buildRowData(msgBaseHolder, msgExtEntity);
        final MsgDirect direct = msgExtEntity.parseDirect();
        try {
            if (direct == MsgDirect.To && msgStateView != null) {
                msgStateView.setMsgEntity(msgExtEntity);
                msgStateView.updateMsgState(msgExtEntity.getSend_status());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        strings = longPressPrompt();
        popupHelper = new RecyclerPopupHelper(context);
        popupHelper.popupWindow(longClickView(), Arrays.asList(strings), new RecyclerPopupHelper.PopupListener() {
            @Override
            public void onPopupListClick(int position) {
                String string = strings[position];
                String[] strings = context.getResources().getStringArray(R.array.prompt_all);
                if (string.equals(strings[0])) {//forwarding
                    transPondTo();
                } else if (string.equals(strings[1])) {//save in phone
                    saveInPhone();
                } else if (string.equals(strings[2])) {//delete
                    deleteChatMsg();
                } else if (string.equals(strings[3])) {//copy
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(getCopyTxt());
                    ToastEUtil.makeText(context, R.string.Set_Copied).show();
                }
            }
        });

        Connect.ChatType chatType = Connect.ChatType.forNumber(msgExtEntity.getChatType());
        switch (chatType) {
            case PRIVATE:
                GlideUtil.loadAvatarRound(headImg, direct == MsgDirect.From ?
                        RoomSession.getInstance().getFriendAvatar():
                        SharedPreferenceUtil.getInstance().getUser().getAvatar());
                headImg.setUserUid(direct == MsgDirect.From ?
                        RoomSession.getInstance().getRoomKey() :
                        SharedPreferenceUtil.getInstance().getUser().getUid());

                headImg.setVisibility(View.VISIBLE);
                headImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (direct == MsgDirect.To) {
                            ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                                    navigation();
                        } else if (direct == MsgDirect.From) {
                            String uid = RoomSession.getInstance().getRoomKey();
                            ARouter.getInstance().build("/iwork/contact/ContactDepartmentActivity")
                                    .withString("userName",uid)
                                    .navigation();
                        }
                    }
                });
                if (memberTxt != null) {
                    memberTxt.setVisibility(View.GONE);
                }
                break;
            case GROUP:
                headImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (direct == MsgDirect.To) {
                            ARouter.getInstance().build("/iwork/set/UserInfoActivity").
                                    navigation();
                        } else if (direct == MsgDirect.From) {
                            String memberKey = msgExtEntity.getMessage_from();
                            ARouter.getInstance().build("/iwork/contact/ContactDepartmentActivity")
                                    .withString("userName",memberKey)
                                    .navigation();
                        }
                    }
                });

                if (direct == MsgDirect.To) {
                    GlideUtil.loadAvatarRound(headImg, SharedPreferenceUtil.getInstance().getUser().getAvatar());
                    if (memberTxt != null) {
                        memberTxt.setVisibility(View.GONE);
                    }
                    headImg.setUserUid(SharedPreferenceUtil.getInstance().getUser().getUid());
                } else if (direct == MsgDirect.From) {
                    memberTxt.setVisibility(View.VISIBLE);
                    String groupKey = msgExtEntity.getMessage_ower();
                    final String memberKey = msgExtEntity.getMessage_from();

                    GroupMemberUtil.getIntance().loadGroupMember(groupKey, memberKey, new BaseListener<GroupMemberEntity>() {
                        @Override
                        public void Success(GroupMemberEntity ts) {
                            GlideUtil.loadAvatarRound(headImg, ts.getAvatar());
                            headImg.setUserUid(memberKey);

                            String memberName = "";
                            if (!TextUtils.isEmpty(ts.getUsername())) {
                                memberName = ts.getUsername();
                            }
                            memberTxt.setText(memberName);
                        }

                        @Override
                        public void fail(Object... objects) {
                            GlideUtil.loadAvatarRound(headImg, "");
                            memberTxt.setText("");
                        }
                    });
                }

                break;
            case CONNECT_SYSTEM:
                if (memberTxt != null) {
                    memberTxt.setVisibility(View.GONE);
                }
                if (direct == MsgDirect.From) {
                    GlideUtil.loadAvatarRound(headImg, R.mipmap.connect_logo);
                } else {
                    String imgpath = SharedPreferenceUtil.getInstance().getUser().getAvatar();
                    GlideUtil.loadAvatarRound(headImg, imgpath);
                }
                break;
        }
    }

    public void deleteChatMsg() {
        ChatMsgEntity msgExtEntity = getMsgExtEntity();
        RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.DELMSG, msgExtEntity);
        MessageHelper.getInstance().deleteMsgByid(msgExtEntity.getMessage_id());
    }

    public void saveInPhone() {

    }

    public void transPondTo() {

    }

    public View longClickView() {
        return contentLayout;
    }

    public String getCopyTxt() {
        return "";
    }
}
