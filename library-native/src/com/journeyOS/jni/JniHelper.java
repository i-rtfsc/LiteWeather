package com.journeyOS.jni;

public class JniHelper {
    static {
        System.loadLibrary("native_weather");
    }

    public static native String[] getWeatherKeys();

}
