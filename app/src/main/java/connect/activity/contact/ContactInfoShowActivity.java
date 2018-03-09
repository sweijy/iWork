package connect.activity.contact;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.DepartmentAvatar;
import connect.widget.TopToolBar;
import protos.Connect;

@Route(path = "/iwork/contact/ContactInfoShowActivity")
public class ContactInfoShowActivity extends BaseActivity {

    @Autowired
    Connect.Workmate workmate;
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

    private ContactInfoShowActivity mActivity;

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
        toolbar.setRightTextEnable(false);

        workmate = (Connect.Workmate) getIntent().getExtras().getSerializable("workmate");
        showView();
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    private void showView() {
        chatBtn.setVisibility(View.GONE);
        contactBtn.setVisibility(View.GONE);
        avatarImageview.setVisibility(View.GONE);

        avatarImage.setVisibility(View.VISIBLE);
        nameText.setText(workmate.getName());
        if (workmate.getGender() == 1) {
            genderImage.setImageResource(R.mipmap.man);
        } else {
            genderImage.setImageResource(R.mipmap.woman);
        }
        departmentText.setText(workmate.getOU());
        accountText.setText(workmate.getUsername());
        avatarImage.setAvatarName(workmate.getName(), true, workmate.getGender());
    }

}
