package connect.activity.chat.set.contract;

import android.view.View;

import connect.activity.base.BaseListener;
import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

/**
 * Created by Administrator on 2017/8/7.
 */

public interface PrivateSetContract {

    interface BView extends BaseView<PrivateSetContract.Presenter> {

        String getUid();

        String getAvatar();

        String getName();

        void switchTop(String name, boolean state);

        void switchDisturb(String name, boolean state);

        void showContactInfo(View view);
    }

    interface Presenter extends BasePresenter {

        void switchTop(boolean checkon, BaseListener<Boolean> listener);

        void switchDisturb(boolean checkon,BaseListener<Boolean> listener);
    }
}
