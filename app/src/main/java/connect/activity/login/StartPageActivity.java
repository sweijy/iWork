package connect.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.huawei.android.hms.agent.HuaweiRegister;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.home.HomeActivity;
import connect.activity.login.contract.StartContract;
import connect.activity.login.presenter.StartPagePresenter;
import connect.ui.activity.R;

/**
 * The App start page.
 */
public class StartPageActivity extends BaseActivity implements StartContract.View {

    @Bind(R.id.start_img)
    ImageView startImg;

    private static String TAG = "_StartPageActivity";
    private StartPageActivity mActivity;
    private StartContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        startImg.setImageResource(R.mipmap.bg_start_man);
        new StartPagePresenter(mActivity).start();
    }

    @Override
    public void setPresenter(StartContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void goIntoLoginForPhone() {
        LoginUserActivity.startActivity(mActivity);
    }

    @Override
    public void goIntoHome() {
        HomeActivity.startActivity(mActivity);
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }
}
