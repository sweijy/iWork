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

import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by PuJin on 2018/3/12.
 */

public class LocalSearchContactsAdapter extends RecyclerView.Adapter<LocalSearchContactsAdapter.ContactsHolder> {

    private List<ContactEntity> contactEntities = new ArrayList<>();

    public void setData(List<ContactEntity> entities) {
        this.contactEntities = entities;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contactEntities.size();
    }

    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        View view = inflater.inflate(R.layout.item_localsearch_rencents, arg0, false);
        ContactsHolder holder = new ContactsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ContactsHolder holder, final int position) {
        final ContactEntity contactEntity = contactEntities.get(position);
        String name = TextUtils.isEmpty(contactEntity.getName()) ? "" : contactEntity.getName();

        holder.nameTxt.setText(name);
        GlideUtil.loadAvatarRound(holder.imageView, contactEntity.getAvatar());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsListener.itemClick(contactEntity);
            }
        });
    }

    class ContactsHolder extends RecyclerView.ViewHolder {

        private View view;
        private View selectView;
        private ImageView imageView;
        private TextView nameTxt;

        public ContactsHolder(View itemView) {
            super(itemView);
            view = itemView;
            selectView = itemView.findViewById(R.id.select);
            imageView = (ImageView) itemView.findViewById(R.id.avatar_rimg);
            nameTxt = (TextView) itemView.findViewById(R.id.text_view);
        }
    }

    private ContactsListener contactsListener;

    public void setContactsListener(ContactsListener contactsListener) {
        this.contactsListener = contactsListener;
    }

    public interface ContactsListener {
        void itemClick(ContactEntity conversionEntity);
    }
}