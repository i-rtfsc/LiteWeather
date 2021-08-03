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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.journeyOS.widget.R;
import com.journeyOS.widget.utils.UIUtils;


public class WeatherTextView extends LinearLayout {

    protected static final int DEFAULT_LEFT_COLOR = 0xff717171;
    protected static final int DEFAULT_RIGHT_COLOR = 0x80646464;

    private TextView mTitletv;
    private int mLeftTextColor;
    private int mLeftTextSize;
    private String mLeftText;

    private TextView mSummarytv;
    private int mRightTextColor;
    private int mRightTextSize;
    private String mRightText;

    private View mBottom;
    private int mBottomColor = -1;
    private boolean mShowBottom = true;

    public WeatherTextView(Context context) {
        super(context);
    }

    public WeatherTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WeatherTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View v = inflate(getContext(), R.layout.layout_weather_textview, this);

        mTitletv = v.findViewById(R.id.title);
        mSummarytv = v.findViewById(R.id.summary);
        mBottom = v.findViewById(R.id.bottom);

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.WeatherTextView);
            if (ta != null) {
                mLeftTextColor = ta.getColor(R.styleable.WeatherTextView_leftTextColor, DEFAULT_LEFT_COLOR);
                setTitleColor(mLeftTextColor);

                mLeftTextSize = ta.getDimensionPixelSize(R.styleable.WeatherTextView_leftTextSize, 0);
                setTitleSize(mLeftTextSize);

                mLeftText = ta.getString(R.styleable.WeatherTextView_left);
                setTitle(mLeftText);

                mRightTextColor = ta.getColor(R.styleable.WeatherTextView_rightTextColor, DEFAULT_RIGHT_COLOR);
                setSummaryColor(mRightTextColor);

                mRightTextSize = ta.getDimensionPixelSize(R.styleable.WeatherTextView_rightTextSize, 0);
                setSummarySize(mRightTextSize);

                mRightText = ta.getString(R.styleable.WeatherTextView_right);
                setSummary(mRightText);


                mShowBottom = ta.getBoolean(R.styleable.WeatherTextView_showBottom, true);
                setShowBottom(mShowBottom);

                mBottomColor = ta.getColor(R.styleable.WeatherTextView_bottomColor, -1);
                setBottomColor(mBottomColor);
            }
        }
    }

    protected int dp2px(int dpValue) {
        return UIUtils.dip2px(getContext(), dpValue);
    }

    protected int px2dp(int pxValue) {
        return UIUtils.px2dip(getContext(), pxValue);
    }

    public void setTitle(String summary) {
        mTitletv.setText(summary);
    }

    public void setTitleColor(int color) {
        mTitletv.setTextColor(color);
    }

    public void setTitleSize(int size) {
        mTitletv.setTextSize(px2dp(size));
    }

    public void setSummary(String summary) {
        mSummarytv.setText(summary);
    }

    public void setSummaryColor(int color) {
        mSummarytv.setTextColor(color);
    }

    public void setSummarySize(int size) {
        mSummarytv.setTextSize(px2dp(size));
    }

    public void setShowBottom(boolean showBottom) {
        mBottom.setVisibility(showBottom ? VISIBLE : GONE);
    }

    public void setBottomColor(int color) {
        mBottom.setBackgroundColor(color);
    }

}