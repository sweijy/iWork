package connect.activity.workbench;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragmentActivity;
import connect.activity.workbench.fragment.ApprovedFragment;
import connect.activity.workbench.fragment.AuditFragment;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;

public class VisitorsActivity extends BaseFragmentActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.to_audit_text)
    TextView toAuditText;
    @Bind(R.id.to_audit_line)
    View toAuditLine;
    @Bind(R.id.the_approved_text)
    TextView theApprovedText;
    @Bind(R.id.the_approved_line)
    View theApprovedLine;
    @Bind(R.id.content_fragment)
    FrameLayout contentFragment;

    private VisitorsActivity mActivity;

    private AuditFragment auditFragment;
    private ApprovedFragment approvedFragment;

    public static void lunchActivity(Activity activity) {
        ActivityUtil.next(activity, VisitorsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbench_visitors);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Work_Visitors_info);
        toolbarTop.setRightText(R.string.Link_Invite);

        auditFragment = AuditFragment.startFragment();
        approvedFragment = ApprovedFragment.startFragment();

        switchFragment(0);
        toAuditText.setSelected(true);
        toAuditLine.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.right_lin)
    void shareInvite(View view) {
        ActivityUtil.next(mActivity, ShareVisitorActivity.class);
    }

    @OnClick({R.id.to_audit_text, R.id.the_approved_text})
    public void OnClickListener(View view) {
        toAuditText.setSelected(false);
        toAuditLine.setVisibility(View.GONE);
        theApprovedText.setSelected(false);
        theApprovedLine.setVisibility(View.GONE);

        switch (view.getId()) {
            case R.id.to_audit_text:
                switchFragment(0);
                toAuditText.setSelected(true);
                toAuditLine.setVisibility(View.VISIBLE);
                auditFragment.initData();
                break;
            case R.id.the_approved_text:
                switchFragment(1);
                theApprovedText.setSelected(true);
                theApprovedLine.setVisibility(View.VISIBLE);
                approvedFragment.initData();
                break;
        }
    }

    public void switchFragment(int code) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment.isVisible()) {
                    fragmentTransaction.hide(fragment);
                }
            }
        }
        switch (code) {
            case 0:
                if (!auditFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content_fragment, auditFragment);
                } else {
                    fragmentTransaction.show(auditFragment);
                }
                break;
            case 1:
                if (!approvedFragment.isAdded()) {
                    fragmentTransaction.add(R.id.content_fragment, approvedFragment);
                } else {
                    fragmentTransaction.show(approvedFragment);
                }
                break;
        }

        //commit :IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ActivityUtil.goBack(mActivity);
                break;
        }
        return true;
    }

}
