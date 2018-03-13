package connect.database.green.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by PuJin on 2018/1/30.
 */

@Entity
public class OrganizerEntity implements Serializable {

    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long _id;

    private long upperId;

    // 部门
    private Long id;
    private Long count;
    private String name;

    // 人员
    private String o_u;
    private String uid;
    private String avatar;
    private String pub_key;
    private Boolean registed;
    private String empNo;
    private String mobile;
    private Integer gender;
    private String tips;
    private String Username;
    private String organizational;

    @Generated(hash = 1722274308)
    public OrganizerEntity(Long _id, long upperId, Long id, Long count, String name,
            String o_u, String uid, String avatar, String pub_key, Boolean registed,
            String empNo, String mobile, Integer gender, String tips,
            String Username, String organizational) {
        this._id = _id;
        this.upperId = upperId;
        this.id = id;
        this.count = count;
        this.name = name;
        this.o_u = o_u;
        this.uid = uid;
        this.avatar = avatar;
        this.pub_key = pub_key;
        this.registed = registed;
        this.empNo = empNo;
        this.mobile = mobile;
        this.gender = gender;
        this.tips = tips;
        this.Username = Username;
        this.organizational = organizational;
    }

    @Generated(hash = 1783806479)
    public OrganizerEntity() {
    }

    public boolean isDepartment() {
        return null != id && id != 0;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public long getUpperId() {
        return this.upperId;
    }

    public void setUpperId(long upperId) {
        this.upperId = upperId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getO_u() {
        return this.o_u;
    }

    public void setO_u(String o_u) {
        this.o_u = o_u;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPub_key() {
        return this.pub_key;
    }

    public void setPub_key(String pub_key) {
        this.pub_key = pub_key;
    }

    public Boolean getRegisted() {
        return this.registed;
    }

    public void setRegisted(Boolean registed) {
        this.registed = registed;
    }

    public String getEmpNo() {
        return this.empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getGender() {
        return this.gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getUsername() {
        return this.Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getOrganizational() {
        return this.organizational;
    }

    public void setOrganizational(String organizational) {
        this.organizational = organizational;
    }

}
