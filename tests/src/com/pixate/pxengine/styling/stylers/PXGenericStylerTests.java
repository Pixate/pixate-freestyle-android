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

import java.util.HashMap;
import java.util.Map;

import android.test.AndroidTestCase;

import com.pixate.mocks.MockAdapter;
import com.pixate.mocks.MockStyleable;
import com.pixate.pxengine.styling.PXDeclaration;
import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;
import com.pixate.pxengine.styling.stylers.PXStylerBase.PXDeclarationHandler;
import com.pixate.pxengine.styling.stylers.PXStylerBase.PXStylerInvocation;

public class PXGenericStylerTests extends AndroidTestCase {

    private static final String TEXT_VAL = "This is some text";
    private static final String STYLESHEET = "* { text: '" + TEXT_VAL + "'; }";

    public PXGenericStylerTests() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Map<String, PXDeclarationHandler> handlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();
        PXStylerInvocation invocation = new PXStylerInvocation() {
            @Override
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                ((MockStyleable) view).put("text", context.getText());
            }
        };

        handlers.put("text", new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                stylerContext.setText(declaration.getStringValue());
            }
        });

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), new MockAdapter(
                new PXGenericStyler(handlers, invocation)));

        PXStylesheet.getStyleSheetFromSource(STYLESHEET, PXStyleSheetOrigin.APPLICATION);
    }

    public void testGenericStyler() {
        MockStyleable styleable = new MockStyleable();

        PXStyleUtils.updateStyle(styleable);
        assertEquals(TEXT_VAL, styleable.get("text"));
    }

}
