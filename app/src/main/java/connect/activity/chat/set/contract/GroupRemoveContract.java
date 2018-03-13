package connect.activity.chat.set.contract;

import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

/**
 * Created by PuJin on 2018/3/9.
 */

public interface GroupRemoveContract {

    interface BView extends BaseView<GroupRemoveContract.Presenter> {

        String getGroupIdentify();

    }

    interface Presenter extends BasePresenter {

    }
}
