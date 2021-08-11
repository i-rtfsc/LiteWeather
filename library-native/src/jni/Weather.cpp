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


jobjectArray jni_native_get_weather_key(JNIEnv *env, jclass thiz) {
    jobjectArray ret;

    const char *keys[5] = {"8aeec77017724b518a5f0ba5d1888820",
                           "7e0c26e74f384de59efb7a86565a1c0f",
                           "def9a507328e4cd395d983fe2589586e",
                           "537664b7e2124b3c845bc0b51278d4af",
                           "bc0418b57b2d4918819d3974ac1285d9"};

    ret = (jobjectArray) env->NewObjectArray(5,
                                             env->FindClass("java/lang/String"),
                                             env->NewStringUTF(""));

    for (int i = 0; i < 5; i++) {
        if (DEBUG) {
            LOGI("weather key %s\n", keys[i]);
        }
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(keys[i]));
    }

    return (ret);
}


