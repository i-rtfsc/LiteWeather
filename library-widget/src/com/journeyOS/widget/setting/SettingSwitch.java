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

package com.journeyOS.widget.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.journeyOS.widget.R;
import com.kyleduo.switchbutton.SwitchButton;

public class SettingSwitch extends SettingView {
    private boolean isCheck = false;
    private SwitchButton mSwitchButton;

    public SettingSwitch(Context context) {
        this(context, null);
    }

    public SettingSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SettingSwitch);

        if (ta != null) {
            isCheck = ta.getBoolean(R.styleable.SettingSwitch_toggle_check, false);
        }

        ta.recycle();

        mSwitchButton.setChecked(isCheck);
    }

    @Override
    protected View getRightView() {
        mSwitchButton = new SwitchButton(getContext());
        mSwitchButton.setThumbColorRes(R.color.icon);
        mSwitchButton.setBackColorRes(R.color.dark_background);
//        mSwitchButton.setText("开", "关");
//        mSwitchButton.setTextColor(getContext().getResources().getColor(R.color.icon_unselected));
//        mSwitchButton.setTextExtra((int) (getResources().getDisplayMetrics().density * 4));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mSwitchButton.setLayoutParams(params);

        return mSwitchButton;
    }

    @Override
    public void setIcon(Drawable drawable) {
        super.setIcon(drawable);
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
        mSwitchButton.setChecked(isCheck);
        requestLayout();
    }

    public void setCheckedImmediately(boolean isCheck) {
        this.isCheck = isCheck;
        mSwitchButton.setCheckedImmediately(isCheck);
        requestLayout();
    }

    public boolean isCheck() {
        return isCheck;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
