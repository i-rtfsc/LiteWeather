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

package com.journeyOS.core.api.cityprovider;

import androidx.annotation.NonNull;
import androidx.room.Entity;


@Entity(tableName = "city", primaryKeys = {"cityId"})
public class City {

    @NonNull
    public String cityId = "";

    public String country;

    public String countryEn;

    public String cityName;

    public String province;

    public String provinceEn;

    public String longitude;

    public String latitude;

    @Override
    public String toString() {
        return String.format("cityId=%s", this.cityName)
                + String.format("province=%s", this.province)
                + String.format("provinceEn=%s", this.provinceEn)
                + String.format("city=%s", this.cityName)
                + String.format("country=%s", this.country)
                + String.format("countryEn=%s", this.countryEn)
                + String.format("longitude=%s", this.longitude)
                + String.format("latitude=%s", this.latitude);
    }

}
