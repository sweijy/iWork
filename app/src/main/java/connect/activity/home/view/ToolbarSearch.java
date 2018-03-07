package connect.activity.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import connect.ui.activity.R;

/**
 * Created by jinlongpu on 2018/3/6.
 */

public class ToolbarSearch extends RelativeLayout {

    private static String TAG = "_ToolbarSearch";
    private View view;

    public ToolbarSearch(Context context) {
        super(context);
    }

    public ToolbarSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolbarSearch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void initView() {
        view = View.inflate(getContext(), R.layout.view_toolbar_search, this);
    }
}
