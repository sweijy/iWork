package connect.database.green.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class ConversionSettingEntity implements Serializable {

    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long _id;
    @Unique
    private String identifier;

    private Integer snap_time;
    private Integer disturb;
    @Generated(hash = 841544130)
    public ConversionSettingEntity(Long _id, String identifier, Integer snap_time,
            Integer disturb) {
        this._id = _id;
        this.identifier = identifier;
        this.snap_time = snap_time;
        this.disturb = disturb;
    }
    @Generated(hash = 721078223)
    public ConversionSettingEntity() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getIdentifier() {
        return this.identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public Integer getSnap_time() {
        return this.snap_time;
    }
    public void setSnap_time(Integer snap_time) {
        this.snap_time = snap_time;
    }
    public Integer getDisturb() {
        return this.disturb;
    }
    public void setDisturb(Integer disturb) {
        this.disturb = disturb;
    }

    
}