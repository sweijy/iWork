package connect.activity.login.contract;

import android.app.Activity;

import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

public interface StartContract {

    interface View extends BaseView<Presenter> {
        void goIntoLoginForPhone();

        void goIntoHome();

        Activity getActivity();
    }

    interface Presenter extends BasePresenter {

    }
}
