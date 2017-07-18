package connect.activity.wallet.manager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;

import connect.activity.base.BaseApplication;
import connect.activity.home.bean.EstimatefeeBean;
import connect.activity.set.bean.PaySetBean;
import connect.activity.wallet.bean.SignRawBean;
import connect.database.SharedPreferenceUtil;
import connect.database.green.DaoHelper.CurrencyHelper;
import connect.database.green.DaoHelper.ParamManager;
import connect.database.green.bean.CurrencyEntity;
import connect.ui.activity.R;
import connect.utils.DialogUtil;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.cryption.DecryptionUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.wallet.jni.AllNativeMethod;
import protos.Connect;
import wallet_gateway.WalletOuterClass;

/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class TransferManager extends BaseTransfer{

    private CurrencyType currencyType;

    public TransferManager(CurrencyType currencyType) {
        super(currencyType);
        this.currencyType = currencyType;
    }

    /**
     * 基本打款
     * @param activity
     * @param txin 输入地址数组
     * @param outPuts 输出地址信息
     * @param onResultCall
     */
    public void getTransferRow(final Activity activity, WalletOuterClass.Txin txin,
                               List<WalletOuterClass.Txout> outPuts, final OnResultCall onResultCall){
        requestCurrencyAddress();
        PaySetBean paySetBean = ParamManager.getInstance().getPaySet();
        WalletOuterClass.TransferRequest.Builder builder = WalletOuterClass.TransferRequest.newBuilder();
        WalletOuterClass.SpentCurrency.Builder builderSend = WalletOuterClass.SpentCurrency.newBuilder();
        builderSend.setCurrency(currencyType.getCode());
        if(txin != null){
            builderSend.setTxin(txin);
        }
        builder.setSpentCurrency(builderSend.build());

        if(paySetBean.isAutoFee()){
            builder.setFee(0L);
        }else{
            builder.setFee(paySetBean.getFee());
        }
        for(WalletOuterClass.Txout txout : outPuts){
            builder.addTxOut(txout);
        }
        builder.setTips("");

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.WALLET_V2_SERVICE_TRANSFER, builder.build(), new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                    Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(imResponse.getCipherData());
                    WalletOuterClass.OriginalTransactionResponse originalResponse = WalletOuterClass.OriginalTransactionResponse.parseFrom(structData.getPlainData());
                    WalletOuterClass.OriginalTransaction originalTransaction = originalResponse.getData();
                    switch (originalResponse.getCode()){
                        case 0:
                            ArrayList<String> priKeyList = checkPin(activity, originalTransaction.getAddressesList());
                            String rawHex = getSignRawTrans(priKeyList, originalTransaction.getVts(), originalTransaction.getRawhex());
                            publishTransfer(rawHex, originalTransaction.getTransactionId(), new OnBaseResultCall(){
                                @Override
                                public void success() {
                                    //广播成功
                                    onResultCall.result("asdasd");
                                }
                            });
                            break;
                        case 1://手续费过小
                            DialogUtil.showAlertTextView(activity, activity.getString(R.string.Set_tip_title),
                                    activity.getString(R.string.Wallet_Transaction_fee_too_low_Continue),
                                    "", "", false, new DialogUtil.OnItemClickListener() {
                                        @Override
                                        public void confirm(String value) {
                                            //checkDust(orderResponse);
                                        }

                                        @Override
                                        public void cancel() {
                                            //connectDialog.dismiss();
                                        }
                                    });
                            break;
                        case 2://手续费为空
                            break;
                        case 3://unspent太大
                            break;
                        default:
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                Toast.makeText(BaseApplication.getInstance().getAppContext(),response.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*###### 红包转账
    - uri: /wallet/v1/redPack/send
    - method: post
    - args:
        * total(个数)
        * fee（*2）
        * amount(金额)
        * type（等额或者随机，默认为0）
        * currency
        * category（外部红或者内部红包）
        * from:(数组：{address, address})
    - response
        * rowhex
        * tvs
        * url (外部红包的url)*/
    public void sendRedPack(final Activity activity, ArrayList<String> fromList, ArrayList<Integer> indexList, int total, Long amount, int category){
        OkHttpUtil.getInstance().postEncrySelf("url", ByteString.copyFrom(new byte[]{}), new ResultCall() {
            @Override
            public void onResponse(Object response) {
                //checkPin(activity, "rowhex", "tvs", "url");
            }

            @Override
            public void onError(Object response) {

            }
        });
    }

    /*###### 外部转账
    - uri: /wallet/v1/outTransfer/send
    - method: post
    - args:
        * amount(金额)
        * fee（*2）
        * currency
        * from:(数组：{address, address})s
    - response
        * rowhex
        * tvs*/
    private void seedOutTransfer(final Activity activity, ArrayList<String> fromList, ArrayList<Integer> indexList, Long amount){
        OkHttpUtil.getInstance().postEncrySelf("url", ByteString.copyFrom(new byte[]{}), new ResultCall() {
            @Override
            public void onResponse(Object response) {
                //checkPin(activity, "rowhex", "tvs", "url");
            }

            @Override
            public void onError(Object response) {

            }
        });
    }

    /**
     * 同步货币地址列表
     */
    private void requestCurrencyAddress(){
        CurrencyEntity currencyEntity = CurrencyHelper.getInstance().loadCurrency(currencyType.getCode());
        WalletOuterClass.Coin.Builder builder = WalletOuterClass.Coin.newBuilder();
        builder.setCurrency(currencyEntity.getCurrency());
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.WALLET_V2_COINS_ADDRESS_LIST, builder.build(), new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                    Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(imResponse.getCipherData());
                    WalletOuterClass.CoinsDetail coinsDetail = WalletOuterClass.CoinsDetail.parseFrom(structData.getPlainData());
                    List<WalletOuterClass.CoinInfo> list = coinsDetail.getCoinInfosList();
                    CurrencyHelper.getInstance().insertCurrencyAddressListCoinInfo(list,currencyType.getCode());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpResponse response) {

            }
        });
    }

    /**
     * 检查交易
     * @return
     */
    private boolean checkTransfer(){
        return true;
    }

    /**
     * 签名交易
     * @param priList 签名交易的PriKey数组
     * @param tvs 所有输入地址的Unspent字符串集合
     * @param rowhex 原始交易
     * @return 签名交易
     */
    private String getSignRawTrans(ArrayList<String> priList, String tvs, String rowhex) {
        String signTransfer = rowhex + " " + tvs + " " + new Gson().toJson(priList);
        String signRawTransfer = AllNativeMethod.cdSignRawTranscation(signTransfer);
        SignRawBean signRawBean = new Gson().fromJson(signRawTransfer, SignRawBean.class);
        return signRawBean.getHex();
    }


    /**
     * 判断金额是否肮脏
     */
    private static boolean isHaveDustWithAmount(long amount) {
        EstimatefeeBean feeBean = SharedPreferenceUtil.getInstance().getEstimatefee();
        if(feeBean == null || TextUtils.isEmpty(feeBean.getData())){
            return false;
        }else{
            return (amount * 1000 / (3 * 182)) < Double.valueOf(feeBean.getData()) * Math.pow(10, 8) / 10;
        }
    }

    /**
     * 自动计算手续费
     */
    private static long getAutoFeeWithUnspentLength(boolean isAddChangeAddress,int txs_length, int sentToLength) {
        if(isAddChangeAddress){ // the change of address
            sentToLength++;
        }
        EstimatefeeBean feeBean = SharedPreferenceUtil.getInstance().getEstimatefee();
        int txSize = 148 * txs_length + 34 * sentToLength + 10;
        double estimateFee = (txSize + 20 + 4 + 34 + 4) / 1000.0 * Double.valueOf(feeBean.getData());
        return Math.round(estimateFee * Math.pow(10, 8));
    }

    public interface OnResultCall {
        void result(String value);
    }

}
