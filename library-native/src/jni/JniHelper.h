/*
 * Copyright (c) 2021 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef _JNI_HELPER_H
#define _JNI_HELPER_H

//对应的java包名
static const char *native_library_class_path_name = "com/journeyOS/jni/JniHelper";

jobjectArray jni_native_get_weather_key(JNIEnv *env, jclass thiz);

jobject jni_native_get_weather_keys(JNIEnv *env, jclass thiz);

//jobject jni_native_get_weather_key(JNIEnv *env, jclass thiz);

/**
 * 有部分同学说看了文档还是不知道signature改怎么写
 * 很简单javap -s JniHelper.class即可，信息如下：
 * Compiled from "JniHelper.java"
 *  public class com.journeyOS.jni.JniHelper {
 *      public com.journeyOS.jni.JniHelper();
 *          descriptor: ()V
 *
 *      public static native java.lang.String[] getWeatherKeys();
 *          descriptor: ()[Ljava/lang/String;
 *
 *      public static native java.util.List<com.journeyOS.jni.WeatherKey> getNativeWeatherKeys();
 *          descriptor: ()Ljava/util/List;
 *
 *      static {};
 *          descriptor: ()V
 *
 */
static JNINativeMethod native_library_methods[] = {
        //name：Java中函数的名字
        //signature：描述了函数的参数和返回值（Java类型跟C类型对应表可自行网上查询）
        //fnPtr：C/C++的函数名
        {"getWeatherKeys",       "()[Ljava/lang/String;", (void *) jni_native_get_weather_key},
        {"getNativeWeatherKeys", "()Ljava/util/List;",    (void *) jni_native_get_weather_keys},
//        {"getNativeWeatherKey", "()Lcom/journeyOS/jni/WeatherKey;",    (void *) jni_native_get_weather_key},
};

#endif //_JNI_HELPER_H
