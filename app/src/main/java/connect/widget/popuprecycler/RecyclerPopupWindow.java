package connect.widget.popuprecycler;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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
    private int mScreenWidth;
    private int mScreenHeight;
    private float mTextSize;
    private int mTextPaddingLeft;
    private int mTextPaddingTop;
    private int mTextPaddingRight;
    private int mTextPaddingBottom;

    public RecyclerPopupWindow(Context context) {
        this.context = context;
        this.mTextSize = SystemUtil.spToPx(16);
        this.mTextPaddingLeft = SystemUtil.dipToPx(12);
        this.mTextPaddingTop = SystemUtil.dipToPx(12);
        this.mTextPaddingRight = SystemUtil.dipToPx(12);
        this.mTextPaddingBottom = SystemUtil.dipToPx(12);
        this.mScreenWidth = SystemUtil.getScreenWidth();
        this.mScreenHeight = SystemUtil.getScreenHeight();
    }

    private float mRawX;
    private float mRawY;
    private PopupWindow popupWindow;
    private View view;
    private List<String> stringList;
    private RecyclerPopupHelper.PopupListener popupListener;

    public void popupWindow(View view, List<String> strings, RecyclerPopupHelper.PopupListener listener) {
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

        if (popupWindow == null) {
            LinearLayout contentView = new LinearLayout(context);
            contentView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentView.setOrientation(LinearLayout.VERTICAL);
            contentView.setBackgroundResource(R.color.color_FFFFFF);

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
                            popupListener.onPopupListClick(index);
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
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }

        if (!popupWindow.isShowing()) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int stateBarHeight = SystemUtil.getStateBarHeight();
            int titleBarHeight = SystemUtil.dipToPx(45);
            // 弹出框的位置是否超出了屏幕底部
            boolean isHandstand = location[1] + mPopupWindowHeight > mScreenHeight;
            // 弹出框的位置
            boolean isOverRight = location[1] + mPopupWindowWidth > mScreenWidth;

            int showLocationY = isHandstand ?
                    (int) mRawY - mPopupWindowHeight :
                    (int) mRawY - mScreenHeight/2 +mPopupWindowHeight/2;

            popupWindow.showAtLocation(
                    view,
                    Gravity.CENTER,
                    (int) mRawX - mScreenWidth / 2,
                    showLocationY
            );
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

    public interface PopupListener {

        void onPopupListClick(int position);
    }
}
