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
import java.util.Map;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.ViewUtil;

class WrappedInflaterFactory implements Factory {

    private static final String TAG = WrappedInflaterFactory.class.getSimpleName();

    /*
     * (non-Javadoc)
     * @see <a href=
     * "http://github.com/android/platform_frameworks_policies_base/blob/c5d93b3b6a/phone/com/android/internal/policy/impl/PhoneLayoutInflater.java#L27"
     * > Android source for these prefixes. </a>
     */
    private static final String[] sKnownClassPrefixes = new String[] { "android.widget.",
            "android.webkit.", "android.view." };

    private static Field sFactorySetField = null;

    private Factory mOriginalFactory = null;
    private LayoutInflater mInflater = null;

    WrappedInflaterFactory(Activity activity) {
        mInflater = activity.getLayoutInflater();
        mOriginalFactory = mInflater.getFactory();
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;

        if (mOriginalFactory != null) {
            view = mOriginalFactory.onCreateView(name, context, attrs);
        } else {
            try {
                if (-1 == name.indexOf(".")) {
                    for (int i = 0; i < sKnownClassPrefixes.length && view == null; i++) {
                        try {
                            view = mInflater.createView(name, sKnownClassPrefixes[i], attrs);
                        } catch (Exception e) {
                            // We do null check on view later.
                        }
                    }
                } else {
                    view = mInflater.createView(name, null, attrs);
                }
            } catch (Exception e) {
                return null;
            }
        }

        if (view != null) {
            ViewUtil.initView(view, attrs);
        }

        return view;

    }

    private static void initReflection() {
        try {
            sFactorySetField = LayoutInflater.class.getDeclaredField("mFactorySet");
            sFactorySetField.setAccessible(true);
        } catch (Exception e) {
            PXLog.e(TAG, e, "Unable to get LayoutInflater's mFactorySet field.");
        }
    }

    private static Map<Object, Boolean> activitiesWithWrappedInflater = new WeakHashMap<Object, Boolean>();

    static void applyTo(Activity activity) {
        if (activitiesWithWrappedInflater.containsKey(activity)) {
            // Already done this, so exit.
            return;
        }
        WrappedInflaterFactory newFactory = new WrappedInflaterFactory(activity);

        if (newFactory.mOriginalFactory != null) {
            /*
             * Need reflection because we need to change the mFactorySet boolean
             * field inside LayoutInflater. It'll be true if a factory is
             * already there, and if we call .setFactory() again we'll get an
             * exception indicating that a factory has already been set. So we
             * need to change the field's value to false first before passing
             * our special Factory to setFactory();
             */
            if (sFactorySetField == null) {
                initReflection();
                if (sFactorySetField == null) {
                    PXLog.w(TAG, "Unable to replace the LayoutInflater's Factory. "
                            + "This view will likely not be styled with CSS.");
                    return;
                }
            }

            try {
                sFactorySetField.setBoolean(activity.getLayoutInflater(), false);
            } catch (Exception e) {
                PXLog.e(TAG, e, "Unable to replace the LayoutInflater's Factory. "
                        + "This view will likely not be styled with CSS.");
                return;
            }
        }

        activity.getLayoutInflater().setFactory(newFactory);
        activitiesWithWrappedInflater.put(activity, Boolean.TRUE);
    }

}
