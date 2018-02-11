package connect.activity.workbench;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import connect.activity.base.BaseActivity;
import connect.activity.base.BaseApplication;
import connect.activity.login.bean.UserBean;
import connect.database.SharedPreferenceUtil;
import connect.ui.activity.R;
import connect.utils.ActivityUtil;
import connect.utils.FileUtil;
import connect.utils.ProgressUtil;
import connect.utils.ToastUtil;
import connect.utils.UriUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import connect.utils.permission.PermissionUtil;
import connect.utils.system.SystemDataUtil;
import connect.utils.system.SystemUtil;
import connect.widget.TopToolBar;
import connect.widget.zxing.utils.CreateScan;
import protos.Connect;

/**
 * Created by Administrator on 2018/2/11 0011.
 */

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
        toolbarTop.setBlackStyle();
        toolbarTop.setLeftImg(R.mipmap.back_white);
        toolbarTop.setTitle(null, R.string.Work_Visitors_share);

        userBean = SharedPreferenceUtil.getInstance().getUser();

        String hint = userBean.getName() + "邀请你访问公司，通过小程序录入基本信息和肖像，以便通过公司门禁AI人像识别设备\n二维码3小时内有效，并且只能使用一次";
        hintText.setText(hint);

        requestToken();
    }

    @OnClick(R.id.left_img)
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
                    Bitmap bitmap = createScan.generateQRCode("https://wx-kq.bitmain.com/guest/info?token=" + staff1.getToken(),
                            mActivity.getResources().getColor(R.color.color_f1f1f1));
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
            //File file = saveBitmap(bitmap);
            shareMsg(getResources().getString(R.string.Work_Visitors_share), "", "", drawShareScan(bitmap));
        }

        @Override
        public void deny(String[] permissions) {
        }
    };

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

    private File drawShareScan(Bitmap valueBitmap){
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_visitor_share_scan,null);
        ImageView scanImage = (ImageView)view.findViewById(R.id.scan_image);
        scanImage.setImageBitmap(valueBitmap);
        view.measure(View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, SystemDataUtil.getScreenWidth(), SystemDataUtil.getScreenHeight());
        view.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

        Bitmap bitmap = Bitmap.createBitmap(SystemDataUtil.getScreenWidth(), SystemDataUtil.getScreenHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        //以分辨率为720*1080准，计算宽高比值
        //解决不同屏幕字体大小不一样
        float ratioWidth = (float) mScreenWidth / 720;
        float ratioHeight = (float) mScreenHeight / 1080;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        int textSize = Math.round(30 * ratioMetrics);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(getResources().getColor(R.color.color_161A21));
        textPaint.setTextSize(textSize);
        //textPaint.setTypeface(Typeface.BOLD);
        textPaint.setAntiAlias(true);
        canvas.drawText("BITMAIN 访客系统", SystemUtil.dipToPx(90), SystemUtil.dipToPx(65), textPaint);

        String hint = userBean.getName() + "邀请你访问公司，通过小程序录入基本信息\n和肖像，以便通过公司门禁AI人像识别设备\n二维码3小时内有效，并且只能使用一次";
        StaticLayout myStaticLayout = new StaticLayout(hint, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(SystemUtil.dipToPx(20), SystemDataUtil.getScreenHeight() - SystemUtil.dipToPx(200));
        myStaticLayout.draw(canvas);

        File file = saveBitmap(bitmap);
        return file;
    }

    private File saveBitmap(Bitmap bm) {
        String path = FileUtil.newSdcardTempFile(FileUtil.FileType.IMG).getAbsolutePath();
        File f = new File(path);
        try {
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return f;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
