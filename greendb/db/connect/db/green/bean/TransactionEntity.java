package connect.database.green.bean;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TRANSACTION_ENTITY".
 */
@Entity
public class TransactionEntity implements java.io.Serializable {

    @Id(autoincrement = true)
    private Long _id;

    @NotNull
    private String message_id;

    @NotNull
    @Unique
    private String hashid;
    private Integer status;
    private Integer pay_count;
    private Integer crowd_count;

    @Generated
    public TransactionEntity() {
    }

    public TransactionEntity(Long _id) {
        this._id = _id;
    }

    @Generated
    public TransactionEntity(Long _id, String message_id, String hashid, Integer status, Integer pay_count, Integer crowd_count) {
        this._id = _id;
        this.message_id = message_id;
        this.hashid = hashid;
        this.status = status;
        this.pay_count = pay_count;
        this.crowd_count = crowd_count;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    @NotNull
    public String getMessage_id() {
        return message_id;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMessage_id(@NotNull String message_id) {
        this.message_id = message_id;
    }

    @NotNull
    public String getHashid() {
        return hashid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setHashid(@NotNull String hashid) {
        this.hashid = hashid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPay_count() {
        return pay_count;
    }

    public void setPay_count(Integer pay_count) {
        this.pay_count = pay_count;
    }

    public Integer getCrowd_count() {
        return crowd_count;
    }

    public void setCrowd_count(Integer crowd_count) {
        this.crowd_count = crowd_count;
    }

}
