package connect.activity.chat.set.presenter;

import connect.activity.chat.set.contract.DepartSelectShowContract;

/**
 * Created by PuJin on 2018/2/22.
 */

public class DepartSelectShowPresenter implements DepartSelectShowContract.Presenter {

    private DepartSelectShowContract.BView view;

    public DepartSelectShowPresenter(DepartSelectShowContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
