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
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.journeyOS.widget.R;
import com.journeyOS.widget.utils.UIUtils;

public class SettingView extends RelativeLayout {
    protected static final int DEFAULT_COLOR = 0xff717171;
    protected static final int DEFAULT_SUMMARY_COLOR = 0x80646464;
    private static final String TAG = SettingView.class.getSimpleName();
    private static final int IMAGE_ICON_ID = 1;
    private static final int TITLE_ID = 2;
    private static final int ARROW_ICON_ID = 3;

    private boolean isProgressing = false;
    private boolean isError = false;

    private ImageView mIcon;
    private TextView mTitle;
    private ImageView mArrow;

    private ImageView mErrorImageView;
    private TextView mErrorTextView;

    private View mRightView;
    private FrameLayout mRightLayout;

    private int mIconWidth, mIconHeight, mArrowWidth, mArrowHeight, mTitleTextSize;
    private int mTitleTextColor;
    private int mIconRes, mArrowRes;
    private int mErrorRes;
    private String mTitleText;

    private TextView mSummary;
    private String mSummaryText;
    private int mSummaryTextColor;
    private int mSummaryTextSize;

    private TextView mRightSummary;
    private String mRightSummaryText;
    private int mRightSummaryTextColor;
    private int mRightSummaryTextSize;

    private String mErrorDesc;

    // 默认值，以及padding
    private int mIconDefaultSize, mArrowDefaultSize;
    private int mLeftPadding, mRightPadding, mTopPadding, mBottomPadding;
    private boolean isShaking = false;

    public SettingView(Context context) {
        this(context, null);
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDefaultValue();

        setPadding(mLeftPadding, mTopPadding, mRightPadding, mBottomPadding);

        init(attrs);
    }

    private void setDefaultValue() {
        mIconDefaultSize = dp2px(25);
        mArrowDefaultSize = dp2px(20);
        mLeftPadding = dp2px(16);
        mRightPadding = dp2px(10);
        mTopPadding = dp2px(10);
        mBottomPadding = mTopPadding;
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SettingView);
            if (ta != null) {

                mIconRes = ta.getResourceId(R.styleable.SettingView_settingIcon, -1);
                mIconWidth = ta.getDimensionPixelSize(R.styleable.SettingView_iconWidth, mIconDefaultSize);
                mIconHeight = ta.getDimensionPixelSize(R.styleable.SettingView_iconHeight, mIconDefaultSize);

                mTitleText = ta.getString(R.styleable.SettingView_settingTitle);
                mTitleTextColor = ta.getColor(R.styleable.SettingView_settingTitleTextColor, DEFAULT_COLOR);
                mTitleTextSize = ta.getDimensionPixelSize(R.styleable.SettingView_settingTitleTextSize, 0);

                mSummaryText = ta.getString(R.styleable.SettingView_settingSummary);
                mSummaryTextColor = ta.getColor(R.styleable.SettingView_settingSummaryTextColor, DEFAULT_SUMMARY_COLOR);
                mSummaryTextSize = ta.getDimensionPixelSize(R.styleable.SettingView_settingSummaryTextSize, 0);
                Boolean isShowSummary = ta.getBoolean(R.styleable.SettingView_showSummary, false) || (mSummaryText != null);

                mRightSummaryText = ta.getString(R.styleable.SettingView_settingRightSummary);
                mRightSummaryTextColor = ta.getColor(R.styleable.SettingView_settingRightSummaryTextColor, DEFAULT_SUMMARY_COLOR);
                mRightSummaryTextSize = ta.getDimensionPixelSize(R.styleable.SettingView_settingRightSummaryTextSize, 0);
                Boolean isShowRightSummary = ta.getBoolean(R.styleable.SettingView_showRightSummary, false) || (mRightSummaryText != null);

                mArrowRes = ta.getResourceId(R.styleable.SettingView_arrow, R.drawable.setting_view_arrow);
                mArrowWidth = ta.getDimensionPixelSize(R.styleable.SettingView_arrowWidth, mArrowDefaultSize);
                mArrowHeight = ta.getDimensionPixelSize(R.styleable.SettingView_arrowHeight, mArrowDefaultSize);

                mErrorRes = ta.getResourceId(R.styleable.SettingView_settingError, R.drawable.setting_error);

                Boolean isShow = ta.getBoolean(R.styleable.SettingView_showRight, true);
                initIcon();
                initTitle(isShowSummary);
                initSummary(isShowSummary);
                initRightSummary(isShowRightSummary);
                initRightLayout();
                initRightView(isShow);

                ta.recycle();
            }
        }
    }

    /**
     * 初始化右侧错图片标识，图片可替换，大小不可变，当显示时候，原本的view包括progressBar都会Gone
     */
    private void initErrorView() {
        int w = dp2px(25);
        int h = dp2px(25);
        mErrorImageView = new ImageView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mErrorImageView.setImageResource(mErrorRes);
        mErrorImageView.setVisibility(GONE);
    }

    /**
     * 初始化右侧错图片文字，文字的大小颜色不可变，当显示时候，原本的view包括progressBar都会Gone
     */
    private void initErrorText() {
        int errorColor = ContextCompat.getColor(getContext(), android.R.color.holo_red_light);
        int errorTextSize = 10;
        mErrorTextView = new TextView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mErrorTextView.setLayoutParams(params);
        mErrorTextView.setTextColor(errorColor);
        mErrorTextView.setTextSize(errorTextSize);
        mErrorTextView.setVisibility(GONE);
    }

    /**
     * 初始化标题，必须后执行于ICON被初始化
     */
    private void initTitle(boolean isShowSummary) {
        mTitle = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, IMAGE_ICON_ID);
        if (!isShowSummary) params.addRule(CENTER_VERTICAL, TRUE);
        mTitle.setLayoutParams(params);
        mTitle.setId(TITLE_ID);
        mTitle.setText(mTitleText != null ? mTitleText : "");
        mTitle.setTextColor(mTitleTextColor);
        if (mTitleTextSize == 0) {
            mTitleTextSize = 14;
        } else {
            mTitleTextSize = px2dp(mTitleTextSize);
        }
        mTitle.setTextSize(mTitleTextSize);
        addView(mTitle);
    }

    private void initSummary(boolean isShowSummary) {
        mSummary = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, IMAGE_ICON_ID);
        params.addRule(RelativeLayout.LEFT_OF, ARROW_ICON_ID);
        params.addRule(RelativeLayout.BELOW, TITLE_ID);
        mSummary.setLayoutParams(params);
        mSummary.setText(mSummaryText != null ? mSummaryText : "");
        mSummary.setTextColor(mSummaryTextColor);
        if (mSummaryTextSize == 0) {
            mSummaryTextSize = 12;
        } else {
            mSummaryTextSize = px2dp(mSummaryTextSize);
        }
        mSummary.setTextSize(mSummaryTextSize);

        addView(mSummary);
        mSummary.setVisibility(isShowSummary ? VISIBLE : GONE);
    }

    private void initRightSummary(boolean isShowSummary) {
        mRightSummary = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.LEFT_OF, ARROW_ICON_ID);
        params.addRule(CENTER_VERTICAL, TRUE);
        params.rightMargin = dp2px(6);
        mRightSummary.setLayoutParams(params);
        mRightSummary.setText(mRightSummaryText != null ? mRightSummaryText : "");
        mRightSummary.setTextColor(mRightSummaryTextColor);
        if (mRightSummaryTextSize == 0) {
            mRightSummaryTextSize = 13;
        } else {
            mRightSummaryTextSize = px2dp(mRightSummaryTextSize);
        }
        mRightSummary.setTextSize(mRightSummaryTextSize);

        addView(mRightSummary);
        mRightSummary.setVisibility(isShowSummary ? VISIBLE : GONE);
    }

    /**
     * 应该最先被初始化
     */
    private void initIcon() {
        mIcon = new ImageView(getContext());
        LayoutParams params = new LayoutParams(mIconWidth, mIconHeight);
        params.rightMargin = dp2px(12);
        params.addRule(CENTER_VERTICAL, TRUE);
        params.addRule(ALIGN_PARENT_LEFT, TRUE);
        mIcon.setLayoutParams(params);
        //noinspection ResourceType
        mIcon.setId(IMAGE_ICON_ID);
        if (mIconRes == -1) {
            mIcon.setVisibility(View.GONE);
        } else {
            mIcon.setImageResource(mIconRes);
        }

        addView(mIcon);
    }

    /**
     * 初始化右侧空间的容器
     */
    private void initRightLayout() {
        mRightLayout = new FrameLayout(getContext());
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL, TRUE);
        params.addRule(ALIGN_PARENT_RIGHT, TRUE);
        mRightLayout.setLayoutParams(params);

        addView(mRightLayout);
    }

    /**
     * 初始化右边的控件，并添加
     */
    private void initRightView(Boolean isShow) {
        initErrorView();
        initErrorText();

        mRightView = getRightView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRightView.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mRightView.setLayoutParams(params);
        mRightLayout.addView(mRightView);
        mRightLayout.addView(mErrorImageView);
        mRightLayout.addView(mErrorTextView);

        mRightLayout.setId(ARROW_ICON_ID);
        if (!isShow) mRightLayout.setVisibility(GONE);
    }

    /**
     * 初始化右侧控件，在子类中重写可以实现不同的控件
     * 此控件是放在FrameLayout里面的，注意LayoutParam的类型
     *
     * @return
     */
    protected View getRightView() {
        int defaultWid = dp2px(20);
        int defaultHei = defaultWid;

        int w = mArrowWidth != -1 ? mArrowWidth : defaultWid;
        int h = mArrowHeight != -1 ? mArrowHeight : defaultHei;

        mArrow = new ImageView(getContext());
        mArrow.setImageResource(mArrowRes);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        mArrow.setLayoutParams(params);

        return mArrow;
    }

    public void setTextError(String errorDesc, boolean isError) {
        this.isError = isError;
        isProgressing = false;
        mErrorDesc = errorDesc;
        mErrorTextView.setText(errorDesc);
    }

    public void setIcon(Drawable drawable) {
        if (mIcon != null) {

            mIcon.setImageDrawable(drawable);
        }
    }

    public void setColorFilter(int colorFilter) {
        if (mIcon != null) {
            mIcon.setColorFilter(colorFilter);
        }
    }

    public void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    public void setTextColor(int color) {
        if (mTitle != null) {
            mTitle.setTextColor(color);
        }
    }

    public void setSummary(String summary) {
        if (mSummary != null) {
            mSummary.setText(summary);
        }
    }

    public void setSummaryColor(int color) {
        if (mSummary != null) {
            mSummary.setTextColor(color);
        }
    }

    public void setRightSummary(String summary) {
        if (mRightSummary != null) {
            mRightSummary.setText(summary);
        }
    }

    public void setRightSummaryColor(int color) {
        if (mRightSummary != null) {
            mRightSummary.setTextColor(color);
        }
    }

    public void setErrorImageRes(int res) {
        mErrorImageView.setImageResource(res);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = h;
        setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    protected int dp2px(int dpValue) {
        return UIUtils.dip2px(getContext(), dpValue);
    }

    protected int px2dp(int pxValue) {
        return UIUtils.px2dip(getContext(), pxValue);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, isError, mErrorDesc);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        isError = ss.isError;
        mErrorDesc = ss.errDesc;

        if (isError) {
            if (TextUtils.isEmpty(mErrorDesc)) {
            } else {
                setTextError(mErrorDesc, isError);
            }
        }

        Log.d(TAG, "onRestoreInstanceState: err: " + isError + " | desc: " + mErrorDesc);
    }
}

class SavedState extends View.BaseSavedState {

    public static final Creator<SavedState> CREATOR
            = new Creator<SavedState>() {
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    };
    boolean isError;
    String errDesc;

    public SavedState(Parcelable superState, boolean isError, String errDesc) {
        super(superState);
        this.isError = isError;
        this.errDesc = errDesc;
    }

    private SavedState(Parcel source) {
        super(source);
        isError = source.readByte() != 0;
        errDesc = source.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeByte((byte) (isError ? 1 : 0));
        out.writeString(errDesc);
    }
}