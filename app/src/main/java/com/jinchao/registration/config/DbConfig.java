package com.jinchao.registration.config;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

/**
 * Created by user on 2017/3/20.
 */

public class DbConfig {
    public static DbManager.DaoConfig getDbConfig(){
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("hotel_netbar.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
                .setDbDir(new File(Constants.DB_PATH)) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setAllowTransaction(true)
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                    }
                });
        return daoConfig;

    }
    public static DbManager getDbManager(){
        return x.getDb(getDbConfig());
    }
}
