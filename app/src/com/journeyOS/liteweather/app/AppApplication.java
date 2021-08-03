package com.journeyOS.liteweather.app;

import com.journeyOS.liteframework.base.BaseApplication;
import com.journeyOS.liteframework.crash.CaocConfig;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.liteweather.BuildConfig;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.ui.weather.activity.TabBarActivity;


public class AppApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
        if (BuildConfig.BUILD_DEBUG) {
            if (!com.squareup.leakcanary.LeakCanary.isInAnalyzerProcess(this)) {
                com.squareup.leakcanary.LeakCanary.install(this);
            }
        }

    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.drawable.svg_weather_icon) //错误图标
                .restartActivity(TabBarActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }
}
