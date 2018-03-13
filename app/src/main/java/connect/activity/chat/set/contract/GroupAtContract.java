package connect.activity.chat.set.contract;

import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

/**
 * Created by Administrator on 2017/8/11.
 */

public interface GroupAtContract {

    interface BView extends BaseView<GroupAtContract.Presenter> {

        String groupIndentify();

        void searchTxtListener();

        void atAll();

        void atGroupManager(String avatar,String name);
    }

    interface Presenter extends BasePresenter {

    }
}
