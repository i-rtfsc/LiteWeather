package com.journeyOS.city;

import android.app.Application;

import com.journeyOS.base.base.IModuleInit;
import com.journeyOS.liteframework.utils.KLog;

public class CityModuleInit implements IModuleInit {
    @Override
    public boolean onInitPrimary(Application application) {
        KLog.d("city module");
        return false;
    }

    @Override
    public boolean onInitSecondary(Application application) {
        KLog.d("city module");
        return false;
    }
}
