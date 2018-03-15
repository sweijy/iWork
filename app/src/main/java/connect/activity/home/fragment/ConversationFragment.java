package connect.activity.home.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import connect.activity.base.BaseFragment;
import connect.activity.home.HomeActivity;
import connect.activity.home.adapter.ConversationAdapter;
import connect.activity.home.bean.ConversationAction;
import connect.activity.home.bean.RoomAttrBean;
import connect.activity.home.view.ConnectStateView;
import connect.activity.home.view.LineDecoration;
import connect.activity.home.view.ToolbarSearch;
import connect.activity.set.bean.SystemSetBean;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.ParamManager;
import connect.ui.activity.R;
import connect.utils.log.LogManager;
import connect.utils.system.SystemUtil;
import connect.widget.popupsearch.SearchPopBean;
import connect.widget.popupsearch.SearchPopWindow;
import connect.widget.popupsearch.SearchPopupListener;
import protos.Connect;

/**
 * Created by gtq on 2016/11/21.
 */
@Route(path = "/iwork/home/fragment/ConversationFragment")
public class ConversationFragment extends BaseFragment {

    @Bind(R.id.recycler_fragment_chat)
    RecyclerView recyclerFragmentChat;
    @Bind(R.id.connectstate)
    ConnectStateView connectStateView;
    @Bind(R.id.txt_count_at)
    TextView txtCountAt;
    @Bind(R.id.relative_at)
    RelativeLayout relativeAt;
    @Bind(R.id.txt_count_attention)
    TextView txtCountAttention;
    @Bind(R.id.relative_attention)
    RelativeLayout relativeAttention;
    @Bind(R.id.toolbar)
    ToolbarSearch toolbar;

    private String Tag = "_ConversationFragment";
    private Activity activity;
    private View view;

    private ConversationAdapter chatFragmentAdapter;
    private ConversationListener conversationListener = new ConversationListener();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogManager.getLogger().d(Tag, "onCreateView()");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
            ButterKnife.bind(this, view);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    public static ConversationFragment startFragment() {
        ConversationFragment chatListFragment = new ConversationFragment();
        return chatListFragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConversationAction action) {
        LogManager.getLogger().d(Tag, "onEventMainThread()");
        switch (action.getConverType()) {
            case LOAD_MESSAGE:
                loadRooms();
                break;
            case LOAD_UNREAD:
                countUnread();
                break;
        }
    }

    private boolean isThreadRun = false;

    /**
     * Query chat message list
     */
    protected void loadRooms() {
        if (!isThreadRun) {
            isThreadRun = true;
            new AsyncTask<Void, Void, List<RoomAttrBean>>() {

                @Override
                protected List<RoomAttrBean> doInBackground(Void... params) {
                    return ConversionHelper.getInstance().loadRoomEntites();
                }

                @Override
                protected void onPostExecute(List<RoomAttrBean> entities) {
                    super.onPostExecute(entities);
                    chatFragmentAdapter.setData(entities);
                    countUnread();
                    isThreadRun = false;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);//并行执行任务
        }
    }

    protected void countUnread() {
        // 消息未读
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                return ConversionHelper.getInstance().countUnReads();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                ((HomeActivity) activity).setFragmentDot(0, integer);
            }
        }.execute();

        //有消息提到你
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                return ConversionHelper.getInstance().countUnReadAt();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (relativeAt != null && txtCountAt != null) {
                    if (integer == 0) {
                        relativeAt.setVisibility(View.GONE);
                    } else {
                        relativeAt.setVisibility(View.VISIBLE);
                        txtCountAt.setText(getResources().getString(R.string.Chat_There_Are_News_Mentions_You, integer));
                    }
                }
            }
        }.execute();


        //有新的关注消息
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                return ConversionHelper.getInstance().countUnReadAttention();
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (relativeAttention != null) {
                    if (integer == 0) {
                        relativeAttention.setVisibility(View.GONE);
                    } else {
                        relativeAttention.setVisibility(View.VISIBLE);
                        txtCountAttention.setText(getResources().getString(R.string.Chat_There_Are_News_Attention_You, integer));
                    }
                }
            }
        }.execute();
    }

    /**
     * The view has been created
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogManager.getLogger().d(Tag, "onActivityCreated()");
        activity = getActivity();
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        if (chatFragmentAdapter == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            recyclerFragmentChat.setLayoutManager(linearLayoutManager);
            chatFragmentAdapter = new ConversationAdapter();
            recyclerFragmentChat.setAdapter(chatFragmentAdapter);
            recyclerFragmentChat.addItemDecoration(new LineDecoration(activity));
            chatFragmentAdapter.setConversationListener(conversationListener);
            loadRooms();

            toolbar.getRelativeLayout().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    SearchPopBean chatBean = new SearchPopBean(R.mipmap.icon_groupchat_start, getString(R.string.Link_Group_Launch));

                    SystemSetBean systemSetBean = ParamManager.getInstance().getSystemSet();
                    String notifyTxt = systemSetBean.isRing() && systemSetBean.isVibrate() ?
                            activity.getString(R.string.Link_Close_to_remind) :
                            activity.getString(R.string.Link_Open_to_remind);
                    SearchPopBean notifyBean = new SearchPopBean(R.mipmap.icon_popup_notify, notifyTxt);
                    SearchPopBean feedbackBean = new SearchPopBean(R.mipmap.icon_popup_feedback, getString(R.string.Set_Help_and_feedback));
                    SearchPopBean[] popBeens = new SearchPopBean[]{chatBean, notifyBean, feedbackBean};

                    PopupWindow popWindow = new SearchPopWindow(getActivity(), Arrays.asList(popBeens), new SearchPopupListener() {
                        @Override
                        public void itemClick(int position, SearchPopBean popBean) {
                            switch (position) {
                                case 0:
                                    ARouter.getInstance().build("/iwork/chat/set/GroupSelectActivity")
                                            .withBoolean("isCreateGroup", true)
                                            .navigation();
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    });
                    int offsetX = SystemUtil.dipToPx(30);
                    int offsetY = SystemUtil.dipToPx(30);
                    popWindow.showAsDropDown(toolbar.findViewById(R.id.relative_layout), offsetX, offsetY);
                }
            });
        }
    }

    private class ConversationListener implements ConversationAdapter.ItemListener {

        @Override
        public void itemClick(Connect.ChatType chatType, String identify) {
            ARouter.getInstance().build("/iwork/chat/ChatActivity")
                    .withSerializable("chatType", chatType)
                    .withString("chatIdentify", identify)
                    .navigation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogManager.getLogger().d(Tag, "onDestroyView()");
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
