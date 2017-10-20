package connect.database.green.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import connect.database.green.bean.GroupMemberEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GROUP_MEMBER_ENTITY".
*/
public class GroupMemberEntityDao extends AbstractDao<GroupMemberEntity, Long> {

    public static final String TABLENAME = "GROUP_MEMBER_ENTITY";

    /**
     * Properties of entity GroupMemberEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Identifier = new Property(1, String.class, "identifier", false, "IDENTIFIER");
        public final static Property Uid = new Property(2, String.class, "uid", false, "UID");
        public final static Property Username = new Property(3, String.class, "username", false, "USERNAME");
        public final static Property Avatar = new Property(4, String.class, "avatar", false, "AVATAR");
        public final static Property Role = new Property(5, Integer.class, "role", false, "ROLE");
        public final static Property Nick = new Property(6, String.class, "nick", false, "NICK");
        public final static Property Connect_id = new Property(7, String.class, "connect_id", false, "CONNECT_ID");
    }


    public GroupMemberEntityDao(DaoConfig config) {
        super(config);
    }
    
    public GroupMemberEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP_MEMBER_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"IDENTIFIER\" TEXT NOT NULL ," + // 1: identifier
                "\"UID\" TEXT NOT NULL ," + // 2: uid
                "\"USERNAME\" TEXT NOT NULL ," + // 3: username
                "\"AVATAR\" TEXT NOT NULL ," + // 4: avatar
                "\"ROLE\" INTEGER," + // 5: role
                "\"NICK\" TEXT," + // 6: nick
                "\"CONNECT_ID\" TEXT);"); // 7: connect_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP_MEMBER_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GroupMemberEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindString(2, entity.getIdentifier());
        stmt.bindString(3, entity.getUid());
        stmt.bindString(4, entity.getUsername());
        stmt.bindString(5, entity.getAvatar());
 
        Integer role = entity.getRole();
        if (role != null) {
            stmt.bindLong(6, role);
        }
 
        String nick = entity.getNick();
        if (nick != null) {
            stmt.bindString(7, nick);
        }
 
        String connect_id = entity.getConnect_id();
        if (connect_id != null) {
            stmt.bindString(8, connect_id);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GroupMemberEntity entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
        stmt.bindString(2, entity.getIdentifier());
        stmt.bindString(3, entity.getUid());
        stmt.bindString(4, entity.getUsername());
        stmt.bindString(5, entity.getAvatar());
 
        Integer role = entity.getRole();
        if (role != null) {
            stmt.bindLong(6, role);
        }
 
        String nick = entity.getNick();
        if (nick != null) {
            stmt.bindString(7, nick);
        }
 
        String connect_id = entity.getConnect_id();
        if (connect_id != null) {
            stmt.bindString(8, connect_id);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public GroupMemberEntity readEntity(Cursor cursor, int offset) {
        GroupMemberEntity entity = new GroupMemberEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.getString(offset + 1), // identifier
            cursor.getString(offset + 2), // uid
            cursor.getString(offset + 3), // username
            cursor.getString(offset + 4), // avatar
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // role
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // nick
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // connect_id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GroupMemberEntity entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIdentifier(cursor.getString(offset + 1));
        entity.setUid(cursor.getString(offset + 2));
        entity.setUsername(cursor.getString(offset + 3));
        entity.setAvatar(cursor.getString(offset + 4));
        entity.setRole(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setNick(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setConnect_id(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(GroupMemberEntity entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(GroupMemberEntity entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GroupMemberEntity entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
