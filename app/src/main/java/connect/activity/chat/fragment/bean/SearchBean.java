package connect.activity.chat.fragment.bean;

/**
 * Created by Administrator on 2018/1/31 0031.
 */

public class SearchBean {

    private int style;
    private int status;
    private String uid;
    private String avatar;
    private String name;
    private String userName;
    private String searchStr;
    private String hinit;
    private int gender;
    private Long countMember;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHinit() {
        return hinit;
    }

    public void setHinit(String hinit) {
        this.hinit = hinit;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getCountMember() {
        return countMember;
    }

    public void setCountMember(Long countMember) {
        this.countMember = countMember;
    }
}
