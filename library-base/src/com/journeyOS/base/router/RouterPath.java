package com.journeyOS.base.router;


public class RouterPath {
    /**
     * 用于组件开发中，ARouter单Activity跳转的统一路径注册
     * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
     */
    public static class Activity {
        /**
         * 主业务组件
         */
        public static class Main {
            private static final String MAIN = "/main";
            /*主业务界面*/
            public static final String PAGER_MAIN = MAIN + "/Main";
        }

        /**
         * 城市组件
         */
        public static class City {
            private static final String CITY = "/city";
            /*城市*/
            public static final String PAGER_CITY = CITY + "/City";
        }
    }

    /**
     * 用于组件开发中，ARouter多Fragment跳转的统一路径注册
     * 在这里注册添加路由路径，需要清楚的写好注释，标明功能界面
     */
    public static class Fragment {
        /**
         * 天气组件
         */
        public static class Weather {
            private static final String WEATHER = "/weather";
            /*天气*/
            public static final String PAGER_WEATHER = WEATHER + "/weather";
        }

        /**
         * 设置组件
         */
        public static class Setting {
            private static final String SETTING = "/setting";
            /*设置*/
            public static final String PAGER_SETTING = SETTING + "/setting";
        }

        /**
         * 城市组件
         */
        public static class City {
            private static final String CITY = "/city";
            /*城市*/
            public static final String PAGER_CITY = CITY + "/city";
        }
    }
}
