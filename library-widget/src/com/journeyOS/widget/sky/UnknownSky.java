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

package com.journeyOS.widget.sky;

import android.content.Context;
import android.graphics.Canvas;

public class UnknownSky extends BaseSky {
    public UnknownSky(Context context, boolean isNight) {
        super(context, isNight);
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        return true;//这里返回false会出现有时候不刷新的问题
    }
}
