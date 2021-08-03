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

import com.journeyOS.liteframework.utils.Utils;

import java.util.ArrayList;

/**
 * 霾
 */
public class HazeSky extends BaseSky {
    private GradientDrawable drawable;
    private ArrayList<HazeHolder> holders = new ArrayList<HazeHolder>();
    private final float minDX, maxDX, minDY, maxDY;

    public HazeSky(Context context, boolean isNight) {
        super(context, isNight);
        drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                isNight ? new int[]{0x55d4ba3f, 0x22d4ba3f} : new int[]{0x88cca667, 0x33cca667});//d4ba3f
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        minDX = 0.04f;
        maxDX = 0.065f;//dp2px(1.5f);
        minDY = -0.02f;//-dp2px(0.5f);
        maxDY = 0.02f;//dp2px(0.5f);

    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (HazeHolder holder : holders) {
            holder.updateRandom(drawable, minDX, maxDX, minDY, maxDY, 0, 0, this.width, this.height, alpha);
//				drawable.setBounds(0, 0, 360, 360);
//				drawable.setGradientRadius(360/2.2f);//测试出来2.2比较逼真
            try {
                drawable.draw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.holders.size() == 0) {
            final float minSize = SkyUIUilts.dp2px(Utils.getContext(), 0.8f);
            final float maxSize = SkyUIUilts.dp2px(Utils.getContext(), 4.4f);
            for (int i = 0; i < 80; i++) {
                final float starSize = SkyUilts.getRandom(minSize, maxSize);
                HazeHolder holder = new HazeHolder(SkyUilts.getRandom(0, width), SkyUilts.getDownRandFloat(0, height), starSize, starSize);
                holders.add(holder);
            }
//			holders.add(new StarHolder(360, 360, 200, 200));
        }
    }


    public static class HazeHolder {
        public float x;
        public float y;
        public float w;
        public float h;

        public HazeHolder(float x, float y, float w, float h) {
            super();
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

        }

        public void updateRandom(GradientDrawable drawable, float minDX, float maxDX,
                                 float minDY, float maxDY, float minX, float minY, float maxX, float maxY, float alpha) {
            //alpha 还没用
            if (maxDX < minDX || (maxDY < minDY)) {
                throw new IllegalArgumentException("max should bigger than min!!!!");
            }
            this.x += (SkyUilts.getRandom(minDX, maxDX) * w);
            ;
            this.y += (SkyUilts.getRandom(minDY, maxDY) * h);
//			this.x = Math.min(maxX, Math.max(this.x, minX));
//			this.y = Math.min(maxY, Math.max(this.y, minY));
            if (x > maxX) {
                x = minX;
            } else if (x < minX) {
                x = maxX;
            }
            if (y > maxY) {
                y = minY;
            } else if (y < minY) {
                y = maxY;
            }

            final int left = Math.round(x - w / 2f);
            final int right = Math.round(x + w / 2f);
            final int top = Math.round(y - h / 2f);
            final int bottom = Math.round(y + h / 2f);
            drawable.setAlpha((int) (255f * alpha));
            drawable.setBounds(left, top, right, bottom);
            drawable.setGradientRadius(w / 2.2f);
        }
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.HAZE_N : ResourceSky.SkyBackground.HAZE_D;
    }
}
