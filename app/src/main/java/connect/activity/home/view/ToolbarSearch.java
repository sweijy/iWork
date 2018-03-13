package connect.activity.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import connect.ui.activity.R;

/**
 * Created by jinlongpu on 2018/3/6.
 */

public class ToolbarSearch extends RelativeLayout {

    private static String TAG = "_ToolbarSearch";
    private View view;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;

    public ToolbarSearch(Context context) {
        super(context);
        initView();
    }

    public ToolbarSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ToolbarSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        view = View.inflate(getContext(), R.layout.view_toolbar_search, this);
        linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }
}
