package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.chat.adapter.GroupCreateAdapter;
import connect.activity.chat.set.contract.GroupCreateContract;
import connect.activity.chat.set.presenter.GroupCreatePresenter;
import connect.activity.home.view.LineDecoration;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * GroupCreateActivity
 */
@Route(path = "/iwork/chat/set/GroupCreateActivity")
public class GroupCreateActivity extends BaseActivity implements GroupCreateContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.edittxt1)
    EditText edittxt1;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    @Autowired
    ArrayList<Connect.Workmate> workmateList;

    private GroupCreateActivity activity;
    private GroupCreateContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Chat_set_Create_New_Group));
        toolbar.setRightText(R.string.Chat_Complete);
        toolbar.setRightTextEnable(true);
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setRightTextEnable(false);

                String groupName = edittxt1.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    groupName = edittxt1.getHint().toString();
                }
                presenter.createGroup(groupName);

                Message message = new Message();
                message.what = 100;
                handler.sendMessageDelayed(message, 3000);
            }
        });

        workmateList = (ArrayList<Connect.Workmate>) getIntent().getSerializableExtra("workmateList");
        StringBuffer stringBuffer = new StringBuffer();
        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        stringBuffer.append(userBean.getName());
        stringBuffer.append(",");
        for (int i = 0; i < 2; i++) {
            Connect.Workmate workmate = workmateList.get(i);
            String showName = TextUtils.isEmpty(workmate.getName()) ?
                    workmate.getUsername() :
                    workmate.getName();
            stringBuffer.append(showName);

            if (i == 0) {
                stringBuffer.append(",");
            }
        }

        edittxt1.setText(stringBuffer.toString());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.addItemDecoration(new LineDecoration(activity));
        GroupCreateAdapter adapter = new GroupCreateAdapter();
        adapter.setData(workmateList);
        recyclerview.setAdapter(adapter);

        new GroupCreatePresenter(this).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    toolbar.setRightTextEnable(true);
                    break;
            }
        }
    };

    @Override
    public List<Connect.Workmate> groupMemberList() {
        return workmateList;
    }

    @Override
    public void setLeftEnanle(boolean b) {
        toolbar.setLeftEnable(b);
    }

    @Override
    public void setPresenter(GroupCreateContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
