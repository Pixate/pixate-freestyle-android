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

import android.graphics.Color;
import android.test.AndroidTestCase;

import com.pixate.mocks.MockAdapter;
import com.pixate.mocks.MockStyleable;
import com.pixate.pxengine.cg.math.PXOffsets;
import com.pixate.pxengine.cg.paints.PXSolidPaint;
import com.pixate.pxengine.styling.PXRuleSet;
import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;
import com.pixate.util.Size;

public class PXFillStylerTests extends AndroidTestCase {
    private PXStyleAdapter insetsAdapter;
    private PXStyleAdapter paddingAdapter;

    public PXFillStylerTests() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        insetsAdapter = new MockAdapter(PXFillStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                PXOffsets inset = context.getInsets();
                ((MockStyleable) context.getStyleable()).put("backgroundInset", inset);
            };
        };

        paddingAdapter = new MockAdapter(PXFillStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                PXOffsets padding = context.getPadding();
                ((MockStyleable) context.getStyleable()).put("backgroundPadding", padding);
            };
        };

    }

    public void testBackgroundColor() {
        PXStyleAdapter adapter = new MockAdapter(PXFillStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                ((MockStyleable) context.getStyleable()).put("backgroundColor", context.getFill());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);

        MockStyleable styleable = new MockStyleable();

        PXStylesheet.getStyleSheetFromSource("* {background-color: red; }",
                PXStyleSheetOrigin.APPLICATION);

        PXStyleUtils.updateStyle(styleable);

        Object value = styleable.get("backgroundColor");

        assertNotNull(value);
        assertTrue(value instanceof PXSolidPaint);

        assertEquals(Color.RED, ((PXSolidPaint) value).getColor());

    }

    public void testBackgroundSize() {

        PXStyleAdapter adapter = new MockAdapter(PXFillStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                // In the styler context, the size is stored in a private field
                // and is used during getBackgroundImage. Don't want to make it
                // public just for this, so using reflection.
                Size size = null;
                Field f;
                try {
                    f = context.getClass().getDeclaredField("imageSize");
                    f.setAccessible(true);
                    size = (Size) f.get(context);
                } catch (NoSuchFieldException e) {
                    fail(e.getMessage());
                } catch (IllegalArgumentException e) {
                    fail(e.getMessage());
                } catch (IllegalAccessException e) {
                    fail(e.getMessage());
                }

                ((MockStyleable) context.getStyleable()).put("backgroundSize", size);
            };
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);

        PXStylesheet.getStyleSheetFromSource("* {background-size: 12px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object sizeValue = styleable.get("backgroundSize");

        assertNotNull(sizeValue);
        assertTrue(sizeValue instanceof Size);
        assertTrue(Size.isNonZero((Size) sizeValue));
        assertEquals(12.0f, ((Size) sizeValue).width, 0.001f);
    }

    public void testBackgroundInset() {

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), insetsAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-inset: 12px 13px 14px 15px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object insetValue = styleable.get("backgroundInset");

        assertNotNull(insetValue);
        assertTrue(insetValue instanceof PXOffsets);
        PXOffsets insets = (PXOffsets) insetValue;

        assertEquals(12.0f, insets.getTop(), 0.001);
        assertEquals(13.0f, insets.getRight(), 0.001);
        assertEquals(14.0f, insets.getBottom(), 0.001);
        assertEquals(15.0f, insets.getLeft(), 0.001);
    }

    public void testBackgroundInsetTop() {

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), insetsAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-inset-top: 20px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object insetValue = styleable.get("backgroundInset");

        assertNotNull(insetValue);
        assertTrue(insetValue instanceof PXOffsets);
        PXOffsets insets = (PXOffsets) insetValue;

        assertEquals(20.0f, insets.getTop(), 0.001);
    }

    public void testBackgroundInsetRight() {
        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), insetsAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-inset-right: 21px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object insetValue = styleable.get("backgroundInset");

        assertNotNull(insetValue);
        assertTrue(insetValue instanceof PXOffsets);
        PXOffsets insets = (PXOffsets) insetValue;

        assertEquals(21.0f, insets.getRight(), 0.001);
    }

    public void testBackgroundInsetBottom() {
        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), insetsAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-inset-bottom: 22px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object insetValue = styleable.get("backgroundInset");

        assertNotNull(insetValue);
        assertTrue(insetValue instanceof PXOffsets);
        PXOffsets insets = (PXOffsets) insetValue;

        assertEquals(22.0f, insets.getBottom(), 0.001);
    }

    public void testBackgroundInsetLeft() {
        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), insetsAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-inset-left: 23px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object insetValue = styleable.get("backgroundInset");

        assertNotNull(insetValue);
        assertTrue(insetValue instanceof PXOffsets);
        PXOffsets insets = (PXOffsets) insetValue;

        assertEquals(23.0f, insets.getLeft(), 0.001);
    }

    public void testBackgroundPadding() {

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), paddingAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-padding: 12px 13px 14px 15px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object paddingValue = styleable.get("backgroundPadding");

        assertNotNull(paddingValue);
        assertTrue(paddingValue instanceof PXOffsets);
        PXOffsets paddings = (PXOffsets) paddingValue;

        assertEquals(12.0f, paddings.getTop(), 0.001);
        assertEquals(13.0f, paddings.getRight(), 0.001);
        assertEquals(14.0f, paddings.getBottom(), 0.001);
        assertEquals(15.0f, paddings.getLeft(), 0.001);
    }

    public void testBackgroundTopPadding() {

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), paddingAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-top-padding: 20px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object paddingValue = styleable.get("backgroundPadding");

        assertNotNull(paddingValue);
        assertTrue(paddingValue instanceof PXOffsets);
        PXOffsets paddings = (PXOffsets) paddingValue;

        assertEquals(20.0f, paddings.getTop(), 0.001);
    }

    public void testBackgroundPaddingRight() {
        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), paddingAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-right-padding: 21px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object paddingValue = styleable.get("backgroundPadding");

        assertNotNull(paddingValue);
        assertTrue(paddingValue instanceof PXOffsets);
        PXOffsets paddings = (PXOffsets) paddingValue;

        assertEquals(21.0f, paddings.getRight(), 0.001);
    }

    public void testBackgroundPaddingBottom() {
        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), paddingAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-bottom-padding: 22px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object paddingValue = styleable.get("backgroundPadding");

        assertNotNull(paddingValue);
        assertTrue(paddingValue instanceof PXOffsets);
        PXOffsets paddings = (PXOffsets) paddingValue;

        assertEquals(22.0f, paddings.getBottom(), 0.001);
    }

    public void testBackgroundPaddingLeft() {
        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), paddingAdapter);

        PXStylesheet.getStyleSheetFromSource("* {background-left-padding: 23px; }",
                PXStyleSheetOrigin.APPLICATION);

        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);

        Object paddingValue = styleable.get("backgroundPadding");

        assertNotNull(paddingValue);
        assertTrue(paddingValue instanceof PXOffsets);
        PXOffsets paddings = (PXOffsets) paddingValue;

        assertEquals(23.0f, paddings.getLeft(), 0.001);
    }

}
