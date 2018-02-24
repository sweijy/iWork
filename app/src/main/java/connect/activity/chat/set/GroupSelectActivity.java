package connect.activity.chat.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseFragmentActivity;
import connect.activity.chat.fragment.BaseDepartSelectFragment;
import connect.activity.chat.set.contract.GroupSelectContract;
import connect.activity.chat.set.presenter.GroupSelectPresenter;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import protos.Connect;

public class GroupSelectActivity extends BaseFragmentActivity implements GroupSelectContract.BView {

    @Bind(R.id.group_select_content)
    FrameLayout groupSelectContent;
    @Bind(R.id.txt_selected)
    TextView txtSelected;
    @Bind(R.id.btn_selected)
    Button btnSelected;
    @Bind(R.id.layout_selected)
    RelativeLayout layoutSelected;

    private GroupSelectActivity activity;
    private GroupSelectContract.Presenter presenter;
    private boolean isCreateGroup = true;
    private String uid = "";
    private Map<String, Object> selectMembers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * @param activity
     * @param iscreate true: 创建群组   false:邀请入群
     * @param uid
     */
    public static void startActivity(Activity activity, boolean iscreate, String uid) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("Is_Create", iscreate);
        bundle.putString("Uid", uid);
        ActivityUtil.next(activity, GroupSelectActivity.class, bundle);
    }

    @Override
    public void initView() {
        activity = this;
        isCreateGroup = getIntent().getBooleanExtra("Is_Create", true);
        uid = getIntent().getStringExtra("Uid");

        switchFragment(BaseDepartSelectFragment.startFragment());
        new GroupSelectPresenter(this).start();
    }

    @OnClick({R.id.btn_selected, R.id.layout_selected})
    void OnClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn_selected:
                selectFinish();
                break;
            case R.id.layout_selected:
                showSelectList();
                break;
        }
    }

    private void selectFinish() {
        ArrayList<Connect.Workmate> workmates = new ArrayList<Connect.Workmate>();
        for (Map.Entry<String, Object> it : selectMembers.entrySet()) {
            Connect.Workmate workmate = (Connect.Workmate) it.getValue();
            workmates.add(workmate);
        }

        if (isCreateGroup) {
            GroupCreateActivity.startActivity(activity, workmates);
        } else {
            presenter.inviteJoinGroup(uid, workmates);
        }
    }

    private void showSelectList(){
        ArrayList<Connect.Workmate> workmates = new ArrayList<Connect.Workmate>();
        for (Map.Entry<String, Object> it : selectMembers.entrySet()) {
            Connect.Workmate workmate = (Connect.Workmate) it.getValue();
            workmates.add(workmate);
        }

        DepartSelectShowAcitivty.startActivity(activity,workmates);
    }

    private void hideFragments() {
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

        //commit :IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void switchFragment(Fragment fragment){
        hideFragments();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.group_select_content, fragment);
        } else {
            fragmentTransaction.show(fragment);
        }

        //commit :IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void popBackStack(){
        getFragmentManager().popBackStack();
    }

    public void updateSelectMemeberCount(int count) {
        txtSelected.setText(getString(R.string.Chat_Selected_Member, count));
        btnSelected.setEnabled(count > 0);
        btnSelected.setText(getString(R.string.Chat_Determine_Count, count));
    }

    public boolean isCreateGroup() {
        return isCreateGroup;
    }

    public String getUid() {
        return uid;
    }

    public Map<String, Object> getSelectMembers() {
        return selectMembers;
    }

    public boolean isContains(String selectKey) {
        return selectMembers.containsKey(selectKey) || (isCreateGroup && uid.equals(selectKey));
    }

    public void removeWorkMate(String selectKey) {
        selectMembers.remove(selectKey);
    }

    public void addWorkMate(Connect.Workmate workmate) {
        String selectKey = workmate.getUid();
        selectMembers.put(selectKey, workmate);
    }

    @Override
    public void setPresenter(GroupSelectContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 100) {
            ArrayList<Connect.Workmate> workmates = (ArrayList<Connect.Workmate>) data.getSerializableExtra("List_Workmate");
            activity.getSelectMembers().clear();

            for (Connect.Workmate workmate : workmates) {
                activity.getSelectMembers().put(workmate.getUid(), workmate);
            }
            selectFinish();
        } else if (requestCode == 120) {
            ArrayList<Connect.Workmate> workmates = (ArrayList<Connect.Workmate>) data.getSerializableExtra("List_Workmate");
            activity.getSelectMembers().clear();

            for (Connect.Workmate workmate : workmates) {
                activity.getSelectMembers().put(workmate.getUid(), workmate);
            }
        }
    }
}
