package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.activity.base.BaseListener;
import connect.activity.chat.set.contract.PrivateSetContract;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.ui.activity.R;
import connect.utils.UriUtil;
import connect.utils.glide.GlideUtil;
import connect.utils.okhttp.OkHttpUtil;
import connect.utils.okhttp.ResultCall;
import protos.Connect;

/**
 * Created by Administrator on 2017/8/7.
 */
public class PrivateSetPresenter implements PrivateSetContract.Presenter {

    private PrivateSetContract.BView view;

    private String uid;
    private Activity activity;

    public PrivateSetPresenter(PrivateSetContract.BView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        uid = view.getUid();
        activity = view.getActivity();

        ContactEntity friendEntity = new ContactEntity();
        friendEntity.setAvatar(view.getAvatar());
        friendEntity.setUid(uid);
        friendEntity.setName(view.getName());
        List<ContactEntity> entities = new ArrayList<>();
        entities.add(friendEntity);
        entities.add(new ContactEntity());
        for (ContactEntity entity : entities) {
            View headerview = LayoutInflater.from(activity).inflate(R.layout.linear_contact, null);
            ImageView headimg = (ImageView) headerview.findViewById(R.id.roundimg);
            TextView name = (TextView) headerview.findViewById(R.id.name);
            if (TextUtils.isEmpty(entity.getName())) {
                name.setVisibility(View.GONE);
            } else {
                name.setVisibility(View.VISIBLE);
                String nametxt = TextUtils.isEmpty(entity.getName()) ? "" : entity.getName();
                name.setText(nametxt);
            }

            if (TextUtils.isEmpty(entity.getAvatar()) && TextUtils.isEmpty(entity.getUid())) {
                GlideUtil.loadAvatarRound(headimg, R.mipmap.message_add_friends2x);
            } else {
                GlideUtil.loadAvatarRound(headimg, entity.getAvatar());
            }
            headerview.setTag(entity.getUid());
            view.showContactInfo(headerview);
        }

        pullSettingInfo();
    }

    @Override
    public void pullSettingInfo() {
        Connect.SessionSet sessionSet = Connect.SessionSet.newBuilder()
                .setUid(uid)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_SESSION_INFO, sessionSet, new ResultCall<Connect.HttpNotSignResponse>() {
            @Override
            public void onResponse(Connect.HttpNotSignResponse response) {
                try {
                    Connect.StructData structData = Connect.StructData.parseFrom(response.getBody());
                    Connect.SessionInfo sessionInfo = Connect.SessionInfo.parseFrom(structData.getPlainData());

                    boolean top = sessionInfo.getTop();
                    boolean mute = sessionInfo.getMute();
                    ConversionHelper.getInstance().updateRoomEntityTop(uid, top);
                    ConversionSettingHelper.getInstance().updateDisturb(uid, mute ? 1 : 0);
                    checkMute();
                    checkTop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Connect.HttpNotSignResponse response) {
                checkMute();
                checkTop();
            }
        });
    }

    public void checkTop() {
        boolean istop = false;
        ConversionEntity roomEntity = ConversionHelper.getInstance().loadRoomEnitity(uid);
        if (roomEntity == null) {
            roomEntity = new ConversionEntity();
            roomEntity.setIdentifier(uid);
            roomEntity.setTop(0);
        }
        if (Integer.valueOf(1).equals(roomEntity.getTop())) {
            istop = true;
        }
        view.switchTop(activity.getResources().getString(R.string.Chat_Sticky_on_Top_chat), istop);
    }

    public void checkMute() {
        boolean isDisturb = false;
        ConversionSettingEntity chatSetEntity = ConversionSettingHelper.getInstance().loadSetEntity(uid);
        if (chatSetEntity == null) {
            chatSetEntity = new ConversionSettingEntity();
            chatSetEntity.setIdentifier(uid);
            chatSetEntity.setDisturb(0);
        }
        if (Integer.valueOf(1).equals(chatSetEntity.getDisturb())) {
            isDisturb = true;
        }
        view.switchDisturb(activity.getResources().getString(R.string.Chat_Mute_Notification), isDisturb);
    }

    @Override
    public void switchTop(boolean checkon, final BaseListener<Boolean> listener) {
        Connect.SessionSet sessionSet = Connect.SessionSet.newBuilder()
                .setUid(uid)
                .setVal(checkon)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_TOP, sessionSet, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                listener.Success(true);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                listener.fail(true);
            }
        });
    }

    @Override
    public void switchDisturb(boolean checkon, final BaseListener<Boolean> listener) {
        Connect.SessionSet sessionSet = Connect.SessionSet.newBuilder()
                .setUid(uid)
                .setVal(checkon)
                .build();

        OkHttpUtil.getInstance().postEncrySelf(UriUtil.BM_USERS_V1_MUTE, sessionSet, new ResultCall<Connect.HttpResponse>() {
            @Override
            public void onResponse(Connect.HttpResponse response) {
                listener.Success(true);
            }

            @Override
            public void onError(Connect.HttpResponse response) {
                listener.fail(true);
            }
        });
    }
}
