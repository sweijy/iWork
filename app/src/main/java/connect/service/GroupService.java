package connect.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connect.activity.contact.bean.ContactNotice;
import connect.activity.home.bean.GroupRecBean;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.utils.ProtoBufUtil;
import connect.utils.RegularUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

public class GroupService extends Service {

    private static String TAG = "_GroupService";

    private GroupService service;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = this;
        EventBus.getDefault().register(this);
    }

    public static void startService(Activity activity) {
        Intent intent = new Intent(activity, GroupService.class);
        activity.startService(intent);
    }

    public static void stopServer(Context context) {
        Intent intent = new Intent(context, GroupService.class);
        context.stopService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GroupRecBean groupRecBean) {
        Object[] objects = null;
        if (groupRecBean.obj != null) {
            objects = (Object[]) groupRecBean.obj;
        }

        switch (groupRecBean.groupRecType) {
            case GroupInfo://get group information
                groupInfo((String) objects[0]);
                break;
            case GroupNotificaton:
                updateGroupMute((String) objects[0], (Integer) objects[1]);
                break;
        }
    }

    public void groupInfo(final String pubkey) {
        Connect.GroupId groupId = Connect.GroupId.newBuilder()
                .setIdentifier(pubkey)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.GROUP_PULLINFO, groupId, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.GroupInfo groupInfo = Connect.GroupInfo.parseFrom(structData.getPlainData());
                    if (ProtoBufUtil.getInstance().checkProtoBuf(groupInfo)) {
                        Connect.Group group = groupInfo.getGroup();
                        String groupIdentifier = group.getIdentifier();

                        GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(groupIdentifier);
                        if (groupEntity == null) {
                            groupEntity = new GroupEntity();
                            groupEntity.setIdentifier(groupIdentifier);
                            String groupname = group.getName();
                            if (TextUtils.isEmpty(groupname)) {
                                groupname = "groupname9";
                            }
                            groupEntity.setCategory(group.getCategory());
                            groupEntity.setName(groupname);
                            groupEntity.setAvatar(RegularUtil.groupAvatar(group.getIdentifier()));
                            ContactHelper.getInstance().inserGroupEntity(groupEntity);
                        }

                        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
                        String uid = userBean.getUid();

                        Map<String, GroupMemberEntity> memberEntityMap = new HashMap<>();
                        for (Connect.GroupMember member : groupInfo.getMembersList()) {
                            GroupMemberEntity memEntity = new GroupMemberEntity();
                            memEntity.setIdentifier(groupIdentifier);
                            memEntity.setUid(member.getUid());
                            memEntity.setAvatar(member.getAvatar());

                            String showName = TextUtils.isEmpty(member.getName()) ?
                                    member.getUsername() :
                                    member.getName();
                            memEntity.setUsername(showName);
                            memEntity.setRole(member.getRole());
                            memberEntityMap.put(member.getUid(), memEntity);

                            if (uid.equals(member.getUid())) {
                                ConversionSettingHelper.getInstance().updateDisturb(groupIdentifier, member.getMute() ? 1 : 0);
                            }
                        }
                        Collection<GroupMemberEntity> memberEntityCollection = memberEntityMap.values();
                        List<GroupMemberEntity> memEntities = new ArrayList<GroupMemberEntity>(memberEntityCollection);
                        ContactHelper.getInstance().inserGroupMemEntity(memEntities);
                        ContactNotice.receiverGroup();
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

    private void updateGroupMute(final String groupkey, final int state) {
        Connect.UpdateGroupMute groupMute = Connect.UpdateGroupMute.newBuilder()
                .setIdentifier(groupkey)
                .setMute(state == 1)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_GROUP_MUTE, groupMute, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                ConversionSettingEntity setEntity = ConversionSettingHelper.getInstance().loadSetEntity(groupkey);
                setEntity.setDisturb(state);
                ConversionSettingHelper.getInstance().insertSetEntity(setEntity);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
