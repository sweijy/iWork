package connect.activity.set;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.glide.GlideUtil;
import connect.widget.TopToolBar;

/**
 * The user basic information.
 */
@Route(path = "/iwork/set/UserInfoActivity")
public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.avatar_ll)
    LinearLayout avatarLl;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.name_ll)
    LinearLayout nameLl;
    @Bind(R.id.avatar_iv)
    ImageView avatarIv;
    @Bind(R.id.employee_number_tv)
    TextView employeeNumberTv;

    private UserInfoActivity mActivity;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Set_My_Profile);

        userBean = SharedPreferenceUtil.getInstance().getUser();
        GlideUtil.loadAvatarRound(avatarIv, userBean.getAvatar());

        nameTv.setText(userBean.getName());
        employeeNumberTv.setText(userBean.getEmp_no());
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.avatar_ll)
    void goAvatar(View view) {
        UserInfoAvatarActivity.startActivity(mActivity);
    }

}
