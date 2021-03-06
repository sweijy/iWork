package connect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import connect.ui.activity.R;
import connect.utils.system.SystemUtil;
import protos.Connect;

/**
 * Created by Administrator on 2018/1/4 0004.
 */

public class NameLinear extends LinearLayout {

    private final Context context;
    private ArrayList<Connect.Department> list = new ArrayList<>();
    private OnItemClickListener itemClickListener;

    public NameLinear(Context context) {
        this(context, null);
    }

    public NameLinear(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NameLinear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void notifyAddView(ArrayList<Connect.Department> list, HorizontalScrollView scrollview){
        this.removeAllViews();
        this.list.clear();
        this.list.addAll(list);
        for(int i = 0; i < list.size(); i ++){
            Connect.Department department = list.get(i);
            TextView textView = new TextView(context);
            textView.setSingleLine();
            textView.setPadding(5,5,5,5);
            textView.setTextSize(14);
            if(i == list.size() - 1){
                textView.setText(department.getName());
                textView.setTextColor(context.getResources().getColor(R.color.color_AEAEAE));
            }else{
                textView.setText(department.getName() + " >");
                textView.setTextColor(context.getResources().getColor(R.color.color_4D84E8));
            }
            textView.setTag(i);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer count = (Integer)v.getTag();
                    itemClickListener.itemClick(count);
                }
            });
            this.addView(textView);
        }
        scrollview.setScrollX(500);
    }

    public int getCurrentDepartmentPosition() {
        return list.size() - 1;
    }

    public Connect.Department getLastDepartment() {
        return list.get(list.size() - 1);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void itemClick(int position);
    }

}
