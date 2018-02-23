package connect.activity.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import connect.activity.base.BaseApplication;
import connect.ui.activity.R;
import connect.utils.glide.GlideUtil;

/**
 * Created by gtq on 2016/11/28.
 */
public class PickHoriScrollAdapter {

    private LayoutInflater inflate;
    private List<String> paths = new ArrayList<>();

    public PickHoriScrollAdapter() {
        Context context = BaseApplication.getInstance().getBaseContext();
        inflate = LayoutInflater.from(context);
    }

    public void setData(List<String> paths) {
        this.paths = paths;
    }

    public int getCount(){
        return paths.size();
    }

    public String getItemObj(int position) {
        return paths.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        PickHolder holder = null;
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.item_pickimg, parent, false);
            holder = new PickHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PickHolder) convertView.getTag();
        }

        String path = paths.get(position);
        GlideUtil.loadAvatarRound(holder.roundimg, path);
        return convertView;
    }

    static class PickHolder {
        ImageView roundimg;
        ImageView state;

        public PickHolder(View view){
            roundimg= (ImageView) view.findViewById(R.id.roundimg);
            state= (ImageView) view.findViewById(R.id.state);
        }
    }
}