package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import connect.activity.chat.set.contract.RobotSetContract;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by jinlongpu on 2018/3/2.
 */

public class RobotSetPresenter implements RobotSetContract.Presenter{

    private RobotSetContract.BView view;
    private Activity activity;

    public RobotSetPresenter(RobotSetContract.BView view){
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        activity = view.getActivity();

        View headerview = LayoutInflater.from(activity).inflate(R.layout.linear_contact, null);
        ImageView headimg = (ImageView) headerview.findViewById(R.id.roundimg);
        ImageView adminImg = (ImageView) headerview.findViewById(R.id.img1);
        TextView name = (TextView) headerview.findViewById(R.id.name);

        adminImg.setVisibility(View.GONE);
        name.setText(activity.getString(R.string.IWork));
        GlideUtil.loadAvatarRound(headimg, R.mipmap.connect_logo);
        view.showContact(headerview);
    }
}
