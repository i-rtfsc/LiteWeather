package com.journeyOS.base.config;

/**
 * 组件生命周期反射类名管理，在这里注册需要初始化的组件，通过反射动态调用各个组件的初始化方法
 * 注意：以下模块中初始化的Module类不能被混淆
 */

public class ModuleLifecycleReflexs {
    private static final String BaseInit = "com.journeyOS.base.base.BaseModuleInit";
    //主业务模块
    private static final String MainInit = "com.journeyOS.main.MainModuleInit";
    //天气验证模块
    private static final String WeatherInit = "com.journeyOS.weather.WeatherModuleInit";
    //设置业务模块
    private static final String SettingInit = "com.journeyOS.setting.SettingModuleInit";
    //城市业务模块
    private static final String CityInit = "com.journeyOS.city.CityModuleInit";

    public static String[] initModuleNames = {BaseInit, MainInit, WeatherInit, SettingInit, CityInit};
}
