package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.chat.set.contract.RobotSetContract;
import connect.activity.chat.set.presenter.RobotSetPresenter;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;

/**
 * RobotSetActivity
 */
@Route(path = "/iwork/chat/set/RobotSetActivity")
public class RobotSetActivity extends BaseActivity implements RobotSetContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.linearlayout)
    LinearLayout linearlayout;

    private RobotSetActivity activity;
    private RobotSetContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robotset);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Chat_System_Set));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        new RobotSetPresenter(this).start();
    }

    @Override
    public void setPresenter(RobotSetContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public String getRoomKey() {
        return "iwork";
    }

    @Override
    public void showContact(View view) {
        linearlayout.addView(view);
    }
}
