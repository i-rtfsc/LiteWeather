/*
 * Copyright (c) 2021 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.core.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.Singleton;

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
        if (BaseUtils.isNull(mDatabaseMap.get(dbClsName))) {
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
                .addMigrations(MIGRATION_1_2)
                .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                .fallbackToDestructiveMigration().build();
    }
}
