package connect.activity.wallet.contract;

import android.app.Activity;
import android.text.TextWatcher;

import connect.activity.wallet.bean.SendOutBean;
import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;
import connect.wallet.cwallet.business.TransferEditView;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public interface PacketContract {

    interface View extends BaseView<PacketContract.Presenter> {
        Activity getActivity();

        String getCurrentBtc();

        void setPayBtnEnable(boolean isEnable);

        String getPacketNumber();

        void setPayFee();

        void goPacketView(SendOutBean sendOutBean);
    }

    interface Presenter extends BasePresenter {
        TextWatcher getNumberWatcher();

        TransferEditView.OnEditListener getEditListener();

        void getPacketDetail(String hashId);
    }

}