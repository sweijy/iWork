package connect.activity.chat.set.presenter;

import java.util.List;

import connect.activity.chat.set.contract.GroupAtContract;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.bean.GroupMemberEntity;

/**
 * Created by Administrator on 2017/8/11.
 */

public class GroupAtPresenter implements GroupAtContract.Presenter {

    private GroupAtContract.BView view;
    private String groupIdentify;

    public GroupAtPresenter(GroupAtContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        groupIdentify = view.groupIndentify();

        List<GroupMemberEntity> memberEntities = ContactHelper.getInstance().loadGroupManagerMemEntities(groupIdentify);
        if (memberEntities != null) {
            GroupMemberEntity memberEntity = memberEntities.get(0);
            String avatar = memberEntity.getAvatar();
            String name = memberEntity.getUsername();
            view.atGroupManager(avatar, name);
        }

        view.searchTxtListener();
        view.atAll();
    }
}
