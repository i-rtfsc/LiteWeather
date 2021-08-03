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


public abstract class BaseSky {
    private static final String TAG = BaseSky.class.getSimpleName();
    protected Context context;
    private final float desity;
    protected int width, height;
    private GradientDrawable skyDrawable;
    protected final boolean isNight;

    public BaseSky(Context context, boolean isNight) {
        super();
        this.context = context;
        this.desity = context.getResources().getDisplayMetrics().density;
        this.isNight = isNight;
    }

    public GradientDrawable makeSkyBackground() {
        return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, getSkyBackgroundGradient());
    }

    protected void drawSkyBackground(Canvas canvas, float alpha) {
        if (skyDrawable == null) {
            skyDrawable = makeSkyBackground();
            skyDrawable.setBounds(0, 0, width, height);
        }
        skyDrawable.setAlpha(Math.round(alpha * 255f));
        skyDrawable.draw(canvas);
    }

    /**
     * @param canvas
     * @return needDrawNextFrame
     */
    public boolean draw(Canvas canvas, float alpha) {
        drawSkyBackground(canvas, alpha);
        //long start = AnimationUtils.currentAnimationTimeMillis();
        boolean needDrawNextFrame = drawWeather(canvas, alpha);
//		if (needDrawNextFrame) {
//			curPercent += getFrameOffsetPercent();
//			if (curPercent > 1) {
//				curPercent = 0f;
//			}
//		}
//         LogUtils.i(TAG, getClass().getSimpleName() + " drawWeather: "
//         + (AnimationUtils.currentAnimationTimeMillis() - start) + "ms");
        return needDrawNextFrame;
    }

    public abstract boolean drawWeather(Canvas canvas, float alpha);

    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.CLEAR_N : ResourceSky.SkyBackground.CLEAR_D;
    }

    protected void setSize(int width, int height) {
        if (this.width != width && this.height != height) {
            this.width = width;
            this.height = height;
            if (this.skyDrawable != null) {
                skyDrawable.setBounds(0, 0, width, height);
            }
        }

    }

}
