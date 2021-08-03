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
import android.graphics.RectF;

import java.util.ArrayList;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Style;


public class WindSky extends BaseSky {
    private Context mContext;
    private Paint paint = new Paint(ANTI_ALIAS_FLAG);
    final int count = 30;
    private ArrayList<ArcHolder> holders = new ArrayList<ArcHolder>();


    public WindSky(Context context, boolean isNight) {
        super(context, isNight);
        mContext = context;
        paint.setStyle(Style.STROKE);
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.holders.size() == 0) {
            final float cx = -width * 0.3f;
            final float cy = -width * 1.5f;
            for (int i = 0; i < count; i++) {
                float radiusWidth = SkyUilts.getRandom(width * 1.3f, width * 3.0f);
                float radiusHeight = radiusWidth * SkyUilts.getRandom(0.92f, 0.96f);//getRandom(width * 0.02f,  width * 1.6f);
                float strokeWidth = SkyUIUilts.dp2px(mContext, SkyUilts.getDownRandFloat(1f, 2.5f));
                float sizeDegree = SkyUilts.getDownRandFloat(8f, 15f);
                this.holders.add(new ArcHolder(cx, cy, radiusWidth, radiusHeight, strokeWidth, 30f, 99f, sizeDegree,
                        isNight ? 0x33ffffff : 0x66ffffff));
            }
        }

    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        for (ArcHolder holder : this.holders) {
            holder.updateAndDraw(canvas, paint, alpha);
        }
        return true;
    }

    public static class ArcHolder {
        private final float cx, cy, radiusWidth, radiusHeight, strokeWidth, fromDegree, endDegree, sizeDegree;
        private final int color;
        private float curDegree;
        private final float stepDegree;
        private RectF rectF = new RectF();

        public ArcHolder(float cx, float cy, float radiusWidth, float radiusHeight, float strokeWidth, float fromDegree, float endDegree,
                         float sizeDegree, int color) {
            super();
            this.cx = cx;
            this.cy = cy;
            this.radiusWidth = radiusWidth;
            this.radiusHeight = radiusHeight;
            this.strokeWidth = strokeWidth;
            this.fromDegree = fromDegree;
            this.endDegree = endDegree;
            this.sizeDegree = sizeDegree;
            this.color = color;
            this.curDegree = SkyUilts.getRandom(fromDegree, endDegree);
            this.stepDegree = SkyUilts.getRandom(0.5f, 0.9f);
        }

        public void updateAndDraw(Canvas canvas, Paint paint, float alpha) {
            paint.setColor(SkyUIUilts.convertAlphaColor(alpha * (Color.alpha(color) / 255f), color));
            paint.setStrokeWidth(strokeWidth);
            curDegree += stepDegree * SkyUilts.getRandom(0.8f, 1.2f);
            if (curDegree > (endDegree - sizeDegree)) {
                curDegree = fromDegree - sizeDegree;
            }
            float startAngle = curDegree;
            float sweepAngle = sizeDegree;
            rectF.left = cx - radiusWidth;
            rectF.top = cy - radiusHeight;
            rectF.right = cx + radiusWidth;
            rectF.bottom = cy + radiusHeight;
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
        }
    }

    @Override
    protected int[] getSkyBackgroundGradient() {
        return isNight ? ResourceSky.SkyBackground.RAIN_N : ResourceSky.SkyBackground.RAIN_D;
    }
}
