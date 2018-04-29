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
import android.util.Log;

import com.journeyOS.liteweather.utils.SkyUIUilts;
import com.journeyOS.liteweather.utils.SkyUilts;

import java.util.ArrayList;

public class StarSkyDark extends BaseSky {
    private Context mContext;
    private GradientDrawable drawable;
    private ArrayList<StarHolder> holders = new ArrayList<StarHolder>();
    private final float minDX, maxDX, minDY, maxDY;

    public StarSkyDark(Context context, int type) {
        super(context, true);
        mContext = context;
        drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{0xffffffff, 0x00ffffff});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setGradientRadius((float) (Math.sqrt(2) * 60));
        minDX = 0f;
        maxDX = SkyUIUilts.dp2px(mContext, 3);
        minDY = -SkyUIUilts.dp2px(mContext, 2);
        maxDY = SkyUIUilts.dp2px(mContext, 2);
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (StarHolder holder : holders) {
            holder.updateRandom(drawable, minDX, maxDX, minDY, maxDY, 0, 0, this.width, this.height, alpha);
//			drawable.setBounds(0, 0, 360, 360);
//			drawable.setGradientRadius(360/2.2f);//测试出来2.2比较逼真
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
            final float starMinSize = SkyUIUilts.dp2px(mContext, 0.5f);
            final float starMaxSize = SkyUIUilts.dp2px(mContext, 44f);
            for (int i = 0; i < 100; i++) {
                final float starSize = SkyUilts.getRandom(starMinSize, starMaxSize);
                StarHolder holder = new StarHolder(SkyUilts.getRandom(0, width), SkyUilts.getRandom(0, height), starSize, starSize);
                holders.add(holder);
            }
//			holders.add(new StarHolder(360, 360, 200, 200));
        }
    }


    public static class StarHolder {
        public float x;
        public float y;
        public float w;
        public float h;

        public StarHolder(float x, float y, float w, float h) {
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
            this.x += SkyUilts.getRandom(minDX, maxDX);
            this.y += SkyUilts.getRandom(minDY, maxDY);
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
            drawable.setBounds(left, top, right, bottom);
            drawable.setGradientRadius(w / 2.2f);
        }
    }
}
