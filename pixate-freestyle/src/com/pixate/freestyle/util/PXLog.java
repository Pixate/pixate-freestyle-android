/*******************************************************************************
 * Copyright 2012-present Pixate, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pixate.freestyle.util;

import android.util.Log;

public class PXLog {

    /**
     * Returns <code>true</code> in case the log is logging any type of message.
     */
    public static boolean isLogging() {
        return false;// BuildConfig.DEBUG;
    }

    public static void i(String tag, String format, Object... args) {
        if (isLogging()) {
            Log.i(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args));
        }
    }

    public static void i(String tag, Throwable t, String format, Object... args) {
        if (isLogging()) {
            Log.i(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args), t);
        }
    }

    public static void e(String tag, String format, Object... args) {
        // log anyway
        Log.e(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args));
    }

    public static void e(String tag, Throwable t, String format, Object... args) {
        // log anyway
        Log.e(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args), t);
    }

    public static void d(String tag, String format, Object... args) {
        if (isLogging()) {
            Log.d(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args));
        }
    }

    public static void d(String tag, Throwable t, String format, Object... args) {
        if (isLogging()) {
            Log.d(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args), t);
        }
    }

    public static void v(String tag, String format, Object... args) {
        if (isLogging()) {
            Log.v(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args));
        }
    }

    public static void v(String tag, Throwable t, String format, Object... args) {
        if (isLogging()) {
            Log.v(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args), t);
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (isLogging()) {
            Log.w(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args));
        }
    }

    public static void w(String tag, Throwable t, String format, Object... args) {
        if (isLogging()) {
            Log.w(tag, ArrayUtil.isEmpty(args) ? format : String.format(format, args), t);
        }
    }
}
