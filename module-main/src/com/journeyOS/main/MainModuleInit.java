package com.journeyOS.main;

import android.app.Application;

import com.journeyOS.base.base.IModuleInit;
import com.journeyOS.liteframework.utils.KLog;

public class MainModuleInit implements IModuleInit {
    @Override
    public boolean onInitPrimary(Application application) {
        KLog.d("main module");
        return false;
    }

    @Override
    public boolean onInitSecondary(Application application) {
        KLog.d("main module");
        return false;
    }
}
