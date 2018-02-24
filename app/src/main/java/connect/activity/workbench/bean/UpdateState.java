package connect.activity.workbench.bean;

/**
 * 工作台界面更新通知
 */
public class UpdateState {

    public enum StatusEnum{
        UPDATE_VISITOR,
        UPDATE_WAREHOUSE,
    }

    private UpdateState.StatusEnum statusEnum;

    public UpdateState() {}

    public UpdateState(StatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

}
