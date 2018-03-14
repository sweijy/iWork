package connect.widget.bottominput;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import connect.ui.activity.R;
import connect.widget.bottominput.view.ExBottomLayout;
import connect.widget.bottominput.view.InputBottomLayout;
import connect.widget.recordvoice.VoiceRecordView;

/**
 * Created by gtq on 2016/11/26.
 */
public class InputPanel extends LinearLayout {

    private static String TAG = "_InputPanel";

    private Activity activity;

    public InputPanel(Context context) {
        super(context);
        initView();
    }

    public InputPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public InputPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public static InputPanel inputPanel;

    public void initView() {
        inputPanel = this;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_inputpanel, this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
    }

    public void switchMoreView() {
        InputBottomLayout.bottomLayout.hideInputBottomEditeText();
        ExBottomLayout.exBottomLayout.switchToMoreView();
    }

    public void switchEmojView() {
        InputBottomLayout.bottomLayout.hideInputBottomEditeText();
        ExBottomLayout.exBottomLayout.switchEmojiView();
    }

    public void hideBottomPanel() {
        InputBottomLayout.bottomLayout.hideInputBottomEditeText();
        ExBottomLayout.exBottomLayout.hideExView();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setVoiceRecordView(VoiceRecordView view){
        InputBottomLayout.bottomLayout.setVoiceRecord(view);
    }
}