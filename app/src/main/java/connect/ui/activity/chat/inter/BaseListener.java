package connect.ui.activity.chat.inter;

/**
 * Created by Administrator on 2017/7/5.
 */

public interface BaseListener<T> {

    void Success(T ts);

    void fail(Object... objects);
}
