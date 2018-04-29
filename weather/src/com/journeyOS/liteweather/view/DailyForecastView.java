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

package com.journeyOS.liteweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.liteweather.R;

import java.util.List;

/**
 * 一周天气预报
 * 按文字算高度18行
 * 文字设置为12，高度是216dp
 */
public class DailyForecastView extends View {

    private int width, height;
    private float percent = 0f;
    ;
    private final float density;
//    private List<WeatherData.DailyEntity> forecastList;
    private Path tmpMaxPath = new Path();
    private Path tmpMinPath = new Path();
//    private Data[] datas;
    private List<DailyData> dailyDataList;

    private final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    public static class DailyData {
        public float minOffsetPercent, maxOffsetPercent;//差值%
        public int Tmax, Tmin;
        public String date;
        public String pop;
        public String weather;
    }

    public DailyForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        if (isInEditMode()) {
            return;
        }
        init(context);
    }

    public void resetAnimation() {
        percent = 0f;
        invalidate();
    }

    private void init(Context context) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1f * density);
        paint.setTextSize(12f * density);
        paint.setStyle(Style.FILL);
        paint.setTextAlign(Align.CENTER);
    }

    //220dp 18hang
    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        paint.setStyle(Style.FILL);
        //一共需要 顶部文字2(+图占8行)+底部文字2 + 【间距1 + 日期1 + 间距0.5 +　晴1 + 间距0.5f + 微风1 + 底部边距1f 】 = 18行
        //                                  12     13       14      14.5    15.5      16      17       18
        final float textSize = this.height / 18f;
        paint.setTextSize(textSize);
        final float textOffset = getTextPaintOffset(paint);
        final float dH = textSize * 8f;
        final float dCenterY = textSize * 6f;
        if (dailyDataList == null || dailyDataList.size() <= 1) {
            canvas.drawLine(0, dCenterY, this.width, dCenterY, paint);//没有数据的情况下只画一条线
            return;
        }

        final float dW = this.width * 1f / dailyDataList.size();
        tmpMaxPath.reset();
        tmpMinPath.reset();
        final int length = dailyDataList.size();
        float[] x = new float[length];
        float[] yMax = new float[length];
        float[] yMin = new float[length];

        final float textPercent = (percent >= 0.6f) ? ((percent - 0.6f) / 0.4f) : 0f;
        final float pathPercent = (percent >= 0.6f) ? 1f : (percent / 0.6f);

        //画底部的三行文字和标注最高最低温度
        paint.setAlpha((int) (255 * textPercent));
        for (int i = 0; i < length; i++) {
            final DailyData d = dailyDataList.get(i);
            x[i] = i * dW + dW / 2f;
            ;
            yMax[i] = dCenterY - d.maxOffsetPercent * dH;
            yMin[i] = dCenterY - d.minOffsetPercent * dH;

            canvas.drawText(d.Tmax + getContext().getString(R.string.unit_t), x[i], yMax[i] - textSize + textOffset, paint);
            canvas.drawText(d.Tmin + getContext().getString(R.string.unit_t), x[i], yMin[i] + textSize + textOffset, paint);
            canvas.drawText(d.weather + "", x[i], textSize * 13.5f + textOffset, paint);
            canvas.drawText(d.pop + getContext().getString(R.string.unit_percent), x[i], textSize * 15f + textOffset, paint);
            canvas.drawText(TimeUtils.prettyDate(d.date), x[i], textSize * 16.5f + textOffset, paint);
        }
        paint.setAlpha(255);

        for (int i = 0; i < (length - 1); i++) {
            final float midX = (x[i] + x[i + 1]) / 2f;
            final float midYMax = (yMax[i] + yMax[i + 1]) / 2f;
            final float midYMin = (yMin[i] + yMin[i + 1]) / 2f;
            if (i == 0) {
                tmpMaxPath.moveTo(0, yMax[i]);
                tmpMinPath.moveTo(0, yMin[i]);
            }
            tmpMaxPath.cubicTo(x[i] - 1, yMax[i], x[i], yMax[i], midX, midYMax);
//			tmpMaxPath.quadTo(x[i], yMax[i], midX, midYMax);
            tmpMinPath.cubicTo(x[i] - 1, yMin[i], x[i], yMin[i], midX, midYMin);
//			tmpMinPath.quadTo(x[i], yMin[i], midX, midYMin);

            if (i == (length - 2)) {
                tmpMaxPath.cubicTo(x[i + 1] - 1, yMax[i + 1], x[i + 1], yMax[i + 1], this.width, yMax[i + 1]);
                tmpMinPath.cubicTo(x[i + 1] - 1, yMin[i + 1], x[i + 1], yMin[i + 1], this.width, yMin[i + 1]);
            }
        }
        //draw max_tmp and min_tmp path
        paint.setStyle(Style.STROKE);
        final boolean needClip = pathPercent < 1f;
        if (needClip) {
            canvas.save();
            canvas.clipRect(0, 0, this.width * pathPercent, this.height);
//            canvas.drawColor(0x66ffffff);
        }
        canvas.drawPath(tmpMaxPath, paint);
        canvas.drawPath(tmpMinPath, paint);
        if (needClip) {
            canvas.restore();
        }
        if (percent < 1) {
            percent += 0.025f;// 0.025f;
            percent = Math.min(percent, 1f);
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    public void setDailyData(List<DailyData> data) {
        if (data != null) {
            dailyDataList = data;
        }
        if (dailyDataList != null) {
            percent = 0f;
            invalidate();
        }
        return;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }

    public static float getTextPaintOffset(Paint paint) {
        FontMetrics fontMetrics = paint.getFontMetrics();
        return -(fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.top;
    }

}
