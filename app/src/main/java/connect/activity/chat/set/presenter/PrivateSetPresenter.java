package connect.activity.chat.set.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.activity.chat.set.contract.PrivateSetContract;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.ConversionSettingHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.database.green.bean.ConversionSettingEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

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
    }
}
