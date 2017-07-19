package connect.activity.chat.exts;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.database.MemoryDataManager;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ParamManager;
import connect.database.green.DaoHelper.TransactionHelper;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.activity.chat.bean.ContainerBean;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.wallet.bean.WalletAccountBean;
import connect.utils.ProtoBufUtil;
import connect.utils.transfer.TransferError;
import connect.utils.transfer.TransferUtil;
import connect.activity.base.BaseActivity;
import connect.activity.base.BaseApplication;
import connect.utils.ActivityUtil;
import connect.utils.data.RateFormatUtil;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.cryption.DecryptionUtil;
import connect.utils.cryption.SupportKeyUril;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.wallet.cwallet.business.BaseBusiness;
import connect.wallet.cwallet.inter.WalletListener;
import connect.widget.MdStyleProgress;
import connect.widget.TopToolBar;
import connect.widget.payment.PaymentPwd;
import connect.widget.roundedimageview.RoundedImageView;
import protos.Connect;
import wallet_gateway.WalletOuterClass;

/**
 * private chat gather
 * Created by gtq on 2016/12/22.
 */
public class GatherDetailSingleActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.roundimg)
    RoundedImageView roundimg;
    @Bind(R.id.txt1)
    TextView txt1;
    @Bind(R.id.txt2)
    TextView txt2;
    @Bind(R.id.txt3)
    TextView txt3;
    @Bind(R.id.txt4)
    TextView txt4;
    @Bind(R.id.btn)
    Button btn;

    private GatherDetailSingleActivity activity;
    private static String GATHER_HASHID = "GATHER_HASHID";
    private static String GATHER_MSGID = "GATHER_MSGID";

    private PaymentPwd paymentPwd;
    private int state;
    private Connect.Bill billDetail = null;

    private String msgId;
    private String hashid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_singledetail);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity, String... strings) {
        Bundle bundle = new Bundle();
        bundle.putString(GATHER_HASHID, strings[0]);
        if (strings.length == 2) {
            bundle.putString(GATHER_MSGID, strings[1]);
        }
        ActivityUtil.next(activity, GatherDetailSingleActivity.class, bundle);
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setBlackStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Wallet_Detail));
        toolbar.setLeftListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });

        hashid = getIntent().getStringExtra(GATHER_HASHID);
        msgId = getIntent().getStringExtra(GATHER_MSGID);
        requestGatherDetail(hashid);
    }

    @OnClick({R.id.btn})
    public void OnClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn:
                int state = (int) view.getTag();
                switch (state) {
                    case 0://Did not pay ,wait for payment
                        ActivityUtil.goBack(activity);
                        break;
                    case 1://Did not pay ,to pay
                        requestPayment();
                        break;
                    case 2:
                        ActivityUtil.goBack(activity);
                        break;
                }
                break;
        }
    }

    protected void requestGatherDetail(final String hashid) {
        final Connect.BillHashId hashId = Connect.BillHashId.newBuilder().setHash(hashid).build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BILLING_INFO, hashId, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                    if (!SupportKeyUril.verifySign(imResponse.getSign(), imResponse.getCipherData().toByteArray())) {
                        throw new Exception("Validation fails");
                    }

                    Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(imResponse.getCipherData());
                    billDetail = Connect.Bill.parseFrom(structData.getPlainData());
                    if(!ProtoBufUtil.getInstance().checkProtoBuf(billDetail)){
                        return;
                    }

                    String username = "";
                    ContactEntity entity = null;
                    if (MemoryDataManager.getInstance().getAddress().equals(billDetail.getReceiver())) {//I started gathering
                        entity = ContactHelper.getInstance().loadFriendEntity(billDetail.getSender());
                        username = TextUtils.isEmpty(entity.getUsername()) ? entity.getRemark() : entity.getUsername();
                        txt1.setText(String.format(getString(R.string.Wallet_has_requested_to_payment), username));
                    } else {//I received the payment
                        entity = ContactHelper.getInstance().loadFriendEntity(billDetail.getReceiver());
                        username = TextUtils.isEmpty(entity.getUsername()) ? entity.getRemark() : entity.getUsername();
                        txt1.setText(String.format(getString(R.string.Wallet_has_requested_for_payment), username));
                    }
                    if (entity != null) {
                        GlideUtil.loadAvater(roundimg, entity.getAvatar());
                    }

                    if (TextUtils.isEmpty(billDetail.getTips())) {
                        txt2.setVisibility(View.GONE);
                    } else {
                        txt2.setText(billDetail.getTips());
                    }

                    String amout = RateFormatUtil.longToDoubleBtc(billDetail.getAmount());
                    txt3.setText(getResources().getString(R.string.Set_BTC_symbol) + amout);

                    if (MemoryDataManager.getInstance().getAddress().equals(billDetail.getReceiver())) {
                        txt4.setVisibility(View.INVISIBLE);
                    } else {
                        txt4.setVisibility(View.VISIBLE);
                        requestWallet();
                    }

                    state = billDetail.getStatus();
                    if (state == 0) {//Did not pay
                        if (MemoryDataManager.getInstance().getAddress().equals(billDetail.getReceiver())) {
                            btn.setText(getResources().getString(R.string.Wallet_Waitting_for_pay));
                            btn.setBackgroundResource(R.drawable.shape_stroke_red);
                            btn.setTag(0);
                        } else {
                            btn.setText(getResources().getString(R.string.Set_Payment));
                            btn.setBackgroundResource(R.drawable.shape_stroke_green);
                            btn.setTag(1);
                        }
                    } else if (state == 1) {
                        btn.setText(getResources().getString(R.string.Common_Cancel));
                        btn.setBackgroundResource(R.drawable.shape_stroke_red);
                        btn.setTag(2);
                    }

                    if (!TextUtils.isEmpty(msgId)) {
                        TransactionHelper.getInstance().updateTransEntity(hashid, msgId, state);
                    }
                    ContainerBean.sendRecExtMsg(ContainerBean.ContainerType.GATHER_DETAIL, msgId, 0, state);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpResponse response) {

            }
        });
    }

    private void requestWallet() {
        String url = String.format(UriUtil.BLOCKCHAIN_UNSPENT_INFO, MemoryDataManager.getInstance().getAddress());
        OkHttpUtil.getInstance().get(url, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    if (response.getCode() == 2000) {
                        Connect.UnspentAmount unspentAmount = Connect.UnspentAmount.parseFrom(response.getBody());
                        if(ProtoBufUtil.getInstance().checkProtoBuf(unspentAmount)){
                            WalletAccountBean accountBean = new WalletAccountBean(unspentAmount.getAmount(), unspentAmount.getAvaliableAmount());
                            txt4.setText(BaseApplication.getInstance().getString(R.string.Wallet_Balance,
                                    RateFormatUtil.longToDoubleBtc(accountBean.getAvaAmount())));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                txt4.setText(BaseApplication.getInstance().getString(R.string.Wallet_Balance,
                        RateFormatUtil.longToDoubleBtc(0)));
            }
        });
    }

    protected void requestPayment() {
        BaseBusiness baseBusiness = new BaseBusiness(activity);
        baseBusiness.typePayment(hashid, 8, new WalletListener<WalletOuterClass.OriginalTransactionResponse>() {
            @Override
            public void success(WalletOuterClass.OriginalTransactionResponse response) {
                ContactEntity entity = ContactHelper.getInstance().loadFriendEntity(billDetail.getReceiver());
                if (entity != null) {
                    String contactName = TextUtils.isEmpty(entity.getRemark()) ? entity.getUsername() : entity.getRemark();
                    String noticeContent = getString(R.string.Chat_paid_the_bill_to, activity.getString(R.string.Chat_You), contactName);
                    RecExtBean.sendRecExtMsg(RecExtBean.ExtType.NOTICE, noticeContent);
                }

                TransactionHelper.getInstance().updateTransEntity(billDetail.getHash(), msgId, 1);
                ToastEUtil.makeText(activity, R.string.Wallet_Payment_Successful).show();
                ActivityUtil.goBack(activity);
            }

            @Override
            public void fail(WalletError error) {

            }
        });
    }
}