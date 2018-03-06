package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;
import java.util.List;

import connect.activity.chat.ChatActivity;
import connect.activity.chat.set.GroupCreateActivity;
import connect.activity.chat.set.contract.GroupSelectContract;
import connect.activity.home.bean.GroupRecBean;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public class GroupSelectPresenter implements GroupSelectContract.Presenter {

    private Activity activity;
    private GroupSelectContract.BView view;

    public GroupSelectPresenter(GroupSelectContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        activity = view.getActivity();
    }

    @Override
    public void createGroup(ArrayList<Connect.Workmate> workmates) {
        ARouter.getInstance().build("/iwork/chat/set/GroupCreateActivity")
                .withSerializable("workmates", workmates)
                .navigation();
    }

    @Override
    public void inviteJoinGroup(final String groupIdentify, ArrayList<Connect.Workmate> workmates) {
        List<String> selectUids = new ArrayList<>();
        for (Connect.Workmate workmate : workmates) {
            selectUids.add(workmate.getUid());
        }

        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        Connect.GroupInviteWorkmate inviteWorkmate = Connect.GroupInviteWorkmate.newBuilder()
                .setInviteBy(userBean.getUid())
                .setIdentifier(groupIdentify)
                .addAllUids(selectUids)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_GROUP_INVITE, inviteWorkmate, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                GroupRecBean.sendGroupRecMsg(GroupRecBean.GroupRecType.GroupInfo, groupIdentify);

                String showHint = activity.getResources().getString(R.string.Link_Group_Invite_Success);
                ToastEUtil.makeText(activity, showHint, 1, new ToastEUtil.OnToastListener() {
                    @Override
                    public void animFinish() {
                        ARouter.getInstance().build("/chat/ChatActivity")
                                .withSerializable("CHAT_TYPE", Connect.ChatType.GROUP)
                                .withString("CHAT_IDENTIFY", groupIdentify)
                                .navigation();
                    }
                }).show();
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                if (response.getCode() == 2430) {
                    ToastEUtil.makeText(activity, R.string.Link_Qr_code_is_invalid, ToastEUtil.TOAST_STATUS_FAILE).show();
                } else {
                    String contentTxt = response.getMessage();
                    if (TextUtils.isEmpty(contentTxt)) {
                        ToastEUtil.makeText(activity, activity.getString(R.string.Network_equest_failed_please_try_again_later), 2).show();
                    } else {
                        ToastEUtil.makeText(activity, contentTxt, 2).show();
                    }
                }
            }
        });
    }
}
