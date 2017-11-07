package connect.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.base.BaseApplication;
import connect.activity.home.HomeActivity;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.ToastEUtil;
import connect.utils.UriUtil;
import connect.utils.cryption.DecryptionUtil;
import connect.utils.cryption.EncryptionUtil;
import connect.utils.cryption.SupportKeyUril;
import connect.utils.okhttp.HttpRequest;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.wallet.jni.AllNativeMethod;
import connect.widget.TopToolBar;
import protos.Connect;

/**
 * password verify
 */

public class LoginPassVerifyActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.password_edit)
    EditText passwordEdit;
    @Bind(R.id.next_btn)
    Button nextBtn;

    private LoginPassVerifyActivity mActivity;

    public static void startActivity(Activity activity, String mobile, String token) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("mobile", mobile);
        bundle.putString("token", token);
        ActivityUtil.next(activity, LoginPassVerifyActivity.class, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password_verify);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Set_Enter_Login_Password);

        passwordEdit.addTextChangedListener(textWatcher);
    }

    @OnClick(R.id.left_img)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.next_btn)
    void confirmPassword(View view) {
        verifyPassword();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = passwordEdit.getText().toString();
            if (!TextUtils.isEmpty(password)) {
                nextBtn.setEnabled(true);
            } else {
                nextBtn.setEnabled(false);
            }
        }
    };

    private void verifyPassword(){
        final String priKey = SupportKeyUril.getNewPriKey();
        final String pubKey = AllNativeMethod.cdGetPubKeyFromPrivKey(priKey);
        Bundle bundle = getIntent().getExtras();
        final String mobile = bundle.getString("mobile");
        String token = bundle.getString("token");
        String password = passwordEdit.getText().toString();

        Connect.PasswordCheck passwordCheck = Connect.PasswordCheck.newBuilder()
                .setMobile(mobile)
                .setToken(token)
                .setCaPub(pubKey)
                .setPassword(password)
                .build();
        Connect.IMRequest imRequest = OkHttpUtil.getInstance().getIMRequest(EncryptionUtil.ExtendedECDH.EMPTY, priKey, pubKey, passwordCheck.toByteString());
        HttpRequest.getInstance().post(UriUtil.CONNECT_V2_SIGN_UP_PASSWORD, imRequest, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                try {
                    Connect.IMResponse imResponse = Connect.IMResponse.parseFrom(response.getBody().toByteArray());
                    Connect.StructData structData = DecryptionUtil.decodeAESGCMStructData(EncryptionUtil.ExtendedECDH.EMPTY,
                            priKey, imResponse.getCipherData());
                    Connect.UserInfo userInfo = Connect.UserInfo.parseFrom(structData.getPlainData());

                    UserBean userBean = new UserBean(userInfo.getUsername(), userInfo.getAvatar(), priKey, pubKey,
                            userInfo.getCaPub(),mobile, userInfo.getConnectId(), userInfo.getUid(), userInfo.getUpdateConnectId());
                    userBean.setOpenPassword(true);
                    SharedPreferenceUtil.getInstance().putUser(userBean);

                    List<Activity> list = BaseApplication.getInstance().getActivityList();
                    for (Activity activity : list) {
                        if (!activity.getClass().getName().equals(mActivity.getClass().getName())) {
                            activity.finish();
                        }
                    }
                    HomeActivity.startActivity(mActivity);
                    mActivity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                ToastEUtil.makeText(mActivity, response.getMessage(), ToastEUtil.TOAST_STATUS_FAILE);
            }
        });
    }

}
