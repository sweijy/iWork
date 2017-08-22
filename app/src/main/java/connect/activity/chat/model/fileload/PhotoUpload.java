package connect.activity.chat.model.fileload;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.google.protobuf.ByteString;
import java.io.File;
import connect.activity.chat.bean.MsgExtEntity;
import connect.activity.chat.inter.FileUpLoad;
import connect.activity.chat.model.content.BaseChat;
import connect.database.MemoryDataManager;
import connect.utils.BitmapUtil;
import connect.utils.FileUtil;
import connect.utils.cryption.EncryptionUtil;
import connect.utils.cryption.SupportKeyUril;
import protos.Connect;

/**
 * Created by gtq on 2016/12/5.
 */
public class PhotoUpload extends FileUpLoad {

    private String Tag = "PhotoUpload";
    private String firstPath;

    public PhotoUpload(Context context, BaseChat baseChat, MsgExtEntity entity, FileUpListener listener) {
        this.context = context;
        this.baseChat = baseChat;
        this.msgExtEntity = entity;
        this.fileUpListener = listener;
    }

    @Override
    public void fileHandle() {
        super.fileHandle();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Connect.PhotoMessage photoMessage = Connect.PhotoMessage.parseFrom(msgExtEntity.getContents());
                    String filePath = photoMessage.getThum();

                    File firstFile = BitmapUtil.getInstance().compress(filePath);
                    File secondFile = BitmapUtil.getInstance().compress(firstFile.getAbsolutePath());
                    firstPath = firstFile.getAbsolutePath();
                    String secondPath = secondFile.getAbsolutePath();

                    String priKey = MemoryDataManager.getInstance().getPriKey();
                    String pubkey = MemoryDataManager.getInstance().getPubKey();

                    Connect.GcmData gcmData = null;
                    Connect.RichMedia richMedia = null;
                    if (baseChat.chatType() == Connect.ChatType.CONNECT_SYSTEM_VALUE) {
                        richMedia = Connect.RichMedia.newBuilder().
                                setThumbnail(ByteString.copyFrom(FileUtil.filePathToByteArray(firstPath))).
                                setEntity(ByteString.copyFrom(FileUtil.filePathToByteArray(secondPath))).build();
                    } else {
                        Connect.GcmData firstGcmData = encodeAESGCMStructData(firstPath);
                        Connect.GcmData secondGcmData = encodeAESGCMStructData(secondPath);
                        richMedia = Connect.RichMedia.newBuilder().
                                setThumbnail(firstGcmData.toByteString()).
                                setEntity(secondGcmData.toByteString()).build();
                    }

                    gcmData = EncryptionUtil.encodeAESGCMStructData(SupportKeyUril.EcdhExts.SALT, priKey, richMedia.toByteString());
                    mediaFile = Connect.MediaFile.newBuilder().setPubKey(pubkey).setCipherData(gcmData).build();

//                    firstFile.delete();
//                    secondFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                localEncryptionSuccess(msgExtEntity);
                fileUp();
            }
        }.execute();
    }

    @Override
    public void fileUp() {
        resultUpFile(mediaFile, new FileResult() {

            @Override
            public void resultUpUrl(Connect.FileData mediaFile) {
                String thumb = getThumbUrl(mediaFile.getUrl(), mediaFile.getToken());
                String url = getUrl(mediaFile.getUrl(), mediaFile.getToken());

                try {
                    Connect.PhotoMessage photoMessage = Connect.PhotoMessage.parseFrom(msgExtEntity.getContents());
                    photoMessage = photoMessage.toBuilder().setThum(thumb)
                            .setUrl(url).build();

                    msgExtEntity = (MsgExtEntity) msgExtEntity.clone();
                    msgExtEntity.setContents(photoMessage.toByteArray());
                    uploadSuccess(msgExtEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}