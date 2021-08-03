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
import android.util.Log;

import java.util.ArrayList;

public class StarSky extends BaseSky {
    private static final String TAG = StarSky.class.getSimpleName();
    private Context mContext;
    private GradientDrawable drawable;
    private ArrayList<StarHolder> holders = new ArrayList<StarHolder>();

    private static final int STAR_COUNT = 80;
    private static final float STAR_MIN_SIZE = 2f;// dp
    private static final float STAR_MAX_SIZE = 6f;// dp

    public StarSky(Context context) {
        super(context, true);
        mContext = context;
        drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{0xffffffff, 0x00ffffff});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setGradientRadius((float) (Math.sqrt(2) * 60));
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (StarHolder holder : holders) {
            holder.updateRandom(drawable, alpha);
            // drawable.setBounds(0, 0, 360, 360);
            // drawable.setGradientRadius(360/2.2f);//测试出来2.2比较逼真
            try {
                drawable.draw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("FUCK", "drawable.draw(canvas)->" + drawable.getBounds().toShortString());
            }
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.holders.size() == 0) {
            final float starMinSize = SkyUIUilts.dp2px(mContext, STAR_MIN_SIZE);
            final float starMaxSize = SkyUIUilts.dp2px(mContext, STAR_MAX_SIZE);
            for (int i = 0; i < STAR_COUNT; i++) {
                final float starSize = SkyUilts.getRandom(starMinSize, starMaxSize);
                final float y = SkyUilts.getDownRandFloat(0, height);
                // 20%的上半部分屏幕最高alpha为1，其余的越靠下最高alpha越小
                final float maxAlpha = 0.2f + 0.8f * (1f - y / height);
                StarHolder holder = new StarHolder(SkyUilts.getRandom(0, width), y, starSize, starSize, maxAlpha);
                holders.add(holder);
            }
            // holders.add(new StarHolder(360, 360, 200, 200));
        }
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return ResourceSky.SkyBackground.CLEAR_N;
    }

    public static class StarHolder {
        public float x;
        public float y;
        public float w;
        public float h;
        public final float maxAlpha;// [0,1]
        public float curAlpha;// [0,1]
        public boolean alphaIsGrowing = true;

        public StarHolder(float x, float y, float w, float h, float maxAlpha) {
            super();
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.maxAlpha = maxAlpha;
            this.curAlpha = SkyUilts.getRandom(0f, maxAlpha);
        }

        public void updateRandom(GradientDrawable drawable, float alpha) {
            // curAlpha += getRandom(-0.01f, 0.01f);
            // curAlpha = Math.max(0f, Math.min(maxAlpha, curAlpha));
            final float delta = SkyUilts.getRandom(0.003f * maxAlpha, 0.012f * maxAlpha);
            if (alphaIsGrowing) {
                curAlpha += delta;
                if (curAlpha > maxAlpha) {
                    curAlpha = maxAlpha;
                    alphaIsGrowing = false;
                }
            } else {
                curAlpha -= delta;
                if (curAlpha < 0) {
                    curAlpha = 0;
                    alphaIsGrowing = true;
                }
            }

            final int left = Math.round(x - w / 2f);
            final int right = Math.round(x + w / 2f);
            final int top = Math.round(y - h / 2f);
            final int bottom = Math.round(y + h / 2f);
            drawable.setBounds(left, top, right, bottom);
            drawable.setGradientRadius(w / 2.2f);
            drawable.setAlpha((int) (255 * curAlpha * alpha));
        }
    }
}
