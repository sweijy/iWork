package connect.activity.contact;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.DepartmentAvatar;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * Created by Administrator on 2018/2/28 0028.
 */

public class ContactInfoShowActivity extends BaseActivity {

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
    @Bind(R.id.sign_tv)
    TextView signTv;
    @Bind(R.id.number_text)
    TextView numberText;
    @Bind(R.id.number_tv)
    TextView numberTv;
    @Bind(R.id.department_tv)
    TextView departmentTv;
    @Bind(R.id.phone_tv)
    TextView phoneTv;
    @Bind(R.id.cell_image)
    ImageView cellImage;
    @Bind(R.id.chat_btn)
    Button chatBtn;

    private ContactInfoShowActivity mActivity;
    private Connect.Workmate workmate;

    public static void lunchActivity(Activity activity, Connect.Workmate workmate) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", workmate);
        ActivityUtil.next(activity, ContactInfoShowActivity.class, bundle);
    }

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
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(null, R.string.Chat_Contact_details);
        toolbar.setRightTextEnable(false);

        workmate = (Connect.Workmate) getIntent().getExtras().getSerializable("bean");
        numberText.setText(mActivity.getString(R.string.Link_Employee_number) + ":");

        showView();
    }

    @OnClick(R.id.left_img)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    private void showView() {
        chatBtn.setVisibility(View.GONE);
        avatarImageview.setVisibility(View.GONE);
        avatarImage.setVisibility(View.VISIBLE);

        nameText.setText(workmate.getName());
        if (workmate.getGender() == 1) {
            genderImage.setImageResource(R.mipmap.man);
        } else {
            genderImage.setImageResource(R.mipmap.woman);
        }
        numberTv.setText(workmate.getEmpNo());
        departmentTv.setText(workmate.getOU());
        phoneTv.setText(workmate.getMobile());
        avatarImage.setAvatarName(workmate.getName(), true, workmate.getGender());
    }

}
