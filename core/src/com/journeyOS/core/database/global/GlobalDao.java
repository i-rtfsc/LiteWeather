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

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.journeyOS.core.database.DBConfigs;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface GlobalDao {
    @Insert(onConflict = REPLACE)
    void saveGlobal(Global global);

    @Query("DELETE FROM " + DBConfigs.Global.TABLE_NAME + " WHERE " + DBConfigs.Global.KEY + " LIKE :key")
    void deleteGlobal(String key);

    @Query("SELECT * FROM " + DBConfigs.Global.TABLE_NAME + " WHERE " + DBConfigs.Global.KEY + " LIKE :key")
    Global getGlobal(String key);

}
