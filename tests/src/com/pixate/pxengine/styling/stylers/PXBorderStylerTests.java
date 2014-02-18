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

import com.pixate.mocks.MockAdapter;
import com.pixate.mocks.MockStyleable;
import com.pixate.pxengine.cg.paints.PXPaint;
import com.pixate.pxengine.cg.paints.PXSolidPaint;
import com.pixate.pxengine.cg.shapes.PXBoxModel;
import com.pixate.pxengine.styling.PXRuleSet;
import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;
import com.pixate.pxengine.styling.infos.PXBorderInfo.PXBorderStyle;
import com.pixate.util.SVGColors;

public class PXBorderStylerTests extends AndroidTestCase {

    private static final int RED = SVGColors.get("red");
    private static final int BLUE = SVGColors.get("blue");
    private static final int GREEN = SVGColors.get("green");
    private static final int YELLOW = SVGColors.get("yellow");

    public PXBorderStylerTests() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PXStyleAdapter.initDefaultStyleAdapters();
    }

    public void testBorderWithAllSettings() {
        PXStylesheet.getStyleSheetFromSource("* {border: 17px dashed red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(17.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(17.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(17.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(17.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderTopPaint()).getColor());
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderRightPaint()).getColor());
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderBottomPaint()).getColor());
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderLeftPaint()).getColor());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderTopStyle());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderRightStyle());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderBottomStyle());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWithOnlyWidth() {
        PXStylesheet.getStyleSheetFromSource("* {border: 18px;}", PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(18.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(18.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(18.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(18.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertNull(boxModel.getBorderTopPaint());
                assertNull(boxModel.getBorderRightPaint());
                assertNull(boxModel.getBorderBottomPaint());
                assertNull(boxModel.getBorderLeftPaint());
                assertNull(boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWithOnlyStyle() {
        PXStylesheet.getStyleSheetFromSource("* {border: dotted;}", PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertNull(boxModel.getBorderTopPaint());
                assertNull(boxModel.getBorderRightPaint());
                assertNull(boxModel.getBorderBottomPaint());
                assertNull(boxModel.getBorderLeftPaint());
                assertEquals(PXBorderStyle.DOTTED, boxModel.getBorderTopStyle());
                assertEquals(PXBorderStyle.DOTTED, boxModel.getBorderRightStyle());
                assertEquals(PXBorderStyle.DOTTED, boxModel.getBorderBottomStyle());
                assertEquals(PXBorderStyle.DOTTED, boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWithOnlyColor() {
        PXStylesheet.getStyleSheetFromSource("* {border: blue;}", PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);

                PXPaint paint = boxModel.getBorderTopPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderRightPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderBottomPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderLeftPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                assertNull(boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderTopWithAllSettings() {
        PXStylesheet.getStyleSheetFromSource("* {border-top: 17px dashed red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(17.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderTopPaint()).getColor());
                assertNull(boxModel.getBorderRightPaint());
                assertNull(boxModel.getBorderBottomPaint());
                assertNull(boxModel.getBorderLeftPaint());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRightWithAllSettings() {
        PXStylesheet.getStyleSheetFromSource("* {border-right: 17px dashed red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(17.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertNull(boxModel.getBorderTopPaint());
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderRightPaint()).getColor());
                assertNull(boxModel.getBorderBottomPaint());
                assertNull(boxModel.getBorderLeftPaint());
                assertNull(boxModel.getBorderTopStyle());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderBottomWithAllSettings() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom: 17px dashed red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(17.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertNull(boxModel.getBorderTopPaint());
                assertNull(boxModel.getBorderRightPaint());
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderBottomPaint()).getColor());
                assertNull(boxModel.getBorderLeftPaint());
                assertNull(boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderLeftWithAllSettings() {
        PXStylesheet.getStyleSheetFromSource("* {border-left: 17px dashed red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(17.0f, boxModel.getBorderLeftWidth(), 0.001f);
                assertNull(boxModel.getBorderTopPaint());
                assertNull(boxModel.getBorderRightPaint());
                assertNull(boxModel.getBorderBottomPaint());
                assertEquals(RED, ((PXSolidPaint) boxModel.getBorderLeftPaint()).getColor());
                assertNull(boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());
                assertEquals(PXBorderStyle.DASHED, boxModel.getBorderLeftStyle());
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusAllOneValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-radius: 5px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(5.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(5.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(5.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(5.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusAllTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-radius: 3px/4px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(3.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(3.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(3.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(3.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusTopLeft().height, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusTopRight().height, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusBottomRight().height, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusBottomLeft().height, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusTopLeftOneValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-left-radius: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusTopLeftTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-left-radius: 1px 4px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusTopLeft().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().height, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusTopRightOneValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-right-radius: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(1.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusTopRightTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-right-radius: 1px 4px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(1.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopLeft().height, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusTopRight().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().height, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusBottomRightOneValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-right-radius: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(1.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusBottomRightTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-right-radius: 1px 4px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(1.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopLeft().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().height, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusBottomRight().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomLeft().height, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusBottomLeftOneValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-left-radius: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(1.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRadiusBottomLeftTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-left-radius: 1px 4px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getRadiusTopLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().width, 0.001f);
                assertEquals(1.0f, boxModel.getRadiusBottomLeft().width, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopLeft().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusTopRight().height, 0.001f);
                assertEquals(0.0f, boxModel.getRadiusBottomRight().height, 0.001f);
                assertEquals(4.0f, boxModel.getRadiusBottomLeft().height, 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWidthAllSingleValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-width: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWidthAllTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-width: 1px 2px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(2.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(2.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWidthAllThreeValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-width: 1px 2px 3px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(2.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(3.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(2.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderWidthAllFourValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-width: 1px 2px 3px 4px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(2.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(3.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(4.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderTopWidth() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-width: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(1.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRightWidth() {
        PXStylesheet.getStyleSheetFromSource("* {border-right-width: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderBottomWidth() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-width: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderLeftWidth() {
        PXStylesheet.getStyleSheetFromSource("* {border-left-width: 1px;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();
                assertEquals(0.0f, boxModel.getBorderTopWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderRightWidth(), 0.001f);
                assertEquals(0.0f, boxModel.getBorderBottomWidth(), 0.001f);
                assertEquals(1.0f, boxModel.getBorderLeftWidth(), 0.001f);
            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderColorAllSingleValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-color: red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXPaint paint = boxModel.getBorderTopPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderRightPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderBottomPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderLeftPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderColorAllTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-color: red blue;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXPaint paint = boxModel.getBorderTopPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderRightPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderBottomPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderLeftPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderColorAllThreeValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-color: red blue green;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXPaint paint = boxModel.getBorderTopPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderRightPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderBottomPaint();
                assertNotNull(paint);
                assertEquals(GREEN, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderLeftPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderColorAllFourValues() {

        PXStylesheet.getStyleSheetFromSource("* {border-color: red blue green yellow;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXPaint paint = boxModel.getBorderTopPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderRightPaint();
                assertNotNull(paint);
                assertEquals(BLUE, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderBottomPaint();
                assertNotNull(paint);
                assertEquals(GREEN, ((PXSolidPaint) paint).getColor());

                paint = boxModel.getBorderLeftPaint();
                assertNotNull(paint);
                assertEquals(YELLOW, ((PXSolidPaint) paint).getColor());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderTopColor() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-color: red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXPaint paint = boxModel.getBorderTopPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                assertNull(boxModel.getBorderRightPaint());
                assertNull(boxModel.getBorderBottomPaint());
                assertNull(boxModel.getBorderLeftPaint());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRightColor() {
        PXStylesheet.getStyleSheetFromSource("* {border-right-color: red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                assertNull(boxModel.getBorderTopPaint());

                PXPaint paint = boxModel.getBorderRightPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                assertNull(boxModel.getBorderBottomPaint());
                assertNull(boxModel.getBorderLeftPaint());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderBottomColor() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-color: red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                assertNull(boxModel.getBorderTopPaint());
                assertNull(boxModel.getBorderRightPaint());

                PXPaint paint = boxModel.getBorderBottomPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

                assertNull(boxModel.getBorderLeftPaint());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderLeftColor() {
        PXStylesheet.getStyleSheetFromSource("* {border-left-color: red;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                assertNull(boxModel.getBorderTopPaint());
                assertNull(boxModel.getBorderRightPaint());
                assertNull(boxModel.getBorderBottomPaint());

                PXPaint paint = boxModel.getBorderLeftPaint();
                assertNotNull(paint);
                assertEquals(RED, ((PXSolidPaint) paint).getColor());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderStyleAllSingleValue() {
        PXStylesheet.getStyleSheetFromSource("* {border-style: dashed;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXBorderStyle style = boxModel.getBorderTopStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderRightStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderBottomStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderLeftStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderStyleAllTwoValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-style: dashed dotted;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXBorderStyle style = boxModel.getBorderTopStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderRightStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOTTED, style);

                style = boxModel.getBorderBottomStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderLeftStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOTTED, style);

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderStyleAllThreeValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-style: dashed dotted double;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXBorderStyle style = boxModel.getBorderTopStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderRightStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOTTED, style);

                style = boxModel.getBorderBottomStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOUBLE, style);

                style = boxModel.getBorderLeftStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOTTED, style);

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderStyleAllFourValues() {
        PXStylesheet.getStyleSheetFromSource("* {border-style: dashed dotted double solid;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXBorderStyle style = boxModel.getBorderTopStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                style = boxModel.getBorderRightStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOTTED, style);

                style = boxModel.getBorderBottomStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DOUBLE, style);

                style = boxModel.getBorderLeftStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.SOLID, style);

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderTopStyle() {
        PXStylesheet.getStyleSheetFromSource("* {border-top-style: dashed;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                PXBorderStyle style = boxModel.getBorderTopStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                assertNull(boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderRightStyle() {
        PXStylesheet.getStyleSheetFromSource("* {border-right-style: dashed;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                assertNull(boxModel.getBorderTopStyle());

                PXBorderStyle style = boxModel.getBorderRightStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                assertNull(boxModel.getBorderBottomStyle());
                assertNull(boxModel.getBorderLeftStyle());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderBottomStyle() {
        PXStylesheet.getStyleSheetFromSource("* {border-bottom-style: dashed;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                assertNull(boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());

                PXBorderStyle style = boxModel.getBorderBottomStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

                assertNull(boxModel.getBorderLeftStyle());

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }

    public void testBorderLeftStyle() {
        PXStylesheet.getStyleSheetFromSource("* {border-left-style: dashed;}",
                PXStyleSheetOrigin.APPLICATION);
        MockStyleable styleable = new MockStyleable();
        MockAdapter adapter = new MockAdapter(PXBorderStyler.getInstance()) {
            @Override
            public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
                super.updateStyle(ruleSet, context);
                PXBoxModel boxModel = context.getBoxModel();

                assertNull(boxModel.getBorderTopStyle());
                assertNull(boxModel.getBorderRightStyle());
                assertNull(boxModel.getBorderBottomStyle());

                PXBorderStyle style = boxModel.getBorderLeftStyle();
                assertNotNull(style);
                assertEquals(PXBorderStyle.DASHED, style);

            }
        };

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        PXStyleUtils.updateStyle(styleable);
    }
}
