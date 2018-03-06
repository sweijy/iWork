package connect.activity.workbench;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.BitmapUtil;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.permission.PermissionUtil;
import connect.widget.TopToolBar;
import connect.widget.zxing.utils.CreateScan;
import protos.Connect;

public class ShareVisitorActivity extends BaseActivity {

    @Bind(R.id.toolbar_top)
    TopToolBar toolbarTop;
    @Bind(R.id.scan_image)
    ImageView scanImage;
    @Bind(R.id.share_texts)
    TextView shareTexts;
    @Bind(R.id.hint_text)
    TextView hintText;

    private ShareVisitorActivity mActivity;
    private UserBean userBean;
    private Connect.Staff staff1 = null;
    private SpannableString spanString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_share);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void initView() {
        mActivity = this;
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Work_Visitors_share);

        userBean = SharedPreferenceUtil.getInstance().getUser();

        String hint = userBean.getName() + " 邀请你访问公司，通过小程序录入基本信息和肖像，以便通过公司门禁AI人像识别设备";
        spanString = new SpannableString(hint);
        ForegroundColorSpan span = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.color_007aff));
        spanString.setSpan(span, 0, userBean.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        hintText.setText(spanString);

        requestToken();
    }

    @OnClick(R.id.left_rela)
    void goBack(View view) {
        ActivityUtil.goBack(mActivity);
    }

    @OnClick(R.id.share_texts)
    void share(View view) {
        if(staff1 != null && !TextUtils.isEmpty(staff1.getToken())){
            PermissionUtil.getInstance().requestPermission(mActivity,
                    new String[]{PermissionUtil.PERMISSION_STORAGE},
                    permissionCallBack);
        }
    }

    private void requestToken(){
        Connect.Staff staff = Connect.Staff.newBuilder().build();
        OkHttpUtil.getInstance().postEncrySelf(UriUtil.CONNECT_V3_PROXY_TOKEN, staff, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    staff1 = Connect.Staff.parseFrom(structData.getPlainData());

                    CreateScan createScan = new CreateScan();
                    Bitmap bitmap = createScan.generateQRCode("https://wx-kq.bitmain.com/guest/info?token=" + staff1.getToken());
                    scanImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                ToastUtil.getInstance().showToast(response.getMessage());
            }
        });
    }

    private PermissionUtil.ResultCallBack permissionCallBack = new PermissionUtil.ResultCallBack(){
        @Override
        public void granted(String[] permissions) {
            CreateScan createScan = new CreateScan();
            Bitmap bitmap = createScan.generateQRCode("https://wx-kq.bitmain.com/guest/info?token=" + staff1.getToken());
            shareMsg(getResources().getString(R.string.Work_Visitors_share), "", "", drawShareScan(bitmap));
        }

        @Override
        public void deny(String[] permissions) {
        }
    };

    /**
     * View转换成Bitmap
     * @param valueBitmap
     * @return
     */
    private File drawShareScan(Bitmap valueBitmap){
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_visitor_share_scan,null);
        ImageView scanImage = (ImageView)view.findViewById(R.id.scan_image);
        scanImage.setImageBitmap(valueBitmap);
        TextView visitorText = (TextView) view.findViewById(R.id.visitor_text);
        visitorText.setText(spanString);

        Bitmap bitmap = BitmapUtil.getInstance().convertViewToBitmap(view);

        File file = BitmapUtil.getInstance().saveBitmap(bitmap);
        return file;
    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText, File file) {
        try {
            //通知图库更新
            //String filepath = file.getAbsolutePath();
            //String imageUri = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), filepath, msgTitle, msgText);
            Intent intent = new Intent(Intent.ACTION_SEND);
            if (file == null) {
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
                intent.putExtra(Intent.EXTRA_TEXT, msgText);
            } else {
                if (file != null && file.exists() && file.isFile()) {
                    intent.setType("image/jpg");
                    Uri u = Uri.fromFile(file);
                    intent.putExtra(Intent.EXTRA_STREAM, u);
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, activityTitle));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
