package connect.widget.random;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import connect.utils.FileUtil;
import connect.utils.StringUtil;
import connect.utils.permission.PermissionUtil;

public class RandomVoicePresenter implements RandomVoiceContract.Presenter {

    private RandomVoiceContract.View mView;
    private final int MAX_LENGTH = 3500;
    private Runnable runnable;
    private Handler handler = new Handler();
    private int videoLength;
    private int rateTime = 10;
    private MediaRecorder iMediaRecorder;
    private File file;
    private ArrayList<Double> dbArray;
    private String random;

    public RandomVoicePresenter(RandomVoiceContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        PermissionUtil.getInstance().requestPermission(mView.getActivity(), new String[]{PermissionUtil.PERMISSION_RECORD_AUDIO,
                PermissionUtil.PERMISSION_STORAGE}, permissionCallBack);
    }

    @Override
    public PermissionUtil.ResultCallBack getPermissionCallBack() {
        return permissionCallBack;
    }

    private PermissionUtil.ResultCallBack permissionCallBack = new PermissionUtil.ResultCallBack() {
        @Override
        public void granted(String[] permissions) {
            try {
                file = FileUtil.newTempFile(FileUtil.FileType.VOICE);
                iMediaRecorder = new MediaRecorder();
                iMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                iMediaRecorder.setOutputFile(file.getPath());
                iMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                iMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                try {
                    iMediaRecorder.prepare();
                    iMediaRecorder.start();
                    timing();
                    mView.changeViewStatus(0);
                } catch (Exception e) {
                    iMediaRecorder = null;
                    mView.denyPermissionDialog();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deny(String[] permissions) {
            mView.denyPermission();
        }
    };

    private void timing() {
        dbArray = new ArrayList<>();
        videoLength = 0;
        runnable = new Runnable() {
            @Override
            public void run() {
                videoLength += rateTime;
                if (videoLength > MAX_LENGTH) {
                    finishSuccess(random);
                } else {
                    mView.setProgressBar(videoLength * ((float) 360 / MAX_LENGTH));
                    handler.postDelayed(this, rateTime);
                }

                if (videoLength < 3000) {
                    int ratio = iMediaRecorder.getMaxAmplitude();
                    double db = 0;
                    if (ratio > 1)
                        db = 20 * Math.log10((double) Math.abs(ratio));
                    dbArray.add(db);
                } else if (videoLength == 3000) {
                    if (!checkVoice()) {
                        mView.changeViewStatus(2);
                        return;
                    } else {
                        if (iMediaRecorder != null) {
                            iMediaRecorder.stop();
                            iMediaRecorder.release();
                            iMediaRecorder = null;
                        }

//                        byte[] valueByte = SupportKeyUril.byteSHA512(FileUtil.filePathToByteArray(file.getPath()));
                        byte[] valueByte=null;
                        String strForBmp = StringUtil.bytesToHexString(valueByte);
                        random = strForBmp;
                    }
                }
            }
        };
        handler.postDelayed(runnable, rateTime);
    }

    @Override
    public void finishSuccess(final String random) {
        if (!TextUtils.isEmpty(random)) {
            mView.changeViewStatus(1);
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    mView.successCollect(random);
                }
            };
            handler.sendEmptyMessageDelayed(1, 1000);
        } else {
            mView.changeViewStatus(2);
        }
    }

    private boolean checkVoice() {
        ArrayList<Double> list = new ArrayList<>();
        for (Double db : dbArray) {
            if (db != 0.0) {
                list.add(db);
            }
        }
        if (list.size() < 20) {
            mView.changeViewStatus(4);
            return false;
        }
        double Sum = 0;
        for (Double data : list) {
            Sum = data + Sum;
        }
        if (Sum / list.size() > 35) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void releaseResource() {
        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
                runnable = null;
            }
            if (iMediaRecorder != null) {
                iMediaRecorder.setOnErrorListener(null);
                iMediaRecorder.setOnInfoListener(null);
                iMediaRecorder.setPreviewDisplay(null);
                iMediaRecorder.stop();
                iMediaRecorder.release();
                iMediaRecorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
