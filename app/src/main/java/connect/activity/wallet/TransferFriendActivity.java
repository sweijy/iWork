package connect.activity.wallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.database.MemoryDataManager;
import connect.database.green.bean.ContactEntity;
import connect.ui.activity.R;
import connect.activity.common.selefriend.SeleUsersActivity;
import connect.activity.set.PayFeeActivity;
import connect.activity.wallet.adapter.FriendGridAdapter;
import connect.activity.wallet.bean.FriendSeleBean;
import connect.activity.wallet.bean.TranAddressBean;
import connect.activity.wallet.contract.TransferFriendContract;
import connect.activity.wallet.presenter.TransferFriendPresenter;
import connect.utils.ToastEUtil;
import connect.utils.transfer.TransferUtil;
import connect.activity.base.BaseActivity;
import connect.utils.ActivityUtil;
import connect.utils.data.RateFormatUtil;
import connect.wallet.cwallet.bean.CurrencyEnum;
import connect.wallet.cwallet.business.BaseBusiness;
import connect.wallet.cwallet.inter.WalletListener;
import connect.widget.TopToolBar;
import connect.widget.payment.PaymentPwd;
import connect.utils.transfer.TransferEditView;

/**
 * Transfer to friend
 * Created by Administrator on 2016/12/20.
 */
public class TransferFriendActivity extends BaseActivity implements TransferFriendContract.View {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.transfer_edit_view)
    TransferEditView transferEditView;
    @Bind(R.id.ok_btn)
    Button okBtn;
    @Bind(R.id.number_tv)
    TextView numberTv;
    @Bind(R.id.gridview)
    GridView gridview;
    @Bind(R.id.sele_friend_img)
    ImageView seleFriendImg;

    private TransferFriendActivity mActivity;
    private TransferFriendContract.Presenter presenter;
    private TransferUtil transaUtil;
    private FriendGridAdapter friendGridAdapter;
    private final int BACK_CODE = 102;
    private final int BACK_DEL_CODE = 103;
    private PaymentPwd paymentPwd;
    private String pubGroup;
    private BaseBusiness baseBusiness;

    public static void startActivity(Activity activity, List<ContactEntity> list,String pubGroup) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", new FriendSeleBean(list));
        bundle.putString("pubGroup",pubGroup);
        ActivityUtil.next(activity, TransferFriendActivity.class, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transfer_friend);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        transferEditView.initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Wallet_Transfer);
        setPresenter(new TransferFriendPresenter(this));

        Bundle bundle = getIntent().getExtras();
        pubGroup = bundle.getString("pubGroup","");
        FriendSeleBean friendSeleBean = (FriendSeleBean) bundle.getSerializable("list");
        List<ContactEntity> list = friendSeleBean.getList();
        presenter.setListData(list);

        friendGridAdapter = new FriendGridAdapter();
        gridview.setAdapter(friendGridAdapter);
        gridview.setOnItemClickListener(presenter.getItemClickListener());

        friendGridAdapter.setNotifyData(list);
        numberTv.setText(getString(R.string.Wallet_transfer_man, list.size()));
        transferEditView.setEditListener(presenter.getOnEditListener());
        presenter.horizontal_layout(gridview);

        transaUtil = new TransferUtil();
        paymentPwd = new PaymentPwd();

        baseBusiness = new BaseBusiness(mActivity, CurrencyEnum.BTC);
    }

    @Override
    public void setPresenter(TransferFriendContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public void addTranferFriend() {
        ArrayList<ContactEntity> list = new ArrayList<ContactEntity>();
        list.addAll(presenter.getListFriend());
        if(TextUtils.isEmpty(pubGroup)){
            SeleUsersActivity.startActivity(mActivity, SeleUsersActivity.SOURCE_FRIEND, "",list);
        }else{
            SeleUsersActivity.startActivity(mActivity, SeleUsersActivity.SOURCE_GROUP, pubGroup,list);
        }
    }

    @Override
    public void setPayFee() {
        PayFeeActivity.startActivity(mActivity);
    }

    @Override
    public String getCurrentBtc() {
        return transferEditView.getCurrentBtc();
    }

    @Override
    public void setBtnEnabled(boolean isEnable) {
        okBtn.setEnabled(isEnable);
    }

    @OnClick(R.id.left_img)
    void goback(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.sele_friend_img)
    void goSeleFriend(View view) {
        TransferFriendDelActivity.startActivity(mActivity, BACK_DEL_CODE, presenter.getListFriend());
    }

    @OnClick(R.id.ok_btn)
    void goTransferOut(View view) {
        HashMap<String,Long> outMap = new HashMap<String,Long>();
        for (ContactEntity friendEntity : presenter.getListFriend()) {
            outMap.put(friendEntity.getAddress(),transferEditView.getCurrentBtcLong());
        }
        baseBusiness.transferConnectUser(null, outMap, new WalletListener<String>() {
            @Override
            public void success(String value) {
                ToastEUtil.makeText(mActivity,R.string.Link_Send_successful).show();
            }

            @Override
            public void fail(WalletError error) {
                ToastEUtil.makeText(mActivity,R.string.Login_Send_failed).show();
            }
        });
    }

    private void checkPayPassword(final long amount, final String inputString, final String outputString) {
        if (!TextUtils.isEmpty(outputString)) {
            paymentPwd.showPaymentPwd(mActivity, new PaymentPwd.OnTrueListener() {
                @Override
                public void onTrue(String value) {
                    String samValue = transaUtil.getSignRawTrans(MemoryDataManager.getInstance().getPriKey(), inputString, outputString);
                    presenter.requestSend(amount, samValue,transferEditView.getNote(),paymentPwd);
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BACK_CODE || requestCode == BACK_DEL_CODE) {
                FriendSeleBean friendSeleBean = (FriendSeleBean) data.getExtras().getSerializable("list");
                List<ContactEntity> list = friendSeleBean.getList();
                presenter.setListData(list);
                presenter.horizontal_layout(gridview);
                friendGridAdapter.setNotifyData(list);
                numberTv.setText(getString(R.string.Wallet_transfer_man, list.size()));
                presenter.checkBtnEnable();
            }else if(requestCode == SeleUsersActivity.CODE_REQUEST){
                ArrayList<ContactEntity> friendList = (ArrayList<ContactEntity>) data.getExtras().getSerializable("list");
                presenter.setListData(friendList);
                presenter.horizontal_layout(gridview);
                friendGridAdapter.setNotifyData(friendList);
                numberTv.setText(getString(R.string.Wallet_transfer_man, friendList.size()));
                presenter.checkBtnEnable();
            }
        }
    }

}
