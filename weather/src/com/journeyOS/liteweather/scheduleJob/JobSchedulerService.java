/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.liteweather.scheduleJob;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import com.journeyOS.core.Messages;
import com.journeyOS.literouter.Message;
import com.journeyOS.literouter.Router;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.liteweather.remoteviews.RemoteViewsHelper;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService implements RouterListener {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Router.getDefault().unregister(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        RemoteViewsHelper.showNotification(this);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void onShowMessage(Message message) {
        Messages msg = new Messages();
        switch (msg.what) {
            case Messages.MSG_UPDATE_NOTIFICATION:
                RemoteViewsHelper.updateNotification(this);
                break;
            case Messages.MSG_ALLOW_NOTIFICATION:
                boolean allow = msg.arg1 == 1;
                if (!allow) {
                    RemoteViewsHelper.stopNotification(this);
                } else {
                    RemoteViewsHelper.showNotification(this);
                }
                break;
            case Messages.MSG_CLEAR_ALARM:
                RemoteViewsHelper.stopAlarm(this);
                break;
        }
    }
}
