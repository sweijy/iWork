package connect.database.green.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/7/10.
 */
@Entity
public class CurrencyEntity implements Serializable{

    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long _id;

    private String baseSeedEncryption;
    private String baseSeedSalt;
    private String currency_salt;
    private Integer currency_index;
    private String currency_code;
    private Integer currency_status;
    private Integer currency_switch;
    private Long currency_balance;
    @Generated(hash = 352144314)
    public CurrencyEntity(Long _id, String baseSeedEncryption, String baseSeedSalt,
            String currency_salt, Integer currency_index, String currency_code,
            Integer currency_status, Integer currency_switch,
            Long currency_balance) {
        this._id = _id;
        this.baseSeedEncryption = baseSeedEncryption;
        this.baseSeedSalt = baseSeedSalt;
        this.currency_salt = currency_salt;
        this.currency_index = currency_index;
        this.currency_code = currency_code;
        this.currency_status = currency_status;
        this.currency_switch = currency_switch;
        this.currency_balance = currency_balance;
    }
    @Generated(hash = 228156879)
    public CurrencyEntity() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getBaseSeedEncryption() {
        return this.baseSeedEncryption;
    }
    public void setBaseSeedEncryption(String baseSeedEncryption) {
        this.baseSeedEncryption = baseSeedEncryption;
    }
    public String getBaseSeedSalt() {
        return this.baseSeedSalt;
    }
    public void setBaseSeedSalt(String baseSeedSalt) {
        this.baseSeedSalt = baseSeedSalt;
    }
    public String getCurrency_salt() {
        return this.currency_salt;
    }
    public void setCurrency_salt(String currency_salt) {
        this.currency_salt = currency_salt;
    }
    public Integer getCurrency_index() {
        return this.currency_index;
    }
    public void setCurrency_index(Integer currency_index) {
        this.currency_index = currency_index;
    }
    public String getCurrency_code() {
        return this.currency_code;
    }
    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }
    public Integer getCurrency_status() {
        return this.currency_status;
    }
    public void setCurrency_status(Integer currency_status) {
        this.currency_status = currency_status;
    }
    public Integer getCurrency_switch() {
        return this.currency_switch;
    }
    public void setCurrency_switch(Integer currency_switch) {
        this.currency_switch = currency_switch;
    }
    public Long getCurrency_balance() {
        return this.currency_balance;
    }
    public void setCurrency_balance(Long currency_balance) {
        this.currency_balance = currency_balance;
    }

}
