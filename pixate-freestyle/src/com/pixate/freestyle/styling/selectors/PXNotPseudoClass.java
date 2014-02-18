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

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.util.PXLog;

public class PXNotPseudoClass extends PXSelector {

    private PXSelector expression;

    /**
     * Initializer a new instance with the specified expression
     * 
     * @param expression The PXElementMatcher expression to negate
     */
    public PXNotPseudoClass(PXSelector expression) {
        super(null);
        this.expression = expression;
    }

    public boolean matches(Object element) {
        boolean result = false;

        // NOTE: I can't find a definition for what to do with an empty :not();
        // however, the W3C selector level 3 tests,
        // specifically #49, implies that *|*:not() should match nothing, so I'm
        // assuming no expression means :not()
        // always fails

        if (expression != null) {
            result = !expression.matches(element);
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXNotPseudoClass.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXNotPseudoClass.class.getSimpleName(), "%s did not match %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.selectors.PXSelector#incrementSpecificity
     * (com.pixate.freestyle.styling.selectors.PXSpecificity)
     */
    @Override
    public void incrementSpecificity(PXSpecificity specificity) {
        if (expression != null) {
            expression.incrementSpecificity(specificity);
        }
    }

    /**
     * Returns the expression to be negated during matching. Or, said another
     * way, the expression that must fail for this selector to succeed during
     * matching.
     */
    public PXSelector getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        if (expression != null) {
            return String.format(":not(%s)", expression);
        } else {
            return ":not()";
        }
    }
}
