package connect.database.green.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import connect.database.green.bean.CurrencyEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CURRENCY_ENTITY".
*/
public class CurrencyEntityDao extends AbstractDao<CurrencyEntity, Long> {

    public static final String TABLENAME = "CURRENCY_ENTITY";

    /**
     * Properties of entity CurrencyEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Currency = new Property(1, Integer.class, "currency", false, "CURRENCY");
        public final static Property Category = new Property(2, Integer.class, "category", false, "CATEGORY");
        public final static Property Salt = new Property(3, String.class, "salt", false, "SALT");
        public final static Property MasterAddress = new Property(4, String.class, "masterAddress", false, "MASTER_ADDRESS");
        public final static Property DefaultAddress = new Property(5, String.class, "defaultAddress", false, "DEFAULT_ADDRESS");
        public final static Property Status = new Property(6, Integer.class, "status", false, "STATUS");
        public final static Property Balance = new Property(7, Long.class, "balance", false, "BALANCE");
        public final static Property Payload = new Property(8, String.class, "payload", false, "PAYLOAD");
    }


    public CurrencyEntityDao(DaoConfig config) {
        super(config);
    }
    
    public CurrencyEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CURRENCY_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"CURRENCY\" INTEGER NOT NULL UNIQUE ," + // 1: currency
                "\"CATEGORY\" INTEGER," + // 2: category
                "\"SALT\" TEXT NOT NULL UNIQUE ," + // 3: salt
                "\"MASTER_ADDRESS\" TEXT," + // 4: masterAddress
                "\"DEFAULT_ADDRESS\" TEXT," + // 5: defaultAddress
                "\"STATUS\" INTEGER," + // 6: status
                "\"BALANCE\" INTEGER," + // 7: balance
                "\"PAYLOAD\" TEXT);"); // 8: payload
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CURRENCY_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CurrencyEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindLong(2, entity.getCurrency());
 
        Integer category = entity.getCategory();
        if (category != null) {
            stmt.bindLong(3, category);
        }
        stmt.bindString(4, entity.getSalt());
 
        String masterAddress = entity.getMasterAddress();
        if (masterAddress != null) {
            stmt.bindString(5, masterAddress);
        }
 
        String defaultAddress = entity.getDefaultAddress();
        if (defaultAddress != null) {
            stmt.bindString(6, defaultAddress);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(7, status);
        }
 
        Long balance = entity.getBalance();
        if (balance != null) {
            stmt.bindLong(8, balance);
        }
 
        String payload = entity.getPayload();
        if (payload != null) {
            stmt.bindString(9, payload);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CurrencyEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindLong(2, entity.getCurrency());
 
        Integer category = entity.getCategory();
        if (category != null) {
            stmt.bindLong(3, category);
        }
        stmt.bindString(4, entity.getSalt());
 
        String masterAddress = entity.getMasterAddress();
        if (masterAddress != null) {
            stmt.bindString(5, masterAddress);
        }
 
        String defaultAddress = entity.getDefaultAddress();
        if (defaultAddress != null) {
            stmt.bindString(6, defaultAddress);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(7, status);
        }
 
        Long balance = entity.getBalance();
        if (balance != null) {
            stmt.bindLong(8, balance);
        }
 
        String payload = entity.getPayload();
        if (payload != null) {
            stmt.bindString(9, payload);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CurrencyEntity readEntity(Cursor cursor, int offset) {
        CurrencyEntity entity = new CurrencyEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.getInt(offset + 1), // currency
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // category
            cursor.getString(offset + 3), // salt
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // masterAddress
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // defaultAddress
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // status
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // balance
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // payload
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CurrencyEntity entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCurrency(cursor.getInt(offset + 1));
        entity.setCategory(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setSalt(cursor.getString(offset + 3));
        entity.setMasterAddress(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDefaultAddress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStatus(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setBalance(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setPayload(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CurrencyEntity entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CurrencyEntity entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CurrencyEntity entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
