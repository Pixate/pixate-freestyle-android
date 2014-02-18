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

import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.PXStyleUtils.PXStyleableChildrenInfo;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.PXLog;

/**
 * A PXPseudoClassPredicate is a selector that asks a true or false question of
 * the styleable attempting to be matched. These questions, or predicates,
 * determine position of the element among its siblings, whether this is the
 * root view, or if it has no children
 */
public class PXPseudoClassPredicate extends PXSelector {

    /**
     * The PXPseudoClassPredicateType enumeration specifies what test should be
     * performed during a match operation
     */
    public enum PXPseudoClassPredicateType {
        PREDICATE_ROOT,
        PREDICATE_FIRST_CHILD,
        PREDICATE_LAST_CHILD,
        PREDICATE_FIRST_OF_TYPE,
        PREDICATE_LAST_OF_TYPE,
        PREDICATE_ONLY_CHILD,
        PREDICATE_ONLY_OF_TYPE,
        PREDICATE_EMPTY
    }

    private PXPseudoClassPredicateType predicateType;;

    /**
     * Initialize a newly allocated instance, setting its operation type
     * 
     * @param type The predicate type
     */
    public PXPseudoClassPredicate(PXPseudoClassPredicateType type) {
        super(PXSpecificityType.CLASS_OR_ATTRIBUTE);
        this.predicateType = type;
    }

    public boolean matches(Object element) {
        boolean result = false;
        PXStyleableChildrenInfo info = PXStyleUtils.getChildrenInfoForStyleable(element);
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(element);
        switch (predicateType) {
            case PREDICATE_ROOT:
                // TODO: not sure how robust this test is
                result = styleAdapter.getParent(element) == null;
                break;

            case PREDICATE_FIRST_CHILD: {
                result = (info.childrenIndex == 1);
                break;
            }

            case PREDICATE_LAST_CHILD: {
                result = (info.childrenIndex == info.childrenCount);
                break;
            }

            case PREDICATE_FIRST_OF_TYPE: {
                result = (info.childrenOfTypeIndex == 1);
                break;
            }

            case PREDICATE_LAST_OF_TYPE: {
                result = (info.childrenOfTypeIndex == info.childrenOfTypeCount);
                break;
            }

            case PREDICATE_ONLY_CHILD: {
                result = (info.childrenCount == 1 && info.childrenIndex == 1);
                break;
            }

            case PREDICATE_ONLY_OF_TYPE: {
                result = (info.childrenOfTypeCount == 1 && info.childrenOfTypeIndex == 1);
                break;
            }

            case PREDICATE_EMPTY: {
                result = (styleAdapter.getChildCount(element) == 0);
                break;
            }
        }

        if (PXLog.isLogging()) {
            if (result) {
                PXLog.v(PXPseudoClassPredicate.class.getSimpleName(), "%s matched %s", toString(),
                        PXStyleUtils.getDescriptionForStyleable(element));
            } else {
                PXLog.v(PXPseudoClassPredicate.class.getSimpleName(), "%s did not match %s",
                        toString(), PXStyleUtils.getDescriptionForStyleable(element));
            }
        }
        return result;
    }

    @Override
    public void getSourceWithSourceWriter(PXSourceWriter writer) {
        writer.printIndent();
        writer.print("(PSEUDO_CLASS_PREDICATE ");
        writer.print(toString());
        writer.print(")");
    }

    @Override
    public String toString() {
        switch (predicateType) {
            case PREDICATE_ROOT:
                return ":root";
            case PREDICATE_FIRST_CHILD:
                return ":first-child";
            case PREDICATE_LAST_CHILD:
                return ":list-child";
            case PREDICATE_FIRST_OF_TYPE:
                return ":first-of-type";
            case PREDICATE_LAST_OF_TYPE:
                return ":last-of-type";
            case PREDICATE_ONLY_CHILD:
                return ":only-child";
            case PREDICATE_ONLY_OF_TYPE:
                return ":only-of-type";
            case PREDICATE_EMPTY:
                return ":empty";
            default:
                return "<uknown-pseudo-class-predicate";
        }
    }
}
