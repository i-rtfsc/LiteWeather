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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import java.util.ArrayList;

/**
 * 雨
 */
public class RainSky extends BaseSky {
    private static final String TAG = RainSky.class.getSimpleName();
    private Context mContext;

    public enum RainLevel {
        LIGHT, MEDIUM, HEAVY, VERY_HEAVY;
    }

    private RainDrawable drawable;
    private ArrayList<RainHolder> holders = new ArrayList<RainHolder>();

    private final int cfg_count = 50;

    public RainSky(Context context, boolean isNight) {
        super(context, isNight);
        mContext = context;
        drawable = new RainDrawable();
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (RainHolder holder : holders) {
            holder.updateRandom(drawable, alpha);
            drawable.draw(canvas);
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.holders.size() == 0) {
            final float rainWidth = SkyUIUilts.dp2px(mContext, 2);//*(1f -  getDownRandFloat(0, 1));
            final float minRainHeight = SkyUIUilts.dp2px(mContext, 8);
            final float maxRainHeight = SkyUIUilts.dp2px(mContext, 14);
            final float speed = SkyUIUilts.dp2px(mContext, 400);
            for (int i = 0; i < cfg_count; i++) {
                float x = SkyUilts.getRandom(0f, width);
                RainHolder holder = new RainHolder(x, rainWidth, minRainHeight, maxRainHeight, height, speed);
                holders.add(holder);
            }
        }
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.RAIN_N : ResourceSky.SkyBackground.RAIN_D;
    }


    public static final float acceleration = 9.8f;

    public static class RainHolder {
        public float x;
        //        public float y;//y 表示雨滴底部的y坐标,由curTime求得
        public final float rainWidth;
        public final float rainHeight;
        public final float maxY;// [0,1]
        public float curTime;// [0,1]
        public final int rainColor;
        public final float v;//速度
//		public boolean alphaIsGrowing = true;

        /**
         * @param x             雨滴中心的x坐标
         * @param rainWidth     雨滴宽度
         * @param maxRainHeight 最大的雨滴长度
         * @param maxY          屏幕高度
         */
        public RainHolder(float x, float rainWidth, float minRainHeight, float maxRainHeight, float maxY, float speed) {
            super();
            this.x = x;
            this.rainWidth = rainWidth;
            this.rainHeight = SkyUilts.getRandom(minRainHeight, maxRainHeight);
            this.rainColor = Color.argb((int) (SkyUilts.getRandom(0.1f, 0.5f) * 255f), 0xff, 0xff, 0xff);
            this.maxY = maxY;
            this.v = speed * SkyUilts.getRandom(0.9f, 1.1f);
            final float maxTime = maxY / this.v;//  (float) Math.sqrt(2f * maxY / acceleration);//s = 0.5*a*t^2;
            this.curTime = SkyUilts.getRandom(0, maxTime);
        }

        public void updateRandom(RainDrawable drawable, float alpha) {
            curTime += 0.025f;
//			float curY = v0 * curTime + 0.5f * acceleration * curTime * curTime;
            float curY = curTime * this.v;
            if ((curY - this.rainHeight) > this.maxY) {
                this.curTime = 0f;
            }
            drawable.setColor(Color.argb((int) (Color.alpha(rainColor) * alpha), 0xff, 0xff, 0xff));
            drawable.setStrokeWidth(rainWidth);
            drawable.setLocation(x, curY, rainHeight);
        }
    }

    public static class RainDrawable {
        public float x, y;
        public float length;
        public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public RainDrawable() {
            super();
            paint.setStyle(Style.STROKE);
//			paint.setStrokeJoin(Paint.Join.ROUND);
//			paint.setStrokeCap(Paint.Cap.ROUND);
        }

        public void setColor(int color) {
            this.paint.setColor(color);
        }

        public void setStrokeWidth(float strokeWidth) {
            this.paint.setStrokeWidth(strokeWidth);
        }

        public void setLocation(float x, float y, float length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }

        public void draw(Canvas canvas) {
            canvas.drawLine(x, y - length, x, y, paint);
        }
    }
}
