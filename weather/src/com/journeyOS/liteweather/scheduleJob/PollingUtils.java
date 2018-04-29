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

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.core.Version;
import com.journeyOS.core.base.ResourceProvider;

import static com.journeyOS.base.Constant.POLLING_TIME;


public class PollingUtils {

    public static void startService(Context context, boolean allowPoll) {

        Class cls = Version.buildVersion() > Build.VERSION_CODES.LOLLIPOP ? JobSchedulerService.class : AlarmService.class;
        context.startService(new Intent(context, cls));
        if (!allowPoll) {
            return;
        }
        startPolling(context);
    }

    //开启轮询
    private static void startPolling(Context context) {
        Scheduler scheduler;
        long seconds = ResourceProvider.getSchedule(SpUtils.getInstant().getInt(POLLING_TIME, 0));
        if (Version.buildVersion() > Build.VERSION_CODES.LOLLIPOP) {
            scheduler = new JobWork();
            scheduler.startPolling(context, seconds, JobSchedulerService.class, JobSchedulerService.class.getSimpleName());
        } else {
            scheduler = new AlarmWork();
            scheduler.startPolling(context, seconds, AlarmService.class, AlarmService.class.getSimpleName());
        }
    }

    //停止轮询
    public static void stopPolling(Context context) {
        Scheduler scheduler;
        if (Version.buildVersion() > Build.VERSION_CODES.LOLLIPOP) {
            scheduler = new JobWork();
            scheduler.stopPolling(context, JobSchedulerService.class, JobSchedulerService.class.getSimpleName());
        } else {
            scheduler = new AlarmWork();
            scheduler.stopPolling(context, AlarmService.class, AlarmService.class.getSimpleName());
        }
    }
}
