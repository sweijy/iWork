package connect.activity.workbench;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.login.bean.UserBean;
import connect.activity.workbench.bean.UpdateState;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.BitmapUtil;
import connect.utils.TimeUtil;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.widget.TopToolBar;
import protos.Connect;

@Route(path = "/iwork/workbench/WarehouseDetailActivity")
public class WarehouseDetailActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.type_tv)
    TextView typeTv;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.is_face_image)
    ImageView isFaceImage;
    @Bind(R.id.know_text)
    TextView knowText;
    @Bind(R.id.bottom_relative)
    RelativeLayout bottomRelative;

    private WarehouseDetailActivity mActivity;
    private long staffId;
    private Connect.StaffLog staffLog;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbench_warehouse_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Work_Warehouse_abnormal_records);
        userBean = SharedPreferenceUtil.getInstance().getUser();

        staffId = getIntent().getExtras().getLong("id");
        requestStaff();
    }

    private void initData(){
        timeTv.setText(getString(R.string.Work_Time, TimeUtil.getTime(staffLog.getDateTime()*1000, TimeUtil.DEFAULT_DATE_FORMAT)));
        typeTv.setText(getString(R.string.Work_Entering_the_warehouse,
                TextUtils.isEmpty(staffLog.getLocation()) ? staffLog.getDeviceId() : staffLog.getLocation()));
        try {
            isFaceImage.setImageBitmap(BitmapUtil.getInstance().base64ToBitmap(staffLog.getFace()));
        }catch (Exception e){
            e.printStackTrace();
        }
        if(staffLog.getStatus() == 1){
            bottomRelative.setVisibility(View.GONE);
        }else{
            bottomRelative.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.know_text)
    void confirmInfo(View view) {
        requestConfirm();
    }

    private void requestStaff(){
        Connect.UnRegisterMessage unRegisterMessage = Connect.UnRegisterMessage.newBuilder()
                .setId(staffId).build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.STORES_V1_IWORK_LOGS_DETAIL, unRegisterMessage, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    staffLog = Connect.StaffLog.parseFrom(structData.getPlainData());
                    initData();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {}
        });
    }

    private void requestConfirm(){
        Connect.UnRegisterCheck  unRegisterCheck = Connect.UnRegisterCheck .newBuilder()
                .setId(staffId)
                .setName(userBean.getName())
                .setUsername(userBean.getUserName())
                .setStatus(1).build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.STORES_V1_IWORK_LOG_COMFIRM, unRegisterCheck, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                ToastUtil.getInstance().showToast(R.string.Wallet_Confirmed);
                EventBus.getDefault().post(new UpdateState(UpdateState.StatusEnum.UPDATE_WAREHOUSE));
                ActivityUtil.goBack(mActivity);
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {}
        });
    }

}
