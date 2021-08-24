package com.journeyOS.jni;

/*
 * 不参与混淆，否则JNI代码找不到造成crash
 */
public class WeatherKey {
    private String owner;
    private String key;

    public WeatherKey(String owner, String key) {
        this.owner = owner;
        this.key = key;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
