package connect.activity.set;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.activity.base.BaseActivity;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;

/**
 *
 */
public class AboutDeveloperActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.myProgressBar)
    ProgressBar myProgressBar;
    private AboutDeveloperActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_feedback);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Set_Open_Source);
        String languageCode = SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.APP_LANGUAGE_CODE);
        webView.loadUrl("https://www.connect.im/mobile/developer?locale=" + languageCode);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        maskLongPress();
        updateProgress();
    }

    @OnClick(R.id.left_img)
    void goback(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                goback(new View(mActivity));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Mask long press the copy function
     */
    private void maskLongPress(){
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    /**
     * Get WebView load progress
     */
    private void updateProgress(){
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 70) {
                    myProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == myProgressBar.getVisibility()) {
                        myProgressBar.setVisibility(View.VISIBLE);
                    }
                    myProgressBar.setProgress(newProgress);
                }
            }
        });
    }
}