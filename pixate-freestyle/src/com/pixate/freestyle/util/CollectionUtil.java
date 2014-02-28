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

import java.util.Collection;

public class CollectionUtil {

    /**
     * Returns a String representation of the given collection by separating the
     * elements with the given separator.
     * 
     * @param collection
     * @param separator
     * @return
     */
    public static String toString(Collection<? extends Object> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int count = collection.size();
        for (Object obj : collection) {
            builder.append(obj);
            if (--count > 0) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    /**
     * Returns <code>true</code> if the given collection is <code>null</code> or
     * empty.
     * 
     * @param collection
     * @return <code>true</code> if the given collection is <code>null</code> or
     *         empty; <code>false</code> otherwise.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
