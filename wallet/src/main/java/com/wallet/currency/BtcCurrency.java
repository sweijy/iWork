package com.wallet.currency;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.wallet.bean.CurrencyBean;
import com.wallet.bean.SignRawBean;
import com.wallet.utils.WalletUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import connect.wallet.jni.AllNativeMethod;

/**
 * 比特币货币管理类
 */

public class BtcCurrency implements BaseCurrency{

    @Override
    public CurrencyBean createCurrency(int type, String value) {
        CurrencyBean currencyBean = null;
        switch (type) {
            case BaseCurrency.CATEGORY_PRIKEY:
                currencyBean = createCurrencyForPriKey(value);
                break;
            case BaseCurrency.CATEGORY_BASESEED:
                currencyBean = createCurrencyForBaseSeed(value);
                break;
            case BaseCurrency.CATEGORY_CURRENCY:
                currencyBean = createCurrencyForCurrencySeed(value);
                break;
            default:
                break;
        }
        return currencyBean;
    }

    @Override
    public CurrencyBean addCurrencyAddress(String baseSeed, String salt, int index) {
        String currencySeed = WalletUtil.xor(baseSeed, salt);
        String pubKey = AllNativeMethod.cdGetPubKeyFromSeedBIP44(currencySeed,44,0,0,0,index);
        String address = AllNativeMethod.cdGetBTCAddrFromPubKey(pubKey);

        CurrencyBean currencyBean = new CurrencyBean(-1, baseSeed, currencySeed, salt, "", index, address);
        return currencyBean;
    }

    @Override
    public List<String> getPriKeyFromAddressIndex(String baseSeed, String salt, List<Integer> indexList) {
        String currencySeed = WalletUtil.xor(baseSeed, salt);
        ArrayList<String> priKeyList = new ArrayList<>();
        for(Integer index : indexList){
            String priKey = AllNativeMethod.cdGetPrivKeyFromSeedBIP44(currencySeed,44,0,0,0,index);
            priKeyList.add(priKey);
        }
        return priKeyList;
    }

    @Override
    public String getSignRawTrans(ArrayList<String> priList, String tvs, String rowHex) {
        String signTransfer = rowHex + " " + tvs + " " + new Gson().toJson(priList);
        String signRawTransfer = AllNativeMethod.cdSignRawTranscation(signTransfer);
        SignRawBean signRawBean = new Gson().fromJson(signRawTransfer, SignRawBean.class);
        if(signRawBean.isComplete()){
            return signRawBean.getHex();
        }else{
            return "";
        }
    }

    @Override
    public String broadcastTransfer(String rawString) {
        return "";
    }

    /**
     * 用PriKey生成货币
     */
    private CurrencyBean createCurrencyForPriKey(String priKey){
        String pubKey = AllNativeMethod.cdGetPubKeyFromPrivKey(priKey);
        String address = AllNativeMethod.cdGetBTCAddrFromPubKey(pubKey);
        CurrencyBean currencyBean = new CurrencyBean(BaseCurrency.CATEGORY_PRIKEY, "", "", "", priKey, 0, address);
        return currencyBean;
    }

    /**
     * 用baseSeed生成货币
     */
    private CurrencyBean createCurrencyForBaseSeed(String baseSeed){
        int index = 0;
        if(TextUtils.isEmpty(baseSeed)){
            baseSeed = WalletUtil.getRandomSeed();
        }
        String salt = WalletUtil.getRandomSeed();
        // 生成货币的种子
        String currencySeed = WalletUtil.xor(baseSeed, salt);
        // 生成master address
        String pubKey = AllNativeMethod.cdGetPubKeyFromSeedBIP44(currencySeed,44,0,0,0,index);
        String masterAddress = AllNativeMethod.cdGetBTCAddrFromPubKey(pubKey);
        CurrencyBean currencyBean = new CurrencyBean(BaseCurrency.CATEGORY_BASESEED, baseSeed, currencySeed, salt, "", index, masterAddress);
        return currencyBean;
    }

    /**
     * 用currencySeed生成货币
     */
    private CurrencyBean createCurrencyForCurrencySeed(String currencySeed){
        int index = 0;
        String pubKey = AllNativeMethod.cdGetPubKeyFromSeedBIP44(currencySeed,44,0,0,0,index);
        String masterAddress = AllNativeMethod.cdGetBTCAddrFromPubKey(pubKey);
        CurrencyBean currencyBean = new CurrencyBean(BaseCurrency.CATEGORY_CURRENCY, "", currencySeed, "", "", 0, masterAddress);
        return currencyBean;
    }

    /**
     * 计算数据是否肮脏
     */
    public static boolean isHaveDustWithAmount(long amount, double data) {
        return (amount * 1000 / (3 * 182)) < data * Math.pow(10, 8) / 10;
    }

    /**
     * 自动计算手续费
     *
     * @param txs_length 输入unSpend个数
     * @param sentToLength 输出地址个数
     * @param data 每kb数据
     * @return 手续费
     */
    public static long getAutoFeeWithUnspentLength(int txs_length, int sentToLength, double data) {
        int txSize = 148 * txs_length + 34 * sentToLength + 10;
        double estimateFee = (txSize + 20 + 4 + 34 + 4) / 1000.0 * data;
        return Math.round(estimateFee * Math.pow(10, 8));
    }

}
