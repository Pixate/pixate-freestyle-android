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
package com.pixate.freestyle.styling.selectors;

import java.util.List;

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.PXLog;

/**
 * A PXPseudoClassSelector is used to represent a specific state of an element,
 * for purposes of styling.
 */
public class PXPseudoClassSelector extends PXSelector {

    private String className;

    public PXPseudoClassSelector(String className) {
        super(PXSpecificityType.CLASS_OR_ATTRIBUTE);
        this.className = className;
    }

    @Override
    public boolean matches(Object element) {
        boolean result = false;
        List<String> pseudoClasses = PXStyleAdapter.getStyleAdapter(element).getSupportedPseudoClasses(element);
        result = (pseudoClasses != null && pseudoClasses.contains(className));

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXPseudoClassSelector.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXPseudoClassSelector.class.getSimpleName(), "%s did not match %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            }
        }

        return result;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return ':' + className;
    }
}
