package connect.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import connect.ui.activity.R;
import connect.utils.system.SystemUtil;

/**
 * Created by PuJin on 2018/3/7.
 */
public class RightTrangleView extends View{

    private static String TAG = "_RightTrangleView";

    private Paint paint = null;

    public RightTrangleView(Context context) {
        super(context);
        initView();
    }

    public RightTrangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RightTrangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.color_D6D7F0));
        paint.setStrokeWidth(SystemUtil.dipToPx(5));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrangle(canvas);
    }

    protected void drawTrangle(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth(), getHeight());
        path.close();
        canvas.drawPath(path, paint);
    }
}
