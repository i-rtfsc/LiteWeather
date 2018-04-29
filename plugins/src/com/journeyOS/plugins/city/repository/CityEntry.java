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

package com.journeyOS.plugins.city.repository;

import java.util.List;

public class CityEntry {
    /**
     * name : 北京
     * name_en : beijing
     * city : {"name":"北京","county":[{"name":"北京","code":"CN101010100","name_en":"beijing"},{"name":"海淀","code":"CN101010200","name_en":"haidian"},{"name":"朝阳","code":"CN101010300","name_en":"chaoyang"},{"name":"顺义","code":"CN101010400","name_en":"shunyi"},{"name":"怀柔","code":"CN101010500","name_en":"huairou"},{"name":"通州","code":"CN101010600","name_en":"tongzhou"},{"name":"昌平","code":"CN101010700","name_en":"changping"},{"name":"延庆","code":"CN101010800","name_en":"yanqing"},{"name":"丰台","code":"CN101010900","name_en":"fengtai"},{"name":"石景山","code":"CN101011000","name_en":"shijingshan"},{"name":"大兴","code":"CN101011100","name_en":"daxing"},{"name":"房山","code":"CN101011200","name_en":"fangshan"},{"name":"密云","code":"CN101011300","name_en":"miyun"},{"name":"门头沟","code":"CN101011400","name_en":"mentougou"},{"name":"平谷","code":"CN101011500","name_en":"pinggu"}]}
     */
    public String name;
    public String name_en;
    public List<CityBean> city;

    public static class CityBean {
        /**
         * name : 北京
         * county : [{"name":"北京","code":"CN101010100","name_en":"beijing"},{"name":"海淀","code":"CN101010200","name_en":"haidian"},{"name":"朝阳","code":"CN101010300","name_en":"chaoyang"},{"name":"顺义","code":"CN101010400","name_en":"shunyi"},{"name":"怀柔","code":"CN101010500","name_en":"huairou"},{"name":"通州","code":"CN101010600","name_en":"tongzhou"},{"name":"昌平","code":"CN101010700","name_en":"changping"},{"name":"延庆","code":"CN101010800","name_en":"yanqing"},{"name":"丰台","code":"CN101010900","name_en":"fengtai"},{"name":"石景山","code":"CN101011000","name_en":"shijingshan"},{"name":"大兴","code":"CN101011100","name_en":"daxing"},{"name":"房山","code":"CN101011200","name_en":"fangshan"},{"name":"密云","code":"CN101011300","name_en":"miyun"},{"name":"门头沟","code":"CN101011400","name_en":"mentougou"},{"name":"平谷","code":"CN101011500","name_en":"pinggu"}]
         */
        public String name;
        public List<CountyBean> county;

        public static class CountyBean {
            /**
             * name : 北京
             * code : CN101010100
             * name_en : beijing
             */
            public String name;
            public String code;
            public String name_en;
        }
    }
}
