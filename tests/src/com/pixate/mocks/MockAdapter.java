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
package com.pixate.mocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pixate.pxengine.styling.PXRuleSet;
import com.pixate.pxengine.styling.adapters.PXStyleAdapter;
import com.pixate.pxengine.styling.stylers.PXStyler;
import com.pixate.pxengine.styling.stylers.PXStylerContext;

public class MockAdapter extends PXStyleAdapter {
    private List<PXStyler> mStylers;

    public MockAdapter() {
    }

    public MockAdapter(PXStyler testStyler) {
        mStylers = new ArrayList<PXStyler>(1);
        mStylers.add(testStyler);
    }

    @Override
    protected List<PXStyler> createStylers() {
        return mStylers;
    }

    @Override
    public List<PXStyler> getStylers() {
        return mStylers;
    }

    // Delegate the updateStyle to the method that accepts collection of styles.
    public void updateStyle(PXRuleSet ruleSet, PXStylerContext context) {
        super.updateStyle(Arrays.asList(ruleSet), Arrays.asList(context));
    }
}
