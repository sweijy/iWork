package connect.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import connect.activity.home.view.LineDecoration;
import connect.ui.activity.R;
import connect.utils.dialog.adapter.DialogListAdapter;
import connect.utils.system.SystemDataUtil;
import connect.utils.system.SystemUtil;

/**
 * Dialog Tooltip tool
 */
public class DialogUtil {

    /**
     * Text prompt
     *
     * @param mContext
     * @param title
     * @param message
     * @param cancelButton
     * @param okButton
     * @param isCancel
     * @param onItemClickListener
     * @return
     */
    public static Dialog showAlertTextView(Context mContext, String title, String message, String cancelButton, String okButton,
                                           boolean isCancel, final OnItemClickListener onItemClickListener) {
        return showAlertTextView(mContext, title, message, cancelButton, okButton, isCancel, true, onItemClickListener);
    }

    public static Dialog showAlertTextView(Context mContext, String title, String message, String cancelButton, String okButton,
                                           boolean isCancel, boolean Outside, final OnItemClickListener onItemClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_hint, null);
        dialog.setContentView(view);
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);
        TextView cancelBtn = (TextView) view.findViewById(R.id.cancelBtn);
        TextView oklBtn = (TextView) view.findViewById(R.id.okBtn);

        if (TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setText(title);
        }

        messageTextView.setText(message);
        if (isCancel) {
            view.findViewById(R.id.centre_line).setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        } else {
            if(!TextUtils.isEmpty(cancelButton))
                cancelBtn.setText(cancelButton);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.cancel();
                    dialog.cancel();
                }
            });
        }

        if(!TextUtils.isEmpty(okButton))
            oklBtn.setText(okButton);
        oklBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.confirm("");
                dialog.cancel();
            }
        });

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = SystemUtil.dipToPx(250);
        mWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(Outside);
        dialog.show();
        return dialog;
    }

    /**
     * EditView diaolog
     *
     * @param mContext
     * @param title
     * @param leftStr
     * @param rightStr
     * @param hinit
     * @param onItemClickListener
     * @return
     */
    public static Dialog showEditView(Context mContext, String title, String leftStr, String rightStr, String message, String hinit,
                                      String text,boolean isGone ,int maxLength, final OnItemClickListener onItemClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_edit, null);
        TextView titleTv = (TextView) view.findViewById(R.id.title_tv);
        final EditText edit = (EditText) view.findViewById(R.id.edit);
        TextView leftTv = (TextView) view.findViewById(R.id.left_tv);
        TextView rightTv = (TextView) view.findViewById(R.id.right_tv);
        TextView messageTv = (TextView) view.findViewById(R.id.message_tv);

        if (isGone) {
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        titleTv.setText(title);
        if (TextUtils.isEmpty(message)) {
            messageTv.setVisibility(View.GONE);
        }else{
            messageTv.setText(message);
            messageTv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(hinit)) {
            edit.setHint(hinit);
        }
        if (!TextUtils.isEmpty(leftStr)) {
            leftTv.setText(leftStr);
        }
        if (!TextUtils.isEmpty(rightStr)) {
            rightTv.setText(rightStr);
        }
        if(!TextUtils.isEmpty(text)){
            edit.setText(text);
        }
        if(maxLength > 0){
            edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }

        leftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.confirm(edit.getText().toString());
                dialog.cancel();
            }
        });

        view.setMinimumWidth(SystemUtil.dipToPx(250));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(view);
        dialog.show();
        SystemUtil.showKeyBoard(edit.getContext(),edit);
        return dialog;
    }

    /**
     * popup dialog
     *
     * @param mContext
     * @param list
     * @param itemClickListener
     * @return
     */
    public static Dialog showBottomView(Context mContext, ArrayList<String> list, final DialogListItemClickListener itemClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_bottomview, null);
        dialog.setContentView(view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        TextView cancel = (TextView) view.findViewById(R.id.tv_popup_cancel);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new LineDecoration(mContext));
        DialogBottomAdapter dialogBottomAdapter = new DialogBottomAdapter(list);
        recyclerView.setAdapter(dialogBottomAdapter);
        dialogBottomAdapter.setItemClickListener(new OnDialogItemClickListener() {
            @Override
            public void itemClick(int position, String string) {
                itemClickListener.confirm(position);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = SystemDataUtil.getScreenWidth();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.DialogAnim);
        mWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    public interface OnGifListener {
        void click();
    }

    /**
     * Bottom dialog adapter
     */
    public static class DialogBottomAdapter extends RecyclerView.Adapter<DialogBottomAdapter.ViewHolder>{

        private ArrayList<String> list;
        private OnDialogItemClickListener itemClickListener;

        public DialogBottomAdapter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.dialog_bottom_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.titleTv.setText(list.get(position));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list!=null&&list.size()>=position){
                        itemClickListener.itemClick(position,list.get(position));
                    }
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTv;
            ViewHolder(View itemview) {
                super(itemview);
                titleTv = (TextView) itemview.findViewById(R.id.title_tv);
            }
        }

        public void setItemClickListener(OnDialogItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    public interface OnDialogItemClickListener{
        void itemClick(int position,String string);
    }

    public interface DialogListItemClickListener {
        void confirm(int position);
    }

    public interface OnItemClickListener {
        void confirm(String value);

        void cancel();
    }

    /**
     * popup dialog
     *
     * @param mContext
     * @param list
     * @param itemClickListener
     * @return
     */
    public static Dialog showItemListView(Context mContext, List<String> list, final DialogListItemClickListener itemClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_listitem, null);
        dialog.setContentView(view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new LineDecoration(mContext));
        DialogListAdapter dialogBottomAdapter = new DialogListAdapter(list);
        recyclerView.setAdapter(dialogBottomAdapter);
        dialogBottomAdapter.setItemClickListener(new DialogListAdapter.OnItemClickListener() {
            @Override
            public void itemClick(int position, String string) {
                itemClickListener.confirm(position);
                dialog.dismiss();
            }
        });

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        view.setMinimumWidth(SystemUtil.dipToPx(250));
        dialog.setCanceledOnTouchOutside(true);
        mWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }


}
