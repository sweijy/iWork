package connect.activity.chat.set;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseActivity;
import connect.activity.base.compare.GroupMemberCompara;
import connect.activity.chat.adapter.GroupAtAdapter;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.set.contract.GroupAtContract;
import connect.activity.chat.set.presenter.GroupAtPresenter;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.glide.GlideUtil;
import connect.widget.SideBar;
import connect.widget.TopToolBar;

/**
 * group At ,select group member
 */
@Route(path = "/iwork/chat/exts/GroupAtActivity")
public class GroupAtActivity extends BaseActivity implements GroupAtContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.siderbar)
    SideBar siderbar;
    @Bind(R.id.search_edit)
    EditText searchEdit;

    @Autowired
    String groupIdentify;

    private static String TAG = "_GroupAtActivity";
    private GroupAtActivity activity;
    private boolean move;
    private int topPosi;

    private GroupAtOnscrollListener onscrollListener = new GroupAtOnscrollListener();
    private GroupAtLetterChanged letterChanged = new GroupAtLetterChanged();
    private LinearLayoutManager linearLayoutManager;
    private GroupAtAdapter adapter;
    private GroupAtContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_at);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Chat_Choose_AT_Member));
        toolbar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        linearLayoutManager = new LinearLayoutManager(activity);
        adapter = new GroupAtAdapter(activity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(adapter);
        recyclerview.addOnScrollListener(onscrollListener);
        adapter.setGroupAtListener(groupAtListener);
        siderbar.setOnTouchingLetterChangedListener(letterChanged);

        new GroupAtPresenter(this).start();
        searchGroupMember("");
    }

    public void searchGroupMember(String memberKey) {
        List<GroupMemberEntity> groupMemEntities = null;
        if (TextUtils.isEmpty(memberKey)) {
            groupMemEntities = ContactHelper.getInstance().loadGroupMemEntities(groupIdentify);
        } else {
            groupMemEntities = ContactHelper.getInstance().loadGroupMemEntitiesLikeKey(groupIdentify, memberKey);
        }

        String myPublicKey = SharedPreferenceUtil.getInstance().getUser().getUid();
        Iterator<GroupMemberEntity> iterator = groupMemEntities.iterator();
        while (iterator.hasNext()) {
            GroupMemberEntity memberEntity = iterator.next();
            if (memberEntity.getUid().equals(myPublicKey)) {
                iterator.remove();
            }
        }

        Collections.sort(groupMemEntities, new GroupMemberCompara());
        adapter.setData(groupMemEntities);
    }

    @Override
    public String groupIndentify() {
        return groupIdentify;
    }

    @Override
    public void searchTxtListener() {
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
    }

    @Override
    public void atAll() {
        View view = findViewById(R.id.include_all);
        TextView manager = (TextView) view.findViewById(R.id.groupat_manager);

        manager.setVisibility(View.GONE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.GROUP_AT, "");
                ActivityUtil.goBack(activity);
            }
        });
    }

    @Override
    public void atGroupManager(String avatar, String name) {
        View view = findViewById(R.id.include_manager);
        ImageView imageView = (ImageView) view.findViewById(R.id.roundimg);
        TextView username = (TextView) view.findViewById(R.id.name);
        TextView manager = (TextView) view.findViewById(R.id.groupat_manager);

        GlideUtil.loadImage(imageView, avatar);
        username.setText(name);
        manager.setVisibility(View.VISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.GROUP_AT, "");
                ActivityUtil.goBack(activity);
            }
        });
    }

    private class GroupAtOnscrollListener extends RecyclerView.OnScrollListener {

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
    }

    private GroupAtAdapter.GroupAtListener groupAtListener = new GroupAtAdapter.GroupAtListener() {

        @Override
        public void groupAt(GroupMemberEntity memEntity) {
            RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.GROUP_AT, memEntity);
            ActivityUtil.goBack(activity);
        }
    };

    private class GroupAtLetterChanged implements SideBar.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            int position = adapter.getPositionForSection(s.charAt(0));

            topPosi = position;
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            if (position <= firstItem) {
                recyclerview.scrollToPosition(position);
            } else if (position <= lastItem) {
                int top = recyclerview.getChildAt(position - firstItem).getTop();
                recyclerview.scrollBy(0, top);
            } else {
                recyclerview.scrollToPosition(position);
                move = true;
            }
        }
    }

    @Override
    public void setPresenter(GroupAtContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }
}
