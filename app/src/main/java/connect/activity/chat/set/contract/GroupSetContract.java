package connect.activity.chat.set.contract;

import android.view.View;

import connect.activity.base.BaseListener;
import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

/**
 * Created by Administrator on 2017/8/8.
 */

public interface GroupSetContract {

    interface BView extends BaseView<GroupSetContract.Presenter> {

        String getUid();

        void countMember(int members);

        void memberList(View view);

        void groupName(String groupname);

        void groupNameClickable(boolean clickable);

        void topSwitch(boolean top);

        void noticeSwitch(boolean notice);

        void commonSwtich(boolean common);

        void exitGroup();

        void addNewMember();
    }

    interface Presenter extends BasePresenter {

        void syncGroupInfo();

        void groupTop(boolean checkon,BaseListener<Boolean> listener);

        void groupMute(boolean checkon,BaseListener<Boolean> listener);

        void groupCommon(boolean checkon,BaseListener<Boolean> listener);

        void requestExitGroup();
    }
}
