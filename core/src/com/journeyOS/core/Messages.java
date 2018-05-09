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

package com.journeyOS.core;


import com.journeyOS.literouter.RouterMsssage;

public class Messages extends RouterMsssage {
    public static final int BASE = 1;
    public static final int MSG_LOCATION = BASE << 0;
    public static final int MSG_ALLOW_NOTIFICATION = BASE << 1;
    public static final int MSG_UPDATE_NOTIFICATION = BASE << 2;
    public static final int MSG_CLEAR_ALARM = BASE << 3;
    public static final int MSG_DRAWER_TYPE_UPDATE = BASE << 4;
    public static final int MSG_CURRENT_CITY_CHANGED = BASE << 5;
    public static final int MSG_CITY_ADD = BASE << 6;
    public static final int MSG_CITY_DELETE = BASE << 7;
    public static final int MSG_ANIMATION = BASE << 8;
    public static final int MSG_CIRCLE = BASE << 9;

    public int what;

    public int arg1;

    public String arg2;

    public Object obj;
}
