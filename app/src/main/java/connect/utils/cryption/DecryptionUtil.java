package connect.utils.cryption;

import connect.database.SharedPreferenceUtil;
import connect.utils.ConfigUtil;
import connect.utils.TimeUtil;
import connect.wallet.jni.AllNativeMethod;
import com.google.protobuf.InvalidProtocolBufferException;

import protos.Connect;

/**
 * Decryption related methods
 */
public class DecryptionUtil {

    /**
     * Decryption gcmData returns a byteArray containing
     */
    public static byte[] decodeAESGCM(EncryptionUtil.ExtendedECDH extendedECDH, String priKey, String pubKey, Connect.GcmData gcmData) {
        byte[] rawECDHKey = SupportKeyUril.getRawECDHKey(priKey, pubKey);
        return DecryptionUtil.decodeAESGCM(extendedECDH, rawECDHKey, gcmData);
    }

    /**
     * Decryption GcmData returned to StructData
     */
    public static Connect.StructData decodeAESGCMStructData(Connect.GcmData gcmData) {
        return decodeAESGCMStructData(EncryptionUtil.ExtendedECDH.SALT, SharedPreferenceUtil.getInstance().getUser().getUid(), gcmData);
    }

    public static Connect.StructData decodeAESGCMStructData(EncryptionUtil.ExtendedECDH extendedECDH, String priKey, Connect.GcmData gcmData) {
        return decodeAESGCMStructData(extendedECDH, priKey, ConfigUtil.getInstance().serverPubKey(), gcmData);
    }

    public static Connect.StructData decodeAESGCMStructData(EncryptionUtil.ExtendedECDH extendedECDH, String priKey, String pubKey, Connect.GcmData gcmData) {
        byte[] rss = SupportKeyUril.getRawECDHKey(priKey, pubKey);
        return decodeAESGCMStructData(extendedECDH, rss, gcmData);
    }

    public static Connect.StructData decodeAESGCMStructData(EncryptionUtil.ExtendedECDH extendedECDH, byte[] byteECDH, Connect.GcmData gcmData) {
        try {
            byte[] dataAESGCM = DecryptionUtil.decodeAESGCM(extendedECDH, byteECDH, gcmData);
            Connect.StructData structData = Connect.StructData.parseFrom(dataAESGCM);
            return structData;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized byte[] decodeAESGCM(EncryptionUtil.ExtendedECDH extendedECDH, byte[] rawECDHkey, Connect.GcmData gcmData) {
        rawECDHkey = EncryptionUtil.getKeyExtendedECDH(extendedECDH, rawECDHkey);
        return decodeAESGCM(rawECDHkey,gcmData);
    }

    public static synchronized byte[] decodeAESGCM(byte[] rawECDHkey, Connect.GcmData gcmData) {
        byte[] aad = gcmData.getAad().toByteArray();
        byte[] iv = gcmData.getIv().toByteArray();
        byte[] cipher = gcmData.getCiphertext().toByteArray();
        byte[] tag = gcmData.getTag().toByteArray();

        byte[] rss = rawECDHkey;
        byte[] dataAESGCM = AllNativeMethod.cdxtalkDecodeAESGCM(cipher, cipher.length,
                aad, aad.length, rss, rss.length, iv, iv.length, tag, tag.length);
        return dataAESGCM;
    }
}