package connect.widget.recordvoice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import connect.activity.chat.bean.MsgSend;
import connect.ui.activity.R;
import connect.utils.TimeUtil;
import connect.utils.dialog.DialogUtil;
import connect.utils.log.LogManager;
import connect.utils.system.SystemUtil;
import connect.widget.recordvoice.inter.RecordVoiceListener;

/**
 * Created by PuJin on 2018/2/24.
 */

public class VoiceRecordButton extends AppCompatButton implements RecordVoiceListener, View.OnTouchListener {

    private static String TAG = "_VoiceRecordButton";
    private AudioUtil audioUtil;
    private VoiceRecordView voiceRecordView;

    private final static int MIN_RECORD_SECOND = 2;
    private boolean isCancle = false;

    public VoiceRecordButton(Context context) {
        super(context);
        initView();
    }

    public VoiceRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VoiceRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        audioUtil = AudioUtil.getInstance();
        audioUtil.setOnAudioRecordListener(new AudioUtil.AudioRecordListener() {

            private long duration;

            @Override
            public void startError() {
                permissionDialog();
            }

            @Override
            public void wellPrepared() {
                duration = TimeUtil.getCurrentTimeInLong();
            }

            @Override
            public void recording(long recordtime, int decibel) {
                if(!isCancle){
                    updateRecordVolume(decibel);
                }
            }

            @Override
            public void cancelRecord() {
                stopRecord();
            }

            @Override
            public void recordFinish(String path) {
                int recordLength = (int) ((TimeUtil.getCurrentTimeInLong() - duration) / 1000);
                if (recordLength < MIN_RECORD_SECOND) {
                    recordShort();
                } else {
                    stopRecord();
                    MsgSend.sendOuterMsg(MsgSend.MsgSendType.Voice, path, recordLength);
                }
            }
        });

        setText(getResources().getString(R.string.Chat_Hold_To_Talk));
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startRecord();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isCancelled(view, motionEvent)) {
                    cancelRecord();
                } else {
                    normalRecord();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isCancle) {
                    audioUtil.cancleRecord();
                } else {
                    audioUtil.finishRecorder();
                }
                break;
        }
        return false;
    }

    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float eventRawX = event.getRawX();
        float eventRawY = event.getRawY();
        int locationX = location[0];
        int locationY = location[1];
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        LogManager.getLogger().d(TAG, "eventRawX= " + eventRawX + "; eventRawY= " + eventRawY + "; locationX= " +
                locationX + "; locationY=" + locationY +
                "; viewWidth=" + viewWidth + "; viewHeight=" + viewHeight);

        int dp40 = SystemUtil.dipToPx(40);
        int dp80 = SystemUtil.dipToPx(80);
        if (event.getRawX() < location[0] - dp40 || event.getRawX() > location[0] + view.getWidth() + dp40 || event.getRawY() < location[1] - dp80) {
            return true;
        }
        return false;
    }

    protected void permissionDialog() {
        DialogUtil.showAlertTextView(getContext(),
                getContext().getString(R.string.Set_tip_title),
                getContext().getString(R.string.Link_Unable_to_get_the_voice_data),
                "",
                getContext().getString(R.string.Set_Setting),
                false,
                new DialogUtil.OnItemClickListener() {
                    @Override
                    public void confirm(String value) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                        getContext().startActivity(intent);
                    }

                    @Override
                    public void cancel() {

                    }
                });
    }

    @Override
    public void startRecord() {
        audioUtil.prepareAudio();
        voiceRecordView.startRecord();
        setText(getResources().getString(R.string.Chat_Release_The_End));
    }

    @Override
    public void updateRecordVolume(int db) {
        voiceRecordView.updateRecordVolume(db);
    }

    @Override
    public void cancelRecord() {
        isCancle = true;
        voiceRecordView.cancelRecord();
        setText(getResources().getString(R.string.Chat_Loosen_The_Finger_Cancel_The_Sending));
    }

    @Override
    public void normalRecord() {
        isCancle = false;
        voiceRecordView.normalRecord();
        setText(getResources().getString(R.string.Chat_Release_The_End));
    }

    @Override
    public void recordShort() {
        buttonListener.stopRecord();
        voiceRecordView.recordShort();
    }

    @Override
    public void stopRecord() {
        buttonListener.stopRecord();
        voiceRecordView.stopRecord();
    }

    public void setVoiceRecordView(VoiceRecordView voiceRecordView) {
        this.voiceRecordView = voiceRecordView;
    }


    private VoiceButtonListener buttonListener;

    public interface VoiceButtonListener {
        void cancelRecord();

        void stopRecord();
    }

    public void setButtonListener(VoiceButtonListener buttonListener) {
        this.buttonListener = buttonListener;
    }
}
