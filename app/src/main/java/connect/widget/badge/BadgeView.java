package connect.widget.badge;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import connect.ui.activity.R;
import connect.utils.system.SystemUtil;

/**
 * Created by PuJin on 2018/2/9.
 */
public class BadgeView extends View {

    private Paint numberPaint;
    private Paint backgroundPaint;

    private int defaultTextColor = Color.WHITE;
    private int defaultBackgroundColor = Color.RED;

    private int messageCount = 0;

    public enum BadgeShape {
        SHAPE_CIRCLE_SMALL,//免打扰
        SHAPE_CIRCLE,//个位数消息
        SHAPTE_ROUND_RECTANGLE,//大于10条
    }

    public BadgeView(Context context) {
        super(context);
        init(context);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberPaint.setColor(defaultTextColor);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTextSize(SystemUtil.spToPx(6));
        numberPaint.setTextAlign(Paint.Align.CENTER);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(getResources().getColor(R.color.color_red));
        backgroundPaint.setStyle(Paint.Style.FILL);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        float textH = fontMetrics.descent - fontMetrics.ascent;

        String showTxt = messageShowTxt(messageCount);
        BadgeShape badgeShape = calculateShape(messageCount);
        switch (badgeShape) {
            case SHAPE_CIRCLE_SMALL:
                canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, getMeasuredWidth() / 4, backgroundPaint);
                break;
            case SHAPE_CIRCLE:
                canvas.drawCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, getMeasuredWidth() / 2, backgroundPaint);
                canvas.drawText(showTxt, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
            case SHAPTE_ROUND_RECTANGLE:
                RectF rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
                int corner = SystemUtil.dipToPx(8);
                canvas.drawRoundRect(rectF, corner, corner, backgroundPaint);
                canvas.drawText(showTxt, getMeasuredWidth() / 2f, getMeasuredHeight() / 2f + (textH / 2f - fontMetrics.descent), numberPaint);
                break;
        }
    }

    public BadgeShape calculateShape(int count) {
        BadgeShape badgeShape = null;
        if (count < 0) {
            badgeShape = BadgeShape.SHAPE_CIRCLE_SMALL;
        } else if (count < 10) {
            badgeShape = BadgeShape.SHAPE_CIRCLE;
        } else {
            badgeShape = BadgeShape.SHAPTE_ROUND_RECTANGLE;
        }
        return badgeShape;
    }

    public String messageShowTxt(int count) {
        String showTxt = null;
        if (count < 0) {
            showTxt = "";
        } else if (count < 99) {
            showTxt = String.valueOf(count);
        } else {
            showTxt = "99+";
        }
        return showTxt;
    }

    public void setBadgeCount(int disturb, int count) {
        if (count > 0) {
            setVisibility(VISIBLE);
            if (disturb > 0 && count > 0) {
                count = -1;
            }
        } else {
            setVisibility(GONE);
        }

        messageCount = count;
        invalidate();
    }
}