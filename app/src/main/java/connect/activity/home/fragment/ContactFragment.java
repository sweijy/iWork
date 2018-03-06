package connect.activity.home.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.contact.bean.ContactNotice;
import connect.activity.home.adapter.ContactAdapter;
import connect.activity.home.bean.ContactBean;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.widget.SideBar;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * The address book contacts
 */
@Route(path = "/iwork/home/fragment/ContactFragment")
public class ContactFragment extends BaseFragment {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.side_bar)
    SideBar sideBar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;

    private FragmentActivity mActivity;
    private ContactAdapter adapter;

    public static ContactFragment startFragment() {
        ContactFragment contactFragment = new ContactFragment();
        return contactFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        toolbarTop.setTitle(null, R.string.Link_Contacts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        adapter = new ContactAdapter(mActivity);
        recyclerview.setAdapter(adapter);
        sideBar.setOnTouchingLetterChangedListener(changedListener);

        adapter.setOnSideMenuListener(onSideMenuListener);
        adapter.updateContact(adapter.updateTypeContact);
    }

    SideBar.OnTouchingLetterChangedListener changedListener = new SideBar.OnTouchingLetterChangedListener(){
        @Override
        public void onTouchingLetterChanged(String s) {
            int position = adapter.getPositionForSection(s.charAt(0));
            if (position >= 0) {
                recyclerview.scrollToPosition(position);
            }
        }
    };

    private ContactAdapter.OnItemChildListener onSideMenuListener = new ContactAdapter.OnItemChildListener() {
        @Override
        public void itemClick(int position, ContactBean entity) {
            switch (entity.getStatus()) {
                case 6:
                    ARouter.getInstance().build("/chat/ChatActivity")
                            .withSerializable("CHAT_TYPE", Connect.ChatType.CONNECT_SYSTEM)
                            .withString("CHAT_IDENTIFY", "Connect")
                            .navigation();
                    break;
                case 2:
                    ARouter.getInstance().build("/chat/ChatActivity")
                            .withSerializable("CHAT_TYPE", Connect.ChatType.GROUP)
                            .withString("CHAT_IDENTIFY", entity.getUid())
                            .navigation();
                    break;
                case 1:
                    ContactEntity contactEntity = ContactHelper.getInstance().loadFriendByUid(entity.getUid());

                    ARouter.getInstance().build("/iwork/contact/ContactInfoActivity")
                            .withString("uid",contactEntity.getUid())
                            .navigation();
                    break;
                case 7:
                    ARouter.getInstance().build("/iwork/contact/ContactInfoShowActivity")
                            .navigation();
                    break;
                default:
                    break;
            }
        }
    };

    @Subscribe
    public void onEventMainThread(ContactNotice notice) {
        if (notice.getNotice() == ContactNotice.ConNotice.RecContact) {
            adapter.updateContact(adapter.updateTypeContact);
        } else if (notice.getNotice() == ContactNotice.ConNotice.RecGroup) {
            adapter.updateContact(adapter.updateTypeGroup);
        } else if (notice.getNotice() == ContactNotice.ConNotice.RecFriend) {
            adapter.updateContact(adapter.updateTypeFriend);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

}
