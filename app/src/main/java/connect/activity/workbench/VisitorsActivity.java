package connect.activity.workbench;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragmentActivity;
import connect.activity.login.bean.UserBean;
import connect.activity.workbench.fragment.ApprovedFragment;
import connect.activity.workbench.fragment.AuditFragment;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.FileUtil;
import connect.utils.ProgressUtil;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.permission.PermissionUtil;
import connect.utils.system.SystemDataUtil;
import connect.utils.system.SystemUtil;
import connect.widget.TopToolBar;
import connect.widget.zxing.utils.CreateScan;
import protos.Connect;

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
    private UserBean userBean;

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
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Work_Visitors_info);
        toolbarTop.setRightText(R.string.Link_Invite);
        userBean = SharedPreferenceUtil.getInstance().getUser();

        auditFragment = AuditFragment.startFragment();
        approvedFragment = ApprovedFragment.startFragment();

        switchFragment(0);
        toAuditText.setSelected(true);
        toAuditLine.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.left_img)
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

}
