package connect.widget.album.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import connect.activity.home.view.LineDecoration;
import connect.ui.activity.R;
import connect.activity.base.BaseFragment;
import connect.utils.ActivityUtil;
import connect.widget.album.AlbumActivity;
import connect.widget.album.adapter.AlbumListAdapter;
import connect.widget.album.model.AlbumFile;

/**
 * All pictures show photo albums
 */
public class AlbumListFragment extends BaseFragment implements View.OnClickListener{

    private AlbumActivity activity;
    private RecyclerView recyclerView;
    private AlbumListAdapter albumListAdapter;

    private TextView titleTxt;
    private TextView noImg;
    private ImageView backImg;
    private TextView sendTxt;
    private TextView albumsTxt;
    private TextView preTxt;

    private List<AlbumFile> albumFiles;

    public static AlbumListFragment newInstance() {
        return new AlbumListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (AlbumActivity) getActivity();
        View rootView = inflater.inflate(R.layout.fragment_album_grid, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        backImg= (ImageView) rootView.findViewById(R.id.iv_back);
        titleTxt= (TextView) rootView.findViewById(R.id.tv_dir_title);
        sendTxt= (TextView) rootView.findViewById(R.id.tv_selected_ok);
        noImg= (TextView) rootView.findViewById(R.id.txt2);
        albumsTxt= (TextView) rootView.findViewById(R.id.txt);
        preTxt= (TextView) rootView.findViewById(R.id.txt1);

        GridLayoutManager layoutManager = new GridLayoutManager(activity, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LineDecoration(activity));

        albumListAdapter = new AlbumListAdapter();
        recyclerView.setAdapter(albumListAdapter);
        List<AlbumFile> infos = activity.getAlbumFiles();
        albumListAdapter.setData(infos);
        albumListAdapter.setAlbumGridListener(new AlbumListAdapter.AlbumGridListener() {

            @Override
            public boolean itemIsSelect(AlbumFile imageInfo) {
                return activity.isSelectImg(imageInfo);
            }

            @Override
            public void ItemClick(int position) {
                activity.getPresenter().galleyFragment(false, position);
            }

            @Override
            public boolean ItemCheck(boolean isChecked, AlbumFile albumFile) {
                boolean canselect = true;
                if (isChecked && !activity.canSelectImg()) {
                    canselect = false;
                } else {
                    activity.updateSelectInfos(isChecked, albumFile);
                    refreshSelectedViewState();
                }
                return canselect;
            }
        });
        refreshSelectedViewState();

        backImg.setOnClickListener(this);
        sendTxt.setOnClickListener(this);
        albumsTxt.setOnClickListener(this);
        preTxt.setOnClickListener(this);

        return rootView;
    }

    public void setTitle(String title) {
        title = title.length() > 8 ? title.substring(0, 8) + "..." : title;
        titleTxt.setText(title);
        albumsTxt.setText(title);
    }

    public void loadData() {
        if (activity.getAlbumFiles() != null) {
            albumFiles = activity.getAlbumFiles();
            albumListAdapter.setData(albumFiles);
        }
    }

    /**
     * Refresh the state of the selected button
     */
    private void refreshSelectedViewState() {
        int selectNum = activity.getSelectSize();
        int maxNum = activity.getMaxSelect();
        if (selectNum == 0) {
            sendTxt.setEnabled(false);
            sendTxt.setText(R.string.Common_OK);

            preTxt.setEnabled(false);
            preTxt.setText(getString(R.string.Chat_Preview));
        } else {
            //Most choose 9 picture
            sendTxt.setEnabled(true);
            sendTxt.setText(getString(R.string.Chat_Select_Count_Max, selectNum, maxNum));

            preTxt.setEnabled(true);
            preTxt.setText(getString(R.string.Chat_Preview_Num, selectNum));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ActivityUtil.goBack(activity);
                break;
            case R.id.tv_selected_ok://send
                activity.sendImgInfos();
                break;
            case R.id.txt://select album
                activity.albumFolderDialog();
                break;
            case R.id.txt1://preview
                activity.getPresenter().galleyFragment(true, 0);
                break;
        }
    }

    @Override
    public void initView() {

    }
}
