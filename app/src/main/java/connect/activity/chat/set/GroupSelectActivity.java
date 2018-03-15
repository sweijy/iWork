package connect.activity.chat.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.base.BaseFragmentActivity;
import connect.activity.chat.fragment.BaseDepartSelectFragment;
import connect.activity.chat.set.contract.GroupSelectContract;
import connect.activity.chat.set.presenter.GroupSelectPresenter;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * 添加新成员/创建群
 */
@Route(path = "/iwork/chat/set/GroupSelectActivity")
public class GroupSelectActivity extends BaseFragmentActivity implements GroupSelectContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.group_select_content)
    FrameLayout groupSelectContent;

    @Autowired
    boolean isCreateGroup = true;

    @Autowired
    String identify;

    private GroupSelectActivity activity;
    private GroupSelectContract.Presenter presenter;
    private UserBean userBean = SharedPreferenceUtil.getInstance().getUser();

    private Map<String, Object> groupMemebers = new LinkedHashMap<>();
    private Map<String, Object> selectMembers = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbarCancel(true);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setTitle(getResources().getString(R.string.Chat_Choose_contact));
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setRightTextEnable(false);
                handler.sendEmptyMessageDelayed(100, 2000);

                selectFinish();
            }
        });
        updateSelectMemeberCount(0);

        if (!isCreateGroup) {
            List<GroupMemberEntity> memberEntities = ContactHelper.getInstance().loadGroupMemberEntities(identify);
            for (GroupMemberEntity entity : memberEntities) {
                String memberUid = entity.getUid();
                groupMemebers.put(memberUid, memberUid);
            }
        }

        switchFragment(BaseDepartSelectFragment.startFragment());
        new GroupSelectPresenter(this).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    toolbar.setRightTextEnable(true);
                    break;
            }
        }
    };

    private void selectFinish() {
        ArrayList<Connect.Workmate> workmates = new ArrayList<Connect.Workmate>();
        for (Map.Entry<String, Object> it : selectMembers.entrySet()) {
            Connect.Workmate workmate = (Connect.Workmate) it.getValue();
            workmates.add(workmate);
        }

        if (isCreateGroup) {
            presenter.createGroup(workmates);
        } else {
            presenter.inviteJoinGroup(identify, workmates);
        }
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

    public void popBackLastFragment() {
        BaseFragment baseFragment = BaseDepartSelectFragment.startFragment();
        switchFragment(baseFragment);
    }

    public void switchFragment(BaseFragment fragment) {
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

    public void toolbarCancel(boolean istxt){
//        if(istxt){
//            toolbar.setLeftText(R.string.Chat_Member_Cancel);
//        }else{
//            toolbar.setLeftImg(R.mipmap.back_white);
//        }
        toolbar.setLeftText(R.string.Chat_Member_Cancel);
    }

    public boolean isCreateGroup() {
        return isCreateGroup;
    }

    public String getUid() {
        return identify;
    }

    public Map<String, Object> getSelectMembers() {
        return selectMembers;
    }

    public boolean isContains(String selectKey) {
        return selectMembers.containsKey(selectKey) ||
                selectKey.equals(userBean.getUid()) ||
                (isCreateGroup && identify.equals(selectKey)) ||
                (!isCreateGroup && groupMemebers.containsKey(selectKey));
    }

    public boolean isSelected(String selectKey) {
        return (isCreateGroup() && !identify.equals(selectKey)) ||
                (!isCreateGroup() && !groupMemebers.containsKey(selectKey));
    }

    public void updateSelectMemeberCount(int count) {
        boolean isenable = isCreateGroup() ? count > 1 : count > 0;
        toolbar.setRightTextEnable(isenable);
        if (count <= 0) {
            toolbar.setRightTextEnable(false);
            toolbar.setRightText(getString(R.string.Common_OK));
        } else {
            toolbar.setRightTextEnable(true);
            toolbar.setRightText(getString(R.string.Chat_Confirm_Number, count));
        }
    }

    public void removeWorkMate(String selectKey) {
        selectMembers.remove(selectKey);
        updateSelectMemeberCount(selectMembers.size());
    }

    public void addWorkMate(Connect.Workmate workmate) {
        String selectKey = workmate.getUid();
        if (!userBean.getUid().equals(selectKey)) {
            selectMembers.put(selectKey, workmate);
            updateSelectMemeberCount(selectMembers.size());
        }
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
            getSelectMembers().clear();

            for (Connect.Workmate workmate : workmates) {
                activity.addWorkMate(workmate);
            }

            BaseDepartSelectFragment selectFragment = BaseDepartSelectFragment.startFragment();
            switchFragment(selectFragment);

            selectFinish();
        } else if (resultCode == 120) {
            ArrayList<Connect.Workmate> workmates = (ArrayList<Connect.Workmate>) data.getSerializableExtra("List_Workmate");
            getSelectMembers().clear();

            for (Connect.Workmate workmate : workmates) {
                activity.addWorkMate(workmate);
            }

            BaseDepartSelectFragment selectFragment = BaseDepartSelectFragment.startFragment();
            switchFragment(selectFragment);
        }
    }
}
