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
package com.pixate.freestyle.styling.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.selectors.PXTypeSelector;
import com.pixate.freestyle.styling.selectors.PXSpecificity.PXSpecificityType;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.StringUtil;

public class PXMediaGroup implements PXMediaExpression {

    private PXStyleSheetOrigin origin;
    private PXMediaExpression query;
    private List<PXRuleSet> ruleSets;
    private Map<String, List<PXRuleSet>> ruleSetsByElementName;
    private Map<String, List<PXRuleSet>> ruleSetsById;
    private Map<String, List<PXRuleSet>> ruleSetsByClass;
    private List<PXRuleSet> uncategorizedRuleSets;

    /**
     * Initializer a newly allocated instance
     * 
     * @param query The medai query for this group
     * @param origin The stylesheet origin for this group
     */
    public PXMediaGroup(PXMediaExpression query, PXStyleSheetOrigin origin) {
        this.query = query;
        this.origin = origin;
    }

    /**
     * Returns an non-mutable array of rule sets that are contained within this
     * stylesheet
     */
    public List<PXRuleSet> getRuleSets() {
        return (ruleSets != null) ? new ArrayList<PXRuleSet>(ruleSets) : null;
    }

    public List<PXRuleSet> getRuleSets(Object styleable) {
        List<PXRuleSet> result = new ArrayList<PXRuleSet>();
        Set<PXRuleSet> items = new HashSet<PXRuleSet>();

        // gather keys
        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
        String elementName = styleAdapter.getElementName(styleable);
        String styleId = styleAdapter.getStyleId(styleable);
        String styleClass = styleAdapter.getStyleClass(styleable);

        // find relevant ruleSets by element name
        if (ruleSetsByElementName != null && !StringUtil.isEmpty(elementName)) {
            List<PXRuleSet> rs = ruleSetsByElementName.get(elementName);
            if (rs != null) {
                for (PXRuleSet ruleSet : rs) {
                    if (!items.contains(ruleSet)) {
                        result.add(ruleSet);
                        items.add(ruleSet);
                    }
                }
            }
        }

        // find relevant ruleSets by id
        if (ruleSetsById != null && !StringUtil.isEmpty(styleId)) {
            List<PXRuleSet> rs = ruleSetsById.get(styleId);
            if (rs != null) {
                for (PXRuleSet ruleSet : rs) {
                    if (!items.contains(ruleSet)) {
                        result.add(ruleSet);
                        items.add(ruleSet);
                    }
                }
            }
        }

        // find relevant ruleSets by class
        if (ruleSetsByClass != null && !StringUtil.isEmpty(styleClass)) {
            String[] styleClasses = PXStyleUtils.PATTERN_WHITESPACE_PLUS.split(styleClass);

            for (String aClass : styleClasses) {
                List<PXRuleSet> rs = ruleSetsByClass.get(aClass);
                if (rs != null) {
                    for (PXRuleSet ruleSet : rs) {
                        if (!items.contains(ruleSet)) {
                            result.add(ruleSet);
                            items.add(ruleSet);
                        }
                    }
                }
            }
        }

        // fallback to all uncategorized ruleSets. Note that these are already
        // included in the element name, id, and class lists above, so we only
        // need to add these if we didn't find any of those
        if (result.isEmpty()) {
            result = uncategorizedRuleSets;
        }

        return result;
    }

    public void addRuleSet(PXRuleSet ruleSet, Map<String, List<PXRuleSet>> partition, String key) {
        List<PXRuleSet> ruleSets = partition.get(key);

        // create ruleset array if we don't have one already
        if (ruleSets == null) {
            ruleSets = new ArrayList<PXRuleSet>();

            // be sure to include in this array all uncategorized ruleSets that
            // came before this one
            if (uncategorizedRuleSets != null) {
                ruleSets.addAll(uncategorizedRuleSets);
            }

            // save the ruleSet array back to the partition dictionary
            partition.put(key, ruleSets);
        }

        // add this ruleSet to the ruleSet array associated with the given key
        ruleSets.add(ruleSet);
    }

    /**
     * Add a new rule set to this stylesheet
     * 
     * @param ruleSet The rule set to add. null values are ignored
     */
    public void addRuleSet(PXRuleSet ruleSet) {
        if (ruleSet != null) {
            if (ruleSets == null) {
                ruleSets = new ArrayList<PXRuleSet>();
            }

            this.ruleSets.add(ruleSet);

            // set origin specificity
            ruleSet.setSpecificity(PXSpecificityType.ORIGIN, origin.ordinal());

            // setup lookup by element type
            PXTypeSelector typeSelector = ruleSet.getTargetTypeSelector();
            String elementName = (typeSelector == null || typeSelector.hasUniversalType()) ? null
                    : typeSelector.getTypeName();
            String styleId = (typeSelector == null) ? null : typeSelector.getStyleId();
            List<String> styleClasses = (typeSelector == null) ? null : typeSelector
                    .getStyleClasses();
            boolean added = false;

            // NOTE: nesting if-statements to avoid walking type selector
            // expressions for id and classes when not needed
            if (elementName != null && !"*".equals(elementName)) {
                if (ruleSetsByElementName == null) {
                    ruleSetsByElementName = new HashMap<String, List<PXRuleSet>>();
                }
                addRuleSet(ruleSet, ruleSetsByElementName, elementName);
                added = true;
            }

            if (!StringUtil.isEmpty(styleId)) {
                if (ruleSetsById == null) {
                    ruleSetsById = new HashMap<String, List<PXRuleSet>>();
                }
                addRuleSet(ruleSet, ruleSetsById, styleId);
                added = true;
            }

            if (!CollectionUtil.isEmpty(styleClasses)) {
                if (ruleSetsByClass == null) {
                    ruleSetsByClass = new HashMap<String, List<PXRuleSet>>();
                }

                for (String styleClass : styleClasses) {
                    addRuleSet(ruleSet, ruleSetsByClass, styleClass);
                }

                added = true;
            }

            // if this wasn't added to any of our partitions, then we need to
            // collect it into the uncategorized partition
            // and add it to all other partitions to preserve rule set order in
            // those sets as well
            if (!added) {
                if (uncategorizedRuleSets == null) {
                    uncategorizedRuleSets = new ArrayList<PXRuleSet>();
                }
                uncategorizedRuleSets.add(ruleSet);

                // add uncategorized ruleSets to all partitions
                if (ruleSetsByElementName != null) {
                    for (String key : ruleSetsByElementName.keySet()) {
                        List<PXRuleSet> items = ruleSetsByElementName.get(key);
                        items.add(ruleSet);
                    }
                }
                if (ruleSetsById != null) {
                    for (String key : ruleSetsById.keySet()) {
                        List<PXRuleSet> items = ruleSetsById.get(key);
                        items.add(ruleSet);
                    }
                }

                if (ruleSetsByClass != null) {
                    for (String key : ruleSetsByClass.keySet()) {
                        List<PXRuleSet> items = ruleSetsByClass.get(key);
                        items.add(ruleSet);
                    }
                }
            }
        }
    }

    /**
     * Returns a PXStylesheetOrigin value indicating the origin of this
     * stylesheet. Origin values are used in specificity calculations.
     */
    public PXStyleSheetOrigin getOrigin() {
        return origin;
    }

    /**
     * Returns the media query associated with this grouping of rule sets
     */
    public PXMediaExpression getQuery() {
        return query;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.media.PXMediaExpression#matches(android.content
     * .Context)
     */
    public boolean matches(Context context) {
        // assume null means "true" to cover non-media-query groups
        return (query != null) ? query.matches(context) : true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        // TODO: wrap in @media with query
        List<String> parts = new ArrayList<String>();

        if (query != null) {
            parts.add(String.format("@media %s {", query.toString()));

            for (PXRuleSet ruleSet : ruleSets) {
                parts.add(String.format("  %s", ruleSet.toString()));
            }

            parts.add("}");
        } else {
            for (PXRuleSet ruleSet : ruleSets) {
                parts.add(ruleSet.toString());
            }
        }
        return StringUtil.join(parts, "\n");
    }
}
