package connect.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import connect.ui.activity.R;
import connect.utils.system.SystemDataUtil;

/**
 * 年、月、日时间选择器
 */

public class DayPickerDialog {

    private OnConfirmListener onConfirmListener;
    private int yearNumber;
    private int mouthNumber;
    private int dayNumber;
    private NumberPicker dayPicker;
    private NumberPicker mouthPicker;
    private NumberPicker yearPicker;

    private Dialog showDayPicker(Context mContext, Integer year, Integer mouth, Integer day, final OnConfirmListener onConfirmListener){
        final Dialog dialog = new Dialog(mContext, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_day_picker, null);
        dialog.setContentView(view);

        yearPicker = (NumberPicker)view.findViewById(R.id.year_picker);
        mouthPicker = (NumberPicker)view.findViewById(R.id.mouth_picker);
        dayPicker = (NumberPicker)view.findViewById(R.id.day_picker);
        TextView confirmText = (TextView)view.findViewById(R.id.confirm_text);

        yearNumber = year;
        mouthNumber = mouth;
        dayNumber = day;

        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setMaxValue(2100);
        yearPicker.setMinValue(2000);
        yearPicker.setValue(year);
        yearPicker.setFormatter(formatter);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                yearNumber = newVal;
                judgeYear();
            }
        });

        mouthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mouthPicker.setMaxValue(12);
        mouthPicker.setMinValue(0);
        mouthPicker.setValue(mouth);
        mouthPicker.setFormatter(formatter);
        mouthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mouthNumber = newVal;
                judgeMonth();
            }
        });

        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dayPicker.setValue(day);
        judgeMonth();   //需要测试 judgeMonth()是否改变dayNumber的值
        dayPicker.setFormatter(formatter);
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dayNumber = newVal;
            }
        });

        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmListener.itemClick(yearNumber, mouthNumber, dayNumber);
            }
        });

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = SystemDataUtil.getScreenWidth();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.DialogAnim);
        mWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
        @Override
        public String format(int value) {
            // TODO Auto-generated method stub
            String tmpStr = String.valueOf(value);
            return tmpStr;
        }
    };

    // 判断是否闰年，从而设置2月份的天数
    private void judgeYear(){
        if(mouthNumber == 2) {
            if(yearNumber%4 == 0) {
                if(dayPicker.getMaxValue() != 29) {
                    dayPicker.setDisplayedValues(null);
                    dayPicker.setMinValue(1);
                    dayPicker.setMaxValue(29);
                }
            } else {
                if(dayPicker.getMaxValue() != 28) {
                    dayPicker.setDisplayedValues(null);
                    dayPicker.setMinValue(1);
                    dayPicker.setMaxValue(28);
                }
            }
        }
        dayNumber = dayPicker.getValue();
    }

    // 判断月份设置天数
    private void judgeMonth() {
        if(mouthNumber == 2) {
            if(yearNumber%4 == 0) {
                if(dayPicker.getMaxValue() != 29) {
                    dayPicker.setDisplayedValues(null);
                    dayPicker.setMinValue(1);
                    dayPicker.setMaxValue(29);
                }
            } else {
                if(dayPicker.getMaxValue() != 28) {
                    dayPicker.setDisplayedValues(null);
                    dayPicker.setMinValue(1);
                    dayPicker.setMaxValue(28);
                }
            }
        } else {
            switch(mouthNumber){
                case 4:
                case 6:
                case 9:
                case 11:
                    if(dayPicker.getMaxValue() != 30) {
                        dayPicker.setDisplayedValues(null);
                        dayPicker.setMinValue(1);
                        dayPicker.setMaxValue(30);
                    }
                    break;
                default:
                    if(dayPicker.getMaxValue() != 31) {
                        dayPicker.setDisplayedValues(null);
                        dayPicker.setMinValue(1);
                        dayPicker.setMaxValue(31);
                    }
            }
        }
        dayNumber = dayPicker.getValue();
    }

    public interface OnConfirmListener{
        void itemClick(Integer year, Integer mouth, Integer day);
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

}
