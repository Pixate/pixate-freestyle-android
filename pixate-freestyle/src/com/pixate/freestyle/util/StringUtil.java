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

import java.util.Locale;

public class StringUtil {

    public static final String EMPTY = "";

    /**
     * Returns <code>true</code> if the given string is <code>null</code> or
     * empty.
     * 
     * @param str
     * @return <code>true</code> if the given string is <code>null</code> or
     *         empty; <code>false</code> otherwise.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Returns <code>true</code> if the given string, when lower-cased, is
     * exactly "true" or "yes".
     * 
     * @param value
     * @return <code>true</code> if the given string, when lower-cased, is
     *         exactly "true" or "yes", else <code>false</code>.
     */
    public static boolean toBoolean(final String value) {
        if (isEmpty(value)) {
            return false;
        }

        // Specify locale per Android lint suggestions.
        String lc = value.toLowerCase(Locale.US);
        return "true".equals(lc) || "yes".equals(lc);
    }

    /**
     * Join a string array into a single string. The given delimiter will be
     * inserted between the string parts.
     * 
     * @param parts
     * @param delimiter
     * @return a joint string.
     */
    public static String join(String[] parts, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (String s : parts) {
            builder.append(s).append(delimiter);
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - delimiter.length());
        }
        return EMPTY;
    }

    /**
     * Join a strings {@link Iterable} into a single string. The given delimiter
     * will be inserted between the string parts.
     * 
     * @param parts
     * @param delimiter
     * @return a joint string.
     */
    public static String join(Iterable<String> parts, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (String s : parts) {
            builder.append(s).append(delimiter);
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - delimiter.length());
        }
        return EMPTY;
    }

    public static boolean contains(String[] components, String value) {
        if (components == null || value == null) {
            return false;
        }
        for (String str : components) {
            if (str.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Truncates the string to a particular length while appending ... to the
     * end.
     * 
     * @param text the string to be truncated
     * @param length the length to truncate to
     * @return the truncated string
     */
    public static String truncate(String text, int length) {
        if (text == null || text.length() <= length) {
            return text;
        }
        return text.substring(0, length - 3) + "...";
    }

    public static int compare(String lhs, String rhs) {
        if (lhs == null) {
            return rhs == null ? 0 : 1;
        }
        return rhs == null ? -1 : lhs.compareTo(rhs);
    }
}
