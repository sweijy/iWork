package connect.activity.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import connect.instant.bean.ConnectState;
import connect.ui.activity.R;

/**
 * Created by pujin on 2017/2/6.
 */
public class ConnectStateView extends RelativeLayout {

    private static String TAG = "_ConnectStateView";

    private View view;
    private ImageView img1;
    private TextView txt1;

    public ConnectStateView(Context context) {
        super(context);
        initView();
    }

    public ConnectStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        view = View.inflate(getContext(), R.layout.view_connectstate, this);
        img1 = (ImageView) view.findViewById(R.id.img1);
        txt1 = (TextView) view.findViewById(R.id.txt1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectState connectState) {
        String showTxt = null;
        switch (connectState.getType()) {
            case DISCONN:
                view.setVisibility(VISIBLE);
                img1.setBackgroundResource(R.mipmap.attention_message3x);
                showTxt = getContext().getString(R.string.Chat_Not_connected);
                txt1.setText(showTxt);
                break;
            case REFRESH_ING:
            case REFRESH_SUCCESS:
                view.setVisibility(GONE);
                showTxt = getContext().getString(R.string.Chat_Loading);
                break;
            case CONNECT:
                view.setVisibility(GONE);
                showTxt = getContext().getString(R.string.Chat_Chats);
                txt1.setText(showTxt);
                break;
            case OFFLINE_PULL:
                view.setVisibility(GONE);
                showTxt = getContext().getString(R.string.Chat_Loading);
                txt1.setText(showTxt);
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }
}
