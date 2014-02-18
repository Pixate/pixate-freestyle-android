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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.util.DisplayMetrics;

import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.cg.paints.PXGradient;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXPaintGroup;
import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.selectors.PXTypeSelector;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualStyleable;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.PXColorUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

/**
 * Pixate styling utilities.
 */
public class PXStyleUtils {

    public static final Pattern PATTERN_WHITESPACE_PLUS = Pattern.compile("\\s+");
    public static final Pattern PATTERN_WHITESPACE = Pattern.compile("\\s");

    private static final String TAG = PXStyleUtils.class.getSimpleName();

    /**
     * A utility class to hold children information
     */
    public static class PXStyleableChildrenInfo {
        public int childrenCount;
        public int childrenIndex;
        public int childrenOfTypeCount;
        public int childrenOfTypeIndex;
    }

    /**
     * Returns a float dimension value
     * 
     * @param value Can be a {@link Number} or a {@link PXDimension}
     * @param metrics
     * @return A float dimension value.
     */
    public static float getFloatFromValue(Object value, DisplayMetrics metrics) {
        float result = 0;

        if (value != null) {
            if (value instanceof Number) {
                result = ((Number) value).floatValue();
            } else if (value instanceof PXDimension) {
                PXDimension dimension = (PXDimension) value;
                PXDimension points = dimension.points(metrics);
                result = points.getNumber();
            }
        }
        return result;
    }

    /**
     * Returns a {@link PXPaint} from a value.
     * 
     * @param value Can be a {@link Number}, {@link PXGradient}, {@link Object}
     *            [] or {@link String}.
     * @return A {@link PXPaint}
     */
    public static PXPaint getPaintFromValue(Object value) {
        PXPaint result = PXSolidPaint.createPaintWithColor(Color.BLACK);

        if (value != null) {
            if (value instanceof Number) {
                result = PXSolidPaint.createPaintWithColor(((Integer) value).intValue());
            } else if (value instanceof PXGradient) {
                result = (PXGradient) value;
            } else if (value instanceof String) {
                result = PXSolidPaint.createPaintWithColor(PXColorUtil.getColorFromSVGName(value
                        .toString()));
            } else if (value instanceof Object[]) {
                Object[] paints = (Object[]) value;
                PXPaintGroup paintGroup = new PXPaintGroup();
                for (Object paint : paints) {
                    paintGroup.addPaint(getPaintFromValue(paint));
                }
                result = paintGroup;
            }
        }
        return result;
    }

    public static void updateStyle(Object styleable) {
        /* @formatter:off
        // TODO - Handle similar Android instances
        if (Pixate.configuration.cacheStyles &&
            ( styleable instanceof TableRow] || [styleable isKindOfClass:[UICollectionViewCell class]]))
        {
            // grab styleable's style hash
            NSString *styleKey = styleable.styleKey;
            PXStyleTreeInfo *cache = [PXCacheManager styleTreeInfoForKey:styleKey];

            // cache this items style info if we haven't seen it before
            if (cache == nil)
            {
                // collect style info
                cache = [[PXStyleTreeInfo alloc] initWithStyleable:styleable];

                // save for later
                [PXCacheManager setStyleTreeInfo:cache forKey:styleKey];
            }

            // apply style info to the styleable and its descendants
            [cache applyStylesToStyleable:styleable];
        }
        else
        {
        */
        // @formatter:on
        PXStyleInfo styleInfo = PXStyleInfo.getStyleInfo(styleable);

        if (styleInfo != null) {
            styleInfo.applyTo(styleable);
        }
        // }
    }

    /**
     * Updates the styles for a given styleable.
     * 
     * @param styleable
     * @param recurse Indicate whether to recursively style the styleable
     *            children. Note that direct virtual children will still be
     *            styled.
     */
    public static void updateStyles(Object styleable, boolean recurse) {
        if (styleable != null) {
            updateStyle(styleable);
            List<Object> children = PXStyleAdapter.getStyleAdapter(styleable).getElementChildren(
                    styleable);
            if (children != null) {
                if (recurse) {
                    for (Object child : children) {
                        updateStyles(child, recurse);
                    }
                } else {
                    // in a non-recursive mode, we still want to style the virtual
                    // children.
                    for (Object child : children) {
                        if (child instanceof PXVirtualStyleable) {
                            updateStyles(child, recurse);
                        }
                    }
                }
            }
        }
    }

    public static String getDescriptionForStyleable(Object styleable) {
        List<String> parts = new ArrayList<String>(7);

        // open text
        parts.add("{ ");

        // add class name and pointer address
        parts.add("Class=");
        parts.add(styleable.toString());
        parts.add(", Hash=" + styleable.hashCode());

        // add selector
        parts.add(", Selector=");
        parts.add(getSelector(styleable));

        // close text
        parts.add(" }");

        return CollectionUtil.toString(parts, StringUtil.EMPTY);
    }

    public static String getSelector(Object styleable) {
        List<String> parts = new ArrayList<String>();

        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
        // add element name
        parts.add(styleAdapter.getElementName(styleable));

        // add id
        String styleId = styleAdapter.getStyleId(styleable);
        if (!StringUtil.isEmpty(styleId)) {
            parts.add("#" + styleId);
        }

        // add classes
        String styleClass = styleAdapter.getStyleClass(styleable);
        if (!StringUtil.isEmpty(styleClass)) {
            String[] classes = PATTERN_WHITESPACE_PLUS.split(styleClass);
            Arrays.sort(classes);
            for (String className : classes) {
                parts.add("." + className);
            }
        }
        return CollectionUtil.toString(parts, StringUtil.EMPTY);
    }

    public static List<PXStyler> getStylers(Object styleable) {
        return PXStyleAdapter.getStyleAdapter(styleable).getStylers();
    }

    public static List<PXRuleSet> getMatchingRuleSets(Object styleable) {
        // find matching rule sets, regardless of any supported or specified
        // pseudo-classes

        PXStylesheet stylesheet = PXStylesheet.getCurrentApplicationStylesheet();
        List<PXRuleSet> ruleSets = null;
        if (stylesheet != null) {
            ruleSets = stylesheet.getRuleSetsMatchingStyleable(styleable);
        } else if (PXLog.isLogging()) {
            PXLog.w(TAG,
                    "Stylesheet was not found. Make sure you have your style CSS in the assets.");
        }
        // TODO: add matching sets from user stylesheet and view stylesheet
        return ruleSets;

    }

    public static PXStyleableChildrenInfo getChildrenInfoForStyleable(Object styleable) {
        PXStyleableChildrenInfo result = new PXStyleableChildrenInfo();

        // init
        result.childrenCount = 0;
        result.childrenOfTypeCount = 0;
        result.childrenIndex = -1;
        result.childrenOfTypeIndex = -1;

        PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);

        Object parent = styleAdapter.getParent(styleable);

        int indexInParent = styleAdapter.getIndexInParent(styleable);
        if (indexInParent > -1) {
            result.childrenIndex = result.childrenOfTypeIndex = indexInParent;
            int childCount = (parent != null) ? styleAdapter.getChildCount(parent) : styleAdapter
                    .getSiblingsCount(styleable);
            result.childrenCount = result.childrenOfTypeCount = childCount;
        } else {
            PXStyleAdapter parentAdapter = PXStyleAdapter.getStyleAdapter(parent);

            List<Object> children = parentAdapter.getElementChildren(parent);
            String styleableElementName = styleAdapter.getElementName(styleable);

            if (!CollectionUtil.isEmpty(children)) {
                for (Object child : children) {
                    PXStyleAdapter childAdapter = PXStyleAdapter.getStyleAdapter(child);
                    String elementName = childAdapter.getElementName(child);
                    if (elementName != null && !elementName.startsWith("#")) {
                        result.childrenCount++;

                        // test for element existence after adding to make index
                        // 1-based
                        if (child == styleable) {
                            result.childrenIndex = result.childrenCount;
                        }

                        if (elementName.equals(styleableElementName)) {
                            result.childrenOfTypeCount++;
                            // test for element existence after adding to make
                            // index 1-based
                            if (child == styleable) {
                                result.childrenOfTypeIndex = result.childrenOfTypeCount;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<PXRuleSet> filterRuleSets(List<PXRuleSet> ruleSets, Object styleable,
            String stateName) {
        // if we have a state name, then filter the specified rulesets to thos
        // only referencing this state
        List<PXRuleSet> ruleSetsForState = new ArrayList<PXRuleSet>();

        // process each rule set
        for (PXRuleSet ruleSet : ruleSets) {
            // grab the target type selector (the selector itself or a
            // combinator's RHS)
            PXTypeSelector selector = ruleSet.getTargetTypeSelector();

            // assume we will not be adding this rule set into our results
            boolean add = false;

            if (!selector.hasPseudoClasses()) {
                // the selector didn't specify a pseudo-class so assume the
                // default psuedo-class was specified
                PXStyleAdapter styleAdapter = PXStyleAdapter.getStyleAdapter(styleable);
                String defaultPseudoClass = styleAdapter.getDefaultPseudoClass(styleable);
                if (defaultPseudoClass != null) {
                    // add rule set if default pseudo-class matches
                    // stateName
                    add = defaultPseudoClass.equals(stateName);
                } else {
                    // add rule set if the styleable doesn't have a default
                    // pseudo-class and the specified state name
                    // was nil
                    add = (stateName == null);
                }
            } else {
                // add if the styleable has the state name in its lists of
                // supported pseudo-classes
                add = selector.hasPseudoClass(stateName);
            }

            if (add) {
                ruleSetsForState.add(ruleSet);
            }
        }

        return ruleSetsForState;
    }

    public List<PXRuleSet> filterRuleSets(List<PXRuleSet> ruleSets, String pseudoElement) {
        List<PXRuleSet> ruleSetsForPseudoElement = null;

        // if we have a pseudo-element name, then filter the specified rulesets
        // to those only referencing this pseudo-element
        if (!StringUtil.isEmpty(pseudoElement)) {
            // process each rule set
            for (PXRuleSet ruleSet : ruleSets) {
                // grab the target type selector (the selector itself or a
                // combinator's RHS)
                PXTypeSelector selector = ruleSet.getTargetTypeSelector();

                if (pseudoElement.equals(selector.getPseudoElement())) {
                    if (ruleSetsForPseudoElement == null) {
                        ruleSetsForPseudoElement = new ArrayList<PXRuleSet>();
                    }

                    ruleSetsForPseudoElement.add(ruleSet);
                }
            }
        }
        return ruleSetsForPseudoElement;
    }
}
