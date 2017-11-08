package connect.activity.contact.contract;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import connect.database.green.bean.FriendRequestEntity;
import connect.activity.base.contract.BasePresenter;
import connect.activity.base.contract.BaseView;

public interface AddFriendContract {

    interface View extends BaseView<AddFriendContract.Presenter> {
        Activity getActivity();

        void itemClick(int tag);

        void notifyData(boolean isShowMoreRecommend, ArrayList<FriendRequestEntity> listFina);
    }

    interface Presenter extends BasePresenter {
        void initGrid(RecyclerView recycler);

        void updateRequestListStatus();

        void queryFriend();

        //void updateRequestStatus(FriendRequestEntity entity, int status);

        void requestRecommendUser();

        void requestNoInterest(String uid);
    }

}
