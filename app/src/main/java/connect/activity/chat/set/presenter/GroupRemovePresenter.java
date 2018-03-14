package connect.activity.chat.set.presenter;

import java.util.List;

import connect.activity.base.BaseListener;
import connect.activity.chat.set.contract.GroupRemoveContract;
import connect.database.green.bean.GroupMemberEntity;

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

    @Override
    public void removeMembers(List<GroupMemberEntity> memberEntities, BaseListener<Boolean> baseListener) {

    }
}
