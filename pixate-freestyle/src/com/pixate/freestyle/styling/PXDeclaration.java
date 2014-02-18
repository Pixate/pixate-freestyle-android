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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.pixate.freestyle.PXEngineConfiguration.PXUpdateStylesType;
import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.parsing.PXTransformParser;
import com.pixate.freestyle.cg.shadow.PXShadowPaint;
import com.pixate.freestyle.styling.infos.PXAnimationInfo;
import com.pixate.freestyle.styling.infos.PXBorderInfo;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationDirection;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationFillMode;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationPlayState;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationTimingFunction;
import com.pixate.freestyle.styling.infos.PXBorderInfo.PXBorderStyle;
import com.pixate.freestyle.styling.infos.PXBorderInfo.PXTextBorderStyle;
import com.pixate.freestyle.styling.infos.PXLineBreakInfo.PXLineBreakMode;
import com.pixate.freestyle.styling.parsing.PXStylesheetLexeme;
import com.pixate.freestyle.styling.parsing.PXStylesheetTokenType;
import com.pixate.freestyle.styling.parsing.PXValueParser;
import com.pixate.freestyle.styling.stylers.PXStylerContext.GridStyle.PXColumnStretchMode;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.ObjectUtil;
import com.pixate.freestyle.util.Size;
import com.pixate.freestyle.util.StringUtil;

@SuppressLint("DefaultLocale")
public class PXDeclaration {

    private static final PXValueParser parser = new PXValueParser();

    private String name = "<unknown>";
    private String filename = null;
    private String source = null;
    private List<PXStylesheetLexeme> lexemes = null;
    private boolean important = false;

    /* STATIC */

    private static final Pattern ESCAPE_SEQUENCES = Pattern.compile("\\\\.", Pattern.DOTALL);
    private static final Map<String, String> ESCAPE_SEQUENCE_MAP;

    private static Map<String, Align> TEXT_ALIGN_MAP;

    static {
        ESCAPE_SEQUENCE_MAP = new HashMap<String, String>();
        ESCAPE_SEQUENCE_MAP.put("\\t", "\t");
        ESCAPE_SEQUENCE_MAP.put("\\r", "\r");
        ESCAPE_SEQUENCE_MAP.put("\\n", "\n");
        ESCAPE_SEQUENCE_MAP.put("\\f", "\f");
    }

    /* CONSTRUCTORS */

    public PXDeclaration() {
    }

    public PXDeclaration(String name) {
        this.name = name;
    }

    public PXDeclaration(String name, String value) {
        this(name);
        setSource(value, /* filename */null);
    }

    /* PUBLIC */

    public void setSource(String source, String filename, List<PXStylesheetLexeme> lexemes) {
        this.source = source;
        this.filename = filename;
        this.lexemes = lexemes;
    }

    public void setSource(String source, String filename) {
        setSource(source, filename, PXValueParser.lexemesForSource(source));
    }

    public String getName() {
        return name;
    }

    public Matrix getAffineTransformValue() {
        return (new PXTransformParser()).parse(getStringValue());
    }

    public List<PXAnimationInfo> getAnimationInfoList() {
        return parser.parseAnimationInfos(lexemes);
    }

    public Align getTextAlignmentValue() {
        synchronized (PXDeclaration.class) {

            if (TEXT_ALIGN_MAP == null) {

                // one-time init
                TEXT_ALIGN_MAP = new HashMap<String, Align>(3);
                TEXT_ALIGN_MAP.put("left", Align.LEFT);
                TEXT_ALIGN_MAP.put("center", Align.CENTER);
                TEXT_ALIGN_MAP.put("right", Align.RIGHT);
            }
        }

        Align alignment = Align.CENTER;
        Align value = TEXT_ALIGN_MAP.get(getFirstWord());

        if (value != null) {
            alignment = value;
        }

        return alignment;

    }

    public PXTextBorderStyle getTextBorderStyleValue() {

        PXTextBorderStyle style = PXTextBorderStyle.NONE;
        PXTextBorderStyle value = PXTextBorderStyle.ofCssValue(getFirstWord());
        if (value != null) {
            style = value;
        }

        return style;
    }

    public List<PXAnimationInfo> getTransitionInfoList() {
        return parser.parseTransitionInfos(lexemes);
    }

    public Uri getURLValue() {
        return parser.parseURL(lexemes);
    }

    public List<PXAnimationDirection> getAnimationDirectionList() {
        return parser.parseAnimationDirectionList(lexemes);
    }

    public List<PXAnimationFillMode> getAnimationFillModeList() {
        return parser.parseAnimationFillModeList(lexemes);
    }

    public List<PXAnimationPlayState> getAnimationPlayStateList() {
        return parser.parseAnimationPlayStateList(lexemes);
    }

    public List<PXAnimationTimingFunction> getAnimationTimingFunctionList() {
        return parser.parseAnimationTimingFunctionList(lexemes);
    }

    public boolean getBooleanValue() {
        return StringUtil.toBoolean(getFirstWord());
    }

    public PXBorderInfo getBorderValue(DisplayMetrics displayMetrics) {
        return parser.parseBorder(this.lexemes, displayMetrics);
    }

    public List<Size> getBorderRadiiList(DisplayMetrics displayMetrics) {
        return parser.parseBorderRadiusList(this.lexemes, displayMetrics);
    }

    public PXBorderStyle getBorderStyleValue() {
        return parser.parseBorderStyle(this.lexemes);
    }

    public List<PXBorderStyle> getBorderStyleList() {
        return parser.parseBorderStyleList(this.lexemes);
    }

    public Integer getColorValue() {
        return parser.parseColor(this.lexemes);
    }

    public int getColumnWidth(DisplayMetrics displayMetrics) {
        return (int) Math.ceil(parser.parseColumnWidth(this.lexemes, displayMetrics));
    }

    public float getFloatValue(DisplayMetrics displayMetrics) {
        return parser.parseFloat(this.lexemes, displayMetrics);
    }

    public List<Float> getFloatListValue() {
        return parser.parseFloatList(this.lexemes);
    }

    public int getColumnCount() {
        return parser.parseColumnCount(this.lexemes);
    }

    public int getColumnGap(DisplayMetrics displayMetrics) {
        return parser.parseColumnGap(this.lexemes, displayMetrics);
    }

    public int getRowGap(DisplayMetrics displayMetrics) {
        return parser.parseRowGap(this.lexemes, displayMetrics);
    }

    public PXOffsets getInsetsValue(DisplayMetrics displayMetrics) {
        return parser.parseInsets(this.lexemes, displayMetrics);
    }

    public PXDimension getLengthValue() {
        PXDimension result = null;

        if (lexemes.size() > 0) {
            PXStylesheetLexeme lexeme = lexemes.get(0);

            if (lexeme.getType() == PXStylesheetTokenType.LENGTH) {
                result = (PXDimension) lexeme.getValue();

            } else if (lexeme.getType() == PXStylesheetTokenType.NUMBER) {
                Number number = (Number) lexeme.getValue();
                result = new PXDimension(number.floatValue(), "px");
            }
        }

        return result;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isImportant() {
        return important;
    }

    // TODO discuss NSLineBreakMode versus Android.
    public PXLineBreakMode getLineBreakModeValue() {
        PXLineBreakMode mode = PXLineBreakMode.ofCssValue(getFirstWord());
        return mode == null ? PXLineBreakMode.TRUNCATE_MIDDLE : mode;

    }

    public List<String> getNameListValue() {
        return parser.parseNameList(this.lexemes);
    }

    public PXOffsets getOffsetsValue(DisplayMetrics displayMetrics) {
        return parser.parseOffsets(this.lexemes, displayMetrics);
    }

    public List<PXPaint> getPaintList() {
        return parser.parsePaints(this.lexemes);
    }

    public PXPaint getPaintValue() {
        return parser.parsePaint(this.lexemes);
    }

    public float getSecondsValue() {
        return parser.parseSeconds(this.lexemes);
    }

    public PXShadowPaint getShadowValue(DisplayMetrics displayMetrics) {
        return parser.parseShadow(this.lexemes, displayMetrics);
    }

    public List<Float> getSecondsListValue() {
        return parser.parseSecondsList(this.lexemes);
    }

    // @formatter:off
    /* WAITING FOR PXShadowPaint
    public PXShadowPaint getShadowValue() {
        return parser.parseShadow(this.lexemes);
    }
    */
    // @formatter:on

    public Size getSizeValue(DisplayMetrics displayMetrics) {
        return parser.parseSize(this.lexemes, displayMetrics);
    }

    public PXColumnStretchMode getColumnStretchMode() {
        return parser.parseColumnStretchMode(this.lexemes);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (source != null ? source.hashCode() : 0);
        hash = hash * 31 + (filename != null ? filename.hashCode() : 0);
        hash = hash * 13 + (lexemes != null ? lexemes.hashCode() : 0);
        return hash;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PXDeclaration) {
            PXDeclaration other = (PXDeclaration) o;
            return ObjectUtil.areEqual(filename, other.filename)
                    && ObjectUtil.areEqual(source, other.source)
                    && ObjectUtil.areEqual(lexemes, other.lexemes);
        }
        return false;
    }

    // TODO test like hell
    public String getStringValue() {
        List<Object> parts = new ArrayList<Object>(lexemes.size());

        for (PXStylesheetLexeme lexeme : lexemes) {
            if (lexeme.getType() == PXStylesheetTokenType.STRING) {
                // grab raw value
                String value = (String) lexeme.getValue();

                // trim quotes
                String content = value.substring(1, value.length() - 1);

                StringBuffer sb = new StringBuffer(content.length());

                // replace escape sequences
                Matcher matcher = ESCAPE_SEQUENCES.matcher(content);
                while (matcher.find()) {
                    String match = matcher.group();
                    String replacement = ESCAPE_SEQUENCE_MAP.get(match);
                    if (replacement == null) {
                        replacement = match.substring(1);
                    }
                    matcher.appendReplacement(sb, replacement);
                }
                matcher.appendTail(sb);

                parts.add(sb.toString());

            } else {
                parts.add(lexeme.getValue());
            }
        }
        return TextUtils.join(" ", parts.toArray());
    }

    public PXUpdateStylesType getUpdateStylesTypeValue() {
        PXUpdateStylesType type = PXUpdateStylesType.AUTO;

        if ("manual".equals(getFirstWord())) {
            type = PXUpdateStylesType.MANUAL;
        }

        return type;
    }

    public String transformString(String value) {
        String text = getFirstWord();

        if ("uppercase".equals(text)) {
            return value.toUpperCase();

        } else if ("lowercase".equals(text)) {
            return value.toLowerCase();

        } else if ("capitalize".equals(text)) {
            // TODO find capitalize function
            // could lift Apache commons-lang WordUtils.capitalizeFully
            // since it's (obviously) apache-licensed
            return value;

        } else {
            return value;
        }
    }

    @Override
    public String toString() {
        if (this.important) {
            return String.format("%s: %s !important;", this.name, this.getStringValue());

        } else {
            return String.format("%s: %s;", this.name, this.getStringValue());
        }
    }

    /* PRIVATE */

    private String getFirstWord() {
        String word = null;
        if (!CollectionUtil.isEmpty(lexemes)) {
            Object value = lexemes.get(0).getValue();
            if (value instanceof String) {
                word = ((String) value).toLowerCase();
            }
        }

        return word;
    }
}
