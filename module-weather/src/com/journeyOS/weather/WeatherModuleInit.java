package com.journeyOS.weather;

import android.app.Application;

import com.journeyOS.base.base.IModuleInit;
import com.journeyOS.liteframework.utils.KLog;

public class WeatherModuleInit implements IModuleInit {
    @Override
    public boolean onInitPrimary(Application application) {
        KLog.d("weather module");
        return false;
    }

    @Override
    public boolean onInitSecondary(Application application) {
        KLog.d("weather module");
        return false;
    }
}
