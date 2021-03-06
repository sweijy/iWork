package connect.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import connect.activity.base.BaseApplication;
import connect.ui.activity.R;

public class TopToolBar extends LinearLayout {

    private final Context context;
    private RelativeLayout leftRela;
    private RelativeLayout titleSearchRela;
    private LinearLayout titleNormalLinear;
    private ImageView leftImg;
    private ImageView titleImg;
    private ImageView titleSearchImg;
    private TextView titleTv;
    private EditText titleEdit;
    private ImageView rightImg;
    private TextView rightText;
    private RelativeLayout rightLayout;
    private TextView leftText;

    public TopToolBar(Context context) {
        this(context, null);
    }

    public TopToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.toolbar_head, this, true);
        initView(view);
    }

    private void initView(View view) {
        leftRela = (RelativeLayout) view.findViewById(R.id.left_rela);
        leftImg = (ImageView) view.findViewById(R.id.left_img);
        leftText = (TextView)view.findViewById(R.id.left_text);

        titleImg = (ImageView) view.findViewById(R.id.title_img);
        titleTv = (TextView) view.findViewById(R.id.title_tv);
        titleEdit = (EditText) view.findViewById(R.id.title_edittext);
        titleNormalLinear = (LinearLayout) view.findViewById(R.id.title_linear_normal);
        titleSearchRela = (RelativeLayout) view.findViewById(R.id.title_relative_edite);
        rightImg = (ImageView) view.findViewById(R.id.right_img);
        rightText = (TextView) view.findViewById(R.id.right_text);
        rightLayout = (RelativeLayout) view.findViewById(R.id.right_lin);
        titleSearchImg = (ImageView) view.findViewById(R.id.image_title_search);

        this.setBackgroundResource(R.color.color_457DE3);
        titleTv.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.color_FFFFFF));
        rightText.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.color_FFFFFF));
        //SystemUtil.setWindowStatusBarColor((Activity)context,R.color.color_161A21);
    }

    @Deprecated
    public void setBlackStyle(){

    }

    public void setRedStyle() {
        this.setBackgroundResource(R.color.color_ff6c5a);
        titleTv.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.color_FFFFFF));
        rightText.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.color_FFFFFF));
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = ((Activity) context).getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(context.getResources().getColor(R.color.color_ff6c5a));
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLeftImg(Integer resourceId) {
        leftRela.setVisibility(View.VISIBLE);
        leftText.setVisibility(View.GONE);
        leftImg.setImageResource(resourceId);
    }

    public void setLeftText(Integer textId){
        leftRela.setVisibility(View.VISIBLE);
        leftImg.setVisibility(View.GONE);
        leftText.setText(textId);
    }

    public void setLeftListener(OnClickListener onClickListener) {
        leftRela.setOnClickListener(onClickListener);
    }

    public void setLeftEnable(boolean b) {
        leftRela.setEnabled(b);
    }

    public void setTitle(String title) {
        titleImg.setVisibility(GONE);
        titleTv.setText(title);
    }

    public void setTitle(Integer imgId, Integer textId) {
        if (imgId != null) {
            titleImg.setVisibility(VISIBLE);
            titleImg.setImageResource(imgId);
        }
        if (textId != null) {
            titleTv.setText(textId);
        }
    }

    public void setTitle(Integer imgId, String text) {
        if (imgId != null) {
            titleImg.setVisibility(VISIBLE);
            titleImg.setImageResource(imgId);
        }
        if (TextUtils.isEmpty(text)) {
            titleTv.setVisibility(GONE);
        }else {
            titleTv.setVisibility(VISIBLE);
            titleTv.setText(text);
        }
    }

    public void setSearchTitle(int resourceid,String hint) {
        titleNormalLinear.setVisibility(GONE);
        titleSearchRela.setVisibility(VISIBLE);

        titleSearchImg.setImageResource(resourceid);
        titleEdit.setHint(hint);
    }

    public String getSearchTxt(){
        String content = titleEdit.getText().toString();
        return content;
    }

    public void clearSearchTxt(){
        titleEdit.setText("");
    }

    public void setRightImg(Integer resId) {
        rightLayout.setVisibility(View.VISIBLE);
        rightText.setVisibility(View.GONE);
        if (resId == null) {
            rightImg.setVisibility(View.GONE);
        } else {
            rightImg.setVisibility(View.VISIBLE);
            rightImg.setImageResource(resId);
        }
    }

    public void setRightText(Integer resId) {
        rightLayout.setVisibility(View.VISIBLE);
        rightImg.setVisibility(View.GONE);
        rightText.setText(resId);
    }

    public void setRightText(String resId) {
        rightLayout.setVisibility(View.VISIBLE);
        rightImg.setVisibility(View.GONE);
        rightText.setText(resId);
    }

    public void setRightListener(OnClickListener onClickListener) {
        rightLayout.setOnClickListener(onClickListener);
    }

    public void rightPerformClick(){
        rightLayout.performClick();
    }

    public void setRightTextColor(Integer resId) {
        rightText.setTextColor(BaseApplication.getInstance().getResources().getColor(resId));
    }

    public void setRightTextEnable(boolean enable) {
        rightText.setEnabled(enable);
        if (enable) {
            rightText.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.color_FFFFFF));
        } else {
            rightText.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.color_FAFBFC));
        }
        rightLayout.setEnabled(enable);
    }

}
