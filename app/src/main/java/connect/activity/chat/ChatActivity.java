package connect.activity.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseListener;
import connect.activity.chat.activity.BaseChatSendActivity;
import connect.activity.chat.adapter.ChatAdapter;
import connect.activity.chat.bean.LinkMessageRow;
import connect.activity.chat.bean.MsgSend;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.bean.RoomSession;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.DaoHelper.MessageHelper;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.instant.inter.ConversationListener;
import connect.instant.model.CFriendChat;
import connect.instant.model.CGroupChat;
import connect.instant.model.CRobotChat;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.BitmapUtil;
import connect.utils.FileUtil;
import connect.utils.MediaUtil;
import connect.utils.ProtoBufUtil;
import connect.utils.RegularUtil;
import connect.utils.TimeUtil;
import connect.utils.UriUtil;
import connect.utils.chatfile.inter.BaseFileUp;
import connect.utils.chatfile.inter.FileUploadListener;
import connect.utils.chatfile.upload.PhotoUpload;
import connect.utils.chatfile.upload.VideoUpload;
import connect.utils.log.LogManager;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.permission.PermissionUtil;
import connect.widget.TopToolBar;
import connect.widget.album.AlbumActivity;
import connect.widget.album.model.AlbumFile;
import connect.widget.bottominput.InputPanel;
import connect.widget.bottominput.view.InputBottomLayout;
import connect.widget.camera.CameraTakeActivity;
import connect.widget.recordvoice.VoiceRecordView;
import instant.bean.ChatMsgEntity;
import instant.sender.model.NormalChat;
import protos.Connect;

/**
 * chat message
 * Created by gtq on 2016/11/22.
 * <p>
 * 传递参数：
 *
 * @ chatType
 * @ chatIdentify
 * @ searchTxt
 */
@Route(path = "/iwork/chat/ChatActivity")
public class ChatActivity extends BaseChatSendActivity {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @Bind(R.id.inputPanel)
    InputPanel inputPanel;
    @Bind(R.id.relativelayout_1)
    RelativeLayout relativelayout1;
    @Bind(R.id.view_voicerecord_state)
    VoiceRecordView viewVoicerecordState;

    private static String TAG = "_ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;

        chatType = (Connect.ChatType) getIntent().getSerializableExtra("chatType");
        chatIdentify = getIntent().getStringExtra("chatIdentify");
        if (TextUtils.isEmpty(chatIdentify) || null == chatType) {
            ActivityUtil.goBack(this);
            return;
        }

        if (chatType == Connect.ChatType.GROUP) {
            GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(chatIdentify);
            List<GroupMemberEntity> memberEntities = ContactHelper.getInstance().loadGroupMemEntities(chatIdentify);
            if (groupEntity == null || memberEntities == null || memberEntities.isEmpty()) {
                pullGroupInfo(new BaseListener<Boolean>() {
                    @Override
                    public void Success(Boolean ts) {
                        initView();
                    }

                    @Override
                    public void fail(Object... objects) {

                    }
                });
                return;
            }
        }

        RoomSession.getInstance().setRoomType(chatType);
        RoomSession.getInstance().setRoomKey(chatIdentify);

        super.initView();
        toolbar.setLeftImg(R.mipmap.back_white);

        if (chatType == Connect.ChatType.PRIVATE) {
            toolbar.setRightImg(R.mipmap.icon_chat_persion);
        } else if (chatType == Connect.ChatType.GROUP) {
            toolbar.setRightImg(R.mipmap.icon_chat_setting);
        }

        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (chatType) {
                    case CONNECT_SYSTEM:
                        ARouter.getInstance().build("/iwork/chat/set/RobotSetActivity")
                                .navigation();
                        break;
                    case PRIVATE:
                        ARouter.getInstance().build("/iwork/chat/set/PrivateSetActivity")
                                .withString("uid", normalChat.chatKey())
                                .withString("avatar", normalChat.headImg())
                                .withString("userName", normalChat.nickName())
                                .navigation();
                        break;
                    case GROUP:
                        ARouter.getInstance().build("/iwork/chat/set/GroupSetActivity")
                                .withString("groupIdentify", chatIdentify)
                                .navigation();
                        break;
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        if (TextUtils.isEmpty(searchTxt)) {
            linearLayoutManager.setStackFromEnd(true);
        }
        chatAdapter = new ChatAdapter(activity, recyclerChat, linearLayoutManager);
        recyclerChat.setLayoutManager(linearLayoutManager);
        recyclerChat.setAdapter(chatAdapter);
        recyclerChat.setItemAnimator(new DefaultItemAnimator());
        recyclerChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                inputPanel.hideBottomPanel();
                return false;
            }
        });
        inputPanel.setVoiceRecordView(viewVoicerecordState);

        scrollHelper.setCheckIfItemViewFullRecycleViewForTop(true);
        scrollHelper.attachToRecycleView(recyclerChat);
        loadChatInfor();

        PermissionUtil.getInstance().requestPermission(activity,
                new String[]{PermissionUtil.PERMISSION_RECORD_AUDIO, PermissionUtil.PERMISSION_STORAGE},
                permissomCallBack);
    }

    public void pullGroupInfo(final BaseListener<Boolean> baseListener) {
        Connect.GroupId groupId = Connect.GroupId.newBuilder()
                .setIdentifier(chatIdentify)
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
                        baseListener.Success(true);
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
    public void loadChatInfor() {
        LogManager.getLogger().d(TAG, "loadChatInfor()");
        new AsyncTask<Void, Void, List<ChatMsgEntity>>() {

            @Override
            protected List<ChatMsgEntity> doInBackground(Void... params) {
                List<ChatMsgEntity> msgEntities = null;
                if (TextUtils.isEmpty(searchTxt)) {
                    msgEntities = MessageHelper.getInstance().loadMoreMsgEntities(normalChat.chatKey(), TimeUtil.getCurrentTimeInLong());
                } else {
                    msgEntities = MessageHelper.getInstance().loadMessageBySearchTxt(normalChat.chatKey(), searchTxt);
                }
                return msgEntities;
            }

            @Override
            protected void onPostExecute(List<ChatMsgEntity> entities) {
                super.onPostExecute(entities);
                chatAdapter.insertItems(entities);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void loadMoreMsgs() {
        LogManager.getLogger().d(TAG, "loadMoreMsgs()");
        new AsyncTask<Void, Void, List<ChatMsgEntity>>() {

            @Override
            protected List<ChatMsgEntity> doInBackground(Void... params) {
                long lastCreateTime = 0;
                List<ChatMsgEntity> msgExtEntities = chatAdapter.getMsgEntities();
                if (msgExtEntities.size() > 0) {
                    ChatMsgEntity baseEntity = msgExtEntities.get(0);
                    lastCreateTime = baseEntity.getCreatetime();
                }
                return MessageHelper.getInstance().loadMoreMsgEntities(normalChat.chatKey(), lastCreateTime);
            }

            @Override
            protected void onPostExecute(List<ChatMsgEntity> msgEntities) {
                super.onPostExecute(msgEntities);
                if (msgEntities.size() > 0) {
                    View firstChild = recyclerChat.getChildAt(0);
                    int top = firstChild.getTop();

                    chatAdapter.insertItems(msgEntities);
                    scrollHelper.scrollToPosition(msgEntities.size(), top);//Some errors, top - SystemUtil.dipToPx(48)
                }
            }
        }.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void updateTitleName() {
        new AsyncTask<Void, Void, ConversionSettingEntity>() {

            @Override
            protected ConversionSettingEntity doInBackground(Void... voids) {
                ConversionSettingEntity settingEntity = ConversionSettingHelper.getInstance().loadSetEntity(chatIdentify);
                if (settingEntity == null) {
                    settingEntity = new ConversionSettingEntity();
                    settingEntity.setIdentifier(chatIdentify);
                    settingEntity.setDisturb(0);
                }
                return settingEntity;
            }

            @Override
            protected void onPostExecute(ConversionSettingEntity settingEntity) {
                super.onPostExecute(settingEntity);


                String titleName = "";
                switch (chatType) {
                    case CONNECT_SYSTEM:
                        titleName = getString(R.string.app_name);
                        toolbar.setTitle(titleName);
                        break;
                    case PRIVATE:
                        titleName = normalChat.nickName();
                        if (titleName.length() > 15) {
                            titleName = titleName.substring(0, 12);
                            titleName += "...";
                        }

                        RoomSession.roomSession.setFriendAvatar(normalChat.headImg());
                        toolbar.setTitle(settingEntity.getDisturb() == 0 ? null : R.mipmap.icon_close_notify, titleName);
                        break;
                    case GROUP:
                        GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(chatIdentify);
                        titleName = TextUtils.isEmpty(groupEntity.getName()) ? "" : groupEntity.getName();
                        if (titleName.length() > 15) {
                            titleName = titleName.substring(0, 12);
                            titleName += "...";
                        }

                        List<GroupMemberEntity> memEntities = ContactHelper.getInstance().loadGroupMemEntities(chatIdentify);
                        toolbar.setTitle(settingEntity.getDisturb() == 0 ? null : R.mipmap.icon_close_notify, (titleName + String.format(Locale.ENGLISH, "(%d)", memEntities.size())));
                        break;
                }
            }
        }.execute();
    }

    /**
     * Accept new message to slide to the end
     *
     * @param bean
     */
    @Override
    public void adapterInsetItem(ChatMsgEntity bean) {
        chatAdapter.insertItem(bean);
        if (scrollHelper.isScrollBottom()) {
            RecExtBean.getInstance().sendEventDelay(RecExtBean.ExtType.SCROLLBOTTOM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_TAKEPHOTO && data != null) {
            String type = data.getStringExtra("mediaType");
            String path = data.getStringExtra("path");

            if (TextUtils.isEmpty(type) || TextUtils.isEmpty(path)) {
                return;
            }

            if (type.equals(CameraTakeActivity.MEDIA_TYPE_PHOTO)) {
                List<String> paths = new ArrayList<>();
                paths.add(path);
                MsgSend.sendOuterMsg(MsgSend.MsgSendType.Photo, paths);
            } else if (type.equals(CameraTakeActivity.MEDIA_TYPE_VEDIO)) {
                int length = data.getIntExtra("length", 10);
                MsgSend.sendOuterMsg(MsgSend.MsgSendType.Video, path, length);
            }
        } else if (requestCode == AlbumActivity.OPEN_ALBUM_CODE && data != null) {
            List<AlbumFile> albumFiles = (List<AlbumFile>) data.getSerializableExtra("list");
            if (albumFiles != null && albumFiles.size() > 0) {
                for (AlbumFile albumFile : albumFiles) {
                    String filePath = albumFile.getPath();
                    File tempFile = new File(filePath);
                    if (tempFile.exists() && tempFile.length() > 0) {
                        if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
                            List<String> paths = new ArrayList<>();
                            paths.add(filePath);
                            MsgSend.sendOuterMsg(MsgSend.MsgSendType.Photo, paths);
                        } else {
                            int length = (int) (albumFile.getDuration() / 1000);
                            MsgSend.sendOuterMsg(MsgSend.MsgSendType.Video, albumFile.getPath(), length);
                        }
                    }
                }
            }
        } else if (requestCode == CODE_REQUEST && resultCode == RESULT_OK) {//relay the message
            transpondTo(data);
        }
    }

    /**
     * relay the message
     *
     * @param data
     */
    protected void transpondTo(Intent data) {
        int roomType = data.getIntExtra("type", 0);
        String roomkey = data.getStringExtra("object");
        Serializable serializables = data.getSerializableExtra("Serializable");
        Object[] objects = (Object[]) serializables;

        ChatMsgEntity msgExtEntity = null;
        NormalChat normalChat = null;
        switch (Connect.ChatType.forNumber(roomType)) {
            case PRIVATE:
                normalChat = new CFriendChat(roomkey);
                break;
            case GROUP:
                GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(roomkey);
                normalChat = new CGroupChat(groupEntity);
                break;
            case CONNECT_SYSTEM:
                normalChat = new CRobotChat();
                break;
        }

        BaseFileUp baseFileUp = null;
        String content = (String) objects[1];
        LinkMessageRow msgType = LinkMessageRow.toMsgType(Integer.parseInt((String) objects[0]));
        switch (msgType) {
            case Text:
                msgExtEntity = normalChat.txtMsg(content);
                MessageHelper.getInstance().insertMsgExtEntity(msgExtEntity);
                ((ConversationListener) normalChat).updateRoomMsg(null, msgExtEntity.showContent(), msgExtEntity.getCreatetime());
                normalChat.sendPushMsg(msgExtEntity);
                break;
            case Photo:
                msgExtEntity = normalChat.photoMsg(content, content, FileUtil.fileSize(content), (int) objects[2], (int) objects[3]);
                baseFileUp = new PhotoUpload(activity, normalChat, msgExtEntity, new FileUploadListener() {
                    @Override
                    public void upSuccess(String msgid) {
                    }

                    @Override
                    public void uploadFail(int code, String message) {

                    }
                });
                baseFileUp.startUpload();
                break;
            case Video:
                String videoPath = content;
                int videoTimeLength = (int) objects[2];
                int videoSize = FileUtil.fileSizeOf(videoPath);

                Bitmap thumbBitmap = BitmapUtil.thumbVideo(videoPath);
                File thumbFile = BitmapUtil.getInstance().bitmapSavePath(thumbBitmap);
                String thumbPath = thumbFile.getAbsolutePath();

                msgExtEntity = normalChat.videoMsg(thumbPath, videoPath, videoTimeLength,
                        videoSize, thumbBitmap.getWidth(), thumbBitmap.getHeight());
                baseFileUp = new VideoUpload(activity, normalChat, msgExtEntity, new FileUploadListener() {
                    @Override
                    public void upSuccess(String msgid) {
                    }

                    @Override
                    public void uploadFail(int code, String message) {

                    }
                });
                baseFileUp.startUpload();
                break;
        }
        if (getNormalChat().chatKey().equals(roomkey)) {
            adapterInsetItem(msgExtEntity);
        }
    }

    @Override
    public void saveRoomInfo() {
        String showtxt = "";
        long sendtime = 0;
        String draft = InputBottomLayout.bottomLayout.getDraft();

        ChatMsgEntity lastExtEntity = null;
        if (chatAdapter.getMsgEntities().size() != 0) {
            lastExtEntity = chatAdapter.getMsgEntities().get(chatAdapter.getItemCount() - 1);
            if (lastExtEntity != null) {
                if (lastExtEntity.getMessageType() != -500) {
                    showtxt = lastExtEntity.showContent();
                    sendtime = lastExtEntity.getCreatetime();
                }
            }
        }

        switch (chatType) {
            case CONNECT_SYSTEM:
                CRobotChat.getInstance().updateRoomMsg(draft, showtxt, sendtime);
                break;
            case PRIVATE:
                ((CFriendChat) normalChat).updateRoomMsg(draft, showtxt, sendtime);
                break;
            case GROUP:
                if (lastExtEntity != null) {
                    GroupMemberEntity memberEntity = ContactHelper.getInstance().loadGroupMemberEntity(chatIdentify, lastExtEntity.getMessage_from());
                    if (memberEntity != null) {
                        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
                        if (!userBean.getUid().equals(memberEntity.getUid())) {
                            String memberName = memberEntity.getUsername();
                            showtxt = memberName + ": " + showtxt;
                        }
                    }
                }

                ((CGroupChat) normalChat).updateRoomMsg(draft, showtxt, sendtime);
                break;
        }
    }

    @OnClick({R.id.relativelayout_1})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.relativelayout_1:
                inputPanel.hideBottomPanel();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(activity, requestCode, permissions, grantResults, permissomCallBack);
    }

    @Override
    protected void onStop() {
        super.onStop();
        inputPanel.hideBottomPanel();
        MediaUtil.getInstance().freeMediaPlayerResource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
