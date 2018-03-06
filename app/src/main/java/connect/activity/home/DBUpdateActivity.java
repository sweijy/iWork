package connect.activity.home;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.ui.activity.R;
import connect.widget.DBUpgradeView;
import connect.widget.TopToolBar;

@Route(path = "/iwork/DBUpdateActivity")
public class DBUpdateActivity extends BaseActivity {

    private String Tag = "DBUpdateActivity";
    private DBUpdateActivity activity;

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.upgradeview)
    DBUpgradeView upgradeview;
    @Bind(R.id.txt1)
    TextView txt1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbupdate);
        ButterKnife.bind(this);
        initView();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 50:
                    ARouter.getInstance().build("/iwork/login/StartPageActivity")
                            .navigation();
                    break;
            }
        }
    };

    @Override
    public void initView() {
        activity = this;
        toolbarTop.setTitle(getString(R.string.Chat_Update_Database));

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(5000).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                upgradeview.setProgress(value);
                if (value < 100) {
                    txt1.setText(getString(R.string.Chat_Updating_Database) + "...");
                } else if (value == 100) {
                    txt1.setText(getString(R.string.Login_Update_successful) + "!");
                    handler.sendEmptyMessageDelayed(50, 1000);
                }
            }
        });
    }
}
