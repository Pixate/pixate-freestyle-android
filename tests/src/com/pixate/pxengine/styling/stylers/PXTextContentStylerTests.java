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

import com.pixate.pxengine.styling.PXStyleUtils;
import com.pixate.pxengine.styling.PXStylesheet;
import com.pixate.pxengine.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;

public class PXTextContentStylerTests extends AndroidTestCase {

    private static final String mTestText = "Character is much easier kept than recovered.";

    public PXTextContentStylerTests() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PXStyleAdapter.initDefaultStyleAdapters();
    }

    public void testTextContent() {
        TextView textView = new TextView(this.getContext());

        PXStylesheet.getStyleSheetFromSource("* {text: '" + mTestText + "';}",
                PXStyleSheetOrigin.APPLICATION);
        PXStyleUtils.updateStyle(textView);
        assertEquals(mTestText, textView.getText());
    }

}
