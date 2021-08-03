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

package com.journeyOS.liteweather.rx;

import android.util.Log;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class RxManager {
    private static final String TAG = RxManager.class.getSimpleName();

    public static <T> Single<T> createAsyncSingle(final Callable<T> func) {
        return Single.create(new SingleOnSubscribe<T>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull SingleEmitter<T> emitter) throws Exception {
                try {
                    T result = func.call();
                    if (result != null) {
                        emitter.onSuccess(result);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "error = " + ex);
                }
            }
        });
    }
}
