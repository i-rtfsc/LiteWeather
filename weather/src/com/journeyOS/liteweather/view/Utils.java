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

import android.animation.ValueAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;

class Utils {
    static ScrolledParent getScrolledParent(ViewGroup child) {

        ViewParent parent = child.getParent();
        int childBetweenParentCount = 0;
        while (parent != null) {
            if ((parent instanceof RecyclerView || parent instanceof AbsListView)) {
                ScrolledParent scrolledParent = new ScrolledParent();
                scrolledParent.scrolledView = (ViewGroup) parent;
                scrolledParent.childBetweenParentCount = childBetweenParentCount;
                return scrolledParent;
            }
            childBetweenParentCount++;
            parent = parent.getParent();
        }
        return null;
    }

    static ValueAnimator createParentAnimator(final View parent, int distance, long duration) {

        ValueAnimator parentAnimator = ValueAnimator.ofInt(0, distance);

        parentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int lastDy;
            int dy;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dy = (int) animation.getAnimatedValue() - lastDy;
                lastDy = (int) animation.getAnimatedValue();
                parent.scrollBy(0, dy);
            }
        });
        parentAnimator.setDuration(duration);

        return parentAnimator;
    }
}
