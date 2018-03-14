package connect.database.green.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class ConversionSettingEntity implements Serializable {

    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    @Unique
    private String identifier;

    private Integer disturb;

    @Generated(hash = 884556003)
    public ConversionSettingEntity(Long _id, @NotNull String identifier,
            Integer disturb) {
        this._id = _id;
        this.identifier = identifier;
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
    public Integer getDisturb() {
        return this.disturb;
    }
    public void setDisturb(Integer disturb) {
        this.disturb = disturb;
    }
}
