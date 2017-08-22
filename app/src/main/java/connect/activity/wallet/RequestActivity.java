package connect.activity.wallet;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.set.manager.EditInputFilterPrice;
import connect.database.green.DaoHelper.CurrencyHelper;
import connect.database.green.bean.CurrencyAddressEntity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ConfigUtil;
import connect.utils.ToastEUtil;
import connect.wallet.cwallet.NativeWallet;
import connect.wallet.cwallet.bean.CurrencyEnum;
import connect.wallet.cwallet.inter.WalletListener;
import connect.widget.TopToolBar;
import connect.widget.zxing.utils.CreateScan;

/**
 * payment
 * Created by Administrator on 2016/12/10.
 */
public class RequestActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.address_tv)
    TextView addressTv;
    @Bind(R.id.address_lin)
    LinearLayout addressLin;
    @Bind(R.id.amount_et)
    EditText amountEt;
    @Bind(R.id.amount_lin)
    LinearLayout amountLin;
    @Bind(R.id.scan_img)
    ImageView scanImg;
    @Bind(R.id.setAmount_tv)
    TextView setAmountTv;
    @Bind(R.id.txt1)
    TextView txt1;

    private RequestActivity mActivity;
    private EditInputFilterPrice bitEditFilter = new EditInputFilterPrice(Double.valueOf(999), 8);
    //bitcoin:1CDheG1rvKoaPMnkswzcr3xphPVTyxxzYY?amount=1.0
    //https://transfer.connect.im/share/v1/pay?address=18gzAo5jxbsF1G2F741EHrwxdD2v11RvXf&amount=0.08
    public String scanHead;
    public String shareUrl = ConfigUtil.getInstance().sharePayAddress() + "?address=";

    private CurrencyEnum currencyBean = CurrencyEnum.BTC;
    private CurrencyAddressEntity addressEntity;
    private String currencyAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_request);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity) {
        ActivityUtil.next(activity, RequestActivity.class);
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.close_white3x);
        toolbarTop.setTitle(null, R.string.Wallet_Receipt);
        toolbarTop.setRightImg(R.mipmap.wallet_share_payment2x);

        InputFilter[] inputFiltersBtc = {bitEditFilter};
        amountEt.setFilters(inputFiltersBtc);
        amountEt.addTextChangedListener(textWatcher);
        txt1.setText(getString(R.string.Wallet_Your_Bitcoin_Address_Title));

        CurrencyAddressEntity entity = CurrencyHelper.getInstance().loadCurrencyMasterAddress(currencyBean.getCode());
        if(entity != null){
            addressEntity = entity;
            currencyAddress = addressEntity.getAddress();
            scanHead = transferHeader(currencyBean) +currencyAddress + "?" + "amount=";
            addressTv.setText(currencyAddress);
            showScanView(transferHeader(currencyBean) + currencyAddress);
        }else{
            NativeWallet.getInstance().initAccount(currencyBean).requestAddressList(new WalletListener<String>() {
                @Override
                public void success(String list) {
                    CurrencyAddressEntity entity = CurrencyHelper.getInstance().loadCurrencyMasterAddress(currencyBean.getCode());
                    if (entity == null) {
                        ActivityUtil.goBack(mActivity);
                    } else {
                        addressEntity = entity;
                        currencyAddress=addressEntity.getAddress();
                        scanHead = transferHeader(currencyBean) +currencyAddress + "?" + "amount=";
                        addressTv.setText(currencyAddress);
                        showScanView(transferHeader(currencyBean) + currencyAddress);
                    }
                }

                @Override
                public void fail(WalletError error) {}
            });
        }
    }

    @OnClick(R.id.left_img)
    void goback(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.address_lin)
    void goCopy(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(currencyAddress);
        ToastEUtil.makeText(mActivity, R.string.Set_Copied).show();
    }

    @OnClick(R.id.right_lin)
    void goshare(View view) {
        if (TextUtils.isEmpty(currencyAddress)) {
            return;
        }

        String url = shareUrl + currencyAddress;
        if (!TextUtils.isEmpty(amountEt.getText().toString())) {
            url = url + "&amount=" + amountEt.getText().toString();
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "share to"));
    }

    @OnClick(R.id.setAmount_tv)
    void setAmount(View view) {
        if (addressLin.getVisibility() == View.VISIBLE) {
            addressLin.setVisibility(View.GONE);
            amountLin.setVisibility(View.VISIBLE);
            setAmountTv.setText(R.string.Wallet_Clear);
        } else {
            addressLin.setVisibility(View.VISIBLE);
            amountLin.setVisibility(View.GONE);
            setAmountTv.setText(R.string.Wallet_Set_Amount);
            amountEt.setText("");
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String scanUrl;
            if (TextUtils.isEmpty(s.toString())) {
                scanUrl = transferHeader(currencyBean) + currencyAddress;
            } else {
                scanUrl = scanHead + s.toString();
            }
            showScanView(scanUrl);
        }
    };

    private void showScanView(final String scanUrl){
        new AsyncTask<Void,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                CreateScan createScan = new CreateScan();
                Bitmap bitmap = createScan.generateQRCode(scanUrl);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                scanImg.setImageBitmap(bitmap);
            }
        }.execute();
    }

    public String transferHeader(CurrencyEnum bean) {
        String index = "bitcoin:";
        switch (bean) {
            case BTC:
                index = "bitcoin:";
                break;
        }
        return index;
    }
}