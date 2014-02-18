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
package com.pixate.pxengine.styling.stylers;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.Button;

import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;

public class PXOpacityStylerTests extends AndroidTestCase {

    public PXOpacityStylerTests() {
    }

    public void testOpacityStyler() {
        Button b = new Button(this.getContext());
        PXStylesheet.getStyleSheetFromSource("button { opacity: 0.6; }", PXStyleSheetOrigin.APPLICATION);
        PXStyleUtils.updateStyle(b);

        float actualAlpha;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actualAlpha = getAlphaHC(b);
        } else {
            actualAlpha = getAlphaGB(b);
        }

        assertEquals(0.6f, actualAlpha, 0.001f);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private float getAlphaHC(View view) {
        return view.getAlpha();
    }

    private float getAlphaGB(View view) {
        Drawable background = view.getBackground();

        if (background != null) {
            Class<?> cls = view.getBackground().getClass();
            while (cls != null && cls != Object.class) {
                try {
                    Field f = cls.getDeclaredField("mAlpha");
                    f.setAccessible(true);
                    Integer alpha = (Integer) f.get(background);
                    float fAlpha = alpha.floatValue() / 255f;
                    return fAlpha;
                } catch (Exception e) {
                    // No-op
                }
                cls = cls.getSuperclass();
            }
        }
        return Float.MIN_VALUE;
    }

}
