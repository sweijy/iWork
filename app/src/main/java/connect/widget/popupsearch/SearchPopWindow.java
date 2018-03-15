package connect.widget.popupsearch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.List;

import connect.activity.home.view.LineDecoration;
import connect.ui.activity.R;
import connect.utils.system.SystemUtil;

/**
 * Created by PuJin on 2018/1/18.
 */

public class SearchPopWindow extends PopupWindow {

    private View conentView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SearchPopWindow(final Activity activity, List<SearchPopBean> popBeanList, SearchPopupListener searchPopupListener) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_search, null);
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 2 - 60);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        RecyclerView recyclerView = (RecyclerView) conentView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        SearchPopupAdapter popupAdapter = new SearchPopupAdapter();
        popupAdapter.setData(popBeanList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(popupAdapter);
        recyclerView.addItemDecoration(new LineDecoration(activity));
        popupAdapter.setOnItemClick(searchPopupListener);
        //this.setElevation(SystemUtil.dipToPx(10));
    }
}
