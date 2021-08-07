package com.journeyOS.setting;

import android.app.Application;

import com.journeyOS.base.base.IModuleInit;
import com.journeyOS.liteframework.utils.KLog;

public class SettingModuleInit implements IModuleInit {
    @Override
    public boolean onInitPrimary(Application application) {
        KLog.d("setting module");
        return false;
    }

    @Override
    public boolean onInitSecondary(Application application) {
        KLog.d("setting module");
        return false;
    }
}
