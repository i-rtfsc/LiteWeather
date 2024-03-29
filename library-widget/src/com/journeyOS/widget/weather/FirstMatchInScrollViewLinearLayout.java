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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/***
 * 第一个child高度为ScrollView的高度
 *
 */
public class FirstMatchInScrollViewLinearLayout extends LinearLayout {

    public FirstMatchInScrollViewLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FirstMatchInScrollViewLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FirstMatchInScrollViewLinearLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            final ViewParent parent = getParent();
            if (parent != null && parent instanceof ScrollView) {
                final int height = ((ScrollView) parent).getMeasuredHeight();
                if (height > 0) {
                    final View firstChild = getChildAt(0);
                    LayoutParams layoutParams = (LayoutParams) firstChild.getLayoutParams();
                    layoutParams.height = height;
                    firstChild.setLayoutParams(layoutParams);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}

