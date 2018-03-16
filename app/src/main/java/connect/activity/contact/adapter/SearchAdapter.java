package connect.activity.contact.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import connect.activity.chat.fragment.bean.SearchBean;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;
import connect.widget.DepartmentAvatar;

/**
 * Created by Administrator on 2018/1/31 0031.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Activity activity;
    private ArrayList<SearchBean> mDataList = new ArrayList<>();
    private SearchAdapter.OnItemChildClickListener childListener;

    public SearchAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_chat_search, parent, false);
        SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, final int position) {
        final SearchBean searchBean = mDataList.get(position);
        if(position == 0){
            holder.topLine.setVisibility(View.GONE);
            holder.titleText.setVisibility(View.VISIBLE);
            setTitleText(holder.titleText, searchBean.getStyle());
        }else if(mDataList.get(position - 1).getStyle() != searchBean.getStyle()){
            holder.topLine.setVisibility(View.VISIBLE);
            holder.titleText.setVisibility(View.VISIBLE);
            setTitleText(holder.titleText, searchBean.getStyle());
        }else{
            holder.topLine.setVisibility(View.VISIBLE);
            holder.titleText.setVisibility(View.GONE);
        }

        if(searchBean.getStyle() == 1 && searchBean.getStatus() == 2){
            holder.contentLinear.setVisibility(View.GONE);
            holder.moreContentText.setVisibility(View.VISIBLE);
            holder.moreContentText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childListener.itemClick(position, searchBean);
                }
            });
        }else{
            holder.contentLinear.setVisibility(View.VISIBLE);
            holder.moreContentText.setVisibility(View.GONE);

            if(searchBean.getStyle() == 1){
                holder.countTv.setText("");
                holder.hintTv.setVisibility(View.VISIBLE);
                holder.hintTv.setText(searchBean.getHinit());
            }else if(searchBean.getStyle() == 2 && searchBean.getStatus() == 1){
                holder.countTv.setText(" (" + searchBean.getCountMember() + ")");
                holder.hintTv.setVisibility(View.GONE);
            }else if(searchBean.getStyle() == 2 && searchBean.getStatus() == 2){
                holder.countTv.setText(" (" + searchBean.getCountMember() + ")");
                holder.hintTv.setVisibility(View.VISIBLE);
                String hint = activity.getResources().getString(R.string.Link_Such_as_in_the_group_of, searchBean.getHinit());
                holder.hintTv.setText(getColorString(searchBean.getHinit(), hint));
            }

            GlideUtil.loadAvatarRound(holder.avatarImage, searchBean.getAvatar());
            holder.nicknameTv.setText(getColorString(searchBean.getSearchStr(), searchBean.getName()));
            holder.contentLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    childListener.itemClick(position, searchBean);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final View topLine;
        private final LinearLayout contentLinear;
        private final ImageView avatarImage;
        private final TextView nicknameTv;
        private final TextView hintTv;
        private final TextView countTv;
        private final TextView moreContentText;

        ViewHolder(View itemView) {
            super(itemView);
            topLine = itemView.findViewById(R.id.top_line);
            titleText = (TextView) itemView.findViewById(R.id.title_text);
            contentLinear = (LinearLayout) itemView.findViewById(R.id.content_linear);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar_image);
            nicknameTv = (TextView) itemView.findViewById(R.id.nickname_tv);
            hintTv = (TextView) itemView.findViewById(R.id.hint_tv);
            countTv = (TextView) itemView.findViewById(R.id.count_tv);
            moreContentText = (TextView) itemView.findViewById(R.id.more_content_text);
        }
    }

    private void setTitleText(TextView text, int style){
        if(style == 1){
            text.setText(R.string.Link_The_contact);
        } else if(style == 2){
            text.setText(R.string.Link_My_group);
        }
    }



    private SpannableString getColorString(String value, String hint){
        SpannableString spanString = new SpannableString(hint);
        int start = hint.indexOf(value);
        if(hint.contains(value)){
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#6B91EA")), start,
                    start + value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanString;
    }

    public void setDataNotify(ArrayList<SearchBean> list){
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemChildListence(OnItemChildClickListener childListener){
        this.childListener = childListener;
    }

    public interface OnItemChildClickListener {
        void itemClick(int position, SearchBean searchBean);
    }
}
