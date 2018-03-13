package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import connect.activity.base.BaseListener;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.set.contract.GroupSetContract;
import connect.activity.home.HomeActivity;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ProtoBufUtil;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.dialog.DialogUtil;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.system.SystemUtil;
import protos.Connect;

/**
 * Created by Administrator on 2017/8/8.
 */

public class GroupSetPresenter implements GroupSetContract.Presenter {

    private final String TAG_ADD = "GROUP_ADD";
    private GroupSetContract.BView view;

    private String groupIdentify;
    private Activity activity;

    public GroupSetPresenter(GroupSetContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        activity = view.getActivity();
        groupIdentify = view.getUid();

        view.addNewMember();
        GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(groupIdentify);
        if (groupEntity == null) {
            ActivityUtil.goBack(activity);
            return;
        }
        view.groupName(groupEntity.getName());

        List<GroupMemberEntity> memberEntities = ContactHelper.getInstance().loadGroupMemEntities(groupIdentify);
        view.countMember(memberEntities.size());

        int screenWidth = SystemUtil.getScreenWidth();
        int singleMemberWidth = SystemUtil.dipToPx(48);
        int maxMemberShow = screenWidth / singleMemberWidth - 1;
        int subPosi = memberEntities.size() >= maxMemberShow ? maxMemberShow : memberEntities.size();
        List<GroupMemberEntity> showMemberEntities = memberEntities.subList(0, subPosi);
        for (GroupMemberEntity entity : showMemberEntities) {
            View headerview = LayoutInflater.from(activity).inflate(R.layout.linear_contact, null);
            ImageView headimg = (ImageView) headerview.findViewById(R.id.roundimg);

            UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
            if (userBean.getUid().equals(entity.getUid())) {
                entity.setUsername(userBean.getName());
                entity.setAvatar(userBean.getAvatar());

                view.groupNameClickable(1 == entity.getRole());
            }
            GlideUtil.loadAvatarRound(headimg, entity.getAvatar());
            view.memberList(headerview);
        }

        ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(groupIdentify);
        boolean top = false;
        if (conversionEntity != null) {
            top = Integer.valueOf(1).equals(conversionEntity.getTop());
        }
        view.topSwitch(top);

        ConversionSettingEntity setEntity = ConversionSettingHelper.getInstance().loadSetEntity(groupIdentify);
        if (setEntity == null) {
            setEntity = new ConversionSettingEntity();
            setEntity.setIdentifier(groupIdentify);
            setEntity.setDisturb(0);
        }
        boolean notice = Integer.valueOf(1).equals(setEntity.getDisturb());
        view.noticeSwitch(notice);

        boolean common = Integer.valueOf(1).equals(groupEntity.getCommon());
        view.commonSwtich(common);
        view.exitGroup();
    }

    @Override
    public void syncGroupInfo() {
        Connect.GroupId groupId = Connect.GroupId.newBuilder()
                .setIdentifier(groupIdentify)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.GROUP_SETTING_INFO, groupId, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.GroupSettingInfo settingInfo = Connect.GroupSettingInfo.parseFrom(structData.getPlainData());
                    if (ProtoBufUtil.getInstance().checkProtoBuf(settingInfo)) {
                        view.noticeSwitch(settingInfo.getMute());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpResponse response) {

            }
        });
    }

    @Override
    public void groupTop(boolean checkon, final BaseListener<Boolean> listener) {
        Connect.UpdateGroupSession groupSession = Connect.UpdateGroupSession.newBuilder()
                .setIdentifier(groupIdentify)
                .setVal(checkon)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_GROUP_TOP, groupSession, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                listener.Success(true);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                listener.fail();
            }
        });
    }

    @Override
    public void groupMute(final boolean state, final BaseListener<Boolean> listener) {
        Connect.UpdateGroupMute groupMute = Connect.UpdateGroupMute.newBuilder()
                .setIdentifier(groupIdentify)
                .setMute(state)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_GROUP_MUTE, groupMute, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                listener.Success(true);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                listener.fail();
            }
        });
    }

    @Override
    public void groupCommon(final boolean state, final BaseListener<Boolean> listener) {
        Connect.GroupId groupId = Connect.GroupId.newBuilder().setIdentifier(groupIdentify).build();
        String coomonUrl = state ? UriUtil.GROUP_COMMON : UriUtil.GROUP_RECOMMON;
        OkHttpUtil.getInstance().postEncrySelf(coomonUrl, groupId, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                listener.Success(true);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                listener.fail(true);
            }
        });
    }

    @Override
    public void requestExitGroup() {
        DialogUtil.showAlertTextView(activity,
                activity.getResources().getString(R.string.Set_tip_title),
                activity.getResources().getString(R.string.Link_Delete_and_Leave),
                "", "", false, new DialogUtil.OnItemClickListener() {
                    @Override
                    public void confirm(String value) {
                        Connect.GroupId groupId = Connect.GroupId.newBuilder().setIdentifier(groupIdentify).build();
                        OkHttpUtil.getInstance().postEncrySelf(UriUtil.GROUP_QUIT, groupId, new ResultCall<Connect.HttpResponse>() {
                            @Override
                            public void onResponse(Connect.HttpResponse response) {
                                RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.CLEAR_HISTORY, groupIdentify);

                                ContactHelper.getInstance().removeGroupInfos(groupIdentify);
                                //FileUtil.deleteDirectory();
                                ActivityUtil.backActivityWithClearTop(activity, HomeActivity.class);
                            }

                            @Override
                            public void onError(Connect.HttpResponse response) {
                                if (response.getCode() == 2424) {
                                    ToastEUtil.makeText(activity, R.string.Link_Already_delete_and_Leave, ToastEUtil.TOAST_STATUS_FAILE).show();
                                } else {
                                    ToastEUtil.makeText(activity, response.getMessage(), ToastEUtil.TOAST_STATUS_FAILE).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
    }
}
