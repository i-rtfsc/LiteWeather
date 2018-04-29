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
import android.graphics.drawable.GradientDrawable;

import com.journeyOS.liteweather.utils.SkyUIUilts;
import com.journeyOS.liteweather.utils.SkyUilts;

import java.util.ArrayList;

/**
 * 雪
 */
public class SnowSky extends BaseSky {
    private static final String TAG = SnowSky.class.getSimpleName();
    private Context mContext;

    private GradientDrawable drawable;
    private ArrayList<SnowHolder> holders = new ArrayList<SnowHolder>();

    private static final int COUNT = 30;
    private static final float MIN_SIZE = 12f;// dp
    private static final float MAX_SIZE = 30f;// dp

    public SnowSky(Context context, boolean isNight) {
        super(context, isNight);
        mContext = context;
        drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{0x99ffffff, 0x00ffffff});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (SnowHolder holder : holders) {
            holder.updateRandom(drawable, alpha);
            drawable.draw(canvas);
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.holders.size() == 0) {
            final float minSize = SkyUIUilts.dp2px(mContext, MIN_SIZE);
            final float maxSize = SkyUIUilts.dp2px(mContext, MAX_SIZE);
            final float speed = SkyUIUilts.dp2px(mContext, 80);// 40当作中雪80
            for (int i = 0; i < COUNT; i++) {
                final float size = SkyUilts.getRandom(minSize, maxSize);
                SnowHolder holder = new SnowHolder(SkyUilts.getRandom(0, width), size, height, speed);
                holders.add(holder);
            }
        }
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.SNOW_N : ResourceSky.SkyBackground.SNOW_D;
    }

    public static class SnowHolder {
        public float x;
        // public float y;//y 表示雨滴底部的y坐标,由curTime求得
        public final float snowSize;
        public final float maxY;// [0,1]
        public float curTime;// [0,1]
        public final float v;// 速度

        /**
         * @param x
         * @param snowSize
         * @param maxY
         * @param averageSpeed
         */
        public SnowHolder(float x, float snowSize, float maxY, float averageSpeed) {
            super();
            this.x = x;
            this.snowSize = snowSize;
            this.maxY = maxY;
            this.v = averageSpeed * SkyUilts.getRandom(0.85f, 1.15f);
            final float maxTime = maxY / this.v;
            this.curTime = SkyUilts.getRandom(0, maxTime);
        }

        public void updateRandom(GradientDrawable drawable, float alpha) {
            curTime += 0.025f;
            float curY = curTime * this.v;
            if ((curY - this.snowSize) > this.maxY) {
                this.curTime = 0f;
            }
            final int left = Math.round(x - snowSize / 2f);
            final int right = Math.round(x + snowSize / 2f);
            final int top = Math.round(curY - snowSize);
            final int bottom = Math.round(curY);
            drawable.setBounds(left, top, right, bottom);
            drawable.setGradientRadius(snowSize / 2.2f);
            drawable.setAlpha((int) (255 * alpha));
        }
    }
}
