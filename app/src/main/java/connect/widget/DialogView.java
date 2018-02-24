package connect.widget;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import connect.activity.base.BaseApplication;
import connect.activity.chat.adapter.PickHoriScrollAdapter;
import connect.activity.chat.bean.MsgSend;
import connect.activity.chat.bean.RecExtBean;
import connect.activity.chat.view.PickHorScrollView;
import connect.ui.activity.R;
import connect.utils.system.SystemDataUtil;

/**
 * Recent pictures
 */

public class DialogView {

    public Dialog showPhotoPick(final Context context){
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_photopick, null);
        dialog.setContentView(view);

        final TextView library = (TextView)view.findViewById(R.id.photo_library);
        TextView cancel = (TextView)view.findViewById(R.id.cancel);
        final PickHorScrollView horScrollView= (PickHorScrollView) view.findViewById(R.id.pickscrollview);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);

        horScrollView.setItemClickListener(new PickHorScrollView.OnItemClickListener() {
            @Override
            public void itemOnClick(List<String> paths) {
                if (paths.size() == 0) {
                    library.setText(context.getResources().getString(R.string.Chat_Photo_libary));
                } else {
                    library.setText(String.format(context.getString(R.string.Chat_Send_Mulphoto), paths.size()));
                }
            }
        });

        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                List<String> strings = recentImages();
                if (strings.size() > 6) {
                    strings = strings.subList(0, 6);
                }
                return strings;
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
                if (strings.size() == 0) {
                    linearLayout.setVisibility(View.GONE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    PickHoriScrollAdapter scrollAdapter = new PickHoriScrollAdapter();
                    scrollAdapter.setData(strings);
                    horScrollView.setPickAdapter(scrollAdapter);
                }
            }
        }.execute();

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (horScrollView.getClickLists().size() == 0) {
                    RecExtBean.getInstance().sendEvent(RecExtBean.ExtType.OPEN_ALBUM);
                } else {
                    MsgSend.sendOuterMsg(MsgSend.MsgSendType.Photo,horScrollView.getClickLists());
                }
                dialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = SystemDataUtil.getScreenWidth();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.DialogAnim);
        mWindow.setAttributes(lp);
        dialog.show();

        return dialog;
    }

    private static final String[] IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.SIZE
    };

    public List<String> recentImages() {
        List<String> pathList = new ArrayList<>();
        Context context = BaseApplication.getInstance().getBaseContext();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGES,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(IMAGES[0]));

                String imagePath = cursor.getString(cursor.getColumnIndex(IMAGES[1]));
                File file = new File(imagePath);
                if (!file.exists() || !file.canRead() || file.length() < 5 * 1024) continue;

                String name = cursor.getString(cursor.getColumnIndex(IMAGES[2]));
                String title = cursor.getString(cursor.getColumnIndex(IMAGES[3]));
                int bucketId = cursor.getInt(cursor.getColumnIndex(IMAGES[4]));
                String bucketName = cursor.getString(cursor.getColumnIndex(IMAGES[5]));
                String mimeType = cursor.getString(cursor.getColumnIndex(IMAGES[6]));
                long addDate = cursor.getLong(cursor.getColumnIndex(IMAGES[7]));
                long modifyDate = cursor.getLong(cursor.getColumnIndex(IMAGES[8]));
                float latitude = cursor.getFloat(cursor.getColumnIndex(IMAGES[9]));
                float longitude = cursor.getFloat(cursor.getColumnIndex(IMAGES[10]));
                long size = cursor.getLong(cursor.getColumnIndex(IMAGES[11]));

                pathList.add(imagePath);
            }
            cursor.close();
        }
        return pathList;
    }
}
