package connect.widget;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
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
    private EditText editText;

    private int editWidth = 0;
    private int avatarWidth = 0;
    private int avatarMargin = 0;
    private int txtSize = 0;
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

        editWidth = SystemUtil.dipToPx(100);
        avatarWidth = SystemUtil.dipToPx(32);
        avatarMargin = SystemUtil.dipToPx(12);
        txtSize = SystemUtil.spToPx(6);
        showAvatars();
    }

    public void showAvatars() {
        linearLayout.removeAllViews();
        if (avatarBeanList.isEmpty()) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(avatarWidth, avatarWidth);
            layoutParams.leftMargin = avatarMargin;
            imageView.setLayoutParams(layoutParams);
            GlideUtil.loadAvatarRound(imageView, R.mipmap.department_search);
            linearLayout.addView(imageView);
        }

        for (AvatarBean bean : avatarBeanList) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(avatarWidth, avatarWidth);
            layoutParams.leftMargin = avatarMargin;
            imageView.setLayoutParams(layoutParams);
            GlideUtil.loadAvatarRound(imageView, bean.getAvatar());
            imageView.setTag(bean.getUid());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tagUid = (String) view.getTag();
                    clickListener.removeUid(tagUid);
                    removeAvatar(tagUid);
                }
            });
            linearLayout.addView(imageView);
        }

        if (editText == null) {
            editText = new EditText(getContext());
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(editWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            editParams.leftMargin = avatarMargin;
            editParams.topMargin = SystemUtil.dipToPx(2);
            editText.setLayoutParams(editParams);
            editText.setTextSize(txtSize);
            editText.setBackgroundColor(getResources().getColor(R.color.color_FFFFFF));
            editText.setTextColor(getResources().getColor(R.color.color_474747));
            editText.setHintTextColor(getResources().getColor(R.color.color_AEAEAE));
            editText.setHint(getResources().getText(R.string.Work_Search));
            editText.setMaxLines(1);
            editText.setGravity(Gravity.BOTTOM);
        }
        linearLayout.addView(editText);
    }

    public void addAvatar(String avatar, String uid) {
        int position = -1;
        for (int i = 0; i < avatarBeanList.size(); i++) {
            AvatarBean avatarBean = avatarBeanList.get(i);
            if (avatarBean.getUid().equals(uid)) {
                position = i;
                break;
            }
        }

        if (position == -1) {
            AvatarBean avatarBean = new AvatarBean(avatar, uid);
            avatarBeanList.add(avatarBean);
            showAvatars();
        }
    }

    public void removeAvatar(String uid) {
        int position = -1;
        for (int i = 0; i < avatarBeanList.size(); i++) {
            AvatarBean avatarBean = avatarBeanList.get(i);
            if (avatarBean.getUid().equals(uid)) {
                position = i;
                break;
            }
        }

        if (position >= 0) {
            avatarBeanList.remove(position);
            showAvatars();
        }
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

    public void addTextWatch(TextWatcher watcher) {
        if (editText != null) {
            editText.addTextChangedListener(watcher);
        }
    }

    public void hideKeyboard(){
        SystemUtil.hideKeyBoard(getContext(),editText);
    }

    private AvatarListener clickListener;

    public interface AvatarListener {

        void removeUid(String uid);
    }

    public void setListener(AvatarListener clickListener) {
        this.clickListener = clickListener;
    }
}
