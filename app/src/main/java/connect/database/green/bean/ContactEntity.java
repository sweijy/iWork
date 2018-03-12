package connect.database.green.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class ContactEntity implements Serializable {

    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long _id;

    @NotNull
    @Unique
    private String uid;
    private String name;
    private String avatar;
    private String ou;
    private String publicKey;
    private String empNo;
    private String mobile;
    private Integer gender;
    private String tips;
    private Boolean registed;
    private String Username;

    @Generated(hash = 516121535)
    public ContactEntity(Long _id, @NotNull String uid, String name, String avatar,
            String ou, String publicKey, String empNo, String mobile,
            Integer gender, String tips, Boolean registed, String Username) {
        this._id = _id;
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.ou = ou;
        this.publicKey = publicKey;
        this.empNo = empNo;
        this.mobile = mobile;
        this.gender = gender;
        this.tips = tips;
        this.registed = registed;
        this.Username = Username;
    }
    @Generated(hash = 393979869)
    public ContactEntity() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getOu() {
        return this.ou;
    }
    public void setOu(String ou) {
        this.ou = ou;
    }
    public String getPublicKey() {
        return this.publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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
    public Boolean getRegisted() {
        return this.registed;
    }
    public void setRegisted(Boolean registed) {
        this.registed = registed;
    }
    public String getUsername() {
        return this.Username;
    }
    public void setUsername(String Username) {
        this.Username = Username;
    }
    
}
