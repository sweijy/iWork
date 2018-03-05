package connect.activity.login.presenter;

import android.app.Activity;

import connect.activity.login.contract.StartContract;
import connect.database.SharedPreferenceUtil;
import connect.utils.FileUtil;
import connect.utils.system.SystemDataUtil;

public class StartPagePresenter implements StartContract.Presenter {

    private StartContract.View mView;

    public StartPagePresenter(StartContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        FileUtil.deleteDirectory(FileUtil.tempPath);

        String languageCode = SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.APP_LANGUAGE_CODE);
        SystemDataUtil.setAppLanguage(mView.getActivity(),languageCode);

        goInActivity(mView.getActivity());
    }

    private void goInActivity(final Activity mActivity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!SharedPreferenceUtil.getInstance().containsUser()) {
                    mView.goIntoLoginForPhone();
                } else {
                    mView.goIntoHome();
                }
                mActivity.finish();
            }
        }).start();
    }

}
