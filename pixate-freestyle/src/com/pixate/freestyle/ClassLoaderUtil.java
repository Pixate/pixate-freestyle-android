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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;

import com.pixate.freestyle.util.PXLog;

/**
 * <p>
 * For replacing the default class loader and replacing it with an instance of
 * {@link CustomClassLoader}.
 * </p>
 * <p>
 * This class might not be necessary, as we've found that we might be able to
 * achieve what we need just by hijacking the layout inflater and setting view
 * hierarchy change callbacks, rather than replacing the class loader.
 * </p>
 * <p>
 * This class is not public. It's used by the CustomClassLoader class in the
 * same package.
 * </p>
 * 
 * @author Bill Dawson
 */
class ClassLoaderUtil {
    private static final String TAG = ClassLoaderUtil.class.getSimpleName();

    private static Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<Class<?>, Map<String, Field>>();

    private static Field getField(Object obj, String name) {
        Class<?> c = obj.getClass();
        Map<String, Field> map = fieldCache.get(c);
        if (map == null) {
            map = new HashMap<String, Field>();
            fieldCache.put(c, map);
        }

        Field f = map.get(name);
        if (f != null) {
            return f;
        }

        while (c != null) {
            try {
                f = c.getDeclaredField(name);
                f.setAccessible(true);
                map.put(name, f);
                return f;
            } catch (Exception e) {
            } finally {
                c = c.getSuperclass();
            }
        }

        PXLog.w(TAG, "Unable to get field %s of $s.", name, obj.getClass().getName());
        return null;
    }

    static void changeClassLoader(Context context) {
        Application app;
        if (context instanceof Application) {
            app = (Application) context;
        } else if (context instanceof Activity) {
            app = ((Activity) context).getApplication();
        } else if (context instanceof Service) {
            app = ((Service) context).getApplication();
        } else {
            PXLog.d(TAG, "Did not change class loader. Passed Context is %s.", context.getClass()
                    .getName());
            return;
        }

        try {
            Field f = getField(app, "mLoadedApk");
            if (f == null) {
                PXLog.w(TAG, "Unable to change the class loader because the application's "
                        + "mLoadedApk field could not be found.");
                return;
            }

            Object loadedApk = f.get(app);
            if (loadedApk == null) {
                PXLog.w(TAG, "Unable to change the class loader because the application's "
                        + "mLoadedApk field was null.");
                return;
            }

            f = getField(loadedApk, "mClassLoader");
            if (f == null) {
                PXLog.w(TAG, "Unable to change the class loader because the LoadedApk "
                        + "class's mClassLoader field could not be found.");
                return;
            }

            Object classLoader = f.get(loadedApk);

            if (classLoader == null) {
                PXLog.w(TAG, "Unable to change the class loader because the LoadedApk "
                        + "class's mClassLoader field was null.");
                return;
            }

            ClassLoader newClassLoader = new CustomClassLoader((ClassLoader) classLoader);

            f.set(loadedApk, newClassLoader);

        } catch (Exception e) {
            PXLog.e(TAG, e, "Unable to replace class loader because of Exception.");
        }
    }

}
