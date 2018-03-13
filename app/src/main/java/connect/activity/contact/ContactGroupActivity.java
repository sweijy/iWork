package connect.activity.contact;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.contact.adapter.ContactGroupAdapter;
import connect.activity.home.view.LineDecoration;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupEntity;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.RegularUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.TopToolBar;
import instant.utils.SharedUtil;
import protos.Connect;

@Route(path = "/iwork/contact/ContactGroupActivity")
public class ContactGroupActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.no_data_lin)
    LinearLayout noDataLin;

    private ContactGroupActivity mActivity;
    private ContactGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_group);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Link_Group);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.addItemDecoration(new LineDecoration(mActivity));

        adapter = new ContactGroupAdapter(mActivity);
        adapter.setItemClickListener(onItemListener);
        recyclerview.setAdapter(adapter);
        updateView();
        getGroup();
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    ContactGroupAdapter.OnItemClickListener onItemListener = new ContactGroupAdapter.OnItemClickListener() {
        @Override
        public void itemClick(int position, GroupEntity groupEntity) {
            ARouter.getInstance().build("/iwork/chat/ChatActivity")
                    .withSerializable("CHAT_TYPE", Connect.ChatType.GROUP)
                    .withString("CHAT_IDENTIFY", groupEntity.getIdentifier())
                    .navigation();
        }
    };

    private void updateView() {
        new AsyncTask<Void, Void, List<GroupEntity>>() {
            @Override
            protected List<GroupEntity> doInBackground(Void... params) {
                List<GroupEntity> localGroup = ContactHelper.getInstance().loadGroupCommonAll();
                return localGroup;
            }

            @Override
            protected void onPostExecute(List<GroupEntity> groupEntities) {
                super.onPostExecute(groupEntities);
                int count;
                if (groupEntities != null && groupEntities.size() > 0) {
                    count = groupEntities.size();
                    GroupEntity groupEntity = new GroupEntity();
                    groupEntity.setIdentifier("count");
                    groupEntities.add(groupEntity);
                    adapter.setNotify(groupEntities, count);
                }else{
                    noDataLin.setVisibility(View.VISIBLE);
                    recyclerview.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    private void getGroup(){
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_GROUP_LIST, ByteString.copyFrom(new byte[]{}), new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.Groups commonGroups = Connect.Groups .parseFrom(structData.getPlainData());
                    List<Connect.Group> groupInfos = commonGroups.getGroupsList();

                    ArrayList<GroupEntity> groupEntities = new ArrayList<>();
                    for (Connect.Group group : groupInfos) {
                        String groupIdentifier = group.getIdentifier();
                        GroupEntity groupEntity = new GroupEntity();
                        groupEntity.setIdentifier(groupIdentifier);
                        groupEntity.setAvatar(RegularUtil.groupAvatar(groupIdentifier));
                        groupEntity.setName(group.getName());
                        groupEntity.setCategory(group.getCategory());
                        groupEntity.setSummary(group.getSummary());
                        groupEntity.setCommon(1);
                        groupEntities.add(groupEntity);
                    }
                    ContactHelper.getInstance().inserGroupEntity(groupEntities);
                    updateView();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                int a=  1;
            }
        });
    }

}
