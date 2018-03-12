package connect.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.utils.system.SystemUtil;

/**
 * 显示头像列表 带搜索框
 * Created by jinlongpu on 2018/3/11.
 */
public class AvatarSearchView extends RelativeLayout {

    private static String TAG = "_AvatarSearchView";
    private LinearLayout linearLayout;

    private List<AvatarBean> avatarBeanList = new ArrayList<>();

    public AvatarSearchView(Context context) {
        super(context);
        initView();
    }

    public AvatarSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AvatarSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_avatarsearch, this);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);

        if (avatarBeanList.isEmpty()) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SystemUtil.dipToPx(26), SystemUtil.dipToPx(26));
            layoutParams.leftMargin = SystemUtil.dipToPx(12);
            imageView.setLayoutParams(layoutParams);
            GlideUtil.loadAvatarRound(imageView, R.mipmap.department_search);
            linearLayout.addView(imageView);

            EditText editText = new EditText(getContext());
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(SystemUtil.dipToPx(200), SystemUtil.dipToPx(26));
            editParams.leftMargin = SystemUtil.dipToPx(12);
            editText.setLayoutParams(editParams);
            editText.setBackgroundColor(getResources().getColor(R.color.color_FFFFFF));
            editText.setTextColor(getResources().getColor(R.color.color_474747));
            editText.setHintTextColor(getResources().getColor(R.color.color_AEAEAE));
            editText.setHint(getResources().getText(R.string.Work_Search));
            linearLayout.addView(editText);
        }
    }

    public void addAvatar(String avatar, String uid) {
        if (avatarBeanList.isEmpty()) {
            linearLayout.removeViewAt(0);
        }

        AvatarBean avatarBean = new AvatarBean(avatar, uid);
        avatarBeanList.add(avatarBean);

        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(SystemUtil.dipToPx(26), SystemUtil.dipToPx(26));
        layoutParams.leftMargin = SystemUtil.dipToPx(12);
        imageView.setLayoutParams(layoutParams);
        GlideUtil.loadAvatarRound(imageView, avatar);
        imageView.setTag(uid);
        linearLayout.addView(imageView);
    }

    public void removeAvatar(String uid) {
        int position = 0;
        for (int i = 0; i < avatarBeanList.size(); i++) {
            AvatarBean avatarBean = avatarBeanList.get(i);
            if (avatarBean.getUid().equals(uid)) {
                position = i;
                break;
            }
        }
        linearLayout.removeViewAt(position);
    }

    private class AvatarBean {
        private String avatar;
        private String uid;

        AvatarBean(String avatar, String uid) {
            this.avatar = avatar;
            this.uid = uid;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getUid() {
            return uid;
        }
    }
}
