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
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.UriUtil;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.DepartmentAvatar;
import connect.widget.TopToolBar;
import protos.Connect;

@Route(path = "/iwork/contact/ContactDepartmentActivity")
public class ContactDepartmentActivity extends BaseActivity {

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

    private ContactDepartmentActivity mActivity;
    private Connect.Workmate workmate;
    @Autowired
    String userName;

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

        userName = getIntent().getExtras().getString("userName", "");
        workmate = (Connect.Workmate) getIntent().getExtras().getSerializable("workmate");
        if(workmate != null){
            showView();
        }
        searchUser(userName);
    }

    private void showView() {
        nameText.setText(workmate.getName());
        departmentText.setText(workmate.getOrganizational());
        accountText.setText(workmate.getUsername());
        if (workmate.getGender() == 1) {
            genderImage.setImageResource(R.mipmap.man);
        } else {
            genderImage.setImageResource(R.mipmap.woman);
        }

        if (ContactHelper.getInstance().loadFriendByUid(workmate.getUsername()) == null) {
            contactBtn.setText(R.string.Link_Add_contacts);
            contactBtn.setTextColor(mActivity.getResources().getColor(R.color.color_414141));
        } else {
            contactBtn.setText(R.string.Link_Delete_contact);
            contactBtn.setTextColor(mActivity.getResources().getColor(R.color.color_F56565));
        }
        if(TextUtils.isEmpty(workmate.getAvatar())){
            avatarImageview.setVisibility(View.GONE);
            avatarImage.setVisibility(View.VISIBLE);
            avatarImage.setAvatarName(workmate.getName(), workmate.getGender());
        }else{
            avatarImageview.setVisibility(View.VISIBLE);
            avatarImage.setVisibility(View.GONE);
            GlideUtil.loadAvatarRound(avatarImageview, workmate.getAvatar(), 8);
        }
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.chat_btn)
    void chat(View view) {
        if (workmate != null) {
            ARouter.getInstance().build("/iwork/chat/ChatActivity")
                    .withSerializable("chatType", Connect.ChatType.PRIVATE)
                    .withString("chatIdentify", workmate.getUid())
                    .navigation();
        }
    }

    private void searchUser(final String userName) {
        if(TextUtils.isEmpty(userName)){
            return;
        }
        Connect.SearchUser searchUser = Connect.SearchUser.newBuilder()
                .setCriteria(userName)
                .setTyp(1)
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_WORKMATE_SEARCH, searchUser, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.Workmates workmates = Connect.Workmates.parseFrom(structData.getPlainData());
                    workmate = workmates.getList(0);
                    showView();

                    if(TextUtils.isEmpty(workmate.getUid())){
                        // 更新聊天列表头像
                        ConversionEntity conversionEntity = ConversionHelper.getInstance().loadRoomEnitity(workmate.getUid());
                        if (conversionEntity != null) {
                            conversionEntity.setAvatar(workmate.getAvatar());
                            conversionEntity.setName(workmate.getName());
                            ConversionHelper.getInstance().insertRoomEntity(conversionEntity);
                        }
                        // 更新群成员列表头像
                        List<GroupMemberEntity> memberEntities = ContactHelper.getInstance().loadGroupMembersByUid(workmate.getUid());
                        if (memberEntities != null && memberEntities.size() > 0) {
                            GroupMemberUtil.getIntance().clearMembersMap();
                            for (GroupMemberEntity entity : memberEntities) {
                                entity.setUsername(workmate.getName());
                                entity.setAvatar(workmate.getAvatar());
                            }
                            ContactHelper.getInstance().inserGroupMemEntity(memberEntities);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {}
        });
    }

}
