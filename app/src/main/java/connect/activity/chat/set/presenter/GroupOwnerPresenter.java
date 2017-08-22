package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.text.TextUtils;

import connect.activity.chat.set.GroupSetActivity;
import connect.activity.chat.set.contract.GroupOwnerContract;
import connect.database.MemoryDataManager;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;
import connect.ui.activity.R;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

/**
 * Created by Administrator on 2017/8/8.
 */

public class GroupOwnerPresenter implements GroupOwnerContract.Presenter{

    private GroupOwnerContract.BView view;

    private String roomKey;
    private Activity activity;

    public GroupOwnerPresenter(GroupOwnerContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        roomKey = view.getRoomKey();
        activity = view.getActivity();
    }

    @Override
    public void groupOwnerTo(String memberKey, final String address) {
        Connect.GroupAttorn attorn = Connect.GroupAttorn.newBuilder()
                .setIdentifier(memberKey)
                .setAddress(address).build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.GROUP_ATTORN, attorn, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                String myaddress = MemoryDataManager.getInstance().getAddress();
                GroupMemberEntity myMember = ContactHelper.getInstance().loadGroupMemberEntity(roomKey, myaddress);
                myMember.setRole(0);
                ContactHelper.getInstance().inserGroupMemEntity(myMember);

                GroupMemberEntity ownerMember = ContactHelper.getInstance().loadGroupMemberEntity(roomKey, address);
                ownerMember.setRole(1);
                ContactHelper.getInstance().inserGroupMemEntity(ownerMember);

                GroupSetActivity.startActivity(activity, roomKey);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                String message = response.getMessage();
                if (!TextUtils.isEmpty(message)) {
                    ToastEUtil.makeText(activity, response.getMessage(), ToastEUtil.TOAST_STATUS_FAILE).show();
                }
            }
        });
    }
}