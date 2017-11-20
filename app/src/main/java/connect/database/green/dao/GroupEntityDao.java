package connect.database.green.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import connect.database.green.bean.GroupEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GROUP_ENTITY".
*/
public class GroupEntityDao extends AbstractDao<GroupEntity, Long> {

    public static final String TABLENAME = "GROUP_ENTITY";

    /**
     * Properties of entity GroupEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Identifier = new Property(1, String.class, "identifier", false, "IDENTIFIER");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Common = new Property(3, Integer.class, "common", false, "COMMON");
        public final static Property Verify = new Property(4, Integer.class, "verify", false, "VERIFY");
        public final static Property Avatar = new Property(5, String.class, "avatar", false, "AVATAR");
        public final static Property Summary = new Property(6, String.class, "summary", false, "SUMMARY");
    }


    public GroupEntityDao(DaoConfig config) {
        super(config);
    }
    
    public GroupEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"IDENTIFIER\" TEXT NOT NULL UNIQUE ," + // 1: identifier
                "\"NAME\" TEXT," + // 2: name
                "\"COMMON\" INTEGER," + // 3: common
                "\"VERIFY\" INTEGER," + // 4: verify
                "\"AVATAR\" TEXT," + // 5: avatar
                "\"SUMMARY\" TEXT);"); // 6: summary
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GroupEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindString(2, entity.getIdentifier());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Integer common = entity.getCommon();
        if (common != null) {
            stmt.bindLong(4, common);
        }
 
        Integer verify = entity.getVerify();
        if (verify != null) {
            stmt.bindLong(5, verify);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(6, avatar);
        }
 
        String summary = entity.getSummary();
        if (summary != null) {
            stmt.bindString(7, summary);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GroupEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindString(2, entity.getIdentifier());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Integer common = entity.getCommon();
        if (common != null) {
            stmt.bindLong(4, common);
        }
 
        Integer verify = entity.getVerify();
        if (verify != null) {
            stmt.bindLong(5, verify);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(6, avatar);
        }
 
        String summary = entity.getSummary();
        if (summary != null) {
            stmt.bindString(7, summary);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public GroupEntity readEntity(Cursor cursor, int offset) {
        GroupEntity entity = new GroupEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.getString(offset + 1), // identifier
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // common
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // verify
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // avatar
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // summary
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GroupEntity entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIdentifier(cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCommon(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setVerify(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setAvatar(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSummary(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(GroupEntity entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(GroupEntity entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GroupEntity entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
