package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import connect.activity.chat.set.contract.GroupSelectContract;
import connect.activity.home.bean.GroupRecBean;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.MessageHelper;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ProtoBufUtil;
import connect.utils.RegularUtil;
import connect.utils.StringUtil;
import connect.utils.TimeUtil;
import connect.utils.ToastEUtil;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import instant.bean.ChatMsgEntity;
import instant.sender.model.GroupChat;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public class GroupSelectPresenter implements GroupSelectContract.Presenter {

    private Activity activity;
    private GroupSelectContract.BView view;

    public GroupSelectPresenter(GroupSelectContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        activity = view.getActivity();
    }

    @Override
    public void createGroup(final ArrayList<Connect.Workmate> workmates) {
        String groupName = getGroupName(workmates);

        List<Connect.AddGroupUserInfo> groupUserInfos = new ArrayList<>();
        for (Connect.Workmate entity : workmates) {
            Connect.AddGroupUserInfo userInfo = Connect.AddGroupUserInfo.newBuilder()
                    .setUid(entity.getUid())
                    .build();
            groupUserInfos.add(userInfo);
        }

        Connect.CreateGroup createGroup = Connect.CreateGroup.newBuilder()
                .setName(groupName)
                .addAllUsers(groupUserInfos)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CREATE_GROUP, createGroup, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.GroupInfo groupInfo = Connect.GroupInfo.parseFrom(structData.getPlainData());
                    if (ProtoBufUtil.getInstance().checkProtoBuf(groupInfo)) {
                        insertLocalData(groupInfo, workmates);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                // - 2421 groupinfo error
                // - 2422 group create failed
                if (response.getCode() == 2421) {
                    ToastEUtil.makeText(activity, R.string.Link_Group_create_information_error, ToastEUtil.TOAST_STATUS_FAILE).show();
                } else if (response.getCode() == 2422) {
                    ToastEUtil.makeText(activity, R.string.Network_equest_failed_please_try_again_later, ToastEUtil.TOAST_STATUS_FAILE).show();
                } else {
                    ToastEUtil.makeText(activity, response.getMessage(), ToastEUtil.TOAST_STATUS_FAILE).show();
                }
            }
        });
    }

    @Override
    public void inviteJoinGroup(final String groupIdentify, ArrayList<Connect.Workmate> workmates) {
        List<String> selectUids = new ArrayList<>();
        for (Connect.Workmate workmate : workmates) {
            selectUids.add(workmate.getUid());
        }

        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        Connect.GroupInviteWorkmate inviteWorkmate = Connect.GroupInviteWorkmate.newBuilder()
                .setInviteBy(userBean.getUid())
                .setIdentifier(groupIdentify)
                .addAllUids(selectUids)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V1_GROUP_INVITE, inviteWorkmate, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                GroupRecBean.sendGroupRecMsg(GroupRecBean.GroupRecType.GroupInfo, groupIdentify);

                String showHint = activity.getResources().getString(R.string.Link_Group_Invite_Success);
                ToastUtil.getInstance().showToast(showHint);
                ARouter.getInstance().build("/iwork/chat/ChatActivity")
                        .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .withSerializable("chatType", Connect.ChatType.GROUP)
                        .withString("chatIdentify", groupIdentify)
                        .navigation();
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                if (response.getCode() == 2430) {
                    ToastEUtil.makeText(activity, R.string.Link_Qr_code_is_invalid, ToastEUtil.TOAST_STATUS_FAILE).show();
                } else {
                    String contentTxt = response.getMessage();
                    if (TextUtils.isEmpty(contentTxt)) {
                        ToastEUtil.makeText(activity, activity.getString(R.string.Network_equest_failed_please_try_again_later), 2).show();
                    } else {
                        ToastEUtil.makeText(activity, contentTxt, 2).show();
                    }
                }
            }
        });
    }

    private String getGroupName(ArrayList<Connect.Workmate> workmates){
        StringBuffer stringBuffer = new StringBuffer();
        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        stringBuffer.append(userBean.getName());
        stringBuffer.append(",");
        int size = workmates.size() > 5 ? 5 : workmates.size();
        for (int i = 0; i < size; i++) {
            Connect.Workmate workmate = workmates.get(i);
            String showName = TextUtils.isEmpty(workmate.getName()) ? workmate.getUsername() : workmate.getName();
            if(i == size-1){
                stringBuffer.append(showName);
            }else{
                stringBuffer.append(showName + ",");
            }
        }
        return stringBuffer.toString();
    }

    public void insertLocalData(Connect.GroupInfo groupInfo, ArrayList<Connect.Workmate> workmates) {
        final String groupKey = groupInfo.getGroup().getIdentifier();
        String groupName = groupInfo.getGroup().getName();

        ConversionEntity roomEntity = new ConversionEntity();
        roomEntity.setType(Connect.ChatType.GROUP_VALUE);
        roomEntity.setIdentifier(groupKey);
        roomEntity.setName(groupName);
        roomEntity.setAvatar(groupInfo.getGroup().getAvatar());
        roomEntity.setLast_time(TimeUtil.getCurrentTimeInLong());
        roomEntity.setContent(activity.getString(R.string.Chat_Tips));
        ConversionHelper.getInstance().insertRoomEntity(roomEntity);

        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setName(groupName);
        groupEntity.setIdentifier(groupKey);
        groupEntity.setAvatar(RegularUtil.groupAvatar(groupKey));
        ContactHelper.getInstance().inserGroupEntity(groupEntity);

        String stringMems = "";
        List<GroupMemberEntity> memEntities = new ArrayList<>();
        for (Connect.Workmate contact : workmates) {
            GroupMemberEntity memEntity = new GroupMemberEntity();
            memEntity.setIdentifier(groupKey);
            memEntity.setUid(contact.getUid());
            memEntity.setAvatar(contact.getAvatar());
            memEntity.setRole(0);

            String showName = TextUtils.isEmpty(contact.getName()) ?
                    contact.getUsername() :
                    contact.getName();
            memEntity.setUsername(showName);
            memEntities.add(memEntity);
            stringMems = stringMems + contact.getName() + ",";
        }
        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        GroupMemberEntity memEntity = new GroupMemberEntity();
        memEntity.setIdentifier(groupKey);
        memEntity.setUid(userBean.getUid());
        memEntity.setAvatar(userBean.getAvatar());
        memEntity.setRole(1);
        memEntity.setUsername(userBean.getName());
        memEntities.add(memEntity);
        ContactHelper.getInstance().inserGroupMemEntity(memEntities);

        GroupChat groupChat = new GroupChat(groupKey);
        stringMems = String.format(activity.getString(R.string.Link_enter_the_group), stringMems);

        ChatMsgEntity invite = groupChat.noticeMsg(0, stringMems, "");
        MessageHelper.getInstance().insertMsgExtEntity(invite);

        ToastEUtil.makeText(activity, activity.getString(R.string.Chat_Create_Group_Success), 1, new ToastEUtil.OnToastListener() {

            @Override
            public void animFinish() {
                ARouter.getInstance().build("/iwork/chat/ChatActivity")
                        .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .withSerializable("chatType", Connect.ChatType.GROUP)
                        .withString("chatIdentify", groupKey)
                        .navigation();
            }
        }).show();
    }
}
