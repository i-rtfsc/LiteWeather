/*
 * Copyright (c) 2021 anqi.huang@outlook.com
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

package com.journeyOS.core.database.global;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.journeyOS.base.utils.SmartLog;
import com.journeyOS.core.database.DBConfigs;


@Entity(tableName = DBConfigs.Global.TABLE_NAME, primaryKeys = {DBConfigs.Global.KEY})
public class Global {

    @NonNull
    @ColumnInfo(name = DBConfigs.Global.KEY)
    public String key = "";

    @ColumnInfo(name = DBConfigs.Global.VALUE)
    public String value;

    @ColumnInfo(name = DBConfigs.Global.OBJECT)
    public String object;

    @Ignore
    public boolean getBoolean() {
        SmartLog.d("GlobalRepositoryImpl", "getBoolean value = [" + value + "]");
        SmartLog.d("GlobalRepositoryImpl", "getBoolean Boolean.getBoolean(value) = [" + Boolean.getBoolean(value) + "]");
        if (Boolean.class.getName().equals(object)) {
            return Boolean.parseBoolean(value);
        } else {
            throw new IllegalStateException("global is " + object);
        }
    }

    @Ignore
    public int getInt() {
        if (Integer.class.getName().equals(object)) {
            return (Integer) Integer.parseInt(value);
        } else {
            throw new IllegalStateException("global is " + object);
        }
    }

    @Ignore
    public String getString() {
        if (String.class.getName().equals(object)) {
            return (String) value;
        } else {
            throw new IllegalStateException("global is " + object);
        }
    }

    @Ignore
    public float getFloat() {
        if (Float.class.getName().equals(object)) {
            return Float.parseFloat(value);
        } else {
            throw new IllegalStateException("global is " + object);
        }
    }


}
