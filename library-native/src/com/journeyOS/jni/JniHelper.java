package com.journeyOS.jni;

import java.util.List;

public class JniHelper {
    static {
        System.loadLibrary("native_weather");
    }

    public static native String[] getWeatherKeys();

    public static native List<WeatherKey> getNativeWeatherKeys();

//    public static native WeatherKey getNativeWeatherKey();

}
