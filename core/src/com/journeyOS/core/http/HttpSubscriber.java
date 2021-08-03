/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.core.http;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Response;

public class HttpSubscriber<T> implements Observer<Response<T>> {

    private HttpObserver<T> mObserver;

    public HttpSubscriber() {
    }

    public HttpSubscriber(HttpObserver<T> observer) {
        mObserver = observer;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull Response<T> tResponse) {
        if (mObserver != null) {
            mObserver.onSuccess(new HttpResponse<>(tResponse));
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (mObserver != null) {
            mObserver.onError(e);
        }
    }

    @Override
    public void onComplete() {

    }

//    @Override
//    public void onCompleted() {
//
//    }
//
//    @Override
//    public void onError(Throwable e) {
//        if (mObserver != null)
//            mObserver.onError(e);
//    }
//
//    @Override
//    public void onNext(Response<T> r) {
//        if (mObserver != null)
//            mObserver.onSuccess(new HttpResponse<>(r));
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
}

