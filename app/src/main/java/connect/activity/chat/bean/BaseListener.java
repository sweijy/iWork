package connect.activity.chat.bean;

/**
 * Created by Administrator on 2017/7/5.
 */

public interface BaseListener<T> {

    void Success(T ts);

    void fail(Object... objects);
}