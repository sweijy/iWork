package connect.activity.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by PuJin on 2018/3/12.
 */
public class LocalSearchRecentsAdapter extends RecyclerView.Adapter<LocalSearchRecentsAdapter.RecentHolder> {

    private List<ConversionEntity> contactEntities = new ArrayList<>();

    public void setData(List<ConversionEntity> entities) {
        this.contactEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactEntities.size();
    }

    @Override
    public RecentHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        View view = inflater.inflate(R.layout.item_localsearch_rencents, arg0, false);
        RecentHolder holder = new RecentHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecentHolder holder, final int position) {
        final ConversionEntity conversionEntity = contactEntities.get(position);
        String name = TextUtils.isEmpty(conversionEntity.getName()) ? "" : conversionEntity.getName();

        holder.nameTxt.setText(name);
        GlideUtil.loadAvatarRound(holder.imageView, conversionEntity.getAvatar());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentsListener.itemClick(conversionEntity);
            }
        });
    }

    class RecentHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private TextView nameTxt;

        public RecentHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.avatar_rimg);
            nameTxt = (TextView) itemView.findViewById(R.id.text_view);
        }
    }

    private RecentsListener recentsListener;

    public void setRecentsListener(RecentsListener recentsListener) {
        this.recentsListener = recentsListener;
    }

    public interface RecentsListener {
        void itemClick(ConversionEntity conversionEntity);
    }
}