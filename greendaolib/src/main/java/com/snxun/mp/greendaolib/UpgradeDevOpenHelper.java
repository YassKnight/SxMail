package com.snxun.mp.greendaolib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.snxun.mp.greendaolib.dao.DaoMaster;

import org.greenrobot.greendao.database.Database;

/**
 * Created by zhouL on 2018/5/9.
 */
public class UpgradeDevOpenHelper extends DaoMaster.DevOpenHelper {

    public UpgradeDevOpenHelper(Context context, String name) {
        super(context, name);
    }

    public UpgradeDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database database, int oldVersion, int newVersion) {
        printLog("Upgrading schema from version " + oldVersion + " to " + newVersion);
        Log.i(GreenDaoManager.TAG, "Upgrading schema from version " + oldVersion + " to " + newVersion);
        if (oldVersion < newVersion) {//升级
            switch (oldVersion) {
                case 1:
//                    version1to2(database);
                case 2:
//                    version2to3(database);
                    break;
                case 3:
//                    version3to4(database);
                    break;
                case 4:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 版本1升级到版本2
     */
    private void version1to2(Database database) {
        // 增加一个实体表
        //        ClubDao.createTable(db, false);
        // 修改Note表
        //        db.execSQL("ALTER TABLE 'User' ADD 'auther' String");
//        DaoConfig config = new DaoConfig(database, UserTableDao.class);
//        database.execSQL("alter table " + config.tablename + " add real_phone BOOLEAN");
//        database.execSQL("alter table " + config.tablename + " add real_phone_txt TEXT");
//        database.execSQL("alter table " + config.tablename + " add real_sfz BOOLEAN");
//        database.execSQL("alter table " + config.tablename + " add real_sfz_limit_day TEXT");

    }

    /**
     * 版本2升级到版本3
     */
    private void version2to3(Database database) {
        // 增加一个实体表
//        H5AppTableDao.createTable(database, false);
//
//        DaoConfig config = new DaoConfig(database, AppTableDao.class);
//        database.execSQL("alter table " + config.tablename + " add using_status BOOLEAN");
//        database.execSQL("alter table " + config.tablename + " add run_flag TEXT");
    }

    /**
     * 版本3升级到版本4
     */
    private void version3to4(Database database) {
//        DaoConfig config = new DaoConfig(database, AppTableDao.class);
//        database.execSQL("alter table " + config.tablename + " add net_type INTEGER ");
//
//        DaoConfig h5config = new DaoConfig(database, H5AppTableDao.class);
//        database.execSQL("alter table " + h5config.tablename + " add net_type INTEGER ");

    }

    /**
     * 版本4升级到版本5
     */
    private void version4to5(Database database) {
        //do something
    }

    /**
     * 打印日志
     *
     * @param log 日志内容
     */
    private void printLog(String log) {
        if (GreenDaoManager.get().isPrintLog()) {
            Log.i(GreenDaoManager.TAG, log);
        }
    }
}