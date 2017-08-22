package connect.activity.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.chat.adapter.ChatAdapter;
import connect.activity.chat.bean.MsgExtEntity;
import connect.activity.chat.bean.MsgSend;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.bean.RoomSession;
import connect.activity.chat.bean.Talker;
import connect.activity.chat.inter.FileUpLoad;
import connect.activity.chat.model.content.FriendChat;
import connect.activity.chat.model.content.GroupChat;
import connect.activity.chat.model.content.NormalChat;
import connect.activity.chat.model.content.RobotChat;
import connect.activity.chat.model.fileload.PhotoUpload;
import connect.activity.chat.model.fileload.VideoUpload;
import connect.activity.chat.set.GroupSetActivity;
import connect.activity.chat.set.SingleSetActivity;
import connect.activity.chat.view.ExBottomLayout;
import connect.activity.common.selefriend.SeleUsersActivity;
import connect.activity.wallet.TransferFriendActivity;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.DaoHelper.MessageHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.im.bean.MsgType;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.BitmapUtil;
import connect.utils.FileUtil;
import connect.utils.MediaUtil;
import connect.utils.TimeUtil;
import connect.utils.log.LogManager;
import connect.utils.permission.PermissionUtil;
import connect.widget.TopToolBar;
import connect.widget.album.AlbumActivity;
import connect.widget.album.model.ImageInfo;
import connect.widget.camera.CameraTakeActivity;
import protos.Connect;

/**
 * chat message
 * Created by gtq on 2016/11/22.
 */
public class ChatActivity extends BaseChatActvity {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @Bind(R.id.layout_exbottom)
    ExBottomLayout layoutExbottom;

    private String Tag = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity, Talker talker) {
        RoomSession.getInstance().setRoomType(talker.getTalkType());
        RoomSession.getInstance().setRoomKey(talker.getTalkKey());

        Bundle bundle = new Bundle();
        bundle.putSerializable(ROOM_TALKER, talker);
        ActivityUtil.next(activity, ChatActivity.class, bundle);
    }

    @Override
    public void initView() {
        super.initView();
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setLeftListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setRightListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (talker.getTalkType()) {
                    case 0:
                        SingleSetActivity.startActivity(activity, talker.getTalkKey());
                        break;
                    case 1:
                        GroupSetActivity.startActivity(activity, talker.getTalkKey());
                        break;
                }
            }
        });
        // robot/stranger donot show setting
        if (!(talker.getTalkType() == Connect.ChatType.CONNECT_SYSTEM_VALUE || normalChat.isStranger())) {
            toolbar.setRightImg(R.mipmap.menu_white);
        }

        ConversionSettingEntity chatSetEntity = ConversionSettingHelper.getInstance().loadSetEntity(talker.getTalkKey());
        long burntime = (chatSetEntity == null || chatSetEntity.getSnap_time() == null) ? 0 : chatSetEntity.getSnap_time();
        roomSession.setBurntime(burntime);
        updateBurnState(burntime == 0 ? 0 : 1);

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

        scrollHelper.attachToRecycleView(recyclerChat);
        loadChatInfor();

        PermissionUtil.getInstance().requestPermissom(activity,
                new String[]{PermissionUtil.PERMISSIM_RECORD_AUDIO, PermissionUtil.PERMISSIM_STORAGE},
                permissomCallBack);
    }

    @Override
    public void loadChatInfor() {
        LogManager.getLogger().d(Tag, "loadChatInfor()");
        new AsyncTask<Void, Void, List<MsgExtEntity>>() {

            @Override
            protected List<MsgExtEntity> doInBackground(Void... params) {
                return normalChat.loadMoreEntities(TimeUtil.getCurrentTimeInLong());
            }

            @Override
            protected void onPostExecute(List<MsgExtEntity> entities) {
                super.onPostExecute(entities);
                MsgExtEntity encryEntity = normalChat.encryptChatMsg();
                if (entities.size() < 20 && encryEntity != null) {
                    long lastTime = entities.size() <= 0 ? TimeUtil.getCurrentTimeInLong() :
                            entities.get(0).getCreatetime();
                    encryEntity.setCreatetime(lastTime);
                    entities.add(0, encryEntity);
                }

                chatAdapter.insertItems(entities);
            }
        }.execute();
    }

    @Override
    public void loadMoreMsgs() {
        LogManager.getLogger().d(Tag, "loadMoreMsgs()");
        new AsyncTask<Void, Void, List<MsgExtEntity>>() {
            @Override
            protected List<MsgExtEntity> doInBackground(Void... params) {
                long lastCreateTime = 0;
                List<MsgExtEntity> msgExtEntities = chatAdapter.getMsgEntities();
                if (msgExtEntities.size() > 0) {
                    MsgExtEntity baseEntity = msgExtEntities.get(0);
                    lastCreateTime = baseEntity.getCreatetime();
                }
                return normalChat.loadMoreEntities(lastCreateTime);
            }

            @Override
            protected void onPostExecute(List<MsgExtEntity> msgEntities) {
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
    public void updateBurnState(int state) {
        String titleName = "";
        switch (state) {
            case 0:// not start
                titleName = normalChat.nickName();
                if (titleName.length() > 15) {
                    titleName = titleName.substring(0, 12);
                    titleName += "...";
                }
                if (normalChat.chatType() == 0 || normalChat.chatType() == 2) {
                    toolbar.setTitle(titleName);
                } else {
                    List<GroupMemberEntity> memEntities = ContactHelper.getInstance().loadGroupMemEntity(normalChat.chatKey());
                    toolbar.setTitle(titleName + String.format(Locale.ENGLISH, "(%d)", memEntities.size()));
                }
                break;
            case 1://have started
                String name = normalChat.nickName();
                StringBuffer indexName = new StringBuffer();
                indexName.append(name.charAt(0));
                if (name.length() > 2) {
                    for (int i = 1; (i < name.length() - 1) && (i < 8); i++) {
                        indexName.append("*");
                    }
                }
                indexName.append(name.charAt(name.length() - 1));
                if (normalChat.chatType() == 0 || normalChat.chatType() == 2) {
                    toolbar.setTitle(R.mipmap.message_privacy_grey2x, indexName.toString());
                } else {
                    List<GroupMemberEntity> memEntities = ContactHelper.getInstance().loadGroupMemEntity(normalChat.chatKey());
                    toolbar.setTitle(indexName + String.format(Locale.ENGLISH, "(%d)", memEntities.size()));
                }
                break;
        }
    }

    /**
     * Accept new message to slide to the end
     *
     * @param bean
     */
    @Override
    public void adapterInsetItem(MsgExtEntity bean) {
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
                MsgSend.sendOuterMsg(MsgType.Photo, paths);
            } else if (type.equals(CameraTakeActivity.MEDIA_TYPE_VEDIO)) {
                int length = data.getIntExtra("length", 10);
                MsgSend.sendOuterMsg(MsgType.Video, path, length);
            }
        } else if (requestCode == AlbumActivity.OPEN_ALBUM_CODE && data != null) {
            List<ImageInfo> imageInfos = (List<ImageInfo>) data.getSerializableExtra("list");
            if (imageInfos != null && imageInfos.size() > 0) {
                for (ImageInfo info : imageInfos) {
                    String filePath = info.getImageFile().getAbsolutePath();
                    File tempFile = new File(filePath);
                    if (tempFile.exists() && tempFile.length() > 0) {
                        if (info.getFileType() == 0) {
                            List<String> paths = new ArrayList<>();
                            paths.add(info.getImageFile().getAbsolutePath());
                            MsgSend.sendOuterMsg(MsgType.Photo, paths);
                        } else {
                            int length = (int) (info.getImageFile().getVideoLength() / 1000);
                            MsgSend.sendOuterMsg(MsgType.Video, info.getImageFile().getAbsolutePath(), length);
                        }
                    }
                }
            }
        } else if (requestCode == CODE_REQUEST && resultCode == CODE_REQUEST) {//relay the message
            transpondTo(data);
        } else if (resultCode == RESULT_OK && requestCode == SeleUsersActivity.CODE_REQUEST) {
            ArrayList<ContactEntity> friendList = (ArrayList<ContactEntity>) data.getExtras().getSerializable("list");
            TransferFriendActivity.startActivity(activity, friendList, normalChat.chatKey());
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

        MsgExtEntity msgExtEntity = null;
        NormalChat normalChat = null;
        switch (roomType) {
            case 0:
                ContactEntity friendEntity = ContactHelper.getInstance().loadFriendEntity(roomkey);
                normalChat = new FriendChat(friendEntity);
                break;
            case 1:
                GroupEntity groupEntity = ContactHelper.getInstance().loadGroupEntity(roomkey);
                normalChat = new GroupChat(groupEntity);
                break;
            case 2:
                normalChat = new RobotChat();
                break;
        }

        FileUpLoad fileUpLoad = null;
        String content = (String) objects[1];
        MsgType msgType = MsgType.toMsgType(Integer.parseInt((String) objects[0]));
        switch (msgType) {
            case Text:
                msgExtEntity = normalChat.txtMsg(content);
                MessageHelper.getInstance().insertMsgExtEntity(msgExtEntity);
                normalChat.updateRoomMsg(null, msgExtEntity.showContent(), msgExtEntity.getCreatetime());
                normalChat.sendPushMsg(msgExtEntity);
                break;
            case Photo:
                msgExtEntity = normalChat.photoMsg(content, content, FileUtil.fileSize(content), 200, 200);
                fileUpLoad = new PhotoUpload(activity, normalChat, msgExtEntity, new FileUpLoad.FileUpListener() {
                    @Override
                    public void upSuccess(String msgid) {
                    }
                });
                fileUpLoad.fileHandle();
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
                fileUpLoad = new VideoUpload(activity, normalChat, msgExtEntity, new FileUpLoad.FileUpListener() {
                    @Override
                    public void upSuccess(String msgid) {
                    }
                });
                fileUpLoad.fileHandle();
                break;
        }
    }

    @Override
    public void saveRoomInfo() {
        String draft = inputPanel.getDraft();
        if (chatAdapter.getMsgEntities().size() != 0) {
            MsgExtEntity lastExtEntity = chatAdapter.getMsgEntities().get(chatAdapter.getItemCount() - 1);
            if (lastExtEntity != null) {
                String showtxt = "";
                long sendtime = 0;

                if (lastExtEntity.getMessageType() != -500) {
                    showtxt = lastExtEntity.showContent();
                    sendtime = lastExtEntity.getCreatetime();
                }
                normalChat.updateRoomMsg(draft, showtxt, sendtime, 0);
            }
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