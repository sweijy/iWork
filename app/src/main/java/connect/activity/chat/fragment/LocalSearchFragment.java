package connect.activity.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.chat.adapter.LocalSearchContactsAdapter;
import connect.activity.chat.adapter.LocalSearchRecentsAdapter;
import connect.activity.chat.set.GroupSelectActivity;
import connect.activity.home.view.LineDecoration;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.system.SystemUtil;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * 本地查询
 * Created by PuJin on 2018/3/12.
 */
public class LocalSearchFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.search_edit)
    EditText searchEdit;
    @Bind(R.id.recyclerview_recents)
    RecyclerView recyclerviewRecents;
    @Bind(R.id.linearlayout_1)
    LinearLayout linearlayout1;
    @Bind(R.id.recyclerview_contacts)
    RecyclerView recyclerviewContacts;
    @Bind(R.id.linearlayout_2)
    LinearLayout linearlayout2;

    private GroupSelectActivity activity;
    private View view;
    private LocalSearchRecentsAdapter recentsAdapter;
    private LocalSearchContactsAdapter contactsAdapter;

    public static LocalSearchFragment startFragment() {
        LocalSearchFragment fragment = new LocalSearchFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_localsearch, container, false);
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
                String string = editable.toString();
                searchTxt(string);
            }
        });

        LinearLayoutManager recentslinearLayoutManager = new LinearLayoutManager(activity);
        recyclerviewRecents.setLayoutManager(recentslinearLayoutManager);
        recyclerviewRecents.addItemDecoration(new LineDecoration(activity));
        recentsAdapter = new LocalSearchRecentsAdapter();
        recyclerviewRecents.setAdapter(recentsAdapter);
        recentsAdapter.setRecentsListener(new LocalSearchRecentsAdapter.RecentsListener() {

            @Override
            public void itemClick(ConversionEntity conversionEntity) {
                Connect.Workmate workmate = Connect.Workmate.newBuilder()
                        .setUid(conversionEntity.getIdentifier())
                        .setName(conversionEntity.getName())
                        .setAvatar(conversionEntity.getAvatar())
                        .build();
                activity.addWorkMate(workmate);
                activity.switchFragment(BaseDepartSelectFragment.startFragment());
            }
        });

        LinearLayoutManager contactslinearLayoutManager = new LinearLayoutManager(activity);
        recyclerviewContacts.setLayoutManager(contactslinearLayoutManager);
        recyclerviewContacts.addItemDecoration(new LineDecoration(activity));
        contactsAdapter = new LocalSearchContactsAdapter();
        recyclerviewContacts.setAdapter(contactsAdapter);
        contactsAdapter.setContactsListener(new LocalSearchContactsAdapter.ContactsListener() {

            @Override
            public void itemClick(ContactEntity contactEntity) {
                Connect.Workmate workmate = Connect.Workmate.newBuilder()
                        .setUid(contactEntity.getUid())
                        .setName(contactEntity.getName())
                        .setAvatar(contactEntity.getAvatar())
                        .build();
                activity.addWorkMate(workmate);
                activity.switchFragment(BaseDepartSelectFragment.startFragment());
            }
        });

        searchTxt("");
    }

    public void searchTxt(String string) {
        List<ConversionEntity> conversionEntities = ConversionHelper.getInstance().loadRoomEnitityLikeName(string);
        if (conversionEntities.isEmpty()) {
            linearlayout1.setVisibility(View.GONE);
        } else {
            linearlayout1.setVisibility(View.VISIBLE);
            recentsAdapter.setData(conversionEntities);
        }

        List<ContactEntity> contactEntities = ContactHelper.getInstance().loadFriendEntityFromText(string);
        if (contactEntities.isEmpty()) {
            linearlayout2.setVisibility(View.GONE);
        } else {
            linearlayout2.setVisibility(View.VISIBLE);
            contactsAdapter.setData(contactEntities);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
