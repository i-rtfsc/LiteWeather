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
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.liteweather.R;

import java.util.List;

import static com.journeyOS.base.Constant.ENABLE_CIRCLE;

/**
 * 一天24h预报
 * 12行
 * 12 * 12 = 144dp
 */
public class HourlyForecastView extends View {

    private int width, height;
    private final float density;
    private List<HourlyData> hourlyDataList;
    private Path tmpPath = new Path();
    private Path goneTmpPath = new Path();
    private final int full_data_count = 9;//理论上有8个数据（从1：00到22:00每隔3小时共8个数据）(添加第一列显示行的名称)，但是api只会返回现在的时间之后的
    private final DashPathEffect dashPathEffect;

    private final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);


    public HourlyForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        dashPathEffect = new DashPathEffect(new float[]{density * 3, density * 3}, 1);
        if (isInEditMode()) {
            return;
        }
        init(context);
    }

    private void init(Context context) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1f * density);
        paint.setTextSize(12f * density);
        paint.setStyle(Style.FILL);
        paint.setTextAlign(Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }

        paint.setStyle(Style.FILL);

        // 一共需要 顶部文字2(+图占4行)+底部文字0 + 【间距1 + 日期1 + 间距0.5 +　晴1 + 间距0.5f + 微风1 +
        // 底部边距1f 】 = 12行
        // 12 13 14 14.5 15.5 16 17 18 都-6
        final float textSize = this.height / 12f;
        paint.setTextSize(textSize);
        final float textOffset = getTextPaintOffset(paint);
        final float dH = textSize * 4f;
        final float dCenterY = textSize * 4f;
        if (hourlyDataList == null || hourlyDataList.size() <= 1) {
            canvas.drawLine(0, dCenterY, this.width, dCenterY, paint);// 没有数据的情况下只画一条线
            return;
        }
        final float dW = this.width * 1f / full_data_count;//datas.length;

        tmpPath.reset();
        goneTmpPath.reset();
        final int length = hourlyDataList.size();
        float[] x = new float[length];
        float[] y = new float[length];

        final float textPercent = 1f;//(percent >= 0.6f) ? ((percent - 0.6f) / 0.4f) : 0f;
        final float pathPercent = 1f;//(percent >= 0.6f) ? 1f : (percent / 0.6f);

        final float smallerHeight = 4 * textSize;
        final float smallerPercent = 1 - smallerHeight / 2f / dH;
        paint.setAlpha((int) (255 * textPercent));
        final int data_length_offset = Math.max(0, full_data_count - length);
        for (int i = 0; i < length; i++) {
            final HourlyData d = hourlyDataList.get(i);
            final int index = i + data_length_offset;
            x[i] = index * dW + dW / 2f;
            y[i] = dCenterY - d.offsetPercent * dH * smallerPercent;

            boolean isCircle = SpUtils.getInstant().getBoolean(ENABLE_CIRCLE, false);
            if (isCircle) {
                int CircleMaxY = 0;
                int CircleMaxYPre = 0;
                if (length >= 2 && i >= 1) {
                    int preT = hourlyDataList.get(i - 1).temperature;
                    int nowT = hourlyDataList.get(i).temperature;
                    if ((preT - nowT) > 3) {
                        CircleMaxY = 7 * Math.abs(preT - nowT) / 3;
                    } else if (-(preT - nowT) > 3) {
                        CircleMaxY = -7 * Math.abs(preT - nowT) / 3;
                    }
                    if (length > i + 1) {
                        int afterT = hourlyDataList.get(i + 1).temperature;
                        if ((nowT - afterT) > 3) {
                            CircleMaxYPre = -4 * Math.abs(nowT - afterT) / 3;
                        } else if (-(nowT - afterT) > 3) {
                            CircleMaxYPre = 4 * Math.abs(nowT - afterT) / 3;
                        }
                    }
                }
                // circle
                canvas.drawCircle(x[i], y[i] - CircleMaxYPre - CircleMaxY, 10, paint);
                // draw the froecast data'text
                canvas.drawText(d.temperature + getContext().getResources().getString(R.string.unit_t), x[i], y[i] - textSize + textOffset + (CircleMaxY > 0 || CircleMaxYPre > 0 ? -16 : 0), paint);
            } else {
                // draw the froecast data'text
                canvas.drawText(d.temperature + getContext().getResources().getString(R.string.unit_t), x[i], y[i] - textSize + textOffset, paint);
            }

            //降水概率Java字符'\ue612'xml字符&#xe612;
            if (i == 0) {
                final float i0_x = dW / 2f;
                canvas.drawText(getContext().getResources().getString(R.string.weather_humidity), i0_x, textSize * 7.5f + textOffset, paint);
                canvas.drawText(getContext().getResources().getString(R.string.weather_wind_level), i0_x, textSize * 9f + textOffset, paint);
                canvas.drawText(getContext().getResources().getString(R.string.weather_time), i0_x, textSize * 10.5f + textOffset, paint);
            }
            canvas.drawText(d.humidity + getContext().getResources().getString(R.string.unit_percent), x[i], textSize * 7.5f + textOffset, paint);
            canvas.drawText(d.windLevel, x[i], textSize * 9f + textOffset, paint);
            canvas.drawText(d.date.substring(11), x[i], textSize * 10.5f + textOffset, paint);
        }
        paint.setAlpha(255);
        paint.setStyle(Style.STROKE);
        final float data_x0 = data_length_offset * dW;

        //draw gone tmp path
        goneTmpPath.moveTo(0, y[0]);
        goneTmpPath.lineTo(data_x0, y[0]);
        paint.setPathEffect(dashPathEffect);
        canvas.drawPath(goneTmpPath, paint);

        for (int i = 0; i < (length - 1); i++) {
            final float midX = (x[i] + x[i + 1]) / 2f;
            final float midY = (y[i] + y[i + 1]) / 2f;
            if (i == 0) {
                tmpPath.moveTo(data_x0, y[i]);
            }
            tmpPath.cubicTo(x[i] - 1, y[i], x[i], y[i], midX, midY);

            if (i == (length - 2)) {
                tmpPath.cubicTo(x[i + 1] - 1, y[i + 1], x[i + 1], y[i + 1], this.width, y[i + 1]);
            }
        }
        // draw tmp path

        final boolean needClip = pathPercent < 1f;
        if (needClip) {
            canvas.save();
            canvas.clipRect(0, 0, this.width * pathPercent, this.height);
        }
        paint.setPathEffect(null);
        canvas.drawPath(tmpPath, paint);
        if (needClip) {
            canvas.restore();
        }
    }

    public void setHourlyData(List<HourlyData> hourlyData) {
        if (hourlyData != null) {
            hourlyDataList = hourlyData;
        }
        if (hourlyDataList != null) {
            invalidate();
        }
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

    public static class HourlyData {
        public float offsetPercent;// , maxOffsetPercent;// 差值%
        public int temperature;// , tmp_min;
        public String date;
        public String windLevel;
        public String humidity;
    }
}
