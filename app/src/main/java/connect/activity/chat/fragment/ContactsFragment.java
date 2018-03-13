package connect.activity.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.base.compare.FriendCompara;
import connect.activity.chat.adapter.SelectContactsAdapter;
import connect.activity.chat.set.GroupSelectActivity;
import connect.activity.home.view.LineDecoration;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.utils.system.SystemUtil;
import connect.widget.SideBar;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * 从联系人列表选择成员
 * Created by PuJin on 2018/3/9.
 */
public class ContactsFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.siderbar)
    SideBar siderbar;

    private GroupSelectActivity activity;
    private LinearLayoutManager linearLayoutManager;
    private SelectContactsAdapter selectContactsAdapter;

    public static ContactsFragment startFragment() {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_select_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (GroupSelectActivity) getActivity();
        initView();
    }

    @Override
    public void initView() {
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getString(R.string.Chat_Contacts_Select));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtil.hideKeyBoard(getContext(), searchEdit);
                activity.popBackLastFragment();
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String memberKey = editable.toString();
                searchGroupMember(memberKey);
            }
        });

        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.addItemDecoration(new LineDecoration(activity));
        selectContactsAdapter = new SelectContactsAdapter();
        recyclerview.setAdapter(selectContactsAdapter);
        siderbar.setOnTouchingLetterChangedListener(new ContactsLetterChanged());
        selectContactsAdapter.setItemClick(new SelectContactsAdapter.ItemClick() {
            @Override
            public void itemClick(ContactEntity contactEntity) {
                if (contactEntity.getRegisted()) {
                    Connect.Workmate workmate = Connect.Workmate.newBuilder()
                            .setUid(contactEntity.getUid())
                            .setName(contactEntity.getName())
                            .setAvatar(contactEntity.getAvatar())
                            .setPubKey(contactEntity.getPublicKey())
                            .setGender(contactEntity.getGender())
                            .build();
                    activity.addWorkMate(workmate);
                    activity.switchFragment(BaseDepartSelectFragment.startFragment());
                }
            }
        });
        searchGroupMember("");
    }

    public void searchGroupMember(String memberKey) {
        List<ContactEntity> contactEntities = null;
        if (TextUtils.isEmpty(memberKey)) {
            contactEntities = ContactHelper.getInstance().loadFriend();
        } else {
            contactEntities = ContactHelper.getInstance().loadFriendEntityFromText(memberKey);
        }

        String myPublicKey = SharedPreferenceUtil.getInstance().getUser().getUid();
        Iterator<ContactEntity> iterator = contactEntities.iterator();
        while (iterator.hasNext()) {
            ContactEntity contactEntity = iterator.next();
            if (contactEntity.getUid().equals(myPublicKey)) {
                iterator.remove();
            }
        }

        Collections.sort(contactEntities, new FriendCompara());
        selectContactsAdapter.setData(contactEntities);
    }

    private class ContactsLetterChanged implements SideBar.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            int position = selectContactsAdapter.getPositionForSection(s.charAt(0));
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            if (position <= firstItem) {
                recyclerview.scrollToPosition(position);
            } else if (position <= lastItem) {
                int top = recyclerview.getChildAt(position - firstItem).getTop();
                recyclerview.scrollBy(0, top);
            } else {
                recyclerview.scrollToPosition(position);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
