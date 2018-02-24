package connect.activity.contact.bean;

public class AppsState {

    public enum AppsEnum{
        APPLICATION,
    }

    private AppsState.AppsEnum appsEnum;

    public AppsState() {}

    public AppsState(AppsEnum appsEnum) {
        this.appsEnum = appsEnum;
    }

    public AppsEnum getAppsEnum() {
        return appsEnum;
    }
}
