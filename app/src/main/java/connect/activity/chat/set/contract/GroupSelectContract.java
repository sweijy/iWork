package connect.activity.chat.set.contract;

import java.util.ArrayList;

import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;
import protos.Connect;

/**
 * Created by PuJin on 2018/2/22.
 */

public interface GroupSelectContract {

    interface BView extends BaseView<GroupSelectContract.Presenter> {

    }

    interface Presenter extends BasePresenter {

        void createGroup(ArrayList<Connect.Workmate> workmates);

        void inviteJoinGroup(String groupIdentify,ArrayList<Connect.Workmate> workmates);
    }
}
