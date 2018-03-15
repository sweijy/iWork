package connect.activity.chat.set.contract;

import java.util.List;

import connect.activity.base.BaseListener;
import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;
import connect.database.green.bean.GroupMemberEntity;

/**
 * Created by PuJin on 2018/3/9.
 */

public interface GroupRemoveContract {

    interface BView extends BaseView<GroupRemoveContract.Presenter> {

        String getGroupIdentify();

    }

    interface Presenter extends BasePresenter {

        void removeMembers(List<GroupMemberEntity> memberEntities, BaseListener<Boolean> baseListener);
    }
}
