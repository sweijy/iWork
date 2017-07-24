package connect.activity.wallet;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.home.view.LineDecoration;
import connect.activity.wallet.adapter.TransactionAdapter;
import connect.database.MemoryDataManager;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ProtoBufUtil;
import connect.utils.UriUtil;
import connect.utils.cryption.DecryptionUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.wallet.cwallet.bean.CurrencyEnum;
import connect.widget.TopToolBar;
import connect.widget.pullTorefresh.EndlessScrollListener;
import protos.Connect;
import wallet_gateway.WalletOuterClass;

/**
 * transaction
 * Created by Administrator on 2016/12/10.
 */
public class TransactionActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshview)
    SwipeRefreshLayout refreshview;


    private TransactionActivity mActivity;
    private final int PAGESIZE_MAX = 10;
    private int page = 1;
    private TransactionAdapter ransactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transaction);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Wallet_Transactions);

        refreshview.setColorSchemeResources(
                R.color.color_ebecee,
                R.color.color_c8ccd5,
                R.color.color_lightgray
        );
        refreshview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshview.setRefreshing(false);
                page = 1;
                requsetTransaction();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        ransactionAdapter = new TransactionAdapter(mActivity);
        recyclerview.setAdapter(ransactionAdapter);
        recyclerview.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                page++;
                requsetTransaction();
            }
        });
        ransactionAdapter.setItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void itemClick(Connect.Transaction transaction) {
                BlockchainActivity.startActivity(mActivity, CurrencyEnum.BTC, transaction.getHash());
            }
        });
        requsetTransaction();
    }

    @OnClick(R.id.left_img)
    void goback(View view) {
        ActivityUtil.goBack(mActivity);
    }

    public void requsetTransaction() {
        WalletOuterClass.Pagination pagination = WalletOuterClass.Pagination.newBuilder()
                .setPage(page)
                .setSize(PAGESIZE_MAX).build();

        WalletOuterClass.GetTx getTx = WalletOuterClass.GetTx.newBuilder()
                .setCurrency(0)
                //.setAddress(MemoryDataManager.getInstance().getAddress())
                .setPage(pagination).build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.WALLET_V2_COINS_ADDRESSES_TX, getTx, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                    Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(imResponse.getCipherData());
                    Connect.Transactions transactions = Connect.Transactions.parseFrom(structData.getPlainData());

                    List<Connect.Transaction> list = transactions.getTransactionsList();
                    ArrayList<Connect.Transaction> listChecks = new ArrayList<>();
                    for (Connect.Transaction transaction : list) {
                        if (ProtoBufUtil.getInstance().checkProtoBuf(transaction)) {
                            listChecks.add(transaction);
                        }
                    }

                    if (page > 1) {
                        ransactionAdapter.setNotifyData(listChecks, false);
                    } else {
                        ransactionAdapter.setNotifyData(listChecks, true);
                    }
                    refreshview.setRefreshing(false);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                refreshview.setRefreshing(false);
            }
        });
    }
}
