package connect.activity.wallet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.wallet.bean.SendOutBean;
import connect.database.MemoryDataManager;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.DialogUtil;
import connect.utils.ExCountDownTimer;
import connect.utils.TimeUtil;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.TopToolBar;
import connect.widget.zxing.utils.CreateScan;
import protos.Connect;

/**
 * send outer lucky packet
 * Created by Administrator on 2016/12/16.
 */
public class PacketSendActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.avater_rimg)
    ImageView avaterRimg;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.left_send_img)
    ImageView leftSendImg;
    @Bind(R.id.scan_img)
    ImageView scanImg;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.send_btn)
    Button sendBtn;
    @Bind(R.id.describe_tv)
    TextView describeTv;
    @Bind(R.id.hint_tv)
    TextView hintTv;
    @Bind(R.id.bottom_lin)
    LinearLayout bottomLin;

    private PacketSendActivity mActivity;
    public static final String RED_PACKET = "red";
    public static final String OUT_VIA = "via";
    private SendOutBean sendOutBean;

    public static void startActivity(Activity activity, SendOutBean sendOutBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("send", sendOutBean);
        ActivityUtil.next(activity, PacketSendActivity.class, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_send_packet);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setRightClickable(false);

        Bundle bundle = getIntent().getExtras();
        sendOutBean = (SendOutBean) bundle.getSerializable("send");

        if (sendOutBean.getType().equals(RED_PACKET)) {
            toolbarTop.setTitle(null, R.string.Wallet_Packet);
            leftSendImg.setImageResource(R.mipmap.luckybag3x);
            describeTv.setText(getString(R.string.Wallet_send_lucky_packet_via_Connect_packets, sendOutBean.getNumber()));
        } else if (sendOutBean.getType().equals(OUT_VIA)) {
            toolbarTop.setTitle(null, R.string.Wallet_Transfer);
            leftSendImg.setImageResource(R.mipmap.bitcoin_message3x);
            describeTv.setText(R.string.Wallet_Transfer_Out_Send_User_Connect);
            hintTv.setText(R.string.Wallet_Wallet_Out_Send_Share);
            if(sendOutBean.getStatus() == 0){
                toolbarTop.setRightImg(R.mipmap.menu_white);
                toolbarTop.setRightClickable(true);
            }
        }

        GlideUtil.loadAvatarRound(avaterRimg, MemoryDataManager.getInstance().getAvatar(), 12);
        nameTv.setText(MemoryDataManager.getInstance().getName());
        CreateScan createScan = new CreateScan();
        if(sendOutBean.getStatus() == 1){
            bottomLin.setVisibility(View.GONE);
            Drawable drawable= getResources().getDrawable(R.mipmap.attention_message3x);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            hintTv.setText(R.string.Wallet_Canceled);
            hintTv.setCompoundDrawables(drawable,null,null,null);
            hintTv.setCompoundDrawablePadding(10);
        }else if(sendOutBean.getStatus() == 2){
            bottomLin.setVisibility(View.GONE);
            Drawable drawable= getResources().getDrawable(R.mipmap.attention_message3x);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            hintTv.setText(R.string.Wallet_Expired);
            hintTv.setCompoundDrawables(drawable,null,null,null);
            hintTv.setCompoundDrawablePadding(10);
        }else if(sendOutBean.getStatus() == 3){
            bottomLin.setVisibility(View.GONE);
            hintTv.setText(R.string.Wallet_Get);
            Drawable drawable= getResources().getDrawable(R.mipmap.success_message3x);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            hintTv.setText(R.string.Wallet_Get);
            hintTv.setCompoundDrawables(drawable,null,null,null);
            hintTv.setCompoundDrawablePadding(10);
        }else{
            Bitmap bitmap = createScan.generateQRCode(sendOutBean.getUrl());
            scanImg.setImageBitmap(bitmap);
            bangPunchTimer(sendOutBean.getDeadline() * 1000);
        }
    }

    @OnClick(R.id.left_img)
    void goback(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.right_lin)
    void goHistory(View view) {
        ArrayList list = new ArrayList<>();
        list.add(mActivity.getResources().getString(R.string.Wallet_transferOutVia_return));
        DialogUtil.showBottomView(mActivity, list, new DialogUtil.DialogListItemClickListener() {
            @Override
            public void confirm(int position) {
                request();
            }
        });
    }

    @OnClick(R.id.send_btn)
    void sendPacket(View view) {
        String url = sendOutBean.getUrl();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "share to"));
    }

    private void request() {
        Connect.BillHashId billHashId = Connect.BillHashId.newBuilder()
                .setHash(sendOutBean.getHashId())
                .build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.WALLET_BILLING_EXTERNAL_CANCLE, billHashId, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                ToastEUtil.makeText(mActivity, R.string.Wallet_transferOutVia_return_Success);
                ActivityUtil.goBack(mActivity);
            }

            @Override
            public void onError(Connect.HttpResponse response) {

            }
        });
    }

    public void bangPunchTimer(final long leavetime) {
        ExCountDownTimer exCountDownTimer = new ExCountDownTimer(leavetime, 1000) {
            @Override
            public void onTick(long millisUntilFinished, int percent) {
                timeTv.setText(TimeUtil.showTimeCount(millisUntilFinished));
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onFinish() {
                timeTv.setText("00:00:00");
            }
        };
        exCountDownTimer.start();
    }
}
