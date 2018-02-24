package connect.widget.recordvoice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import connect.ui.activity.R;
import connect.widget.recordvoice.inter.RecordVoiceListener;

/**
 * Created by PuJin on 2018/2/23.
 */
public class VoiceRecordView extends RelativeLayout implements RecordVoiceListener {

    private View view;
    private ImageView recordStateImg;
    private TextView recordTipsTxt;


    public VoiceRecordView(Context context) {
        super(context);
        initView();
    }

    public VoiceRecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VoiceRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        Context context = getContext();
        view = LayoutInflater.from(context).inflate(R.layout.view_voicerecord, this);
        recordStateImg = (ImageView) view.findViewById(R.id.img_voicerecord_state);
        recordTipsTxt = (TextView) view.findViewById(R.id.txt_voicerecord_tips);
    }

    @Override
    public void startRecord() {
        view.setVisibility(VISIBLE);
    }

    @Override
    public void updateRecordVolume(int db) {
        switch (db / 5) {
            case 0:
                recordStateImg.setImageResource(R.mipmap.ic_volume_1);
                break;
            case 1:
                recordStateImg.setImageResource(R.mipmap.ic_volume_2);
                break;
            case 2:
                recordStateImg.setImageResource(R.mipmap.ic_volume_3);
                break;
            case 3:
                recordStateImg.setImageResource(R.mipmap.ic_volume_4);
                break;
            case 4:
                recordStateImg.setImageResource(R.mipmap.ic_volume_5);
                break;
            case 5:
                recordStateImg.setImageResource(R.mipmap.ic_volume_6);
                break;
            case 6:
                recordStateImg.setImageResource(R.mipmap.ic_volume_7);
                break;
            default:
                recordStateImg.setImageResource(R.mipmap.ic_volume_8);
        }
    }

    @Override
    public void cancelRecord() {
        recordStateImg.setImageResource(R.mipmap.ic_volume_cancel);
        recordTipsTxt.setText(R.string.Chat_Loosen_The_Finger_Cancel_The_Sending);
        recordTipsTxt.setBackground(getResources().getDrawable(R.drawable.shape_stroke_red));
    }

    @Override
    public void normalRecord() {
        recordTipsTxt.setText(R.string.Chat_Finger_Slippery_Cancel_The_Sending);
        recordTipsTxt.setBackground(null);
    }

    @Override
    public void recordShort() {
        recordStateImg.setImageResource(R.mipmap.ic_volume_wraning);
        recordTipsTxt.setText(R.string.Chat_Voice_Record_Short);
        handler.sendEmptyMessageDelayed(100, 200);
    }

    @Override
    public void stopRecord() {
        handler.sendEmptyMessageDelayed(100, 200);
    }

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            view.setVisibility(GONE);
        }
    };
}
