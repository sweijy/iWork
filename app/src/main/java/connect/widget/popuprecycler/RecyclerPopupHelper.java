package connect.widget.popuprecycler;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import connect.utils.system.SystemUtil;

/**
 * Created by PuJin on 2018/2/26.
 */

public class RecyclerPopupHelper {

    private Context context;
    private StateListDrawable mLeftItemBackground;
    private StateListDrawable mRightItemBackground;
    private StateListDrawable mCornerItemBackground;
    private ColorStateList mTextColorStateList;
    private GradientDrawable mCornerBackground;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mPopupWindowWidth;
    private int mPopupWindowHeight;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mNormalTextColor;
    private int mPressedTextColor;
    private float mTextSize;
    private int mTextPaddingLeft;
    private int mTextPaddingTop;
    private int mTextPaddingRight;
    private int mTextPaddingBottom;
    private int mNormalBackgroundColor;
    private int mPressedBackgroundColor;
    private int mBackgroundCornerRadius;
    private int mDividerColor;
    private int mDividerWidth;
    private int mDividerHeight;

    private View mIndicatorView;

    public RecyclerPopupHelper(Context context) {
        this.context = context;
        this.mNormalTextColor = Color.WHITE;
        this.mPressedTextColor = Color.WHITE;
        this.mTextSize = SystemUtil.spToPx(14);
        this.mTextPaddingLeft = SystemUtil.dipToPx(10);
        this.mTextPaddingTop = SystemUtil.dipToPx(5);
        this.mTextPaddingRight = SystemUtil.dipToPx(10);
        this.mTextPaddingBottom = SystemUtil.dipToPx(5);
        this.mNormalBackgroundColor = 0xCC000000;
        this.mPressedBackgroundColor = 0xE7777777;
        this.mBackgroundCornerRadius = SystemUtil.dipToPx(8);
        this.mDividerColor = 0x9AFFFFFF;
        this.mDividerWidth = SystemUtil.dipToPx(1);
        this.mDividerHeight = SystemUtil.dipToPx(16);
        this.mScreenWidth = SystemUtil.getScreenWidth();
        this.mScreenHeight = SystemUtil.getScreenHeight();

        refreshBackgroundOrRadiusStateList();
        refreshTextColorStateList(mPressedTextColor, mNormalTextColor);
    }

    private float mRawX;
    private float mRawY;
    private PopupWindow popupWindow;
    private View view;
    private List<String> stringList;
    private PopupListener popupListener;

    public void popupWindow(View view, List<String> strings, PopupListener listener) {
        this.view = view;
        this.stringList = strings;
        this.popupListener = listener;
        this.popupWindow = null;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRawX = event.getRawX();
                mRawY = event.getRawY();
                return false;
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (popupListener != null) {
                    showPopupWindow();
                }
                return true;
            }
        });
    }

    public void showPopupWindow() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int stateBarHeight = SystemUtil.getStateBarHeight();
        int titleBarHeight = SystemUtil.dipToPx(45);
        int defaultPopHeight = SystemUtil.dipToPx(16);//默认弹出框大小
        boolean isHandstand = location[1] > stateBarHeight + titleBarHeight + defaultPopHeight;

        if (popupWindow == null) {
            LinearLayout contentView = new LinearLayout(context);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentView.setOrientation(LinearLayout.VERTICAL);

            LinearLayout popupListContainer = new LinearLayout(context);
            popupListContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            popupListContainer.setOrientation(LinearLayout.HORIZONTAL);
            popupListContainer.setBackgroundDrawable(mCornerBackground);

            this.mIndicatorView = getTriangleIndicatorView(context, isHandstand, SystemUtil.dipToPx(16), SystemUtil.dipToPx(8), 0xCC000000);
            LinearLayout.LayoutParams layoutParams;
            if (mIndicatorView.getLayoutParams() == null) {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            } else {
                layoutParams = (LinearLayout.LayoutParams) mIndicatorView.getLayoutParams();
            }
            layoutParams.gravity = Gravity.CENTER;
            mIndicatorView.setLayoutParams(layoutParams);
            ViewParent viewParent = mIndicatorView.getParent();
            if (viewParent instanceof ViewGroup) {
                ((ViewGroup) viewParent).removeView(mIndicatorView);
            }

            if (isHandstand) {
                contentView.addView(popupListContainer);
                contentView.addView(mIndicatorView);
            } else {
                contentView.addView(mIndicatorView);
                contentView.addView(popupListContainer);
            }

            for (int i = 0; i < stringList.size(); i++) {
                TextView textView = new TextView(context);
                textView.setTextColor(mTextColorStateList);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                textView.setPadding(mTextPaddingLeft, mTextPaddingTop, mTextPaddingRight, mTextPaddingBottom);
                textView.setClickable(true);

                textView.setText(stringList.get(i));

                final int index = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupListener != null) {
                            popupListener.onPopupListClick(index);
                            hidePopupWindow();
                        }
                    }
                });

                if (stringList.size() > 1 && i == 0) {
                    textView.setBackgroundDrawable(mLeftItemBackground);
                } else if (stringList.size() > 1 && i == stringList.size() - 1) {
                    textView.setBackgroundDrawable(mRightItemBackground);
                } else if (stringList.size() == 1) {
                    textView.setBackgroundDrawable(mCornerItemBackground);
                } else {
                    textView.setBackgroundDrawable(getCenterItemBackground());
                }
                popupListContainer.addView(textView);
                if (stringList.size() > 1 && i != stringList.size() - 1) {
                    View divider = new View(context);
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(mDividerWidth, mDividerHeight);
                    dividerParams.gravity = Gravity.CENTER;
                    divider.setLayoutParams(dividerParams);
                    divider.setBackgroundColor(mDividerColor);
                    popupListContainer.addView(divider);
                }
            }

            if (mPopupWindowWidth == 0) {
                mPopupWindowWidth = getViewWidth(popupListContainer);
            }
            if (mIndicatorView != null && mIndicatorWidth == 0) {
                if (mIndicatorView.getLayoutParams().width > 0) {
                    mIndicatorWidth = mIndicatorView.getLayoutParams().width;
                } else {
                    mIndicatorWidth = getViewWidth(mIndicatorView);
                }
            }
            if (mIndicatorView != null && mIndicatorHeight == 0) {
                if (mIndicatorView.getLayoutParams().height > 0) {
                    mIndicatorHeight = mIndicatorView.getLayoutParams().height;
                } else {
                    mIndicatorHeight = getViewHeight(mIndicatorView);
                }
            }
            if (mPopupWindowHeight == 0) {
                mPopupWindowHeight = getViewHeight(popupListContainer) + mIndicatorHeight;
            }
            popupWindow = new PopupWindow(contentView, mPopupWindowWidth, mPopupWindowHeight, true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }

        if (mIndicatorView != null) {
            float marginLeftScreenEdge = mRawX;
            float marginRightScreenEdge = mScreenWidth - mRawX;
            if (marginLeftScreenEdge < mPopupWindowWidth / 2f) {
                // in case of the draw of indicator out of corner's bounds
                if (marginLeftScreenEdge < mIndicatorWidth / 2f + mBackgroundCornerRadius) {
                    mIndicatorView.setTranslationX(mIndicatorWidth / 2f + mBackgroundCornerRadius - mPopupWindowWidth / 2f);
                } else {
                    mIndicatorView.setTranslationX(marginLeftScreenEdge - mPopupWindowWidth / 2f);
                }
            } else if (marginRightScreenEdge < mPopupWindowWidth / 2f) {
                if (marginRightScreenEdge < mIndicatorWidth / 2f + mBackgroundCornerRadius) {
                    mIndicatorView.setTranslationX(mPopupWindowWidth / 2f - mIndicatorWidth / 2f - mBackgroundCornerRadius);
                } else {
                    mIndicatorView.setTranslationX(mPopupWindowWidth / 2f - marginRightScreenEdge);
                }
            } else {
                mIndicatorView.setTranslationX(0);
            }
        }
        if (!popupWindow.isShowing()) {
            int showLocationY = isHandstand ?
                    (int) mRawY - mScreenHeight / 2 - mPopupWindowHeight + mIndicatorHeight :
                    (int) mRawY - mScreenHeight / 2 + mIndicatorHeight;

            popupWindow.showAtLocation(
                    view,
                    Gravity.CENTER,
                    (int) mRawX - mScreenWidth / 2,
                    showLocationY
            );
        }
    }

    private void refreshBackgroundOrRadiusStateList() {
        // left
        GradientDrawable leftItemPressedDrawable = new GradientDrawable();
        leftItemPressedDrawable.setColor(mPressedBackgroundColor);
        leftItemPressedDrawable.setCornerRadii(new float[]{
                mBackgroundCornerRadius, mBackgroundCornerRadius,
                0, 0,
                0, 0,
                mBackgroundCornerRadius, mBackgroundCornerRadius});
        GradientDrawable leftItemNormalDrawable = new GradientDrawable();
        leftItemNormalDrawable.setColor(Color.TRANSPARENT);
        leftItemNormalDrawable.setCornerRadii(new float[]{
                mBackgroundCornerRadius, mBackgroundCornerRadius,
                0, 0,
                0, 0,
                mBackgroundCornerRadius, mBackgroundCornerRadius});
        mLeftItemBackground = new StateListDrawable();
        mLeftItemBackground.addState(new int[]{android.R.attr.state_pressed}, leftItemPressedDrawable);
        mLeftItemBackground.addState(new int[]{}, leftItemNormalDrawable);
        // right
        GradientDrawable rightItemPressedDrawable = new GradientDrawable();
        rightItemPressedDrawable.setColor(mPressedBackgroundColor);
        rightItemPressedDrawable.setCornerRadii(new float[]{
                0, 0,
                mBackgroundCornerRadius, mBackgroundCornerRadius,
                mBackgroundCornerRadius, mBackgroundCornerRadius,
                0, 0});
        GradientDrawable rightItemNormalDrawable = new GradientDrawable();
        rightItemNormalDrawable.setColor(Color.TRANSPARENT);
        rightItemNormalDrawable.setCornerRadii(new float[]{
                0, 0,
                mBackgroundCornerRadius, mBackgroundCornerRadius,
                mBackgroundCornerRadius, mBackgroundCornerRadius,
                0, 0});
        mRightItemBackground = new StateListDrawable();
        mRightItemBackground.addState(new int[]{android.R.attr.state_pressed}, rightItemPressedDrawable);
        mRightItemBackground.addState(new int[]{}, rightItemNormalDrawable);
        // corner
        GradientDrawable cornerItemPressedDrawable = new GradientDrawable();
        cornerItemPressedDrawable.setColor(mPressedBackgroundColor);
        cornerItemPressedDrawable.setCornerRadius(mBackgroundCornerRadius);
        GradientDrawable cornerItemNormalDrawable = new GradientDrawable();
        cornerItemNormalDrawable.setColor(Color.TRANSPARENT);
        cornerItemNormalDrawable.setCornerRadius(mBackgroundCornerRadius);
        mCornerItemBackground = new StateListDrawable();
        mCornerItemBackground.addState(new int[]{android.R.attr.state_pressed}, cornerItemPressedDrawable);
        mCornerItemBackground.addState(new int[]{}, cornerItemNormalDrawable);
        mCornerBackground = new GradientDrawable();
        mCornerBackground.setColor(mNormalBackgroundColor);
        mCornerBackground.setCornerRadius(mBackgroundCornerRadius);
    }

    private StateListDrawable getCenterItemBackground() {
        StateListDrawable centerItemBackground = new StateListDrawable();
        GradientDrawable centerItemPressedDrawable = new GradientDrawable();
        centerItemPressedDrawable.setColor(mPressedBackgroundColor);
        GradientDrawable centerItemNormalDrawable = new GradientDrawable();
        centerItemNormalDrawable.setColor(Color.TRANSPARENT);
        centerItemBackground.addState(new int[]{android.R.attr.state_pressed}, centerItemPressedDrawable);
        centerItemBackground.addState(new int[]{}, centerItemNormalDrawable);
        return centerItemBackground;
    }

    public View getTriangleIndicatorView(Context context, final boolean ishansstand, final float widthPixel,
                                         final float heightPixel, final int color) {
        ImageView indicator = new ImageView(context);
        Drawable drawable = new Drawable() {

            @Override
            public void draw(Canvas canvas) {
                if (ishansstand) {
                    drawHandstandTriangle(canvas);
                } else {
                    drawTriangle(canvas);
                }
            }

            public void drawTriangle(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(widthPixel / 2, 0f);
                path.lineTo(widthPixel, heightPixel);
                path.lineTo(0f, heightPixel);
                path.close();
                canvas.drawPath(path, paint);
            }

            public void drawHandstandTriangle(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);
                path.moveTo(0f, 0f);
                path.lineTo(widthPixel, 0f);
                path.lineTo(widthPixel / 2, heightPixel);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }

            @Override
            public int getIntrinsicWidth() {
                return (int) widthPixel;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) heightPixel;
            }
        };
        indicator.setImageDrawable(drawable);
        return indicator;
    }

    private void refreshTextColorStateList(int pressedTextColor, int normalTextColor) {
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{};
        int[] colors = new int[]{pressedTextColor, normalTextColor};
        mTextColorStateList = new ColorStateList(states, colors);
    }

    public void hidePopupWindow() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private int getViewWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    private int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public interface PopupListener {

        void onPopupListClick(int position);
    }
}
