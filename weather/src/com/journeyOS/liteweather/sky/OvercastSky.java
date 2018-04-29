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

package com.journeyOS.liteweather.sky;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import static com.journeyOS.liteweather.sky.CloudySky.CircleHolder;

/**
 * 阴天
 */
public class OvercastSky extends BaseSky {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final ArrayList<CircleHolder> holders = new ArrayList<CircleHolder>();

    public OvercastSky(Context context, boolean isNight) {
        super(context, isNight);

    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (holders.size() == 0) {
            holders.add(new CircleHolder(0.20f * width, -0.30f * width, 0.06f * width, 0.022f * width, 0.56f * width, 0.0015f, isNight ? 0x44374d5c : 0x4495a2ab));
            holders.add(new CircleHolder(0.59f * width, -0.35f * width, -0.18f * width, 0.032f * width, 0.6f * width, 0.00125f, isNight ? 0x55374d5c : 0x335a6c78));
            holders.add(new CircleHolder(0.9f * width, -0.18f * width, 0.08f * width, -0.015f * width, 0.42f * width, 0.0025f, isNight ? 0x5a374d5c : 0x556f8a8d));
        }
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (CircleHolder holder : this.holders) {
            holder.updateAndDraw(canvas, paint, alpha);
        }
        return true;
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.OVERCAST_N : ResourceSky.SkyBackground.OVERCAST_D;
    }
}
