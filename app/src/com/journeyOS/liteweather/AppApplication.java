package com.journeyOS.liteweather;

import com.journeyOS.base.config.ModuleLifecycleConfig;
import com.journeyOS.liteframework.base.BaseApplication;


public class AppApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化组件(靠前)
        ModuleLifecycleConfig.getInstance().initModulePrimary(this);
        //....
        //初始化组件(靠后)
        ModuleLifecycleConfig.getInstance().initModuleSecondary(this);
    }

}
