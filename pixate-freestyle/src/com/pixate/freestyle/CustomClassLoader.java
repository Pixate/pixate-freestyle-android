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
package com.pixate.freestyle;

import android.content.Context;
import android.widget.Button;

import com.pixate.freestyle.util.PXLog;

/**
 * <p>
 * A custom class loader that lets us intercept loadClass(String), check if the
 * class is a View, and replace it with our own View class.
 * </p>
 * <p>
 * This class might not be necessary, as we've found that we might be able to
 * achieve what we need just by hijacking the layout inflater and setting view
 * hierarchy change callbacks, rather than replacing the class loader.
 * </p>
 * 
 * @author Bill Dawson
 */
public class CustomClassLoader extends ClassLoader {
    private static final String TAG = CustomClassLoader.class.getSimpleName();
    private ClassLoader mBaseLoader;

    public CustomClassLoader(ClassLoader base) {
        mBaseLoader = base;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        PXLog.d(TAG, "loadClass: " + className);
        String newClassName = className;
        if (Button.class.getName().equals(newClassName)) {
            // Code to load our own Button class.
        } // etc. etc.
        return mBaseLoader.loadClass(newClassName);
    }

    /**
     * Replace the class loader in the given Android context.
     * 
     * @param context The Android {@link android.content.Context Context}.
     */
    public static void useFor(Context context) {
        ClassLoaderUtil.changeClassLoader(context);
    }

}
