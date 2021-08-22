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
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.journeyOS.widget.R;

import java.util.Calendar;


/**
 * 太阳和风速
 */
public class AstroView extends View {

    private int width, height;
    private final float density;
    private final DashPathEffect dashPathEffect;
    private Path sunPath = new Path();
    private RectF sunRectF = new RectF();
    TimeResult sunTimeResult;
    TimeResult moonTimeResult;
    /**
     * 旋转的风车的扇叶
     */
    private Path fanPath = new Path();
    /**
     * 旋转的风车的柱子
     */
    private Path fanPillarPath = new Path();
    /**
     * 旋转的风尘的角度
     */
    private float curRotate;

    private float fanPillerHeight;
    private final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float paintTextOffset;
    final float offsetDegree = 15f;

    private AstroData mAstroData;
    private float sunArcHeight, sunArcRadius;

    private Rect visibleRect = new Rect();

    public void setAstroData(AstroData astroData) {
        if (astroData != null) {
            mAstroData = astroData;
        }

        if (this.mAstroData != null) {
            invalidate();
        }
    }

    public AstroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        dashPathEffect = new DashPathEffect(new float[]{density * 3, density * 3}, 1);
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(density);
        paint.setTextAlign(Align.CENTER);
//		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(0x33ffffff);
        if (this.mAstroData == null) {
            return;
        }
        paint.setColor(Color.WHITE);
        float textSize = paint.getTextSize();
        try {
            paint.setStrokeWidth(density);
            paint.setStyle(Style.STROKE);
            // draw sun path
            paint.setColor(0x55ffffff);
            paint.setPathEffect(dashPathEffect);
            canvas.drawPath(sunPath, paint);
            paint.setPathEffect(null);
            paint.setColor(Color.WHITE);
            int saveCount = canvas.save();
            canvas.translate(width / 2f - fanPillerHeight * 1f, textSize + sunArcHeight - fanPillerHeight);
            // draw wind text
            paint.setStyle(Style.FILL);
            paint.setTextAlign(Align.LEFT);
            final float fanHeight = textSize * 2f;
            canvas.drawText(getContext().getResources().getText(R.string.weather_wind_speed) + " " + mAstroData.windSpeed + "km/h", fanHeight + textSize, (float) -(textSize * 1.5), paint);
            canvas.drawText(getContext().getResources().getText(R.string.weather_wind_direction) + " " + mAstroData.windDirection, fanHeight + textSize, 0, paint);
            canvas.drawText(getContext().getResources().getText(R.string.weather_atm) + " " + mAstroData.pressure + getContext().getResources().getText(R.string.unit_hpa), fanHeight + textSize, (float) +(textSize * 1.5), paint);
            // draw fan and fanPillar
            paint.setStyle(Style.STROKE);
            canvas.drawPath(fanPillarPath, paint);
            canvas.rotate(curRotate * 360f);
            float speed = 0f;
            try {
                speed = Float.parseFloat(mAstroData.windSpeed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            speed = Math.max(speed, 0.75f);
            curRotate += 0.001f * speed;
            if (curRotate > 1f) {
                curRotate = 0f;
            }
            paint.setStyle(Style.FILL);
            canvas.drawPath(fanPath, paint);
            canvas.rotate(120f);
            canvas.drawPath(fanPath, paint);
            canvas.rotate(120f);
            canvas.drawPath(fanPath, paint);
            canvas.restoreToCount(saveCount);

            //draw bottom line
            paint.setStyle(Style.STROKE);
            //paint.setColor(0x55ffffff);
            final float lineLeft = width / 2f - sunArcRadius;
            canvas.drawLine(lineLeft, sunArcHeight + textSize, width - lineLeft, sunArcHeight + textSize, paint);

            drawSun(canvas, textSize);
            drawMoon(canvas, textSize);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        getGlobalVisibleRect(visibleRect);
        if (!visibleRect.isEmpty()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 间距1 图8.5行 间距0.5 字1行 间距1 = 12;
        // 9.5 10 11 12
        this.width = w;
        this.height = h;

        try {
            final float textSize = height / 12f;
            paint.setTextSize(textSize);
            paintTextOffset = UIUtils.getTextPaintOffset(paint);

            sunPath.reset();

            sunArcHeight = textSize * 8.5f;
            sunArcRadius = (float) (sunArcHeight / (1f - Math.sin(Math.toRadians(offsetDegree))));
            final float sunArcLeft = width / 2f - sunArcRadius;
            sunRectF.left = sunArcLeft;
            sunRectF.top = textSize;
            sunRectF.right = width - sunArcLeft;
            sunRectF.bottom = sunArcRadius * 2f + textSize;
            sunPath.addArc(sunRectF, -165, +150);//圆形的最右端点为0，顺时针sweepAngle

            //fanPath和fanPillarPath的中心点在扇叶圆形的中间
            fanPath.reset();
            final float fanSize = textSize * 0.2f;//风扇底部半圆的半径
            final float fanHeight = textSize * 2f;
            final float fanCenterOffsetY = fanSize * 1.6f;
            //fanPath.moveTo(fanSize, -fanCenterOffsetY);
            //也可以用arcTo 从右边到底部到左边了的弧
            fanPath.addArc(new RectF(-fanSize, -fanSize - fanCenterOffsetY, fanSize, fanSize - fanCenterOffsetY), 0,
                    180);
            //fanPath.lineTo(0, -fanHeight - fanCenterOffsetY);
            fanPath.quadTo(-fanSize * 1f, -fanHeight * 0.5f - fanCenterOffsetY, 0, -fanHeight - fanCenterOffsetY);
            fanPath.quadTo(fanSize * 1f, -fanHeight * 0.5f - fanCenterOffsetY, fanSize, -fanCenterOffsetY);
            fanPath.close();

            fanPillarPath.reset();
            final float fanPillarSize = textSize * 0.25f;//柱子的宽度
            fanPillarPath.moveTo(0, 0);
            fanPillerHeight = textSize * 4f;//柱子的宽度
            fanPillarPath.lineTo(fanPillarSize, fanPillerHeight);
            fanPillarPath.lineTo(-fanPillarSize, fanPillerHeight);
            fanPillarPath.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSun(Canvas canvas, float textSize) {
        try {
            String[] sr = mAstroData.sunRise.split(":");//日出
            int beginHour = Integer.parseInt(sr[0]);
            int beginMin = Integer.parseInt(sr[1]);

            String[] ss = mAstroData.sunSet.split(":");//日落
            int endHour = Integer.parseInt(ss[0]);
            int endMin = Integer.parseInt(ss[1]);

            paint.setStyle(Style.FILL);
            paint.setTextAlign(Align.RIGHT);
            //draw astor info
            final float textLeft = width / 2f - sunArcRadius;// sunArcSize;
            paint.setTextAlign(Align.CENTER);
            canvas.drawText(mAstroData.sunRise, textLeft, textSize * 10.5f + paintTextOffset, paint);
            canvas.drawText(mAstroData.sunSet, width - textLeft, textSize * 10.5f + paintTextOffset, paint);
            canvas.drawText("太阳", width / 2, textSize * 10.5f + paintTextOffset, paint);

            TimeResult result = atTheCurrentTime(beginHour, beginMin, endHour, endMin);
            if (result.inner) {//说明是在白天，需要画太阳
                canvas.save();
                canvas.translate(width / 2f, sunArcRadius + textSize);// 先平移到圆心
                float degree = 15f + 150f * result.percent;
                final float sunRadius = density * 6f;
                canvas.rotate(degree);//旋转到太阳的角度

                paint.setStyle(Style.FILL);
//                canvas.drawLine(-1000, 0, 1000, 0, paint);//测试角度的线
                paint.setStrokeWidth(density * 1.333f);//宽度是2对应半径是6
                canvas.translate(-sunArcRadius, 0);//平移到太阳应该在的位置
                canvas.rotate(-degree);// 转正方向。。。
                canvas.drawCircle(0, 0, sunRadius, paint);
                paint.setStyle(Style.STROKE);
                final int light_count = 9;
                for (int i = 0; i < light_count; i++) {//画刻度
                    double radians = Math.toRadians(i * (360 / light_count));
                    float x1 = (float) (Math.cos(radians) * sunRadius * 1.6f);
                    float y1 = (float) (Math.sin(radians) * sunRadius * 1.6f);
                    float x2 = x1 * (1f + 0.4f * 1f);
                    float y2 = y1 * (1f + 0.4f * 1f);
                    canvas.drawLine(0 + x1, y1, 0 + x2, y2, paint);
                }
                canvas.restore();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawMoon(Canvas canvas, float textSize) {
        try {
            // draw the moon
            String[] sr = mAstroData.moonRise.split(":");//月升
            int beginHour = Integer.parseInt(sr[0]);
            int beginMin = Integer.parseInt(sr[1]);

            String[] ss = mAstroData.moonSet.split(":");//月落
            int endHour = Integer.parseInt(ss[0]);
            int endMin = Integer.parseInt(ss[1]);

            paint.setStyle(Style.FILL);
            paint.setTextAlign(Align.RIGHT);
            //draw astor info
            final float textLeft = width / 2f - sunArcRadius;
            paint.setTextAlign(Align.CENTER);
            paint.setColor(Color.LTGRAY);
            canvas.drawText(mAstroData.moonRise, textLeft * 2, sunArcHeight + paintTextOffset, paint);
            canvas.drawText(mAstroData.moonSet, width - textLeft * 2, sunArcHeight + paintTextOffset, paint);
            canvas.drawText(mAstroData.moonPhase, width / 2, sunArcHeight + paintTextOffset, paint);

            TimeResult result = atTheCurrentTime(beginHour, beginMin, endHour, endMin);
            if (result.inner) {//说明在月亮升起期间，需要画月
                canvas.save();
                canvas.translate(width / 2f, sunArcRadius + textSize);//先平移到圆心
                float degree = 15f + 150f * result.percent;
                canvas.rotate(degree);

                paint.setStyle(Style.FILL);
//                canvas.drawLine(-1000, 0, 1000, 0, paint);//测试角度的线
                paint.setStrokeWidth(density * 1.333f);
                canvas.translate(-sunArcRadius, 0);
                canvas.rotate(-degree);
                canvas.drawCircle(0, 0, density * 5f, paint);
                canvas.restore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TimeResult atTheCurrentTime(int beginHour, int beginMin, int endHour, int endMin) {
        long diff = 0;
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;

        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;

        /**跨天的特殊情况(比如23:00-2:00)*/
        if (!startTime.before(endTime)) {
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            diff = 24 * 60 * 60 - (beginHour * 60 * 60 + beginMin * 60) + endHour * 60 * 60 + endMin * 60;

            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            /**普通情况(比如5:00-10:00)*/
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            diff = endHour * 60 * 60 + endMin * 60 - (beginHour * 60 * 60 + beginMin * 60);
        }
        Calendar c = Calendar.getInstance();
        int curTime = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.MINUTE);
        float percent = (curTime - (float) (beginHour * 60 * 60 + beginMin * 60)) / diff;
        return new TimeResult(result, percent);
    }

    public static class TimeResult {
        public boolean inner = false;
        public float percent = 0f;

        public TimeResult(boolean inner, float percent) {
            this.inner = inner;
            this.percent = percent;
        }
    }

    public static class AstroData {
        //风速
        public String windSpeed;
        //风向
        public String windDirection;
        //大气压强
        public String pressure;
        //日出时间
        public String sunRise;
        //日落时间
        public String sunSet;

        //夜间风速
        public String windSpeedNight;
        //夜间风向
        public String windDirectionNight;
        //月相名称
        public String moonPhase;
        //月升时间
        public String moonRise;
        //月落时间
        public String moonSet;
    }
}
