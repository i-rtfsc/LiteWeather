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

package com.journeyOS.widget.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class AqiView extends View {
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF = new RectF();
    private AqiData aqiData;

    public AqiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint.setTextAlign(Align.CENTER);
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float w = getWidth();
        final float h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        final float lineSize = h / 10f;//大约是12dp
        if (aqiData == null) {
            textPaint.setStyle(Style.FILL);
            textPaint.setTextSize(lineSize * 1.25f);
            textPaint.setColor(0xaaffffff);
            canvas.drawText("暂无数据", w / 2f, h / 2f, textPaint);
            return;
        }
        float currAqiPercent = -1f;
        try {
            currAqiPercent = Float.valueOf(aqiData.aqi) / 500f;//污染%
            currAqiPercent = Math.min(currAqiPercent, 1f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // canvas.drawColor(0x33ffffff);

        float aqiArcRadius = lineSize * 4f;
        textPaint.setStyle(Style.STROKE);
        textPaint.setStrokeWidth(lineSize * 1);
        textPaint.setColor(0x55ffffff);
        rectF.set(-aqiArcRadius, -aqiArcRadius, aqiArcRadius, aqiArcRadius);
        final int saveCount = canvas.save();
        canvas.translate(w / 2f, h / 2f);
        // draw aqi restPercent arc
        final float startAngle = -210f;
        final float sweepAngle = 240f;
        canvas.drawArc(rectF, startAngle + sweepAngle * currAqiPercent, sweepAngle * (1f - currAqiPercent), false,
                textPaint);
        if (currAqiPercent >= 0f) {
            // draw aqi aqiPercent arc
            textPaint.setColor(0x99ffffff);
            canvas.drawArc(rectF, startAngle, sweepAngle * currAqiPercent, false, textPaint);
            // draw aqi arc center circle
            textPaint.setColor(0xffffffff);
            textPaint.setStrokeWidth(lineSize / 8f);
            canvas.drawCircle(0, 0, lineSize / 3f, textPaint);
            // draw aqi number and text
            textPaint.setStyle(Style.FILL);
            textPaint.setTextSize(lineSize * 1.5f);
            textPaint.setColor(0xffffffff);
            try {
                canvas.drawText(aqiData.aqi + "", 0, lineSize * 3, textPaint);
            } catch (Exception e) {
            }
            textPaint.setTextSize(lineSize * 1f);
            textPaint.setColor(0x88ffffff);
            try {
                canvas.drawText(aqiData.quality + "", 0, lineSize * 4.25f, textPaint);
            } catch (Exception e) {
            }

            // draw the aqi line
            canvas.rotate(startAngle + sweepAngle * currAqiPercent - 180f);
            textPaint.setStyle(Style.STROKE);
            textPaint.setColor(0xffffffff);
            float startX = lineSize / 3f;
            canvas.drawLine(-startX, 0, -lineSize * 4.5f, 0, textPaint);
        }
        canvas.restoreToCount(saveCount);
    }

    public void setAqiData(AqiData aqi) {
        if (aqi != null) {
            this.aqiData = aqi;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public static class AqiData {
        public String aqi;
        public String pm10;
        public String pm25;
        public String so2;
        public String no2;
        public String quality;
    }
}
