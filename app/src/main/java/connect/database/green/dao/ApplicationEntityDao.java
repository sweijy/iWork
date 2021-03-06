package connect.database.green.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import connect.database.green.bean.ApplicationEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APPLICATION_ENTITY".
*/
public class ApplicationEntityDao extends AbstractDao<ApplicationEntity, Long> {

    public static final String TABLENAME = "APPLICATION_ENTITY";

    /**
     * Properties of entity ApplicationEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Code = new Property(1, String.class, "code", false, "CODE");
        public final static Property Category = new Property(2, int.class, "category", false, "CATEGORY");
    }


    public ApplicationEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ApplicationEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APPLICATION_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"CODE\" TEXT NOT NULL UNIQUE ," + // 1: code
                "\"CATEGORY\" INTEGER NOT NULL );"); // 2: category
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APPLICATION_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ApplicationEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindString(2, entity.getCode());
        stmt.bindLong(3, entity.getCategory());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ApplicationEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindString(2, entity.getCode());
        stmt.bindLong(3, entity.getCategory());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ApplicationEntity readEntity(Cursor cursor, int offset) {
        ApplicationEntity entity = new ApplicationEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.getString(offset + 1), // code
            cursor.getInt(offset + 2) // category
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ApplicationEntity entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCode(cursor.getString(offset + 1));
        entity.setCategory(cursor.getInt(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ApplicationEntity entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ApplicationEntity entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ApplicationEntity entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
