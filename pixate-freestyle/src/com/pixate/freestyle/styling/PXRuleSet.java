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
package com.pixate.freestyle.styling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.combinators.PXCombinator;
import com.pixate.freestyle.styling.selectors.PXSelector;
import com.pixate.freestyle.styling.selectors.PXSpecificity;
import com.pixate.freestyle.styling.selectors.PXTypeSelector;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.CollectionUtil;

public class PXRuleSet extends PXDeclarationContainer {

    private PXSpecificity specificity;
    private List<PXSelector> selectors;

    public PXRuleSet() {
        specificity = new PXSpecificity();
    }

    public void addSelector(PXSelector selector) {
        if (selector != null) {
            if (selectors == null) {
                selectors = new ArrayList<PXSelector>(3);
            }
            selectors.add(selector);
            selector.incrementSpecificity(specificity);
        }
    }

    public List<PXSelector> getSelectors() {
        return selectors == null ? Collections.<PXSelector>emptyList() : new ArrayList<PXSelector>(selectors);
    }

    public PXTypeSelector getTargetTypeSelector() {
        PXTypeSelector result = null;

        if (!CollectionUtil.isEmpty(selectors)) {
            PXSelector candidate = selectors.get(0);

            if (candidate != null) {
                if (candidate instanceof PXCombinator) {
                    PXCombinator combinator = (PXCombinator) candidate;

                    // NOTE: PXStylesheetParser grows expressions down and to
                    // the left. This guarantees that the top-most nodes
                    // RHS will be a type selector, and will be the last in the
                    // expression
                    result = (PXTypeSelector) combinator.getRhs();
                } else if (candidate instanceof PXTypeSelector) {
                    result = (PXTypeSelector) candidate;
                }
            }
        }

        return result;
    }

    public boolean matches(Object element) {
        if (!CollectionUtil.isEmpty(selectors)) {
            for (PXSelector selector : selectors) {
                if (!selector.matches(element)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void setSpecificity(PXSpecificityType specificity, int value) {
        this.specificity.setSpecificity(specificity, value);
    }

    @Override
    public String toString() {
        PXSourceWriter writer = new PXSourceWriter();
        if (selectors != null) {
            for (PXSelector selector : selectors) {
                writer.print(selector);
                writer.print(' ');
            }
        }
        writer.println('{');
        writer.increaseIndent();
        writer.printIndent();
        writer.print("// specificity = ");
        writer.println(specificity.toString());

        if (declarations != null) {

            for (PXDeclaration declaration : declarations) {
                writer.printIndent();
                writer.print(declaration.toString());
            }
        }
        writer.decreaseIndent();
        writer.println('}');
        return writer.toString();
    }

    public static PXRuleSet mergeRuleSets(List<PXRuleSet> ruleSets) {
        PXRuleSet result = new PXRuleSet();
        if (!CollectionUtil.isEmpty(ruleSets)) {
            // Sort rules by specificity
            Collections.sort(ruleSets, new Comparator<PXRuleSet>() {
                public int compare(PXRuleSet lhs, PXRuleSet rhs) {
                    if (lhs.specificity == null) {
                        return (rhs.specificity == null) ? 0 : -1;
                    } else {
                        return (rhs.specificity == null) ? 1 : lhs.specificity
                                .compareTo(rhs.specificity);
                    }
                }
            });

            for (int i = ruleSets.size() - 1; i >= 0; i--) {
                // add selectors
                PXRuleSet ruleSet = ruleSets.get(i);
                for (PXSelector selector : ruleSet.getSelectors()) {
                    result.addSelector(selector);
                }
                // add declarations
                for (PXDeclaration declaration : ruleSet.getDeclarations()) {
                    if (result.hasDeclarationForName(declaration.getName())) {
                        if (declaration.isImportant()) {
                            PXDeclaration addedDeclaration = result
                                    .getDeclarationForName(declaration.getName());

                            if (!addedDeclaration.isImportant()) {
                                // replace old with this !important one
                                result.removeDeclaration(addedDeclaration);
                                result.addDeclaration(declaration);
                            }
                        }
                    } else {
                        result.addDeclaration(declaration);
                    }
                }
            }
        }
        return result;
    }
}
