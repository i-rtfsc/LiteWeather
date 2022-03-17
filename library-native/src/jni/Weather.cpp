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

#include <jni.h>

#include "JniLog.h"

#define KEY_NUM 5

jobjectArray jni_native_get_weather_key(JNIEnv *env, jclass thiz) {
    jobjectArray ret;

    const char *keys[KEY_NUM] = {
            "5f39e34b589c48b5bc8196040c8ebeb1",
            "2d849c62d67a4b9e94607d0f1c744561",
            "def9a507328e4cd395d983fe2589586e",
            "537664b7e2124b3c845bc0b51278d4af",
            "bc0418b57b2d4918819d3974ac1285d9"};

    ret = (jobjectArray) env->NewObjectArray(KEY_NUM,
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));

    for (int i = 0; i < KEY_NUM; i++) {
        if (DEBUG) {
            LOGI("weather key %s\n", keys[i]);
        }
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(keys[i]));
    }

    return (ret);
}

jobject jni_native_get_weather_keys(JNIEnv *env, jclass thiz) {
    //获取ArrayList类引用
    jclass list_jcs = env->FindClass("java/util/ArrayList");
    if (list_jcs == nullptr) {
        LOGE("ArrayList no find!");
        return nullptr;
    }

    //获取ArrayList构造函数id
    jmethodID list_init = env->GetMethodID(list_jcs, "<init>", "()V");
    //创建一个ArrayList对象
    jobject list_obj = env->NewObject(list_jcs, list_init, "");
    //获取ArrayList对象的add()的methodID
    jmethodID list_add = env->GetMethodID(list_jcs, "add",
                                          "(Ljava/lang/Object;)Z");
    //获取WeatherKey类
    //WeatherKey不参与混淆，否则JNI代码找不到造成crash
    jclass weather_cls = env->FindClass("com/journeyOS/jni/WeatherKey");
    //获取WeatherKey的构造函数
    jmethodID weather_init = env->GetMethodID(weather_cls, "<init>",
                                              "(Ljava/lang/String;Ljava/lang/String;)V");

    const char *owner[KEY_NUM] = {
            "Solo",
            "unknown",
            "unknown",
            "unknown",
            "unknown"};
    const char *keys[KEY_NUM] = {
            "5f39e34b589c48b5bc8196040c8ebeb1",
            "2d849c62d67a4b9e94607d0f1c744561",
            "def9a507328e4cd395d983fe2589586e",
            "537664b7e2124b3c845bc0b51278d4af",
            "bc0418b57b2d4918819d3974ac1285d9"};

    for (int i = 0; i < KEY_NUM; i++) {
        jobject weather_obj = env->NewObject(weather_cls, weather_init,
                                             env->NewStringUTF(owner[i]),
                                             env->NewStringUTF(keys[i]));
        env->CallBooleanMethod(list_obj, list_add, weather_obj);
    }

    return list_obj;
}

//jobject jni_native_get_weather_key(JNIEnv *env, jclass thiz) {
//    //获取WeatherKey类
//    jclass weather_cls = env->FindClass("com/journeyOS/jni/WeatherKey");
//    //获取WeatherKey的构造函数
//    jmethodID weather_init = env->GetMethodID(weather_cls, "<init>",
//                                              "(Ljava/lang/String;Ljava/lang/String;)V");
//
//    jobject weather_obj = env->NewObject(weather_cls, weather_init,
//                                         env->NewStringUTF("Solo"),
//                                         env->NewStringUTF("8aeec77017724b518a5f0ba5d1888820"));
//    return weather_obj;
//}

