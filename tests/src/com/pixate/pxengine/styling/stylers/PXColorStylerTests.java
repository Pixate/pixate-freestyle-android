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

import android.graphics.Color;
import android.test.AndroidTestCase;

import com.pixate.mocks.MockAdapter;
import com.pixate.mocks.MockStyleable;
import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;
import com.pixate.pxengine.styling.stylers.PXStylerBase.PXStylerInvocation;

public class PXColorStylerTests extends AndroidTestCase {

    public PXColorStylerTests() {
    }

    public void testColorStyler() {
        PXStyleAdapter adapter = new MockAdapter(new PXColorStyler(new PXStylerInvocation() {
            @Override
            public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                ((MockStyleable) view).put("color", (Integer) context.getPropertyValue("color"));
            }
        }));

        PXStyleAdapter.registerStyleAdapter(MockStyleable.class.getName(), adapter);
        MockStyleable styleable = new MockStyleable();
        PXStylesheet.getStyleSheetFromSource("* {color: red;}", PXStyleSheetOrigin.APPLICATION);
        PXStyleUtils.updateStyle(styleable);
        assertEquals(Color.RED, styleable.get("color"));
    }

}
