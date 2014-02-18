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

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSelector;
import com.pixate.freestyle.util.PXLog;

/**
 * PXAdjacentSiblingCombinator implements the adjacent sibling combinator. This
 * combinator requires that its selector on its right-hand side must match
 * immediately after its left-hand selector, where the two matches must be
 * against siblings
 */
public class PXAdjacentSiblingCombinator extends PXCombinatorBase {

    /**
     * Constructs a new PXAdjacentSiblingCombinator
     * 
     * @param lhs
     * @param rhs
     */
    public PXAdjacentSiblingCombinator(PXSelector lhs, PXSelector rhs) {
        super(lhs, rhs);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.combinators.PXCombinatorBase#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return "ADJACENT_SIBLING_COMBINATOR";
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.combinators.PXCombinatorBase#matches(java
     * .lang.Object)
     */
    @Override
    public boolean matches(Object element) {
        boolean result = false;
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(element);

        if (rhs.matches(element)) {
            Object parent = styleAdapter.getParent(element);

            if (PXStyleAdapter.isStyleable(parent)) {
                Object previousSibling = styleAdapter.getPreviousSibling(element);
                if (previousSibling != null && PXStyleAdapter.isStyleable(previousSibling)) {
                    result = lhs.matches(previousSibling);
                }
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXAdjacentSiblingCombinator.class.getSimpleName(), "%s matched %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXAdjacentSiblingCombinator.class.getSimpleName(), "%s did not match %s",
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
        return String.format("%s + %s", lhs, rhs);
    }
}
