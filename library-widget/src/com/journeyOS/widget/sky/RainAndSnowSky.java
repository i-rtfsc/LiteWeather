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
import android.graphics.drawable.GradientDrawable;

import java.util.ArrayList;

import static com.journeyOS.widget.sky.RainSky.RainDrawable;
import static com.journeyOS.widget.sky.RainSky.RainHolder;
import static com.journeyOS.widget.sky.SnowSky.SnowHolder;


/**
 * 雨夹雪
 */
public class RainAndSnowSky extends BaseSky {
    private static final String TAG = RainAndSnowSky.class.getSimpleName();
    private Context mContext;
    private GradientDrawable snowDrawable;
    private RainDrawable rainDrawable;
    private ArrayList<SnowHolder> snowHolders = new ArrayList<SnowHolder>();
    private ArrayList<RainHolder> rainHolders = new ArrayList<RainHolder>();

    private static final int SNOW_COUNT = 15;
    private static final int RAIN_COUNT = 30;
    private static final float MIN_SIZE = 6f;// dp
    private static final float MAX_SIZE = 14f;// dp

    public RainAndSnowSky(Context context, boolean isNight) {
        super(context, isNight);
        mContext = context;
        snowDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{0x88ffffff, 0x33ffffff});
        snowDrawable.setShape(GradientDrawable.OVAL);
        snowDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        rainDrawable = new RainDrawable();
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (SnowHolder holder : snowHolders) {
            holder.updateRandom(snowDrawable, alpha);
            snowDrawable.draw(canvas);
        }
        for (RainHolder holder : rainHolders) {
            holder.updateRandom(rainDrawable, alpha);
            rainDrawable.draw(canvas);
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.snowHolders.size() == 0) {
            final float minSize = SkyUIUilts.dp2px(mContext, MIN_SIZE);
            final float maxSize = SkyUIUilts.dp2px(mContext, MAX_SIZE);
            final float speed = SkyUIUilts.dp2px(mContext, 200);// 40当作中雪
            for (int i = 0; i < SNOW_COUNT; i++) {
                final float size = SkyUilts.getRandom(minSize, maxSize);
                SnowHolder holder = new SnowHolder(SkyUilts.getRandom(0, width), size, height, speed);
                snowHolders.add(holder);
            }
        }
        if (this.rainHolders.size() == 0) {
            final float rainWidth = SkyUIUilts.dp2px(mContext, 2);//*(1f -  getDownRandFloat(0, 1));
            final float minRainHeight = SkyUIUilts.dp2px(mContext, 8);
            final float maxRainHeight = SkyUIUilts.dp2px(mContext, 14);
            final float speed = SkyUIUilts.dp2px(mContext, 360);
            for (int i = 0; i < RAIN_COUNT; i++) {
                float x = SkyUilts.getRandom(0f, width);
                RainHolder holder = new RainHolder(x, rainWidth, minRainHeight, maxRainHeight, height, speed);
                rainHolders.add(holder);
            }
        }
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.RAIN_N : ResourceSky.SkyBackground.RAIN_D;
    }
}
