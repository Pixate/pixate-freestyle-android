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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.styling.animation.PXKeyframe;
import com.pixate.freestyle.styling.media.PXMediaExpression;
import com.pixate.freestyle.styling.media.PXMediaGroup;
import com.pixate.freestyle.styling.parsing.PXStylesheetParser;
import com.pixate.freestyle.util.IOUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

// TODO: Create container to hold PXStylesheet so parser can return that instead of having to subclass this from Symbol
public class PXStylesheet {

    // @formatter:off
    public enum PXStyleSheetOrigin {
        APPLICATION,
        USER,
        VIEW,
        INLINE
    }
    // @formatter:on

    private static String TAG = PXStylesheet.class.getSimpleName();

    // TODO: Use a parser pool since this is not thread safe now
    private static PXStylesheetParser PARSER = new PXStylesheetParser();

    private static PXStylesheet currentApplicationStylesheet;
    private static PXStylesheet currentUserStylesheet;
    private static PXStylesheet currentViewStylesheet;

    private List<PXMediaGroup> mediaGroups;
    private PXMediaExpression activeMediaQuery;
    private PXMediaGroup activeMediaGroup;
    private Map<String, String> namespacePrefixMap;
    private Map<String, PXKeyframe> keyframesByName;

    private PXStyleSheetOrigin origin;
    private List<String> errors;
    private String filePath;

    public static PXStylesheet getStyleSheetFromSource(String source, PXStyleSheetOrigin origin) {
        return getStyleSheetFromSource(source, origin, null);
    }

    public static PXStylesheet getStyleSheetFromSource(String source, PXStyleSheetOrigin origin,
            String fileName) {
        PXStylesheet result = null;

        if (!StringUtil.isEmpty(source)) {
            result = PARSER.parse(source, origin, fileName);
            result.setErrors(PARSER.getErrors());
            assignCurrentStylesheet(result, origin);
        } else {
            result = new PXStylesheet(origin);
        }

        return result;
    }

    public static PXStylesheet getStyleSheetFromFilePath(Context context, String aFilePath,
            PXStyleSheetOrigin origin) {
        try {
            String source = IOUtil.read(context.getAssets().open(aFilePath));
            return getStyleSheetFromSource(source, origin, aFilePath);
        } catch (IOException e) {
            PXLog.e(TAG, "Error reading stylesheet from " + aFilePath);
        }
        return null;
    }

    // PUBLIC

    public PXStylesheet() {
        this(PXStyleSheetOrigin.APPLICATION);
    }

    public PXStylesheet(PXStyleSheetOrigin origin) {
        this.origin = origin;
        assignCurrentStylesheet(this, origin);
    }

    // Getters

    // TODO cache per context
    public List<PXRuleSet> getRuleSets(Context context) {
        List<PXRuleSet> combined = new ArrayList<PXRuleSet>();
        if (mediaGroups != null) {
            for (PXMediaGroup group : mediaGroups) {
                if (group.matches(context)) {
                    combined.addAll(group.getRuleSets());
                }
            }
        }
        if (combined.isEmpty()) {
            return null;
        }
        return combined;
    }

    public List<PXRuleSet> getRuleSets(Object styleable, Context context) {
        List<PXRuleSet> combined = new ArrayList<PXRuleSet>();
        if (mediaGroups != null) {
            for (PXMediaGroup group : mediaGroups) {
                if (group.matches(context)) {
                    List<PXRuleSet> ruleSets = group.getRuleSets(styleable);
                    if (ruleSets != null) {
                        combined.addAll(ruleSets);
                    }
                }
            }
        }
        if (combined.isEmpty()) {
            return null;
        }
        return combined;
    }

    public List<PXMediaGroup> getMediaGroups() {
        return mediaGroups != null ? new ArrayList<PXMediaGroup>(mediaGroups) : null;
    }

    public static PXStylesheet getCurrentApplicationStylesheet() {
        return currentApplicationStylesheet;
    }

    public static PXStylesheet getCurrentUserStylesheet() {
        return currentUserStylesheet;
    }

    public static PXStylesheet getCurrentViewStylesheet() {
        return currentViewStylesheet;
    }

    // Setters

    public void setActiveMediaQuery(PXMediaExpression mediaExpression) {
        // TODO: test for equivalence of active query? If they match, then do
        // nothing
        this.activeMediaQuery = mediaExpression;
        this.activeMediaGroup = null;
    }

    // Methods
    public void addRuleSet(PXRuleSet ruleSet) {
        if (ruleSet != null) {
            if (activeMediaGroup == null) {
                activeMediaGroup = new PXMediaGroup(activeMediaQuery, origin);

                addMediaGroup(activeMediaGroup);
            }
            activeMediaGroup.addRuleSet(ruleSet);
        }
    }

    public void addMediaGroup(PXMediaGroup mediaGroup) {
        if (mediaGroups == null) {
            mediaGroups = new ArrayList<PXMediaGroup>();
        }

        mediaGroups.add(mediaGroup);
    }

    public List<PXRuleSet> getRuleSetsMatchingStyleable(Object styleable) {
        List<PXRuleSet> result = new ArrayList<PXRuleSet>();

        if (styleable != null) {
            List<PXRuleSet> candidateRuleSets = getRuleSets(styleable,
                    PixateFreestyle.getAppContext());
            if (candidateRuleSets != null) {
                for (PXRuleSet ruleSet : candidateRuleSets) {
                    if (ruleSet.matches(styleable)) {
                        if (PXLog.isLogging()) {
                            PXLog.i(PXStylesheet.class.getSimpleName(), "%s matched\n%s",
                                    PXStyleUtils.getDescriptionForStyleable(styleable),
                                    ruleSet.toString());
                        }
                        result.add(ruleSet);
                    }
                }
            }
        }
        return result;
    }

    public void setURI(String uri, String prefix) {
        if (uri != null) {
            if (prefix == null) {
                prefix = StringUtil.EMPTY;
            }

            if (namespacePrefixMap == null) {
                namespacePrefixMap = new HashMap<String, String>();
            }
            namespacePrefixMap.put(prefix, uri);
        }
    }

    public String getNamespaceForPrefix(String prefix) {
        if (namespacePrefixMap != null) {
            if (prefix == null) {
                prefix = StringUtil.EMPTY;
            }
            return namespacePrefixMap.get(prefix);
        }
        return null;
    }

    public void addKeyframe(PXKeyframe keyframe) {
        if (keyframe != null) {
            if (keyframesByName == null) {
                keyframesByName = new HashMap<String, PXKeyframe>();
            }
            keyframesByName.put(keyframe.getName(), keyframe);
        }
    }

    public PXKeyframe getKeyframeForName(String name) {
        return keyframesByName == null ? null : keyframesByName.get(name);
    }

    public PXStyleSheetOrigin getOrigin() {
        return origin;
    }

    public PXMediaExpression getActiveMediaQuery() {
        return activeMediaQuery;
    }

    /**
     * Set the errors that were detected during the parsing of this stylesheet.
     * 
     * @param errors
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Returns the parsing errors. Can be <code>null</code>.
     * 
     * @return A list of parsing error strings, or <code>null</code>.
     */
    public List<String> getErrors() {
        return errors;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    // Static private methods
    private static void assignCurrentStylesheet(PXStylesheet stylesheet,
            PXStyleSheetOrigin forOrigin) {
        switch (forOrigin) {
            case APPLICATION:
                currentApplicationStylesheet = stylesheet;
                break;
            case USER:
                currentUserStylesheet = stylesheet;
                break;
            case VIEW:
                currentViewStylesheet = stylesheet;
                break;
            case INLINE:
                // this origin type should never be handled here, but in
                // PXStyleController directly
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (mediaGroups != null) {
            int size = mediaGroups.size();
            for (int i = 0; i < size; i++) {
                builder.append(mediaGroups.get(i).toString());
                if (i + 1 < size) {
                    builder.append('\n');
                }
            }
        }
        return builder.toString();
    }
}
