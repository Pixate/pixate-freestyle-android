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
package com.pixate.freestyle.styling.fonts;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.pixate.freestyle.util.PXLog;

/**
 * PXFontRegistry is a singleton responsible for mapping a font family, style,
 * and weight to a specific instance of an Android
 * {@link android.graphics.Typeface}. Fall-back mechanisms are used when a
 * specific configuration is not available. All lookups are cached, so future
 * lookups are quite fast.
 */
public class PXFontRegistry {

    private static String TAG = PXFontRegistry.class.getSimpleName();

    /**
     * A map that hold {@link Typeface} instances that are mapped by a font
     * "key. A key is defined as "font-family:weight:style" string.
     */
    public static final Map<String, Typeface> FONT_BY_KEY = new HashMap<String, Typeface>();

    /**
     * A map that hold {@link Typeface} instances that are mapped by a URL .
     */
    public static final Map<URL, Typeface> FONT_BY_URL = new HashMap<URL, Typeface>();

    private PXFontRegistry() {
        // No-op. Avoid instantiation.
    }

    public static Typeface getTypeface(String family, String weight, String style) {
        String key = deriveKey(family, weight, style);
        Typeface match = FONT_BY_KEY.get(key);
        Typeface result;

        if (match != null) {
            result = match;

        } else {
            int typefaceStyle = Typeface.NORMAL; // default

            if ("bold".equals(weight) && "italic".equals(style)) {
                typefaceStyle = Typeface.BOLD_ITALIC;
            } else if ("bold".equals(weight)) {
                typefaceStyle = Typeface.BOLD;
            } else if ("italic".equals(style)) {
                typefaceStyle = Typeface.ITALIC;
            }

            result = Typeface.create(family, typefaceStyle);
            FONT_BY_KEY.put(key, result);
        }

        return result;

    }

    public static Typeface getTypeface(AssetManager manager, URL path) {
        Typeface match = FONT_BY_URL.get(path);
        if (match != null) {
            return match;
        }
        // We don't have it. Try to load that font.
        Typeface typeface = Typeface.createFromAsset(manager, path.toString());
        if (typeface != null) {
            FONT_BY_URL.put(path, typeface);
        } else {
            PXLog.e(TAG, "Could not load a Typeface from " + path);
        }
        return typeface;
    }

    private static String deriveKey(String family, String weight, String style) {
        return String.format("%s:%s:%s", family, weight, style);
    }
}
