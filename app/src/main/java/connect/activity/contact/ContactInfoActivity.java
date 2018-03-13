package connect.activity.contact;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.chat.bean.GroupMemberUtil;
import connect.activity.contact.bean.ContactNotice;
import connect.activity.contact.model.ContactListManage;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.DepartmentAvatar;
import connect.widget.TopToolBar;
import instant.utils.SharedUtil;
import protos.Connect;

@Route(path = "/iwork/contact/ContactInfoActivity")
public class ContactInfoActivity extends BaseActivity {


    @Autowired
    String uid;
    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.avatar_imageview)
    ImageView avatarImageview;
    @Bind(R.id.avatar_image)
    DepartmentAvatar avatarImage;
    @Bind(R.id.avatar_rela)
    RelativeLayout avatarRela;
    @Bind(R.id.name_text)
    TextView nameText;
    @Bind(R.id.gender_image)
    ImageView genderImage;
    @Bind(R.id.account_text)
    TextView accountText;
    @Bind(R.id.department_text)
    TextView departmentText;
    @Bind(R.id.chat_btn)
    Button chatBtn;
    @Bind(R.id.contact_btn)
    Button contactBtn;

    private ContactInfoActivity mActivity;
    private ContactEntity contactEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(null, R.string.Set_Personal_information);

        uid = getIntent().getExtras().getString("uid", "");
        contactEntity = (ContactEntity) getIntent().getExtras().getSerializable("contactEntity");
        if(contactEntity != null){
            showView();
        }
        searchUser(uid);
    }

    private void showView() {
        nameText.setText(contactEntity.getName());
        departmentText.setText(contactEntity.getOu());
        accountText.setText(contactEntity.getUsername());
        if (contactEntity.getGender() == 1) {
            genderImage.setImageResource(R.mipmap.man);
        } else {
            genderImage.setImageResource(R.mipmap.woman);
        }

        if (ContactHelper.getInstance().loadFriendByUid(contactEntity.getUid()) == null) {
            contactBtn.setText(R.string.Link_Add_contacts);
            contactBtn.setTextColor(mActivity.getResources().getColor(R.color.color_414141));
        } else {
            contactBtn.setText(R.string.Link_Delete_contact);
            contactBtn.setTextColor(mActivity.getResources().getColor(R.color.color_F56565));
        }
        avatarImageview.setVisibility(View.VISIBLE);
        avatarImage.setVisibility(View.GONE);
        GlideUtil.loadAvatarRound(avatarImageview, contactEntity.getAvatar(), 8);
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.contact_btn)
    void attention(View view) {
        if (ContactHelper.getInstance().loadFriendByUid(contactEntity.getUid()) == null) {
            addFollow(true);
        } else {
            addFollow(false);
        }
    }

    @OnClick(R.id.chat_btn)
    void chat(View view) {
        if (contactEntity != null && !TextUtils.isEmpty(contactEntity.getUid())) {
            ARouter.getInstance().build("/iwork/chat/ChatActivity")
                    .withSerializable("CHAT_TYPE", Connect.ChatType.PRIVATE)
                    .withString("CHAT_IDENTIFY", contactEntity.getUid())
                    .navigation();
        }
    }

    private void searchUser(final String uid) {
        if(TextUtils.isEmpty(uid)){
            return;
        }
        Connect.SearchUser searchUser = Connect.SearchUser.newBuilder()
                .setCriteria(uid)
                .setTyp(2)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_WORKMATE_SEARCH, searchUser, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.Workmates workmates = Connect.Workmates.parseFrom(structData.getPlainData());
                    Connect.Workmate workmate = workmates.getList(0);
                    contactEntity = new ContactListManage().convertContactEntity(workmate);
                    showView();
                    // 更新通信录好友头像
                    final ContactEntity contactEntityLocal = ContactHelper.getInstance().loadFriendByUid(workmate.getUid());
                    if (contactEntityLocal != null) {
                        if (!workmate.getAvatar().equals(contactEntityLocal.getAvatar())) {
                            contactEntityLocal.setAvatar(workmate.getAvatar());
                            showView();
                            ContactHelper.getInstance().insertContact(contactEntityLocal);
                            ContactNotice.receiverContact();
                        }
                    }
                    // 更新聊天列表头像
                    ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(uid);
                    if (conversionEntity != null) {
                        conversionEntity.setAvatar(workmate.getAvatar());
                        conversionEntity.setName(workmate.getName());
                        ConversionHelper.getInstance().insertRoomEntity(conversionEntity);
                    }
                    // 更新群成员列表头像
                    List<GroupMemberEntity> memberEntities = ContactHelper.getInstance().loadGroupMembersByUid(uid);
                    if (memberEntities != null && memberEntities.size() > 0) {
                        GroupMemberUtil.getIntance().clearMembersMap();
                        for (GroupMemberEntity entity : memberEntities) {
                            entity.setUsername(workmate.getName());
                            entity.setAvatar(workmate.getAvatar());
                        }
                        ContactHelper.getInstance().inserGroupMemEntity(memberEntities);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {}
        });
    }

    private void addFollow(final boolean isAdd) {
        Connect.UserFollow userFollow = Connect.UserFollow.newBuilder()
                .setFollow(isAdd)
                .setUid(contactEntity.getUid())
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_USERS_FOLLOW, userFollow, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.WorkmateVersion workmateVersion = Connect.WorkmateVersion.parseFrom(structData.getPlainData());
                    SharedUtil.getInstance().putValue(SharedUtil.CONTACTS_VERSION, workmateVersion.getVersion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isAdd) {
                    ContactHelper.getInstance().insertContact(contactEntity);
                    ToastUtil.getInstance().showToast(R.string.Link_Added_to_contact);

                    ContactNotice.receiverContact();
                    contactBtn.setText(R.string.Link_Delete_contact);
                    contactBtn.setTextColor(mActivity.getResources().getColor(R.color.color_F56565));
                } else {
                    ContactHelper.getInstance().deleteEntity(contactEntity.getUid());
                    ToastUtil.getInstance().showToast(R.string.Link_Deleted_contact);

                    ContactNotice.receiverContact();
                    contactBtn.setText(R.string.Link_Add_contacts);
                    contactBtn.setTextColor(mActivity.getResources().getColor(R.color.color_161A21));
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
            }
        });
    }

}
