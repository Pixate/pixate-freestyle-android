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

import android.test.AndroidTestCase;
import android.widget.TextView;

import com.pixate.mocks.MockAdapter;
import com.pixate.mocks.MockStyleable;
import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;
import com.pixate.pxengine.styling.stylers.PXStylerBase.PXStylerInvocation;

public class PXFontStylerTests extends AndroidTestCase {

    public PXFontStylerTests() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PXStyleAdapter.initDefaultStyleAdapters();
    }

    public void testFontFamily() {
        PXStyleAdapter adapter = new MockAdapter(new PXFontStyler(new PXStylerInvocation() {
            @Override
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                ((MockStyleable) view).put("fontFamily", context.getFontName());
            }
        }));

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);

        PXStylesheet.getStyleSheetFromSource("* {font-family: serif;}", PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        assertEquals("serif", styleable.get("fontFamily"));
    }

    public void testFontSize() {
        PXStyleAdapter adapter = new MockAdapter(new PXFontStyler(new PXStylerInvocation() {
            @Override
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                ((MockStyleable) view).put("fontSize", context.getFontSize());
            }
        }));

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);

        PXStylesheet.getStyleSheetFromSource("* {font-size: 17px;}", PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        assertEquals(17.0f, (Float) styleable.get("fontSize"), 0.001f);
    }

    public void testFontStretch() {
        PXStyleAdapter adapter = new MockAdapter(new PXFontStyler(new PXStylerInvocation() {
            @Override
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                ((MockStyleable) view).put("fontStretch", context.getFontStretch());
            }
        }));

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);

        PXStylesheet.getStyleSheetFromSource("* {font-stretch: ultra-expanded;}", PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        assertEquals("ultra-expanded", styleable.get("fontStretch"));
    }

    // For the below tests we can get a bit more fancy because Android's
    // Typeface class actually has .isBold and isItalic methods, whereas
    // it has no methods or fields re font family or size.

    public void testFontStyleItalic() {

        PXStylesheet.getStyleSheetFromSource("* {font-style: italic;}", PXStyleSheetOrigin.APPLICATION);
        TextView tv = new TextView(this.getContext());
        PXStyleUtils.updateStyle(tv);
        assertTrue(tv.getTypeface().isItalic());
    }

    public void testFontStyleNotItalic() {
        PXStylesheet.getStyleSheetFromSource("* {font-style: normal;}", PXStyleSheetOrigin.APPLICATION);
        TextView tv = new TextView(this.getContext());
        PXStyleUtils.updateStyle(tv);
        assertFalse(tv.getTypeface().isItalic());
    }

    public void testFontWeightBold() {
        PXStylesheet.getStyleSheetFromSource("* {font-weight: bold;}", PXStyleSheetOrigin.APPLICATION);
        TextView tv = new TextView(this.getContext());
        PXStyleUtils.updateStyle(tv);
        assertTrue(tv.getTypeface().isBold());
    }

    public void testFontWeightNotBold() {
        PXStylesheet.getStyleSheetFromSource("* {font-weight: normal;}", PXStyleSheetOrigin.APPLICATION);
        TextView tv = new TextView(this.getContext());
        PXStyleUtils.updateStyle(tv);
        assertFalse(tv.getTypeface().isBold());
    }
}
