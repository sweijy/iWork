package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.chat.set.contract.GroupNameContract;
import connect.activity.chat.set.presenter.GroupNamePresenter;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;

@Route(path = "/iwork/chat/set/GroupNameActivity")
public class GroupNameActivity extends BaseActivity implements GroupNameContract.BView{

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.txt1)
    TextView txt1;
    @Bind(R.id.edittxt1)
    EditText edittxt1;

    @Autowired
    String identify;

    private GroupNameActivity activity;
    private static String TAG = "_GroupNameActivity";

    private GroupNameTextWatcher textWatcher=new GroupNameTextWatcher();
    private GroupNameContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_name);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Link_Group));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setRightText(R.string.Common_OK);
        toolbar.setRightTextEnable(false);
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = edittxt1.getText().toString().trim();
                if (groupName.length() >= 2) {
                    presenter.updateGroupName(groupName);
                }
            }
        });

        edittxt1.addTextChangedListener(textWatcher);
        new GroupNamePresenter(this).start();
    }

    private class GroupNameTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() <2) {
                toolbar.setRightTextEnable(false);
            } else {
                toolbar.setRightTextEnable(true);
            }
        }
    }

    @Override
    public String getRoomKey() {
        return identify;
    }

    @Override
    public void setPresenter(GroupNameContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void groupName(String groupname) {
        edittxt1.setText(groupname);
    }
}
