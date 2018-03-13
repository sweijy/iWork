package connect.widget.popupsearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by PuJin on 2018/3/13.
 */
public class SearchPopupAdapter extends RecyclerView.Adapter<SearchPopupAdapter.SearchHolder> {

    public List<SearchPopBean> popBeanList = null;

    public void setData(List<SearchPopBean> list) {
        this.popBeanList = list;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_popup_search, parent, false);
        SearchHolder holder = new SearchHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, final int position) {
        final SearchPopBean searchPopBean = popBeanList.get(position);
        GlideUtil.loadAvatarRound(holder.cateImg, searchPopBean.getImg1());
        holder.cateTxt.setText(searchPopBean.getTxt());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.itemClick(position,searchPopBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = null == popBeanList ? 0 : popBeanList.size();
        return count;
    }

    class SearchHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView cateImg;
        TextView cateTxt;

        public SearchHolder(View itemView) {
            super(itemView);
            view = itemView;
            cateImg = (ImageView) itemView.findViewById(R.id.image1);
            cateTxt = (TextView) itemView.findViewById(R.id.txt1);
        }
    }

    private SearchPopupListener onItemClick;

    public void setOnItemClick(SearchPopupListener onItemClick) {
        this.onItemClick = onItemClick;
    }
}
