package connect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import connect.ui.activity.R;
import connect.utils.system.SystemUtil;

/**
 * 录音音量大小
 * Created by PuJin on 2018/3/12.
 */
public class VoiceVolumeView extends View {

    private static String TAG = "_VoiceVolumeView";

    private int maxVolume = 7;
    private int currentVolume;
    private Paint boardPaint = null;
    private Paint maxPaint = null;
    private Paint volumePaint = null;

    public VoiceVolumeView(Context context) {
        super(context);
        initView();
    }

    public VoiceVolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VoiceVolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        boardPaint = new Paint();
        boardPaint.setAntiAlias(true);
        boardPaint.setStrokeWidth(SystemUtil.dipToPx(5));
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setColor(getResources().getColor(R.color.color_FEFEFE));

        maxPaint = new Paint();
        maxPaint.setAntiAlias(true);
        maxPaint.setStrokeWidth(SystemUtil.dipToPx(5));
        maxPaint.setStyle(Paint.Style.STROKE);
        maxPaint.setStyle(Paint.Style.FILL);
        maxPaint.setColor(getResources().getColor(R.color.color_FEFEFE));

        volumePaint = new Paint();
        volumePaint.setAntiAlias(true);
        volumePaint.setStyle(Paint.Style.FILL);
        volumePaint.setStrokeWidth(SystemUtil.dipToPx(5));
        volumePaint.setColor(getResources().getColor(R.color.color_75AA29));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawVolume(canvas);
    }

    public void drawBoard(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        canvas.drawCircle(width / 2, height / 2, width / 2, boardPaint);
    }

    public void drawVolume(Canvas canvas) {
        /**
         * 中间音量的柱形图外框，x方向从 2/8开始，偏移1/8。y方向开始 1/4，偏移 5dp
         */

        int width = getWidth();
        int height = getHeight();
        canvas.drawRect(width * (2 / 8) - SystemUtil.dipToPx(5), height * (1 / 3) - SystemUtil.dipToPx(5), width * (2 / 8) + SystemUtil.dipToPx(5), height * (2 / 3) - SystemUtil.dipToPx(5), maxPaint);
        canvas.drawRect(width * (3 / 8) - SystemUtil.dipToPx(5), height * (1 / 3), width * (3 / 8) + SystemUtil.dipToPx(5), height * (2 / 3), maxPaint);
        canvas.drawRect(width * (5 / 8) - SystemUtil.dipToPx(5), height * (1 / 3), width * (5 / 8) + SystemUtil.dipToPx(5), height * (2 / 3), maxPaint);
        canvas.drawRect(width * (6 / 8) - SystemUtil.dipToPx(5), height * (1 / 3) - SystemUtil.dipToPx(5), width * (6 / 8) + SystemUtil.dipToPx(5), height * (2 / 3) - SystemUtil.dipToPx(5), maxPaint);


        canvas.drawRect(width * (2 / 8) - SystemUtil.dipToPx(5), (height * (1 / 3) - SystemUtil.dipToPx(5)) * (currentVolume / maxVolume), width * (2 / 8) + SystemUtil.dipToPx(5), height * (2 / 3) - SystemUtil.dipToPx(5), volumePaint);
        canvas.drawRect(width * (3 / 8) - SystemUtil.dipToPx(5), height * (1 / 3) * (currentVolume / maxVolume), width * (3 / 8) + SystemUtil.dipToPx(5), height * (2 / 3), volumePaint);
        canvas.drawRect(width * (5 / 8) - SystemUtil.dipToPx(5), height * (1 / 3) * (currentVolume / maxVolume), width * (5 / 8) + SystemUtil.dipToPx(5), height * (2 / 3), volumePaint);
        canvas.drawRect(width * (6 / 8) - SystemUtil.dipToPx(5), (height * (1 / 3) - SystemUtil.dipToPx(5)) * (currentVolume / maxVolume), width * (6 / 8) + SystemUtil.dipToPx(5), height * (2 / 3) - SystemUtil.dipToPx(5), volumePaint);
    }

    public void setVolumePaint(int volume) {
        if (volume < 0) {
            this.currentVolume = 0;
        } else if (volume > maxVolume) {
            this.currentVolume = maxVolume;
        } else {
            this.currentVolume = volume;
        }
        invalidate();
    }
}
