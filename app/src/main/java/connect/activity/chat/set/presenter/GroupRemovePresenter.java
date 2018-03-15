package connect.activity.chat.set.presenter;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import connect.activity.base.BaseListener;
import connect.activity.chat.set.contract.GroupRemoveContract;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.UriUtil;
import connect.utils.dialog.DialogUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

/**
 * Created by PuJin on 2018/3/9.
 */

public class GroupRemovePresenter implements GroupRemoveContract.Presenter {

    private GroupRemoveContract.BView view;
    private Activity activity;
    private String identify;

    public GroupRemovePresenter(GroupRemoveContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        activity = view.getActivity();
        identify = view.getidentify();
    }

    @Override
    public void removeMembers(final List<GroupMemberEntity> memberEntities, final BaseListener<Boolean> baseListener) {
        String title = activity.getString(R.string.Chat_Set_Remove_Members);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < memberEntities.size(); i++) {
            GroupMemberEntity entity = memberEntities.get(i);
            buffer.append(entity.getUsername());
            if (i < memberEntities.size() - 1) {
                buffer.append(",");
            }
        }

        String content = buffer.toString();
        String leftCancle = activity.getString(R.string.Chat_Member_Cancel);
        String rightConfirm = activity.getString(R.string.Common_OK);
        DialogUtil.showAlertTextView(activity, title, content, leftCancle, rightConfirm, true, new DialogUtil.OnItemClickListener() {

            @Override
            public void confirm(String value) {
                final List<String> uids = new ArrayList<String>();
                for (GroupMemberEntity entity : memberEntities) {
                    uids.add(entity.getUid());
                }

                Connect.DelOrQuitGroupMember groupId = Connect.DelOrQuitGroupMember.newBuilder()
                        .setIdentifier(identify)
                        .addAllUids(uids)
                        .build();

                OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_GROUP_DELUSER, groupId, new ResultCall<Connect.HttpResponse>() {
                    @Override
                    public void onResponse(Connect.HttpResponse response) {
                        ContactHelper.getInstance().removeMemberEntity(identify, uids);
                        baseListener.Success(true);
                    }

                    @Override
                    public void onError(Connect.HttpResponse response) {
                        baseListener.fail();
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });
    }
}
