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

import connect.database.green.DaoHelper.ParamManager;
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
        switch (connectState.getType()) {
            case DISCONN:
                setNetNotConnect();
                break;
            case CONNECT:
                int notify = ParamManager.getInstance().getInt(ParamManager.CONVERSATION_NOTIFY);
                if (notify == 0) {
                    setConnect();
                } else {
                    setCloseNotify();
                }
                break;
        }
    }

    public void setConnect() {
        setVisibility(GONE);
    }

    public void setNetNotConnect() {
        setVisibility(VISIBLE);
        view.setBackgroundColor(getResources().getColor(R.color.color_F7E7E7));
        img1.setImageResource(R.mipmap.icon_conversation_error);
        String showTxt = getContext().getString(R.string.Chat_Not_connected);
        txt1.setText(showTxt);
    }

    public void setCloseNotify() {
        setVisibility(VISIBLE);
        view.setBackgroundColor(getResources().getColor(R.color.color_DBE7EF));
        img1.setImageResource(R.mipmap.icon_conversation_notify);
        String showTxt = getContext().getString(R.string.Chat_Conversation_Notify_Close);
        txt1.setText(showTxt);
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
