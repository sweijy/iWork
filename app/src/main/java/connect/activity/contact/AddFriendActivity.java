package connect.activity.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.contact.adapter.NewRequestAdapter;
import connect.activity.contact.bean.SourceType;
import connect.activity.contact.contract.AddFriendContract;
import connect.activity.contact.presenter.AddFriendPresenter;
import connect.activity.home.view.LineDecoration;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.FriendRequestEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ConfigUtil;
import connect.widget.TopToolBar;

/**
 * add new friend.
 */
public class AddFriendActivity extends BaseActivity implements AddFriendContract.View {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.wallet_menu_recycler)
    RecyclerView recycler;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private AddFriendActivity mActivity;
    private AddFriendContract.Presenter presenter;
    private NewRequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add_friend);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != presenter) {
            presenter.queryFriend();
        }
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(null, R.string.Link_New_friend);
        new AddFriendPresenter(this).start();

        presenter.initGrid(recycler);
        requestAdapter = new NewRequestAdapter(mActivity);
        requestAdapter.setOnAcceptListener(onAcceptListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.addItemDecoration(new LineDecoration(mActivity));
        recyclerview.setAdapter(requestAdapter);
        recyclerview.addOnScrollListener(onScrollListener);

        presenter.requestRecommendUser();
        presenter.updateRequestListStatus();
    }

    @OnClick(R.id.left_img)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @Override
    public void itemClick(int tag) {
        switch (tag) {
            case 0:
                ActivityUtil.nextBottomToTop(mActivity, ScanAddFriendActivity.class, null, -1);
                break;
            case 1:
                ActivityUtil.next(mActivity, AddFriendPhoneActivity.class);
                break;
            case 2:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, ConfigUtil.getInstance().shareCardAddress()
                        + "?address=" + SharedPreferenceUtil.getInstance().getUser().getUid());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "share to"));
                break;
            default:
                break;
        }
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener(){
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            requestAdapter.closeMenu();
        }
    };

    private NewRequestAdapter.OnAcceptListener onAcceptListener = new NewRequestAdapter.OnAcceptListener() {
        @Override
        public void accept(int position, FriendRequestEntity entity) {
            if (entity.getStatus() == NewRequestAdapter.RECOMMEND_ADD_FRIEND) {
                StrangerInfoActivity.startActivity(mActivity, entity.getUid(), SourceType.RECOMMEND);
            } else {
                AddFriendAcceptActivity.startActivity(mActivity, entity);
            }
        }

        @Override
        public void itemClick(int position, FriendRequestEntity entity) {
            if (TextUtils.isEmpty(entity.getUid())) {
                //load more
                ActivityUtil.next(mActivity, AddFriendRecommendActivity.class);
            } else if (entity.getStatus() == NewRequestAdapter.RECOMMEND_ADD_FRIEND) {
                //introduce
                StrangerInfoActivity.startActivity(mActivity, entity.getUid(), SourceType.RECOMMEND);
            } else {
                ContactEntity friendEntity = ContactHelper.getInstance().loadFriendEntity(entity.getUid());
                if (entity.getStatus() == NewRequestAdapter.ACCEPTE_ADD_FRIEND) {
                    AddFriendAcceptActivity.startActivity(mActivity, entity);
                } else if (friendEntity == null) {
                    StrangerInfoActivity.startActivity(mActivity, entity.getUid(), SourceType.getSourceType(entity.getSource()));
                } else {
                    FriendInfoActivity.startActivity(mActivity, entity.getUid());
                }
            }
        }

        @Override
        public void deleteItem(int position, FriendRequestEntity entity) {
            requestAdapter.closeMenu();
            if (entity.getStatus() == NewRequestAdapter.RECOMMEND_ADD_FRIEND) {
                presenter.requestNoInterest(entity.getUid());
            } else {
                ContactHelper.getInstance().deleteRequestEntity(entity.getUid());
                presenter.queryFriend();
            }
        }
    };

    @Override
    public void setPresenter(AddFriendContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }

    /**
     * After the access to the data update interface
     */
    @Override
    public void notifyData(boolean isShowMoreRecommend, ArrayList<FriendRequestEntity> listFina) {
        requestAdapter.setDataNotify(isShowMoreRecommend, listFina);
    }

}
