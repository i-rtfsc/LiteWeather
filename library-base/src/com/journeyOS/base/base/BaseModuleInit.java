package com.journeyOS.base.base;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.journeyOS.liteframework.BuildConfig;
import com.journeyOS.liteframework.utils.KLog;


/**
 * 基础库自身初始化操作
 */

public class BaseModuleInit implements IModuleInit {
    @Override
    public boolean onInitPrimary(Application application) {
        //开启打印日志
        KLog.init(true);
        KLog.d("base module");
//        //初始化阿里路由框架
//        if (BuildConfig.DEBUG) {
//            ARouter.openLog();     // 打印日志
//            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        }
//        ARouter.init(application); // 尽可能早，推荐在Application中初始化
        return false;
    }

    @Override
    public boolean onInitSecondary(Application application) {
        KLog.d("base module");
        return false;
    }
}
