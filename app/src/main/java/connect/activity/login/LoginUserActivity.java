package connect.activity.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.home.bean.HomeAction;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.dialog.DialogUtil;
import connect.utils.okhttp.HttpRequest;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

/**
 * Login
 */
@Route(path = "/iwork/login/LoginUserActivity")
public class LoginUserActivity extends BaseActivity {

    @Bind(R.id.name_et)
    EditText nameEt;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.next_btn)
    TextView nextBtn;
    @Bind(R.id.text_forget_password)
    TextView textForgetPassword;
    @Bind(R.id.image_loading)
    View imageLoading;
    @Bind(R.id.relative_login)
    RelativeLayout relativeLogin;
    @Bind(R.id.name_clear_image)
    ImageView nameClearImage;
    @Bind(R.id.password_clear_image)
    ImageView passwordClearImage;

    @Autowired
    String value;
    @Bind(R.id.login_edit_linear)
    LinearLayout loginEditLinear;

    private LoginUserActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_code);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        nextBtn.setEnabled(false);
        nameEt.addTextChangedListener(textWatcher);
        passwordEt.addTextChangedListener(textWatcher);
        relativeLogin.setEnabled(false);

        //// TODO: 2018/3/13 测试
//        nameEt.setText("jinlong.pu");
//        passwordEt.setText("Bitmain.com1!");

        String deveiceName = getIntent().getStringExtra("VALUE");
        if (!TextUtils.isEmpty(deveiceName)) {
            popRomteLoginDialog(deveiceName);
        }
    }

    public void popRomteLoginDialog(String deveiceName) {
        String showContent = TextUtils.isEmpty(deveiceName) ?
                getString(R.string.Error_Device_Remote_Other_Login) :
                getString(R.string.Error_Device_Remote_Login, deveiceName);
        DialogUtil.showAlertTextView(mActivity, null, showContent, null, getString(R.string.Common_OK), true, false, new DialogUtil.OnItemClickListener() {
            @Override
            public void confirm(String value) {
                HomeAction.getInstance().sendEvent(HomeAction.HomeType.EXIT);
            }

            @Override
            public void cancel() {

            }
        });
    }

    @OnClick(R.id.name_clear_image)
    void clearName(View view) {
        nameEt.setText("");
    }

    @OnClick(R.id.password_clear_image)
    void clearPassword(View view) {
        passwordEt.setText("");
    }

    @OnClick(R.id.text_forget_password)
    void forgetPasswordClick() {
        DialogUtil.showAlertTextView(mActivity,
                mActivity.getString(R.string.Set_tip_title),
                mActivity.getString(R.string.Login_Connect_Change_Password),
                "", "", false, new DialogUtil.OnItemClickListener() {
                    @Override
                    public void confirm(String value) {}

                    @Override
                    public void cancel() {}
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (LoginUserActivity.this.getCurrentFocus() != null) {
                if (LoginUserActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(LoginUserActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @OnClick(R.id.relative_login)
    void nextBtn(View view) {
        imageLoading.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.loading_white);
        imageLoading.startAnimation(loadAnimation);

        SharedPreferenceUtil.getInstance().setUserLogin(0);

        final String userName = nameEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        Connect.LoginReq loginReq = Connect.LoginReq.newBuilder()
                .setUsername(userName)
                .setPassword(password).build();
        HttpRequest.getInstance().post(UriUtil.CONNECT_V3_LOGIN, loginReq, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    imageLoading.clearAnimation();
                    imageLoading.setVisibility(View.GONE);
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody().toByteArray());
                    Connect.UserLoginInfo userLoginInfo = Connect.UserLoginInfo.parseFrom(structData.getPlainData());

                    UserBean userBean = new UserBean(userLoginInfo.getName(), userName, userLoginInfo.getAvatar(), userLoginInfo.getUid(),
                            userLoginInfo.getOU(), userLoginInfo.getToken());
                    userBean.setEmp_no(userLoginInfo.getEmpNo());
                    userBean.setGender(userLoginInfo.getGender());
                    userBean.setMobile(userLoginInfo.getMobile());
                    userBean.setTips(userLoginInfo.getTips());
                    SharedPreferenceUtil.getInstance().putUser(userBean);

                    ARouter.getInstance().build("/iwork/HomeActivity")
                            .navigation(mActivity, new NavCallback() {
                                @Override
                                public void onArrival(Postcard postcard) {
                                    mActivity.finish();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                imageLoading.clearAnimation();
                imageLoading.setVisibility(View.GONE);
                ToastUtil.getInstance().showToast(R.string.Login_User_name_or_password_error);
            }

            @Override
            public void onError() {
                super.onError();
                imageLoading.clearAnimation();
                imageLoading.setVisibility(View.GONE);
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String name = nameEt.getText().toString();
            String password = passwordEt.getText().toString();
            if (TextUtils.isEmpty(name)) {
                nameClearImage.setVisibility(View.GONE);
            } else {
                nameClearImage.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(password)) {
                passwordClearImage.setVisibility(View.GONE);
            } else {
                passwordClearImage.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                relativeLogin.setEnabled(false);
            } else {
                relativeLogin.setEnabled(true);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
