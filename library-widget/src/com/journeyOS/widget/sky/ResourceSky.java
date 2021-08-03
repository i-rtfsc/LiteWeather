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

package com.journeyOS.widget.sky;

import android.content.Context;

public class ResourceSky {

    public static final class SkyBackground {
        public static final int[] BLACK = new int[]{0xff000000, 0xff000000};
//		public static final int[] CLEAR_D = new int[] { 0xff3d99c2, 0xff4f9ec5 };
//		public static final int[] CLEAR_N = new int[] { 0xff0d1229, 0xff262c42 };

        public static final int[] CLEAR_D = new int[]{0xff303F9F, 0xff3F51B5};
        public static final int[] CLEAR_N = new int[]{0xff0b0f25, 0xff252b42};

        public static final int[] OVERCAST_D = new int[]{0xff151b45, 0xff192154};//0xff748798, 0xff617688
        public static final int[] OVERCAST_N = new int[]{0xff262921, 0xff23293e};//0xff1b2229, 0xff262921

        public static final int[] RAIN_D = new int[]{0xff303f9f, 0xff303f9f};
        public static final int[] RAIN_N = new int[]{0xff0d0d15, 0xff22242f};

        public static final int[] FOG_D = new int[]{0xff3545ae, 0xff394bbd};
        public static final int[] FOG_N = new int[]{0xff2f3c47, 0xff24313b};

        public static final int[] SNOW_D = new int[]{0xff273381, 0xff2b3990};
        public static final int[] SNOW_N = new int[]{0xff1e2029, 0xff212630};

        public static final int[] CLOUDY_D = new int[]{0xff222d72, 0xff273381};
        public static final int[] CLOUDY_N = new int[]{0xff071527, 0xff252b42};// 0xff193353 };//{ 0xff0e1623, 0xff222830 }

        public static final int[] HAZE_D = new int[]{0xff616e70, 0xff474644};// 0xff999b95, 0xff818e90
        public static final int[] HAZE_N = new int[]{0xff373634, 0xff25221d};

        public static final int[] SAND_D = new int[]{0xffb5a066, 0xffd5c086};//0xffa59056
        public static final int[] SAND_N = new int[]{0xff312820, 0xff514840};
    }

    public static BaseSky getSky(Context context, SkyType type) {
        switch (type) {
            case CLEAR_D:
                return new SunnySky(context);
            case CLEAR_N:
                return new StarSky(context);
            case RAIN_D:
                return new RainSky(context, false);
            case RAIN_N:
                return new RainSky(context, true);
            case SNOW_D:
                return new SnowSky(context, false);
            case SNOW_N:
                return new SnowSky(context, true);
            case CLOUDY_D:
                return new CloudySky(context, false);
            case CLOUDY_N:
                return new CloudySky(context, true);
            case OVERCAST_D:
                return new OvercastSky(context, false);
            case OVERCAST_N:
                return new OvercastSky(context, true);
            case FOG_D:
                return new FogSky(context, false);
            case FOG_N:
                return new FogSky(context, true);
            case HAZE_D:
                return new HazeSky(context, false);
            case HAZE_N:
                return new HazeSky(context, true);
            case SAND_D:
                return new SandSky(context, false);
            case SAND_N:
                return new SandSky(context, true);
            case WIND_D:
                return new WindSky(context, false);
            case WIND_N:
                return new WindSky(context, true);
            case RAIN_SNOW_D:
                return new RainAndSnowSky(context, false);
            case RAIN_SNOW_N:
                return new RainAndSnowSky(context, true);
            case UNKNOWN_D:
                return new UnknownSky(context, false);
            case UNKNOWN_N:
                return new UnknownSky(context, true);
            case DEFAULT:
                return new SunnySky(context);
            default:
                return new DefaultSky(context);
        }
    }
}
