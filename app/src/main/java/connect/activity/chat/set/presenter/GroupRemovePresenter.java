package connect.activity.chat.set.presenter;

import connect.activity.chat.set.contract.GroupRemoveContract;

/**
 * Created by PuJin on 2018/3/9.
 */

public class GroupRemovePresenter implements GroupRemoveContract.Presenter{

    private GroupRemoveContract.BView view;

    public GroupRemovePresenter(GroupRemoveContract.BView view){
        this.view = view;
    }

    @Override
    public void start() {

    }
}
