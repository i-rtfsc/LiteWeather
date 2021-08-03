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


package com.journeyOS.core.database.global;

import android.content.Context;

import com.journeyOS.base.utils.SmartLog;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.db.DataResultListener;
import com.journeyOS.core.api.db.IGlobalProvider;
import com.journeyOS.core.async.AsyncManager;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.WeatherDatabase;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ARouterInject(api = IGlobalProvider.class)
public class GlobalRepositoryImpl implements IGlobalProvider {
    private static final String TAG = GlobalRepositoryImpl.class.getSimpleName();
    private Context mContext;
    private GlobalDao mGlobalDao;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        mContext = CoreManager.getContext();
        WeatherDatabase database = DBHelper.getDefault().getRoomDatabaseBuilder(mContext, WeatherDatabase.class, DBConfigs.DB_NAME);
        mGlobalDao = database.globalDao();
    }


    @Override
    public void saveGlobal(final String key, final Object value) {
        SmartLog.d(TAG, "key = [" + key + "], value = [" + value + "]");
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Global global = mGlobalDao.getGlobal(key);
                if (global == null) {
                    Class<?> clazz = value.getClass();
                    final String object = clazz.getName();

                    global = new Global();
                    global.key = key;
                    global.value = value.toString();
                    global.object = object;
                }
                mGlobalDao.saveGlobal(global);
            }
        });
    }

    @Override
    public void getGlobal(final String key, final Object defaultValue, boolean finishedOnMainThread, final DataResultListener listener) {
        SmartLog.d(TAG, "getGlobal 1 key = [" + key + "], default value = [" + defaultValue + "]");
        new AsyncManager.AsyncJobBuilder<Global>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<Global>() {
                    @Override
                    public Global doAsync() {
                        Global global = mGlobalDao.getGlobal(key);
                        SmartLog.d(TAG, "doAsync global = [" + global + "]");
                        if (global == null) {
                            Class<?> clazz = defaultValue.getClass();
                            final String object = clazz.getName();

                            global = new Global();
                            global.key = key;
                            global.value = defaultValue.toString();
                            global.object = object;
                        }
                        return global;
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<Global>() {
                    @Override
                    public void onResult(Global global) {
                        SmartLog.d(TAG, "onResult global = [" + global.value + "]");
                        listener.onResult(global);
                    }
                }, finishedOnMainThread)
                .create()
                .start();
    }
}
