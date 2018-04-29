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

package com.journeyOS.liteweather.utils;

import java.util.Random;

public class SkyUilts {

    // 获得0--n之内的不等概率随机整数，0概率最大，1次之，以此递减，n最小
    public static int getAnyRandInt(int n) {
        int max = n + 1;
        int bigend = ((1 + max) * max) / 2;
        Random rd = new Random();
        int x = Math.abs(rd.nextInt() % bigend);
        int sum = 0;
        for (int i = 0; i < max; i++) {
            sum += (max - i);
            if (sum > x) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 获取[min, max)内的随机数，越大的数概率越小
     * 参考http://blog.csdn.net/loomman/article/details/3861240
     */
    public static float getDownRandFloat(float min, float max) {
        float bigend = ((min + max) * max) / 2f;
        // Random rd = new Random();
        float x = getRandom(min, bigend);// Math.abs(rd.nextInt() % bigend);
        int sum = 0;
        for (int i = 0; i < max; i++) {
            sum += (max - i);
            if (sum > x) {
                return i;
            }
        }
        return min;
    }

    public static float getRandom(float min, float max) {
        if (max < min) {
            throw new IllegalArgumentException("max should bigger than min!!!!");
        }
        return (float) (min + Math.random() * (max - min));
    }

    /**
     * 必须取[0,1]之间的float
     */
    public static float fixAlpha(float alpha) {
        if (alpha > 1f) {
            return 1f;
        }
        if (alpha < 0f) {
            return 0f;
        }
        return alpha;
    }
}
