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
package com.pixate.freestyle.styling.combinators;

import java.util.List;

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSelector;
import com.pixate.freestyle.util.PXLog;

/**
 * PXSiblingCombinator implements the general sibling combinator. This
 * combinator requires that its selector on its right-hand side must match as a
 * sibling after its left-hand selector.
 */
public class PXSiblingCombinator extends PXCombinatorBase {

    public PXSiblingCombinator(PXSelector lhs, PXSelector rhs) {
        super(lhs, rhs);
    }

    @Override
    public String getDisplayName() {
        return "GENERAL_SIBLING_COMBINATOR";
    }

    @Override
    public boolean matches(Object element) {
        boolean result = false;

        if (rhs.matches(element)) {
            Object parent = PXStyleAdapter.getStyleAdapter(element).getParent(element);

            if (PXStyleAdapter.isStyleable(parent)) {
                List<Object> children = PXStyleAdapter.getStyleAdapter(parent).getElementChildren(parent);
                if (children != null && !children.isEmpty()) {
                    for (Object previousSibling : children) {
                        if (result || previousSibling == element) {
                            break;
                        }
                        if (PXStyleAdapter.isStyleable(previousSibling)) {
                            result = lhs.matches(previousSibling);
                        }
                    }
                }
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXSiblingCombinator.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXSiblingCombinator.class.getSimpleName(), "%s did not match %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s ~ %s", lhs, rhs);
    }
}
