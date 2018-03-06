package connect.activity.contact;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.widget.TopToolBar;

@Route(path = "/iwork/contact/ContactInfoShowActivity")
public class ScanResultActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.result_text)
    TextView resultText;

    @Autowired
    String value;

    private ScanResultActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_scan_result);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Link_Scan_Result);

        String value = getIntent().getExtras().getString("value");
        resultText.setText(value);
        resultText.setTextIsSelectable(true);
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

}
