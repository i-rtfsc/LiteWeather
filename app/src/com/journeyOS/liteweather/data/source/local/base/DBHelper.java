package com.journeyOS.liteweather.data.source.local.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.journeyOS.liteframework.utils.Singleton;

import java.util.HashMap;
import java.util.Map;

public class DBHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private static final Singleton<DBHelper> gDefault = new Singleton<DBHelper>() {
        @Override
        protected DBHelper create() {
            return new DBHelper();
        }
    };

    private DBHelper() {
    }

    public static DBHelper getDefault() {
        return gDefault.get();
    }

    private final Map<String, Object> mDatabaseMap = new HashMap<>();

    public synchronized <T extends RoomDatabase> T getRoomDatabaseBuilder(Context context, Class<T> dbCls, String dbName) {
        String dbClsName = dbCls.getName();
        if (mDatabaseMap.get(dbClsName) == null) {
            T database = provider(context, dbCls, dbName);
            mDatabaseMap.put(dbClsName, database);
            return database;
        } else {
            Object database = mDatabaseMap.get(dbClsName);
            return (T) database;
        }
    }

    private final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //upgrade
        }
    };

    private <T extends RoomDatabase> T provider(Context context, Class<T> dbCls, String dbName) {
        return Room.databaseBuilder(context, dbCls, dbName)
//                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                .fallbackToDestructiveMigration().build();
    }
}
