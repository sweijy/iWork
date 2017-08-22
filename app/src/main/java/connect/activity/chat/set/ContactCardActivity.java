package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.chat.set.contract.ContactCardContract;
import connect.activity.chat.set.presenter.ContactCardPresenter;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.im.bean.MsgType;
import connect.ui.activity.R;
import connect.activity.chat.bean.MsgSend;
import connect.activity.chat.bean.RoomSession;
import connect.activity.chat.model.FriendCompara;
import connect.activity.chat.adapter.ContactCardAdapter;
import connect.activity.base.BaseActivity;
import connect.utils.ActivityUtil;
import connect.widget.SideBar;
import connect.widget.TopToolBar;

/**
 * Created by gtq on 2016/12/13.
 */
public class ContactCardActivity extends BaseActivity implements ContactCardContract.BView {

    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.siderbar)
    SideBar siderbar;
    @Bind(R.id.toolbar)
    TopToolBar toolbar;

    private ContactCardActivity activity;
    private String pubKey;
    private boolean move;
    private int topPosi;
    private LinearLayoutManager linearLayoutManager;
    private ContactCardContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactcard);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity) {
        startActivity(activity,null);
    }

    public static void startActivity(Activity activity,String pubkey) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PUBKEY", pubkey);
        ActivityUtil.next(activity, ContactCardActivity.class, bundle);
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Chat_Send_a_namecard));
        toolbar.setLeftListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        String type = getIntent().getExtras().getString("type");

        pubKey = getIntent().getStringExtra("PUBKEY");

        linearLayoutManager = new LinearLayoutManager(activity);
        List<ContactEntity> friendEntities = ContactHelper.getInstance().loadFriend(RoomSession.getInstance().getRoomKey());
        Collections.sort(friendEntities, new FriendCompara());

        final ContactCardAdapter contactCardAdapter = new ContactCardAdapter(activity, friendEntities);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(contactCardAdapter);
        contactCardAdapter.setItemClickListener(new ContactCardAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v) {
                ContactEntity entity = (ContactEntity) v.getTag();
                MsgSend.sendOuterMsg(MsgType.Name_Card,entity.getPub_key(),entity.getUsername(),entity.getAvatar());
                ActivityUtil.goBack(activity);
            }
        });
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (move) {
                    move = false;
                    int n = topPosi - linearLayoutManager.findFirstVisibleItemPosition();
                    if (0 <= n && n < recyclerview.getChildCount()) {
                        int top = recyclerview.getChildAt(n).getTop();
                        recyclerview.scrollBy(0, top);
                    }
                }
            }
        });

        siderbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactCardAdapter.getPositionForSection(s.charAt(0));
                moveToPosition(position);
            }
        });

        new ContactCardPresenter(this).start();
    }

    private void moveToPosition(int posi) {
        this.topPosi = posi;
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        if (posi <= firstItem) {
            recyclerview.scrollToPosition(posi);
        } else if (posi <= lastItem) {
            int top = recyclerview.getChildAt(posi - firstItem).getTop();
            recyclerview.scrollBy(0, top);
        } else {
            recyclerview.scrollToPosition(posi);
            move = true;
        }
    }

    @Override
    public String getRoomKey() {
        return pubKey;
    }

    @Override
    public void setPresenter(ContactCardContract.Presenter presenter) {

    }

    @Override
    public Activity getActivity() {
        return activity;
    }
}