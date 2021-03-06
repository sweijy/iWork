package instant.utils.cryption;

import com.google.protobuf.ByteString;

import java.security.SecureRandom;

import connect.wallet.jni.AllNativeMethod;
import connect.wallet.jni.GCMModel;
import protos.Connect;

/**
 * Encryption related methods
 */
public class EncryptionUtil {

    /**
     * Take byteArray encryption
     */
    public static Connect.GcmData encodeAESGCM(ExtendedECDH extendedECDH, String priKey, String ServerPubKey, byte[] encodes) {
        byte[] rawECDHKey = SupportKeyUril.getRawECDHKey(priKey, ServerPubKey);
        return encodeAESGCM(extendedECDH, rawECDHKey, encodes);
    }

    /**
     * Take struct encryption
     */
    public static Connect.GcmData encodeAESGCMStructData(ExtendedECDH extendedECDH, String priKey, ByteString bytes) {
        return encodeAESGCMStructData(extendedECDH, priKey, "", bytes);
    }

    public static Connect.GcmData encodeAESGCMStructData(ExtendedECDH extendedECDH, String priKey, String serverPubKey, ByteString bytes) {
        byte[] rawECDHKey = SupportKeyUril.getRawECDHKey(priKey, serverPubKey);
        if(null == rawECDHKey){
            return null;
        }
        return encodeAESGCMStructData(extendedECDH, rawECDHKey, bytes);
    }

    public static Connect.GcmData encodeAESGCMStructData(ExtendedECDH extendedECDH, byte[] rawECDHKey, ByteString bytes) {
        ByteString random = ByteString.copyFrom(SupportKeyUril.createBinaryRandom());
        Connect.StructData structData = Connect.StructData.newBuilder()
                .setRandom(random)
                .setPlainData(bytes)
                .build();
        return encodeAESGCM(extendedECDH, rawECDHKey, structData.toByteArray());
    }

    public static byte[] ab = "ConnectEncrypted".getBytes();

    public static Connect.GcmData encodeAESGCM(ExtendedECDH extendedECDH, byte[] rawECDHKey, byte[] encodes) {
        rawECDHKey = getKeyExtendedECDH(extendedECDH, rawECDHKey);

        ByteString iv = ByteString.copyFrom(SecureRandom.getSeed(16));
        byte[] ib = iv.toByteArray();

        GCMModel gc = AllNativeMethod.cdxtalkEncodeAESGCM(encodes, encodes.length,
                ab, ab.length, rawECDHKey, rawECDHKey.length, ib, ib.length);

        ByteString enc = ByteString.copyFrom(gc.encrypt);
        ByteString tag = ByteString.copyFrom(gc.tag);

        Connect.GcmData gcmData = Connect.GcmData.newBuilder().setIv(iv).
                setAad(ByteString.copyFrom(ab)).setCiphertext(enc).setTag(tag).build();
        return gcmData;
    }

    /**
     * ExtendedECDH type
     */
    public enum ExtendedECDH {
        NONE,//Don't need to extension
        EMPTY,//Empty salt extension
        SALT,//token salt
        OTHER;//other extension

        byte[] bytes;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    public static synchronized byte[] getKeyExtendedECDH(ExtendedECDH extendedECDH, byte[] rawECDHKey) {
        byte[] salts = null;
        switch (extendedECDH) {
            case NONE:
                break;
            case EMPTY:
                salts = new byte[64];
                rawECDHKey = AllNativeMethod.cdxtalkPBKDF2HMACSHA512(rawECDHKey, rawECDHKey.length, salts, salts.length, 12, 32);
                break;
            case OTHER:
                salts = extendedECDH.getBytes();
                rawECDHKey = AllNativeMethod.cdxtalkPBKDF2HMACSHA512(rawECDHKey, rawECDHKey.length, salts, salts.length, 12, 32);
                break;
            default:
                break;
        }
        return rawECDHKey;
    }

    public static void setAb(byte[] ab) {
        EncryptionUtil.ab = ab;
    }

    public static void defaultAdd(){
        EncryptionUtil.ab = "ConnectEncrypted".getBytes();
    }
}