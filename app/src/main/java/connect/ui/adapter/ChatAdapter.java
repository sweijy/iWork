package connect.ui.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import connect.db.green.DaoHelper.MessageHelper;
import connect.db.green.bean.MessageEntity;
import connect.im.bean.MsgType;
import connect.ui.activity.R;
import connect.ui.activity.chat.bean.ItemViewType;
import connect.ui.activity.chat.bean.MsgDefinBean;
import connect.ui.activity.chat.bean.MsgDirect;
import connect.ui.activity.chat.bean.MsgEntity;
import connect.ui.activity.chat.inter.BaseListener;
import connect.ui.activity.chat.model.ChatMsgUtil;
import connect.ui.activity.chat.view.holder.MsgBaseHolder;
import connect.ui.activity.chat.view.row.MsgBaseRow;
import connect.utils.FileUtil;
import connect.utils.TimeUtil;

/**
 *
 * Created by gtq on 2016/11/23.
 */
public class ChatAdapter extends BaseChatAdapter {

    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    protected List<MsgEntity> msgEntities = new ArrayList<>();

    /** mapping msgid，MsgEntity ,quickly query msgid and Message Entities */
    private Map<String, MsgEntity> msgEntityMap = new HashMap<>();

    public ChatAdapter(Activity activity, RecyclerView recycler, LinearLayoutManager manager) {
        this.inflater = LayoutInflater.from(activity);
        this.recyclerView = recycler;
        this.layoutManager = manager;
    }

    public List<MsgEntity> getMsgEntities() {
        return msgEntities;
    }

    @Override
    public int getItemCount() {
        return msgEntities.size();
    }

    /**
     * direct -1:From 1:To
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        MsgDefinBean definBean = msgEntities.get(position).getMsgDefinBean();
        MsgDirect dirct = ChatMsgUtil.parseMsgDirect(definBean);
        return definBean.getType() * dirct.dirct;
    }

    @Override
    public MsgBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewType itemType = ItemViewType.toItemViewType(viewType);
        MsgBaseRow baseRow = null;

        if (itemType == null) {
            itemType = ItemViewType.NOTICE_FROM;
            baseRow = MsgType.NOTICE.msgBaseRow;
        } else {
            baseRow = itemType.msgType.msgBaseRow;
        }
        return baseRow.buildRowView(inflater, MsgDirect.toDirect(itemType.direct));
    }

    @Override
    public void onBindViewHolder(MsgBaseHolder holder, int position) {
        long curtime = msgEntities.get(position).getMsgDefinBean().getSendtime();

        long nexttime = 0;
        if (position == msgEntities.size() - 1) {//Slide to the last
            if (position == 0) {//just only one message
                nexttime = curtime - 4 * 60 * 1000;
            } else {//
                nexttime = msgEntities.get(position - 1).getMsgDefinBean().getSendtime();
            }
        } else {
            nexttime = msgEntities.get(position + 1).getMsgDefinBean().getSendtime();
        }

        holder.buildMsgTime(curtime, nexttime);
        holder.buildRowData(holder, msgEntities.get(position));
    }

    public void insertItem(MsgEntity t) {
        int posi = msgEntities.size();
        msgEntities.add(posi, t);
        notifyItemInserted(posi);

        msgEntityMap.put(t.getMsgid(), t);
    }

    public void insertItems(List<MsgEntity> entities) {
        msgEntities.addAll(0, entities);
        notifyDataSetChanged();

        for (MsgEntity entity : entities) {
            msgEntityMap.put(entity.getMsgid(), entity);
        }
    }

    public void removeItem(MsgEntity t) {
        int posi = msgEntities.lastIndexOf(t);
        if (posi >= 0) {
            msgEntities.remove(posi);
            notifyItemRemoved(posi);

            msgEntityMap.remove(t.getMsgid());
        }
    }

    public void updateItemSendState(String msgid, int state) {
        MsgEntity msgEntity = msgEntityMap.get(msgid);
        if (msgEntity != null) {
            msgEntity.setSendstate(state);
        }
    }

    public void showImgMsgs(final BaseListener listener) {
        new AsyncTask<List<MsgEntity>,Void,ArrayList<String>>(){
            @Override
            protected ArrayList<String> doInBackground(List<MsgEntity>... params) {
                ArrayList<String> imgList = new ArrayList<>();
                for (int i = 0; i < msgEntities.size(); i++) {
                    MsgEntity index = msgEntities.get(i);
                    MsgDefinBean definBean = index.getMsgDefinBean();
                    if (definBean.getType() == MsgType.Photo.type) {
                        String thumb = definBean.getContent();
                        String path = FileUtil.islocalFile(thumb) ? thumb : FileUtil.newContactFileName(index.getPubkey(), definBean.getMessage_id(), FileUtil.FileType.IMG);
                        imgList.add(path);
                    }
                }
                return imgList;
            }

            @Override
            protected void onPostExecute(ArrayList<String> strings) {
                super.onPostExecute(strings);
                listener.Success(strings);
            }
        }.execute(msgEntities);
    }

    public void unReadVoice(final String msgid) {
        new AsyncTask<List<MsgEntity>, Void, Integer>() {
            @Override
            protected Integer doInBackground(List<MsgEntity>... params) {
                int holdPosi = -1;
                MsgEntity msgEntity = msgEntityMap.get(msgid);
                if (msgEntity != null) {
                    List<MsgEntity> msgEntities = params[0];
                    int readPosi = msgEntities.indexOf(msgEntity);
                    for (int i = readPosi; i < msgEntities.size(); i++) {
                        msgEntity = msgEntities.get(i);//Ergodic voice list
                        MsgDefinBean definBean = msgEntity.getMsgDefinBean();
                        if (definBean.getType() == MsgType.Voice.type && msgEntity.getReadstate() == 0) {
                            holdPosi = i;
                        }
                    }
                }
                return holdPosi;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if (integer != -1) {
                    MsgBaseHolder holder = viewHoldByPosition(integer);
                    if (holder != null) {
                        holder.itemView.findViewById(R.id.voicemsg).performClick();
                    }
                }
            }
        }.execute(msgEntities);
    }

    /**
     * The message has been read
     * @param msgid
     */
    public void hasReadBurnMsg(String msgid) {
        MsgEntity msgEntity = msgEntityMap.get(msgid);
        long burntime = TimeUtil.getCurrentTimeInLong();
        if (msgEntity != null) {
            msgEntity.setBurnstarttime(burntime);
        }

        //Modify read time
        MessageEntity messageEntity = MessageHelper.getInstance().loadMsgByMsgid(msgid);
        if (messageEntity != null) {
            messageEntity.setSnap_time(burntime);
            MessageHelper.getInstance().updateMsg(messageEntity);
        }
    }

    public MsgBaseHolder viewHoldByPosition(int position) {
        int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
        MsgBaseHolder viewHolder = null;
        if (position - firstItemPosition >= 0) {
            View view = recyclerView.getChildAt(position - firstItemPosition);
            if (null != recyclerView.getChildViewHolder(view)) {
                viewHolder = (MsgBaseHolder) recyclerView.getChildViewHolder(view);
            }
        }
        return viewHolder;
    }

    public void clearHistory() {
        this.msgEntities.clear();
        notifyDataSetChanged();
    }
}
