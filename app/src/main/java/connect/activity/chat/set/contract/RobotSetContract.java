package connect.activity.chat.set.contract;

import android.view.View;

import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

/**
 * Created by jinlongpu on 2018/3/2.
 */

public interface RobotSetContract {

    interface BView extends BaseView<RobotSetContract.Presenter> {

        String getRoomKey();

        void showContact(View view);
    }

    interface Presenter extends BasePresenter {

    }
}
