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

import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.selectors.PXSelector;
import com.pixate.freestyle.styling.selectors.PXSpecificity;

/**
 * The PXCombinatorBase is an abstract base class used to capture the common
 * functionality of all combinators in one place.
 */
public abstract class PXCombinatorBase extends PXSelector implements PXCombinator {

    /**
     * A text representation of this combinator used for debugging and testing
     */
    protected PXSelector lhs;
    protected PXSelector rhs;

    /**
     * Initialize a new instance with the specified left- and right-side
     * selectors
     * 
     * @param lhs The selector to the left of this combinator
     * @param rhs The selector to the right of this combinator
     */
    public PXCombinatorBase(PXSelector lhs, PXSelector rhs) {
        super(null);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void setLhs(PXSelector lhs) {
        this.lhs = lhs;
    }

    public PXSelector getLhs() {
        return lhs;
    }

    public void setRhs(PXSelector rhs) {
        this.rhs = rhs;
    }

    public PXSelector getRhs() {
        return rhs;
    }

    public void incrementSpecificity(PXSpecificity specificity) {
        lhs.incrementSpecificity(specificity);
        rhs.incrementSpecificity(specificity);
    }

    public abstract String getDisplayName();

    public abstract boolean matches(Object element);

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.parsing.PXSourceEmitter#getSourceWithSourceWriter
     * (com.pixate.freestyle.parsing.PXSourceWriter)
     */
    public void getSourceWithSourceWriter(PXSourceWriter writer) {
        writer.printIndent();
        writer.print("(");
        writer.print(getDisplayName());
        writer.println();
        writer.increaseIndent();

        lhs.getSourceWithSourceWriter(writer);
        writer.println();
        rhs.getSourceWithSourceWriter(writer);

        writer.print(")");
        writer.decreaseIndent();
    }
}
