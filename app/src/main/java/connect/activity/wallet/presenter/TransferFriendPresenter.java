package connect.activity.wallet.presenter;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.List;

import connect.activity.chat.bean.MsgEntity;
import connect.activity.chat.model.content.FriendChat;
import connect.activity.chat.model.content.NormalChat;
import connect.activity.wallet.contract.TransferFriendContract;
import connect.database.green.DaoHelper.ContactHelper;
import connect.database.green.DaoHelper.ConversionHelper;
import connect.database.green.DaoHelper.MessageHelper;
import connect.database.green.DaoHelper.TransactionHelper;
import connect.database.green.bean.ContactEntity;
import connect.database.green.bean.ConversionEntity;
import connect.ui.activity.R;
import connect.utils.TimeUtil;
import connect.utils.data.RateFormatUtil;
import connect.utils.system.SystemUtil;
import connect.wallet.cwallet.business.TransferEditView;

/**
 * Created by Administrator on 2017/4/18 0018.
 */
public class TransferFriendPresenter implements TransferFriendContract.Presenter{

    private TransferFriendContract.View mView;
    private List<ContactEntity> list;

    public TransferFriendPresenter(TransferFriendContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void start() {

    }

    @Override
    public void setListData(List<ContactEntity> list) {
        this.list = list;
    }

    @Override
    public List<ContactEntity> getListFriend() {
        return list;
    }

    @Override
    public AdapterView.OnItemClickListener getItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public TransferEditView.OnEditListener getOnEditListener() {
        return onEditListener;
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ContactEntity friendEntity = (ContactEntity) parent.getAdapter().getItem(position);
            if (TextUtils.isEmpty(friendEntity.getAvatar())) {
                mView.addTranferFriend();
            }
        }
    };

    private TransferEditView.OnEditListener onEditListener = new TransferEditView.OnEditListener() {
        @Override
        public void onEdit(String value) {
            if (TextUtils.isEmpty(value) || list.size() == 0 ||
                    Double.valueOf(mView.getCurrentBtc()) < 0.0001) {
                mView.setBtnEnabled(false);
            } else {
                mView.setBtnEnabled(true);
            }
        }

        @Override
        public void setFee() {
            mView.setPayFee();
        }
    };

    @Override
    public void checkBtnEnable(){
        if (!TextUtils.isEmpty(mView.getCurrentBtc())
                && list.size() > 0
                && Double.valueOf(mView.getCurrentBtc()) >= 0.0001) {
            mView.setBtnEnabled(true);
        } else {
            mView.setBtnEnabled(false);
        }
    }

    @Override
    public void horizontal_layout(GridView gridView) {
        int spacingH = SystemUtil.dipToPx(10);
        int itemWidth = SystemUtil.dipToPx(45);
        int size = list.size() + 1;
        int allWidth = ((spacingH + itemWidth) * size);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params);
        gridView.setColumnWidth(itemWidth);
        gridView.setHorizontalSpacing(spacingH);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size);
    }

    @Override
    public void sendTransferMessage(String hashid, String address,String note) {
        ContactEntity friendEntity = ContactHelper.getInstance().loadFriendEntity(address);
        if (friendEntity != null) {
            NormalChat friendChat = new FriendChat(friendEntity);
            long amount = RateFormatUtil.stringToLongBtc(mView.getCurrentBtc());
            MsgEntity msgEntity = friendChat.transferMsg(hashid, amount, note,0);
            MessageHelper.getInstance().insertToMsg(msgEntity.getMsgDefinBean());

            friendChat.sendPushMsg(msgEntity);

            ConversionEntity roomEntity = ConversionHelper.getInstance().loadRoomEnitity(friendEntity.getPub_key());
            if (roomEntity == null) {
                roomEntity = new ConversionEntity();
                roomEntity.setIdentifier(friendEntity.getPub_key());
                roomEntity.setAvatar(friendEntity.getAvatar());
                roomEntity.setName(friendEntity.getUsername());
                roomEntity.setType(0);
            }

            roomEntity.setContent(mView.getActivity().getString(R.string.Chat_Transfer));
            roomEntity.setLast_time(TimeUtil.getCurrentTimeInLong());
            ConversionHelper.getInstance().insertRoomEntity(roomEntity);

            TransactionHelper.getInstance().updateTransEntity(hashid, msgEntity.getMsgid(), 1);
        }
    }

}
