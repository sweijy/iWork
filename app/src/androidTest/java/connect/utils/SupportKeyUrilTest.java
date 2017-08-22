package connect.utils;

import android.text.TextUtils;

import com.google.protobuf.ByteString;

import org.junit.Test;
import java.security.SecureRandom;

import connect.utils.cryption.DecryptionUtil;
import connect.utils.cryption.EncoPinBean;
import connect.utils.cryption.EncryptionUtil;
import connect.utils.cryption.SupportKeyUril;
import protos.Connect;

import static org.junit.Assert.assertTrue;

/**
 * Created by Administrator on 2017/8/2 0002.
 */

public class SupportKeyUrilTest {

    @Test
    public void createrBinaryRandom() throws Exception {
        byte[] byteRand = SupportKeyUril.createrBinaryRandom();
        if(byteRand != null && (byteRand.length >= 16 & byteRand.length <= 32)){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

    @Test
    public void hmacSHA512() throws Exception {
        String hmac = SupportKeyUril.hmacSHA512("12345678910",SupportKeyUril.HmacSalt);
        assertTrue(!TextUtils.isEmpty(hmac));
    }

    @Test
    public void encoPinDefult() throws Exception {
        String value = "12345678910adb";
        EncoPinBean encoPinBean = SupportKeyUril.encoPinDefult(1,value,"1234");
        String deValue = SupportKeyUril.decodePinDefult(1,encoPinBean.getPayload(),"1234");
        if(value.equals(deValue)){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

    @Test
    public void createTalkKey() throws Exception {
        String priKey = "L12LREW9xUHDADSi37RckeML7wVX17FWLaRmtfWgdRVHs3SM8cEY";
        String address = "18ZWCA1Ujv6GL3NQe5GXZX4era5BugKyuZ";
        String pass = "123456";
        String takeStr = SupportKeyUril.createTalkKey(priKey, address, pass);
        String value = SupportKeyUril.decodeTalkKey(takeStr, pass);
        if(!TextUtils.isEmpty(value) && priKey.equals(value)){
            assertTrue(true);
        }else {
            assertTrue(false);
        }
    }

    @Test
    public void encodePri() throws Exception {
        String priKey = "L12LREW9xUHDADSi37RckeML7wVX17FWLaRmtfWgdRVHs3SM8cEY";
        String salt = StringUtil.bytesToHexString(SecureRandom.getSeed(64));
        String pass = "123456";
        String encryStr = SupportKeyUril.encodePri(priKey,salt,pass);
        String value = SupportKeyUril.decodePri(encryStr,salt,pass);
        if(value.equals(priKey)){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }

    @Test
    public void encodeAESGCMStructData() throws Exception {
        String value = "1234";
        String priKey = "L12LREW9xUHDADSi37RckeML7wVX17FWLaRmtfWgdRVHs3SM8cEY";
        Connect.GcmData gcmData = EncryptionUtil.encodeAESGCMStructData(SupportKeyUril.EcdhExts.EMPTY, priKey, ByteString.copyFrom(value.getBytes()));
        Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(SupportKeyUril.EcdhExts.EMPTY, priKey, gcmData);
        String valueDe = new String(structData.getPlainData().toByteArray());
        if(value.equals(valueDe)){
            assertTrue(true);
        }else{
            assertTrue(false);
        }
    }
    
}