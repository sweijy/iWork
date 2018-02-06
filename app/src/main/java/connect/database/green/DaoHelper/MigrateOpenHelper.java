package connect.database.green.DaoHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import connect.activity.home.DBUpdateActivity;
import connect.database.green.dao.DaoMaster;
import instant.utils.SharedUtil;

public class MigrateOpenHelper extends DaoMaster.OpenHelper {

    private static String TAG = "_MigrateOpenHelper";
    private boolean mainTmpDirSet = false;

    public MigrateOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    /**
     * BUG:SQLiteCantOpenDatabaseException: unable to open database file (code 14)
     *
     * @return
     */
    @Override
    public SQLiteDatabase getReadableDatabase() {
//        if (!mainTmpDirSet) {
//            Context context = BaseApplication.getInstance().getBaseContext();
//            String innerPath = context.getFilesDir().getParentFile().getPath();
//            boolean rs = new File(innerPath + "/databases/main").mkdirs();
//            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.iwork.im/databases/main'");
//            mainTmpDirSet = true;
//        }
        return super.getReadableDatabase();
    }

    @Override
    public Database getEncryptedReadableDb(String password) {
//        if (!mainTmpDirSet) {
//            Context context = BaseApplication.getInstance().getBaseContext();
//            String innerPath = context.getFilesDir().getParentFile().getPath();
//            boolean rs = new File(innerPath + "/databases/main").mkdirs();
//            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.iwork.im/databases/main'");
//            mainTmpDirSet = true;
//        }
        return super.getEncryptedReadableDb(password);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // super.onUpgrade(getWritableDatabase(), oldVersion, newVersion);

        DaoMaster.dropAllTables(db, true);
        onCreate(db);
        SharedUtil.getInstance().deleteUserInfo();
        DBUpdateActivity.startActivity();

//        MigrationHelper.migrate(db,
//                ApplicationEntityDao.class,
//                ContactEntityDao.class,
//                ConversionEntityDao.class,
//                ConversionSettingEntityDao.class,
//                CurrencyAddressEntityDao.class,
//                CurrencyEntityDao.class,
//                FriendRequestEntityDao.class,
//                GroupEntityDao.class,
//                GroupMemberEntityDao.class,
//                MessageEntityDao.class,
//                OrganizerEntityDao.class,
//                ParamEntityDao.class,
//                TransactionEntityDao.class
//        );
    }
}
