package connect.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import org.junit.Test;

import java.io.InputStream;

import connect.activity.base.BaseApplication;
import connect.ui.activity.R;
import connect.utils.data.ResourceUtil;

import static org.junit.Assert.assertTrue;

/**
 * Created by Administrator on 2017/8/2 0002.
 */

public class DataUtilTest {

    @Test
    public void getRateData() {

    }

    @Test
    public void getRate() {

    }

    @Test
    public void getDrawable() {
        Drawable drawable =  ResourceUtil.getDrawable(BaseApplication.getInstance().getBaseContext(), R.mipmap.album_arrow_back2x);
        if(drawable == null){
            assertTrue(false);
        }else{
            assertTrue(true);
        }
    }

    @Test
    public void getColor() {
        int color =  ResourceUtil.getColor(BaseApplication.getInstance().getBaseContext(), R.color.color_007aff);
        if(color == Color.BLACK){
            assertTrue(false);
        }else{
            assertTrue(true);
        }
    }

    @Test
    public void getEmotDrawable() {
        Drawable drawable =  ResourceUtil.getEmotDrawable("emoji/activities_normal.png");
        if(drawable == null){
            assertTrue(false);
        }else{
            assertTrue(true);
        }
    }

    @Test
    public void getAssetsStream() {
        InputStream inputStream =  ResourceUtil.getAssetsStream("emoji/activities_normal.png");
        if(inputStream == null){
            assertTrue(false);
        }else{
            assertTrue(true);
        }
    }

}
