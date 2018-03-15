package connect.widget.popuprecycler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import connect.ui.activity.R;
import connect.utils.system.SystemUtil;

/**
 * Created by jinlongpu on 2018/3/6.
 */
public class RecyclerPopupWindow {

    private Context context;
    private int mPopupWindowWidth;
    private int mPopupWindowHeight;
    private float mTextSize;
    private int mTextPaddingLeft;
    private int mTextPaddingTop;
    private int mTextPaddingRight;
    private int mTextPaddingBottom;

    public RecyclerPopupWindow(Context context) {
        this.context = context;
        this.mTextSize = SystemUtil.spToPx(12);
        this.mTextPaddingLeft = SystemUtil.dipToPx(16);
        this.mTextPaddingTop = SystemUtil.dipToPx(16);
        this.mTextPaddingRight = SystemUtil.dipToPx(16);
        this.mTextPaddingBottom = SystemUtil.dipToPx(16);
    }

    private float mRawX;
    private float mRawY;
    private PopupWindow popupWindow;
    private View view;
    private List<String> stringList;
    private RecyclerPopupListener popupListener;

    public void popupWindow(final View view, List<String> strings, RecyclerPopupListener listener) {
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
                    popupListener.longPress(view);
                    showPopupWindow();
                }
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showPopupWindow() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }

        if (popupWindow == null) {
            LinearLayout contentView = new LinearLayout(context);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentView.setOrientation(LinearLayout.VERTICAL);
            contentView.setBackgroundResource(R.drawable.shape_8px_ffffff);

            for (int i = 0; i < stringList.size(); i++) {
                TextView textView = new TextView(context);
                textView.setTextColor(context.getResources().getColor(R.color.color_5D5D5D));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                textView.setPadding(mTextPaddingLeft, mTextPaddingTop, mTextPaddingRight, mTextPaddingBottom);
                textView.setClickable(true);
                textView.setText(stringList.get(i));

                final int index = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (popupListener != null) {
                            popupListener.onItemClick(index);
                            hidePopupWindow();
                        }
                    }
                });
                contentView.addView(textView);
            }

            if (mPopupWindowWidth == 0) {
                mPopupWindowWidth = getViewWidth(contentView);
            }
            if (mPopupWindowHeight == 0) {
                mPopupWindowHeight = getViewHeight(contentView);
            }
            popupWindow = new PopupWindow(contentView, mPopupWindowWidth, mPopupWindowHeight, true);
            popupWindow.setTouchable(true);
            // popupWindow.setElevation(SystemUtil.dipToPx(10));
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }

        if (!popupWindow.isShowing()) {
            int screenWidth = SystemUtil.getScreenWidth();
            int screenHeight = SystemUtil.getScreenHeight();

            // 弹出框的位置是否超出了屏幕右半部(默认: 是当前手指位置右边)
            int showX = 0;
            boolean isOverScreenRight = mRawX + mPopupWindowWidth > screenWidth;
            if (isOverScreenRight) {
                showX = (int) mRawX - mPopupWindowHeight;
            } else {
                showX = (int) mRawX;
            }

            // 弹出框的位置是否超出了屏幕底部
            boolean isOverScreenBottom = mRawY + mPopupWindowHeight + SystemUtil.dipToPx(30) > screenHeight;
            int showY = 0;
            if (isOverScreenBottom) {
                showY = (int) (mRawY - mPopupWindowHeight - SystemUtil.dipToPx(5));
            } else {
                showY = (int) mRawY + SystemUtil.dipToPx(5);
            }

            popupWindow.showAtLocation(
                    view,
                    Gravity.LEFT | Gravity.TOP,
                    showX,
                    showY
            );
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupListener.pressCancle(view);
                }
            });
        }
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

    public interface RecyclerPopupListener {

        void longPress(View view);

        void onItemClick(int position);

        void pressCancle(View view);
    }
}
