package connect.widget.recordvoice.inter;

/**
 * Created by PuJin on 2018/2/23.
 */

public interface RecordVoiceListener {

    void startRecord();

    void updateRecordVolume(int db);

    void cancelRecord();

    void normalRecord();

    void recordShort();

    void stopRecord();
}
