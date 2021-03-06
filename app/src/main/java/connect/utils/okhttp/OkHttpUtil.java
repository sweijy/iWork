package connect.utils.okhttp;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;

import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.utils.StringUtil;
import connect.utils.log.LogManager;
import protos.Connect;

/**
 * OkHttp network tool
 */
public class OkHttpUtil {

    private static OkHttpUtil mInstance;

    public synchronized static OkHttpUtil getInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtil();
        }
        return mInstance;
    }

    /**
     * post(receive ProtoBuff)
     * @param url
     * @param body
     * @param resultCall
     */
    public void postEncrySelf(String url, GeneratedMessageV3 body, final ResultCall resultCall){
        LogManager.getLogger().http("param:" + body.toString());
        ByteString bytes = body == null ? ByteString.copyFrom(new byte[]{}) : body.toByteString();
        postEncrySelf(url,bytes,resultCall);
    }

    /**
     * post(receive ProtoBuff)
     * @param url
     * @param body
     * @param exts
     * @param resultCall
     */
//    public void postEncrySelf(String url, GeneratedMessageV3 body, EncryptionUtil.ExtendedECDH exts, final ResultCall resultCall){
//        ByteString bytes = body == null ? ByteString.copyFrom(new byte[]{}) : body.toByteString();
//        postEncrySelf(url, bytes, resultCall);
//        /*UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
//        ByteString bytes = body == null ? ByteString.copyFrom(new byte[]{}) : body.toByteString();
//        Connect.HttpRequest httpRequest = getHttpRequest(exts, userBean.getPriKey(),userBean.getPubKey(), userBean.getUid(), bytes);
//        if(null == httpRequest)
//            return;
//        HttpRequest.getInstance().post(url,httpRequest,resultCall);*/
//    }

    /**
     * post(receive ByteString)
     * @param url
     * @param bytes
     * @param resultCall
     */
    public void postEncrySelf(String url, ByteString bytes, final ResultCall resultCall){
        ByteString random = ByteString.copyFrom(StringUtil.getSecureRandom(16));
        Connect.StructData structData = Connect.StructData.newBuilder()
                .setRandom(random)
                .setPlainData(bytes)
                .build();

        UserBean userBean = SharedPreferenceUtil.getInstance().getUser();
        Connect.HttpRequest httpRequest = Connect.HttpRequest.newBuilder()
                .setUid(userBean.getUid())
                .setBody(structData.toByteString())
                .setToken(userBean.getToken()).build();
        HttpRequest.getInstance().post(url, httpRequest, resultCall);
    }


//    private Connect.HttpRequest getHttpRequest(EncryptionUtil.ExtendedECDH exts, String priKey, String pubKey,String uid, ByteString bytes){
//        Connect.GcmData gcmData = EncryptionUtil.encodeAESGCMStructData(exts, priKey, bytes);
//        if(null == gcmData){
//            return null;
//        }
//        Connect.HttpRequest httpRequest = Connect.HttpRequest.newBuilder()
//                .setUid(uid)
//                .setBody(bytes)
//                .setToken("").build();
//        return httpRequest;
//    }
//
//    public Connect.IMRequest getIMRequest(EncryptionUtil.ExtendedECDH exts, String priKey, String pubKey, ByteString bytes) {
//        Connect.GcmData gcmData = EncryptionUtil.encodeAESGCMStructData(exts, priKey, bytes);
//        if(null == gcmData){
//            return null;
//        }
//        Connect.IMRequest imRequest = Connect.IMRequest.newBuilder()
//                .setPubKey(pubKey)
//                .setCipherData(gcmData)
//                .setSign(SupportKeyUril.signHash(priKey, gcmData.toByteArray())).build();
//        return imRequest;
//    }

}
