package connect.activity.chat.exts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.chat.exts.contract.LuckyPacketContract;
import connect.activity.chat.exts.presenter.LuckyPacketPresenter;
import connect.activity.set.PayFeeActivity;
import connect.activity.wallet.PacketHistoryActivity;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.RegularUtil;
import connect.utils.glide.GlideUtil;
import connect.wallet.cwallet.bean.CurrencyEnum;
import connect.wallet.cwallet.business.BaseBusiness;
import connect.wallet.cwallet.business.TransferEditView;
import connect.widget.TopToolBar;
import connect.widget.random.RandomVoiceActivity;
import connect.widget.roundedimageview.RoundedImageView;

/**
 * send lucky packet
 * Created by gtq on 2016/12/28.
 */
public class LuckyPacketActivity extends BaseActivity implements LuckyPacketContract.BView {

    @Bind(R.id.toolbar)
    TopToolBar toolbar;
    @Bind(R.id.roundimg)
    RoundedImageView roundimg;
    @Bind(R.id.txt1)
    TextView txt1;
    @Bind(R.id.layout_first)
    RelativeLayout layoutFirst;
    @Bind(R.id.edit)
    EditText edit;
    @Bind(R.id.layout_second)
    RelativeLayout layoutSecond;
    @Bind(R.id.transfer_edit_view)
    TransferEditView transferEditView;
    @Bind(R.id.btn)
    Button btn;

    private LuckyPacketActivity activity;
    private static String RED_TYPE = "RED_TYPE";
    private static String RED_KEY = "RED_KEY";
    /** packet type 1:private 2:group */
    private int redType;
    /** packet address */
    private String redKey;
    private BaseBusiness baseBusiness;

    private LuckyPacketContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redpacket);
        ButterKnife.bind(this);
        initView();
    }

    public static void startActivity(Activity activity, int type, String roomkey) {
        Bundle bundle = new Bundle();
        bundle.putInt(RED_TYPE, type);
        bundle.putString(RED_KEY, roomkey);
        ActivityUtil.next(activity, LuckyPacketActivity.class, bundle);
    }

    @Override
    public void initView() {
        activity = this;
        toolbar.setRedStyle();
        toolbar.setLeftImg(R.mipmap.back_white);
        toolbar.setTitle(getResources().getString(R.string.Wallet_Packet));
        toolbar.setLeftListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.goBack(activity);
            }
        });
        toolbar.setRightText(getString(R.string.Chat_History));
        toolbar.setRightListence(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PacketHistoryActivity.startActivity(activity);
            }
        });

        redType = getIntent().getIntExtra(RED_TYPE, 0);
        redKey = getIntent().getStringExtra(RED_KEY);

        if (redType == 0) {
            layoutFirst.setVisibility(View.VISIBLE);
            layoutSecond.setVisibility(View.GONE);
        } else if (redType == 1) {
            layoutFirst.setVisibility(View.GONE);
            layoutSecond.setVisibility(View.VISIBLE);
        }
        baseBusiness = new BaseBusiness(activity, CurrencyEnum.BTC);

        new LuckyPacketPresenter(this).start();
        presenter.requestRoomEntity(redType);
    }

    @Override
    protected void onStart() {
        super.onStart();
        transferEditView.initView(activity);
        transferEditView.setNote(getString(R.string.Wallet_Best_wishes));
        transferEditView.setEditListener(new TransferEditView.OnEditListener() {
            @Override
            public void onEdit(String value) {
                if (TextUtils.isEmpty(value) || Double.valueOf(transferEditView.getCurrentBtc()) < 0.0001) {
                    btn.setEnabled(false);
                } else {
                    btn.setEnabled(true);
                }
            }

            @Override
            public void setFee() {
                PayFeeActivity.startActivity(activity);
            }
        });
    }

    @OnClick({R.id.btn})
    public void OnClickListener(View view) {
        switch (view.getId()) {
            case R.id.btn:
                int packetcount = 1;
                if (redType == 1) {
                    String sizeString = edit.getText().toString();
                    if (RegularUtil.matches(sizeString, RegularUtil.ALL_NUMBER)) {
                        packetcount = Integer.valueOf(sizeString);
                    } else {
                        return;
                    }
                }

                long amount = transferEditView.getCurrentBtcLong();
                String note = transferEditView.getNote();
                presenter.sendLuckyPacket(redType, packetcount, amount, note);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RandomVoiceActivity.REQUEST_CODE:
                    transferEditView.createWallet(data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public String getRoomKey() {
        return redKey;
    }

    @Override
    public void setPresenter(LuckyPacketContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void showUserInfo(String avatar, String name) {
        GlideUtil.loadAvater(roundimg, name);
        txt1.setText(getString(R.string.Wallet_Send_Lucky_Packet_to, name));
    }

    @Override
    public void showGroupInfo(int count) {
        edit.setText(String.valueOf(count));
    }

    @Override
    public BaseBusiness getBusiness() {
        return baseBusiness;
    }
}
