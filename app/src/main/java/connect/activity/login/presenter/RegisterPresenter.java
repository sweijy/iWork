package connect.activity.login.presenter;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;

import connect.activity.login.bean.UserBean;
import connect.activity.login.contract.RegisterContract;
import connect.ui.activity.R;
import connect.utils.BitmapUtil;
import connect.utils.FileUtil;
import connect.utils.ProgressUtil;
import connect.utils.ProtoBufUtil;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.HttpRequest;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View mView;
    //private String headPath = "https://short.connect.im/avatar/v1/b040e0a970bc6d80b675586c5a55f9e9109168ba.png";
    private String headPath = "http://192.168.40.4:18081/avatar/v1/2ks8as909jaey9mxwv87d4uprgdteuwxc2mkd0w8.jpg";

    public RegisterPresenter(RegisterContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void start() {}

    /**
     * Upload the picture.
     *
     * @param pathLocal avatar path
     */
    @Override
    public void requestUserHead(final String pathLocal) {
        ProgressUtil.getInstance().showProgress(mView.getActivity());
        File file = BitmapUtil.getInstance().compress(pathLocal);
        String path = file.getAbsolutePath();
        byte[] headByte = BitmapUtil.bmpToByteArray(BitmapFactory.decodeFile(path),100);
        FileUtil.deleteFile(path);
        HttpRequest.getInstance().post(UriUtil.AVATAR_V1_UP, headByte, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                ProgressUtil.getInstance().dismissProgress();
                // Delete the cropping images
                if (!TextUtils.isEmpty(pathLocal)) {
                    FileUtil.deleteFile(pathLocal);
                }
                // After successful upload display image
                try {
                    Connect.AvatarInfo userAvatar = Connect.AvatarInfo.parseFrom(response.getBody());
                    if (ProtoBufUtil.getInstance().checkProtoBuf(userAvatar)) {
                        headPath = userAvatar.getUrl();
                        mView.showAvatar(headPath);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                if (!TextUtils.isEmpty(pathLocal)) {
                    FileUtil.deleteFile(pathLocal);
                }
                ProgressUtil.getInstance().dismissProgress();
                ToastEUtil.makeText(mView.getActivity(),R.string.Login_Avatar_upload_failed,ToastEUtil.TOAST_STATUS_FAILE).show();
            }
        });
    }

    /**
     * Registered users.
     *
     * @param nicName
     * @param token Check the phone number of the token
     * @param userBeanOut
     */
    @Override
    public void registerUser(final String nicName, final String token, final UserBean userBeanOut) {
        /*message RegisterUser {
            string username = 1 [(validator.field) = {string_not_empty:true}];
            string avatar = 2 [(validator.field) = {string_not_empty:true}];
            string mobile = 3;
            string token = 4;
            string device_id = 5;
            string identity_key = 6;
            string signed_per_key = 7;
            repeated string one_time_pre_keys = 8;
        }*/

//        Connect.RegisterUser.Builder builder = Connect.RegisterUser.newBuilder()
//                .setToken(token)
//                .setAvatar(headPath)
//                .setUsername(nicName)
//                .setDeviceId(SystemDataUtil.getDeviceId())
//                .setIdentityKey("aaaaaaaaaaaaaaa")
//                .setSignedPerKey(userBeanOut.getUid());
//        for(int i = 0; i<100; i++){
//            builder.addOneTimePreKeys("aaaaaaaaaaaaa"+i);
//        }
//
//        Connect.IMRequest imRequest = OkHttpUtil.getInstance().getIMRequest(EncryptionUtil.ExtendedECDH.EMPTY, userBeanOut.getUid(),
//                userBeanOut.getUid(), builder.build().toByteString());
//        HttpRequest.getInstance().post(UriUtil.CONNECT_V2_SIGN_UP, imRequest, new ResultCall<Connect.HttpResponse>() {
//            @Override
//            public void onResponse(Connect.HttpResponse response) {
//                try {
//                    ProgressUtil.getInstance().dismissProgress();
//                    Connect.HttpNotSignResponse imResponse = Connect.HttpNotSignResponse.parseFrom(response.getBody().toByteArray());
//                    Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(EncryptionUtil.ExtendedECDH.EMPTY,
//                            userBeanOut.getUid(), imResponse.getCipherData());
//                    Connect.UserInfo userInfo = Connect.UserInfo.parseFrom(structData.getPlainData());
//
//                    /*UserBean userBean = new UserBean(userInfo.getUsername(), userInfo.getAvatar(),userBeanOut.getPhone(),
//                            userInfo.getConnectId(), userInfo.getUid(), false);
//                    SharedPreferenceUtil.getInstance().putUser(userBean);
//
//                    mView.launchHome();*/
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Connect.HttpResponse response) {
//                ProgressUtil.getInstance().dismissProgress();
//                if (response.getCode() == 2101) {
//                    Toast.makeText(mView.getActivity(), R.string.Login_User_avatar_is_illegal, Toast.LENGTH_LONG).show();
//                } else if(response.getCode() == 2102){
//                    Toast.makeText(mView.getActivity(), R.string.Login_username_already_exists, Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(mView.getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

}
