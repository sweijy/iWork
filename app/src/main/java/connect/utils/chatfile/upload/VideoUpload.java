package connect.utils.chatfile.upload;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connect.ui.activity.R;
import connect.utils.FileUtil;
import connect.utils.chatfile.inter.BaseFileUp;
import connect.utils.chatfile.inter.FileUploadListener;
import connect.utils.log.LogManager;
import instant.bean.ChatMsgEntity;
import instant.bean.Session;
import instant.sender.model.BaseChat;
import protos.Connect;

/**
 * Created by gtq on 2016/12/5.
 */
public class VideoUpload extends BaseFileUp {

    private static String TAG = "_VideoUpload";
    private Compressor compressor;

    public VideoUpload(Context context, BaseChat baseChat, ChatMsgEntity entity, FileUploadListener listener) {
        super();
        this.context = context;
        this.context = context;
        this.baseChat = baseChat;
        this.msgExtEntity = entity;
        this.fileUpListener = listener;
    }

    @Override
    public void startUpload() {
        super.startUpload();
        compressor = new Compressor((Activity) context);
        compressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                Log.v(TAG, "load library succeed");
                fileCompress();
            }

            @Override
            public void onLoadFail(String reason) {
                Log.i(TAG, "load library fail:" + reason);
                fileNormal();
            }
        });
    }

    private String thumbCompressFile;
    private String sourceCompressFile;

    public void fileNormal() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Connect.VideoMessage videoMessage = Connect.VideoMessage.parseFrom(msgExtEntity.getContents());
                    thumbCompressFile = videoMessage.getCover();
                    sourceCompressFile = videoMessage.getUrl();

                    fileEncrypt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                fileUpload();
            }
        }.execute();
    }

    @Override
    public void fileCompress() {
        super.fileCompress();
        try {
            final Connect.VideoMessage videoMessage = Connect.VideoMessage.parseFrom(msgExtEntity.getContents());
            thumbCompressFile = videoMessage.getCover();
            sourceCompressFile = videoMessage.getUrl();

            String sourcepath = videoMessage.getUrl();
            File file = FileUtil.newContactFile(FileUtil.FileType.VIDEO);
            final String fileOutPath = file.getAbsolutePath();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("-y -i ");
            stringBuffer.append(sourcepath);
            stringBuffer.append(" -strict -2 -vcodec libx264 -preset ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 640x480 -aspect 16:9 ");
            stringBuffer.append(fileOutPath);

            compressor.execCommand(stringBuffer.toString(), new CompressListener() {
                @Override
                public void onExecSuccess(String message) {
                    Log.i(TAG, "success " + message);
                    sourceCompressFile = fileOutPath;

                    fileEncrypt();
                }

                @Override
                public void onExecFail(String reason) {
                    Log.i(TAG, "fail " + reason);
                    fileEncrypt();
                }

                @Override
                public void onExecProgress(String message) {
                    Log.i(TAG, "progress " + message);
                    LogManager.getLogger().d(TAG, context.getString(R.string.compress_progress, getProgress(message, videoMessage.getTimeLength())));
                }
            });
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private String getProgress(String source, int videolength) {
        //progress frame=   28 fps=0.0 q=24.0 size= 107kB time=00:00:00.91 bitrate= 956.4kbits/s
        Pattern p = Pattern.compile("00:\\d{2}:\\d{2}");
        Matcher m = p.matcher(source);
        if (m.find()) {
            //00:00:00
            String result = m.group(0);
            String temp[] = result.split(":");
            Double seconds = Double.parseDouble(temp[1]) * 60 + Double.parseDouble(temp[2]);
            Log.v(TAG, "current second = " + seconds);
            if (0 != videolength) {
                return seconds / videolength + "";
            }
            return "0";
        }
        return "";
    }

    @Override
    public void fileEncrypt() {
        super.fileEncrypt();

        byte[] thumbnailFileByte = encodeAESGCMStructData(thumbCompressFile);
        byte[] sourceFileByte = encodeAESGCMStructData(sourceCompressFile);
        ByteString thumbnailFileBytes = ByteString.copyFrom(thumbnailFileByte);
        ByteString sourceFileBytes = ByteString.copyFrom(sourceFileByte);

        Connect.RichMedia richMedia = Connect.RichMedia.newBuilder()
                .setThumbnail(thumbnailFileBytes)
                .setEntity(sourceFileBytes)
                .build();

        Connect.StructData structData = Connect.StructData.newBuilder()
                .setPlainData(richMedia.toByteString())
                .build();

        String uid = Session.getInstance().getConnectCookie().getUid();
        String token = Session.getInstance().getChatCookie().getToken();
        mediaFile = Connect.MediaFile.newBuilder()
                .setUid(uid)
                .setToken(token)
                .setBody(structData.toByteString())
                .build();
    }

    @Override
    public void fileUpload() {
        super.fileUpload();
        resultUpFile(mediaFile, new FileResult() {
            @Override
            public void resultUpUrl(Connect.FileData mediaFile) {
                String thumb = getThumbUrl(mediaFile.getUrl(), mediaFile.getToken());
                String url = getUrl(mediaFile.getUrl(), mediaFile.getToken());

                try {
                    Connect.VideoMessage videoMessage = Connect.VideoMessage.parseFrom(msgExtEntity.getContents());
                    videoMessage = videoMessage.toBuilder()
                            .setCover(thumb)
                            .setUrl(url)
                            .setFileKey(ByteString.copyFrom(getRandomNumber()))
                            .build();

                    msgExtEntity = (ChatMsgEntity) msgExtEntity.clone();
                    msgExtEntity.setContents(videoMessage.toByteArray());
                    uploadSuccess(msgExtEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
