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
package com.pixate.freestyle.styling.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXTypeSelector;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.PXColorUtil;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

public class PXStyleInfo {

    /**
     * The default style key value. For example, the {@link PXDrawableUtil} and
     * the {@link PXColorUtil} assign values to their state maps for this
     * default style.
     */
    public static final String DEFAULT_STYLE = "default";

    private Map<String, List<PXDeclaration>> declarationsByState;
    private Map<String, Set<PXStyler>> stylersByState;
    private String styleKey;

    /**
     * Constructs a new {@link PXStyleInfo}.
     * 
     * @param styleKey
     */
    public PXStyleInfo(String styleKey) {
        this.styleKey = styleKey;
    }

    public Set<String> getStates() {
        return declarationsByState != null ? declarationsByState.keySet() : null;
    }

    public void addDeclarations(List<PXDeclaration> declarations, String stateName) {
        if (stateName != null && !CollectionUtil.isEmpty(declarations)) {
            if (declarationsByState == null) {
                declarationsByState = new HashMap<String, List<PXDeclaration>>();
            }
            // TODO: check for pre-existing?
            declarationsByState.put(stateName, declarations);
        }
    }

    public void addStylers(Set<PXStyler> stylers, String stateName) {
        if (stateName != null && !CollectionUtil.isEmpty(stylers)) {
            if (stylersByState == null) {
                stylersByState = new HashMap<String, Set<PXStyler>>();
            }
            stylersByState.put(stateName, stylers);
        }
    }

    public List<PXDeclaration> getDeclarations(String stateName) {
        return (declarationsByState != null) ? declarationsByState.get(stateName) : null;
    }

    public Set<PXStyler> getStylers(String stateName) {
        return (stylersByState != null) ? stylersByState.get(stateName) : null;
    }

    public void applyTo(Object styleable) {
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
        // abort application of style info if the styleable's style key does not
        // match the info's style key
        if (!ObjectUtil.areEqual(styleKey, styleAdapter.getStyleKey(styleable))) {
            if (PXLog.isLogging()) {
                PXLog.w(PXStyleInfo.class.getSimpleName(),
                        "StyleKey mismatch (%s != %s). Aborted applyStyleInfo for %s", styleKey,
                        styleAdapter.getStyleId(styleable),
                        PXStyleUtils.getDescriptionForStyleable(styleable));
            }
            return;
        }

        List<PXStyler> stylers = styleAdapter.getStylers();
        Map<String, PXStyler> stylersByProperty = styleAdapter.getStylersByProperty();

        Set<String> states = getStates();
        if (states == null) {
            return;
        }
        List<PXRuleSet> ruleSets = new ArrayList<PXRuleSet>(states.size());
        List<PXStylerContext> contexts = new ArrayList<PXStylerContext>(states.size());
        for (String stateName : states) {
            List<PXDeclaration> activeDeclarations = getDeclarations(stateName);

            // No need for Android (note: this is not a View invalidate()
            // equivalent)
            // if (forceInvalidation) {
            // PXStyleUtils.invalidateStyleable(styleable);
            // }

            if (true /*
                      * FIXME always true for now ![PXStyleUtils
                      * stylesOfStyleable:styleable
                      * matchDeclarations:activeDeclarations state:stateName]
                      */) {
                Set<PXStyler> activeStylers = getStylers(stateName);

                // create context and store styleable and state name there
                int styleHash = 17
                        * PXStyleAdapter.getStyleAdapter(styleable).getBounds(styleable).hashCode()
                        + activeDeclarations.hashCode();
                PXStylerContext context = new PXStylerContext(styleable, stateName, styleHash);

                // process declarations in styler order
                for (PXStyler currentStyler : stylers) {
                    if (activeStylers != null && activeStylers.contains(currentStyler)) {
                        // process the declarations, in order
                        for (PXDeclaration declaration : activeDeclarations) {
                            PXStyler styler = stylersByProperty.get(declaration.getName());

                            if (styler == currentStyler) {
                                styler.processDeclaration(declaration, context);
                            }
                        }
                        // apply styler completion block
                        currentStyler.applyStylesWithContext(context);
                    }
                }

                // see if there's a catch-all 'updateStyleWithRuleSet:context:'
                // method to call
                PXRuleSet ruleSet = new PXRuleSet();

                for (PXDeclaration declaration : activeDeclarations) {
                    ruleSet.addDeclaration(declaration);
                }
                // Collect the items that will be sent to the Adapter's
                // updateStyle
                ruleSets.add(ruleSet);
                contexts.add(context);
            }
        }
        // Batch update.
        styleAdapter.updateStyle(ruleSets, contexts);
    }

    @Override
    public String toString() {
        List<String> parts = new ArrayList<String>();

        Set<String> states = getStates();
        if (states != null) {
            for (String state : states) {
                String stateName = (state.length() > 0) ? state : "default";

                // emit state
                parts.add(String.format("%s {", stateName));

                // emit declaration
                List<PXDeclaration> declarations = getDeclarations(state);
                if (declarations != null) {
                    for (PXDeclaration declaration : declarations) {
                        parts.add(String.format("  %s", declaration.toString()));
                    }
                }
                // close
                parts.add("}");
            }
        }
        return CollectionUtil.toString(parts, "\n");
    }

    /**
     * Creates and returns a {@link PXStyleInfo} for a given styleable.
     * 
     * @param styleable
     * @return a {@link PXStyleInfo} (may be <code>null</code>)
     */
    public static PXStyleInfo getStyleInfo(Object styleable) {
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
        PXStyleInfo result = new PXStyleInfo(styleAdapter.getStyleKey(styleable));

        // find all rule sets that apply to this styleable
        List<PXRuleSet> ruleSets = PXStyleUtils.getMatchingRuleSets(styleable);

        if (!CollectionUtil.isEmpty(ruleSets)) {

            // remove pseudo-element rule sets
            List<PXRuleSet> toRemove = new ArrayList<PXRuleSet>();

            for (PXRuleSet ruleSet : ruleSets) {
                PXTypeSelector selector = ruleSet.getTargetTypeSelector();

                if (!StringUtil.isEmpty(selector.getPseudoElement())) {
                    toRemove.add(ruleSet);
                }
            }

            ruleSets.removeAll(toRemove);
        }

        // process by state
        if (!CollectionUtil.isEmpty(ruleSets)) {
            // grab a list of supported pseudo-classes for this styleable object
            List<String> pseudoClasses = styleAdapter.getSupportedPseudoClasses(styleable);

            // style pseudo-classes
            if (!CollectionUtil.isEmpty(pseudoClasses)) {
                for (String pseudoClass : pseudoClasses) {
                    // filter the list of rule sets to only those that specify
                    // the current state
                    List<PXRuleSet> ruleSetsForState = PXStyleUtils.filterRuleSets(ruleSets,
                            styleable, pseudoClass);

                    if (!CollectionUtil.isEmpty(ruleSetsForState)) {
                        setStyleInfo(result, ruleSetsForState, styleable, pseudoClass);
                    }
                }
            } else {
                setStyleInfo(result, ruleSets, styleable, DEFAULT_STYLE);
            }
        }
        return (!CollectionUtil.isEmpty(result.getStates())) ? result : null;
    }

    private static void setStyleInfo(PXStyleInfo styleInfo, List<PXRuleSet> ruleSets,
            Object styleable, String stateName) {
        // merge all rule sets into a single rule set based on origin and
        // weight/specificity
        PXRuleSet mergedRuleSet = PXRuleSet.mergeRuleSets(ruleSets);
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
        List<PXStyler> stylers = styleAdapter.getStylers();
        Map<String, PXStyler> stylersByProperty = styleAdapter.getStylersByProperty();

        // build a set of stylers that are active based on the property names we
        // have in the merged rule set
        Set<PXStyler> activeStylers = new HashSet<PXStyler>();

        // keep track of active declarations
        List<PXDeclaration> activeDeclarations = new ArrayList<PXDeclaration>();

        for (PXDeclaration declaration : mergedRuleSet.getDeclarations()) {
            PXStyler styler = stylersByProperty.get(declaration.getName());
            // In case the styler is null, we also check if the adapter supports
            // it. This is important for the Node tests.
            if (styler != null || !styleAdapter.isSupportingStylers()) {
                activeDeclarations.add(declaration);
                if (styler != null) {
                    activeStylers.add(styler);
                }
            } else if (stylers == null) {
                activeDeclarations.add(declaration);
            }
        }

        styleInfo.addDeclarations(activeDeclarations, stateName);
        styleInfo.addStylers(activeStylers, stateName);
    }
}
