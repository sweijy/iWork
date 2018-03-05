package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.chat.adapter.DepartSelectShowAdapter;
import connect.activity.chat.set.contract.DepartSelectShowContract;
import connect.activity.chat.set.presenter.DepartSelectShowPresenter;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;
import protos.Connect;

@Route(path = "/chat/set/DepartSelectShowAcitivty")
public class DepartSelectShowAcitivty extends BaseActivity implements DepartSelectShowContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private DepartSelectShowAcitivty acitivty;
    private boolean isCreateGroup = true;
    private String uid = "";
    private ArrayList<Connect.Workmate> workmates = new ArrayList<>();
    private DepartSelectShowAdapter showAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depart_selectshow);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity, boolean iscreate, String uid, ArrayList<Connect.Workmate> workmates) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("Is_Create", iscreate);
        bundle.putString("Uid", uid);
        bundle.putSerializable("List_Workmate", workmates);
        ActivityUtil.next(activity, DepartSelectShowAcitivty.class, bundle, 100);
    }

    @Override
    public void initView() {
        acitivty = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setRightText(getString(R.string.Common_OK));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackActivity();
            }
        });
        toolbar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFinish();
            }
        });

        isCreateGroup = getIntent().getBooleanExtra("Is_Create", true);
        uid = getIntent().getStringExtra("Uid");
        workmates = (ArrayList<Connect.Workmate>) getIntent().getSerializableExtra("List_Workmate");

        int workMateSize = workmates.size();
        toolbar.setTitle(getString(R.string.Chat_Select_Count, workmates.size()));
        toolbar.setRightTextEnable(isCreateGroup ? workMateSize >= 2 : workMateSize >= 1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(acitivty);
        showAdapter = new DepartSelectShowAdapter();
        showAdapter.setDepartRemoveListener(new DepartSelectShowAdapter.DepartRemoveListener() {
            @Override
            public void removeWorkMate(Connect.Workmate workmate) {
                workmates.remove(workmate);

                int workMateSize = workmates.size();
                toolbar.setTitle(getString(R.string.Chat_Select_Count, workMateSize));
                toolbar.setRightTextEnable(isCreateGroup ? workMateSize >= 2 : workMateSize >= 1);
            }
        });
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(showAdapter);
        showAdapter.setData(isCreateGroup,isCreateGroup ? uid : "", workmates);

        new DepartSelectShowPresenter(this).start();
    }

    private void goBackActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("List_Workmate", workmates);
        ActivityUtil.goBackWithResult(acitivty, 120, bundle);
    }

    private void selectFinish() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("List_Workmate", workmates);
        ActivityUtil.goBackWithResult(acitivty, 100, bundle);
    }

    @Override
    public void setPresenter(DepartSelectShowContract.Presenter presenter) {

    }

    @Override
    public Activity getActivity() {
        return acitivty;
    }
}
