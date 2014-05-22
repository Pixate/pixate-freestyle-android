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
package com.pixate.freestyle.styling.parsing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.GridView;

import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.paints.PXImagePaint;
import com.pixate.freestyle.cg.paints.PXLinearGradient;
import com.pixate.freestyle.cg.paints.PXLinearGradient.PXLinearGradientDirection;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXPaintGroup;
import com.pixate.freestyle.cg.paints.PXRadialGradient;
import com.pixate.freestyle.cg.paints.PXSolidPaint;
import com.pixate.freestyle.cg.shadow.PXShadow;
import com.pixate.freestyle.cg.shadow.PXShadowGroup;
import com.pixate.freestyle.cg.shadow.PXShadowPaint;
import com.pixate.freestyle.styling.infos.PXAnimationInfo;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationDirection;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationFillMode;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationPlayState;
import com.pixate.freestyle.styling.infos.PXAnimationInfo.PXAnimationTimingFunction;
import com.pixate.freestyle.styling.infos.PXBorderInfo;
import com.pixate.freestyle.styling.infos.PXBorderInfo.PXBorderStyle;
import com.pixate.freestyle.styling.stylers.PXStylerContext.GridStyle.PXColumnStretchMode;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.SVGColors;
import com.pixate.freestyle.util.Size;
import com.pixate.freestyle.util.StringUtil;

@SuppressLint("DefaultLocale")
public class PXValueParser {

    private List<PXStylesheetLexeme> lexemes = null;
    private PXStylesheetLexeme currentLexeme;
    private int lexemeIndex = 0;
    private List<String> errors;
    private String filename;

    /* STATIC */
    // TODO Probably will change value's data type. PorterDuff (I guess) doesn't
    // have everything.
    private static final Map<String, PorterDuff.Mode> BLEND_MODE_MAP;

    private static final Set<PXStylesheetTokenType> NUMBER_SET;
    private static final Set<PXStylesheetTokenType> COLOR_SET;
    private static final Set<PXStylesheetTokenType> PAINT_SET;

    private static final String TAG = PXValueParser.class.getSimpleName();

    // Lazy inits
    private static Set<String> ANIMATION_KEYWORDS;
    private static Map<String, PXBorderStyle> BORDER_STYLE_MAP;

    static {
        NUMBER_SET = EnumSet.of(PXStylesheetTokenType.NUMBER, PXStylesheetTokenType.LENGTH);
        COLOR_SET = EnumSet.of(PXStylesheetTokenType.RGB, PXStylesheetTokenType.RGBA,
                PXStylesheetTokenType.HSB, PXStylesheetTokenType.HSBA, PXStylesheetTokenType.HSL,
                PXStylesheetTokenType.HSLA, PXStylesheetTokenType.HEX_COLOR,
                PXStylesheetTokenType.IDENTIFIER, PXStylesheetTokenType.ID);

        PAINT_SET = EnumSet.of(PXStylesheetTokenType.LINEAR_GRADIENT,
                PXStylesheetTokenType.RADIAL_GRADIENT, PXStylesheetTokenType.URL);
        PAINT_SET.addAll(COLOR_SET);

        BLEND_MODE_MAP = new HashMap<String, PorterDuff.Mode>();
        BLEND_MODE_MAP.put("normal", PorterDuff.Mode.SRC_OVER);
        BLEND_MODE_MAP.put("multiply", PorterDuff.Mode.MULTIPLY);
        BLEND_MODE_MAP.put("screen", PorterDuff.Mode.SCREEN);
        // BLEND_MODE_MAP.put("overlay", PorterDuff.Mode.OVERLAY); OVERLAY
        // requires api 11
        BLEND_MODE_MAP.put("darken", PorterDuff.Mode.DARKEN);
        BLEND_MODE_MAP.put("lighten", PorterDuff.Mode.LIGHTEN);
        // TODO BLEND_MODE_MAP.put("color-dodge", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("color-burn", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("soft-light", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("hard-light", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("difference", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("exclusion", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("hue", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("saturation", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("color", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("luminosity", PorterDuff.Mode.);
        BLEND_MODE_MAP.put("clear", PorterDuff.Mode.CLEAR);
        // TODO BLEND_MODE_MAP.put("copy", PorterDuff.Mode.);
        BLEND_MODE_MAP.put("source-in", PorterDuff.Mode.SRC_IN);
        BLEND_MODE_MAP.put("source-out", PorterDuff.Mode.SRC_OUT);
        BLEND_MODE_MAP.put("source-atop", PorterDuff.Mode.SRC_ATOP);
        BLEND_MODE_MAP.put("destination-over", PorterDuff.Mode.DST_OVER);
        BLEND_MODE_MAP.put("destination-in", PorterDuff.Mode.DST_IN);
        BLEND_MODE_MAP.put("destination-out", PorterDuff.Mode.DST_OUT);
        BLEND_MODE_MAP.put("destination-atop", PorterDuff.Mode.DST_ATOP);
        BLEND_MODE_MAP.put("xor", PorterDuff.Mode.XOR);
        // TODO BLEND_MODE_MAP.put("plus-darker", PorterDuff.Mode.);
        // TODO BLEND_MODE_MAP.put("plus-lighter", PorterDuff.Mode.);

    }

    public static List<PXStylesheetLexeme> lexemesForSource(String source) {
        List<PXStylesheetLexeme> lexemes = new ArrayList<PXStylesheetLexeme>();

        PXStylesheetLexer lexer = new PXStylesheetLexer();
        lexer.setSource(source);

        PXStylesheetLexeme lexeme = lexer.nextLexeme();
        while (!(lexeme == null || lexeme.getType() == PXStylesheetTokenType.EOF)) {
            lexemes.add(lexeme);
            lexeme = lexer.nextLexeme();
        }

        if (PXLog.isLogging()) {
            PXLog.d(TAG, "Lexemes for source \"%s\"", source);
            for (PXStylesheetLexeme l : lexemes) {
                PXLog.d(TAG, "%s: %s", l.getTypeName().toUpperCase(Locale.US), l.getValue()
                        .toString());
            }
        }

        return lexemes;
    }

    /* PUBLIC METHODS */

    public List<PXAnimationInfo> parseAnimationInfos(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<PXAnimationInfo> items = new ArrayList<PXAnimationInfo>();

        try {
            items.add(parseAnimationInfo());

            while (isType(PXStylesheetTokenType.COMMA)) {
                advance();
                items.add(parseAnimationInfo());
            }
        } catch (Exception e) {
            exceptionWithMessage(e.getMessage());
        }

        return items;
    }

    public List<PXAnimationInfo> parseTransitionInfos(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<PXAnimationInfo> items = new ArrayList<PXAnimationInfo>();

        try {
            items.add(parseTransitionInfo());

            while (isType(PXStylesheetTokenType.COMMA)) {
                advance();
                items.add(parseTransitionInfo());
            }
        } catch (Exception e) {
            exceptionWithMessage(e.getMessage());
        }

        return items;
    }

    public Uri parseURL(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);
        return parseURL();
    }

    public List<PXAnimationDirection> parseAnimationDirectionList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<PXAnimationDirection> items = new ArrayList<PXAnimationDirection>();

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                items.add(this.getAnimationDirection());
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // advance over ','
                    advance();

                    if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                        items.add(this.getAnimationDirection());
                        advance();
                    } else {
                        exceptionWithMessage("Expected an animation direction after a comma in the times list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public List<PXAnimationFillMode> parseAnimationFillModeList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<PXAnimationFillMode> items = new ArrayList<PXAnimationFillMode>();

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                items.add(this.getAnimationFillMode());
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // advance over ','
                    advance();

                    if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                        items.add(this.getAnimationFillMode());
                        advance();
                    } else {
                        exceptionWithMessage("Expected an animation fill mode after a comma in the times list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public List<PXAnimationPlayState> parseAnimationPlayStateList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<PXAnimationPlayState> items = new ArrayList<PXAnimationPlayState>();

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                items.add(this.getAnimationPlayState());
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // advance over ','
                    advance();

                    if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                        items.add(this.getAnimationPlayState());
                        advance();
                    } else {
                        exceptionWithMessage("Expected an animation play state after a comma in the times list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public List<PXAnimationTimingFunction> parseAnimationTimingFunctionList(
            List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<PXAnimationTimingFunction> items = new ArrayList<PXAnimationTimingFunction>();

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                items.add(this.getAnimationTimingFunction());
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // advance over ','
                    advance();

                    if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                        items.add(this.getAnimationTimingFunction());
                        advance();
                    } else {
                        exceptionWithMessage("Expected an animation timing function "
                                + "after a comma in the times list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public PXBorderInfo parseBorder(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        PXBorderInfo settings = new PXBorderInfo();

        try {
            if (isInTypeSet(NUMBER_SET)) {
                settings.setWidth(readNumber(displayMetrics));
            }

            if (isType(PXStylesheetTokenType.IDENTIFIER) && !isSVGColorName()) {
                settings.setStyle(parseBorderStyle());
            }

            if (isInTypeSet(PAINT_SET)) {
                settings.setPaint(parseSinglePaint());
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return settings;
    }

    public List<Size> parseBorderRadiusList(List<PXStylesheetLexeme> lexemes,
            DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        // @formatter:off
        float topLeftWidth, topLeftHeight, topRightWidth, topRightHeight, bottomRightWidth,
                bottomRightHeight, bottomLeftWidth, bottomLeftHeight;
        topLeftWidth = topLeftHeight = topRightWidth = topRightHeight = bottomRightWidth =
                bottomRightHeight = bottomLeftWidth = bottomLeftHeight = 0.0f;
        // @formatter:on

        try {
            PXOffsets xRadii = parseOffsets(displayMetrics);

            topLeftWidth = topLeftHeight = xRadii.getTop();
            topRightWidth = topRightHeight = xRadii.getRight();
            bottomRightWidth = bottomRightHeight = xRadii.getBottom();
            bottomLeftWidth = bottomLeftHeight = xRadii.getLeft();

            if (isType(PXStylesheetTokenType.SLASH)) {
                advance(); // past '/'

                PXOffsets yRadii = parseOffsets(displayMetrics);

                topLeftHeight = yRadii.getTop();
                topRightHeight = yRadii.getRight();
                bottomRightHeight = yRadii.getBottom();
                bottomLeftHeight = yRadii.getLeft();
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return Arrays.asList(new Size(topLeftWidth, topLeftHeight), new Size(topRightWidth,
                topRightHeight), new Size(bottomRightWidth, bottomRightHeight), new Size(
                bottomLeftWidth, bottomLeftHeight));
    }

    public PXBorderStyle parseBorderStyle(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);
        return parseBorderStyle();
    }

    public List<PXBorderStyle> parseBorderStyleList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        PXBorderStyle top, right, bottom, left;
        top = right = bottom = left = PXBorderStyle.NONE;

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                top = right = bottom = left = parseBorderStyle();
            }
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                right = left = parseBorderStyle();
            }
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                bottom = parseBorderStyle();
            }
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                left = parseBorderStyle();
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return Arrays.asList(top, right, bottom, left);
    }

    public Integer parseColor(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        Integer result = null;

        try {
            result = getColor();
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public float parseColumnWidth(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        float result = 0.0f;

        try {
            result = getColumnWidth(displayMetrics);
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public int parseColumnGap(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        int result = 0; // android uses 0 internally to indicate no setting for
                        // spacing.

        try {
            result = getColumnGap(displayMetrics);
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public PXColumnStretchMode parseColumnStretchMode(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        PXColumnStretchMode result = PXColumnStretchMode.COLUMN_WIDTH; // Android's
                                                                       // default

        try {
            result = getColumnStretchMode();
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public int parseRowGap(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        int result = 0; // android uses 0 internally to indicate no setting for
                        // spacing.

        try {
            result = getRowGap(displayMetrics);
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public float parseFloat(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        float result = 0.0f;

        try {
            result = getFloatValue(displayMetrics);
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;

    }

    public List<Float> parseFloatList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<Float> items = new ArrayList<Float>();

        try {
            if (isType(PXStylesheetTokenType.NUMBER)) {
                items.add(Float.parseFloat((String) currentLexeme.getValue()));
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    if (isType(PXStylesheetTokenType.NUMBER)) {
                        items.add(Float.parseFloat((String) currentLexeme.getValue()));
                        advance();
                    } else {
                        exceptionWithMessage("Expected an number after a comma in the number list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public int parseColumnCount(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);
        int result = GridView.AUTO_FIT;

        try {
            result = getColumnCount();
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public PXOffsets parseInsets(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        return parseOffsets(lexemes, displayMetrics);
    }

    public List<String> parseNameList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<String> items = new ArrayList<String>();

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                items.add((String) currentLexeme.getValue());
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // Advance over ","
                    advance();

                    if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                        items.add((String) currentLexeme.getValue());
                        advance();

                    } else {
                        exceptionWithMessage("Expected an identifier after a comma in the name list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public PXOffsets parseOffsets(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        return parseOffsets(displayMetrics);
    }

    public PXOffsets parseOffsets(DisplayMetrics displayMetrics) {
        float top, right, bottom, left;
        top = right = bottom = left = 0.0f;

        try {
            // one number
            if (isInTypeSet(NUMBER_SET)) {
                top = right = bottom = left = readNumber(displayMetrics);
            }

            // two numbers
            if (isInTypeSet(NUMBER_SET)) {
                right = left = readNumber(displayMetrics);
            }

            // three numbers
            if (isInTypeSet(NUMBER_SET)) {
                bottom = readNumber(displayMetrics);
            }

            // four numbers
            if (isInTypeSet(NUMBER_SET)) {
                left = readNumber(displayMetrics);
            }
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return new PXOffsets(top, right, bottom, left);
    }

    public PXPaint parsePaint(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        PXPaint result = null;

        try {
            result = parseSinglePaint();

            if (isType(PXStylesheetTokenType.COMMA)) {

                PXPaintGroup group = new PXPaintGroup();

                group.addPaint(result);

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // Advance over comma
                    advance();

                    group.addPaint(parseSinglePaint());
                }

                result = group;
            }
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;

    }

    public List<PXPaint> parsePaints(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        PXPaint topPaint, bottomPaint, rightPaint, leftPaint;
        topPaint = bottomPaint = rightPaint = leftPaint = null;

        try {
            // NOTE: There can be zero or more paints in the token stream. If
            // the very first item is not a paint, we go ahead and default all
            // colors to black. The following if-blocks will fail, so doing it
            // here is similar to setting a default value for these items.
            if (isInTypeSet(PAINT_SET)) {
                topPaint = rightPaint = bottomPaint = leftPaint = parseSinglePaint();
            } else {
                topPaint = rightPaint = bottomPaint = leftPaint = PXSolidPaint
                        .createPaintWithColor(Color.BLACK);
            }

            if (isInTypeSet(PAINT_SET)) {
                rightPaint = leftPaint = parseSinglePaint();
            }

            if (isInTypeSet(PAINT_SET)) {
                bottomPaint = parseSinglePaint();
            }

            if (isInTypeSet(PAINT_SET)) {
                leftPaint = parseSinglePaint();
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return Arrays.asList(topPaint, rightPaint, bottomPaint, leftPaint);
    }

    public float parseSeconds(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        float seconds = 0.0f;

        try {
            seconds = getSecondsValue();

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return seconds;
    }

    public List<Float> parseSecondsList(List<PXStylesheetLexeme> lexemes) {
        setupWithLexemes(lexemes);

        List<Float> items = new ArrayList<Float>();

        try {
            if (isType(PXStylesheetTokenType.TIME)) {

                items.add(getSecondsValue());
                advance();

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // advance over ","
                    advance();

                    if (isType(PXStylesheetTokenType.TIME)) {
                        items.add(getSecondsValue());
                        advance();

                    } else {
                        exceptionWithMessage("Expected an time after a comma in the times list");
                    }
                }
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return items;
    }

    public PXShadowPaint parseShadow(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);
        PXShadowPaint result = null;

        try {
            result = parseShadow(displayMetrics);

            if (isType(PXStylesheetTokenType.COMMA)) {
                PXShadowGroup group = new PXShadowGroup();

                group.add(result);

                while (isType(PXStylesheetTokenType.COMMA)) {
                    // skip over ","
                    advance();

                    group.add(parseShadow(displayMetrics));
                }

                result = group;
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    public Size parseSize(List<PXStylesheetLexeme> lexemes, DisplayMetrics displayMetrics) {
        setupWithLexemes(lexemes);

        float width = 0.0f;
        float height = 0.0f;

        try {

            // one number
            if (isInTypeSet(NUMBER_SET)) {
                width = height = readNumber(displayMetrics);
            }

            // two numbers
            if (isInTypeSet(NUMBER_SET)) {
                height = readNumber(displayMetrics);
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return new Size(width, height);

    }

    public void setFilename(String name) {
        filename = name;
    }

    public String getFilename() {
        return filename;
    }

    /* (end public) */

    /* PRIVATE METHODS */

    private void addError(String message) {
        String offset = isType(PXStylesheetTokenType.EOF) ? "EOF" : (currentLexeme != null ? String
                .valueOf(currentLexeme.getOffset()) : "Null lexeme");

        addError(message, filename, offset);
    }

    private void addError(String message, String filename, String offset) {
        String amendedError;

        if (!StringUtil.isEmpty(filename)) {
            amendedError = MessageFormat.format("[PXEngine.ParseError, file={0}, offset={1}]: {2}",
                    filename, offset, message);
        } else {
            amendedError = MessageFormat.format("[PXEngine.ParseError, offset={0}]: {1}", offset,
                    message);
        }

        internalAddError(amendedError);

        // TODO equiv of Obj-C [PXEngine.configuration
        // sendParseMessage:ammendedError];
    }

    private void internalAddError(String message) {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        errors.add(message);
    }

    private void setupWithLexemes(List<PXStylesheetLexeme> lexemes) {
        clearErrors();
        setLexemes(lexemes);
        advance();
    }

    private void clearErrors() {
        if (errors != null) {
            errors.clear();
        }
    }

    private PXStylesheetLexeme advance() {
        if (CollectionUtil.isEmpty(lexemes)) {
            return null;
        }

        return currentLexeme = lexemeIndex < lexemes.size() ? lexemes.get(lexemeIndex++) : null;

    }

    private void advanceIfIsType(PXStylesheetTokenType tokenType) {
        if (isType(tokenType)) {
            advance();
        }
    }

    private PXStylesheetLexeme assertTypeAndAdvance(PXStylesheetTokenType tokenType) {
        assertType(tokenType);
        return advance();
    }

    private void assertTypeInSet(Set<PXStylesheetTokenType> typeSet) {
        if (!isInTypeSet(typeSet)) {
            StringBuilder sb = new StringBuilder();
            for (PXStylesheetTokenType tokenType : typeSet) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(tokenType.name());
            }

            exceptionWithMessage(MessageFormat.format(
                    "Expected a token of one of these types: {0}", sb.toString()));
        }
    }

    private void setLexemes(List<PXStylesheetLexeme> lexemes) {
        this.lexemes = lexemes;
        this.lexemeIndex = 0;
    }

    private PXBorderStyle parseBorderStyle() {
        synchronized (PXValueParser.class) {

            if (BORDER_STYLE_MAP == null) {

                // One-time init
                BORDER_STYLE_MAP = new HashMap<String, PXBorderStyle>(10);
                BORDER_STYLE_MAP.put("none", PXBorderStyle.NONE);
                BORDER_STYLE_MAP.put("hidden", PXBorderStyle.HIDDEN);
                BORDER_STYLE_MAP.put("dotted", PXBorderStyle.DOTTED);
                BORDER_STYLE_MAP.put("dashed", PXBorderStyle.DASHED);
                BORDER_STYLE_MAP.put("solid", PXBorderStyle.SOLID);
                BORDER_STYLE_MAP.put("double", PXBorderStyle.DOUBLE);
                BORDER_STYLE_MAP.put("groove", PXBorderStyle.GROOVE);
                BORDER_STYLE_MAP.put("ridge", PXBorderStyle.RIDGE);
                BORDER_STYLE_MAP.put("inset", PXBorderStyle.INSET);
                BORDER_STYLE_MAP.put("outset", PXBorderStyle.OUTSET);
            }
        }

        PXBorderStyle result = PXBorderStyle.NONE;

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                String text = (String) currentLexeme.getValue();
                PXBorderStyle value = BORDER_STYLE_MAP.get(text.toLowerCase());
                if (value != null) {
                    result = value;
                }
                advance();
            }
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    private PXShadowPaint parseShadow(DisplayMetrics displayMetrics) {
        PXShadow result = new PXShadow();

        if (isIdentifierWithName("inset")) {
            result.setIsInset(true);
            advance();
        }

        // grab required x-offset
        assertTypeInSet(NUMBER_SET);
        result.setHorizontalOffset(getFloatValue(displayMetrics));

        // grab required y-offset
        assertTypeInSet(NUMBER_SET);
        result.setVerticalOffset(getFloatValue(displayMetrics));

        // Next two lengths are optional
        if (isType(PXStylesheetTokenType.LENGTH) || isType(PXStylesheetTokenType.NUMBER)) {
            result.setBlurDistance(getFloatValue(displayMetrics));

            if (isType(PXStylesheetTokenType.LENGTH) || isType(PXStylesheetTokenType.NUMBER)) {
                result.setSpreadDistance(getFloatValue(displayMetrics));
            }
        }

        // Color is optional
        result.setColor(isInTypeSet(COLOR_SET) ? getColor() : Color.BLACK);

        return result;
    }

    private boolean isType(PXStylesheetTokenType tokenType) {
        return currentLexeme != null && currentLexeme.getType() == tokenType;
    }

    private boolean isIdentifierWithName(String name) {
        return isType(PXStylesheetTokenType.IDENTIFIER)
                && name.equals((String) currentLexeme.getValue());
    }

    private boolean isInTypeSet(Set<PXStylesheetTokenType> typeSet) {
        return currentLexeme != null && typeSet.contains(currentLexeme.getType());
    }

    private boolean isSVGColorName() {
        if (!isType(PXStylesheetTokenType.IDENTIFIER)) {
            return false;
        }

        String value = (String) currentLexeme.getValue();
        return SVGColors.has(value);
    }

    private PXAnimationInfo parseAnimationInfo() {
        synchronized (PXValueParser.class) {
            if (ANIMATION_KEYWORDS == null) {

                // One-time init
                ANIMATION_KEYWORDS = new HashSet<String>();
                ANIMATION_KEYWORDS.addAll(PXAnimationDirection.getCssValueSet());
                ANIMATION_KEYWORDS.addAll(PXAnimationFillMode.getCssValueSet());
                ANIMATION_KEYWORDS.addAll(PXAnimationPlayState.getCssValueSet());
                ANIMATION_KEYWORDS.addAll(PXAnimationTimingFunction.getCssValueSet());
            }
        }

        PXAnimationInfo info = new PXAnimationInfo();

        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && !ANIMATION_KEYWORDS.contains(currentLexeme.getValue())) {
            info.animationName = (String) currentLexeme.getValue();
            advance();
        }
        if (isType(PXStylesheetTokenType.TIME)) {
            info.animationDuration = this.getSecondsValue();
        }
        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && PXAnimationTimingFunction.ofCssValue((String) currentLexeme.getValue()) != null) {
            info.animationTimingFunction = this.getAnimationTimingFunction();
        }
        if (isType(PXStylesheetTokenType.TIME)) {
            info.animationDelay = this.getSecondsValue();
        }
        if (isType(PXStylesheetTokenType.NUMBER)) {
            info.animationIterationCount = Integer.parseInt((String) currentLexeme.getValue());
            advance();
        }
        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && PXAnimationDirection.ofCssValue((String) currentLexeme.getValue()) != null) {
            info.animationDirection = this.getAnimationDirection();
        }
        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && PXAnimationFillMode.ofCssValue((String) currentLexeme.getValue()) != null) {
            info.animationFillMode = this.getAnimationFillMode();
        }
        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && PXAnimationPlayState.ofCssValue((String) currentLexeme.getValue()) != null) {
            info.animationPlayState = this.getAnimationPlayState();
        }

        return info;
    }

    private PXPaint parseSinglePaint() {
        PXPaint result = null;

        if (isInTypeSet(PAINT_SET)) {
            if (isType(PXStylesheetTokenType.LINEAR_GRADIENT)) {
                result = getLinearGradient();

            } else if (isType(PXStylesheetTokenType.RADIAL_GRADIENT)) {
                result = getRadialGradient();

            } else if (isType(PXStylesheetTokenType.URL)) {
                result = new PXImagePaint(parseURL());

            } else if (isInTypeSet(COLOR_SET)) {
                result = new PXSolidPaint(getColor());

            } else {
                exceptionWithMessage("Unsupported paint token type");
            }

            if (isType(PXStylesheetTokenType.IDENTIFIER) && !isSVGColorName()) {
                String blendingMode = (String) currentLexeme.getValue();
                @SuppressWarnings("rawtypes")
                Enum blendModeValue = BLEND_MODE_MAP.get(blendingMode);
                if (blendModeValue instanceof PorterDuff.Mode) {
                    // TODO this is the only one we have so far (Porter Duff,
                    // that is)
                    result.setBleningMode(new PorterDuffXfermode((PorterDuff.Mode) blendModeValue));
                }

                advance();
            }

        } else {
            exceptionWithMessage("Unrecognized paint token type");
        }

        return result;
    }

    private PXAnimationInfo parseTransitionInfo() {
        PXAnimationInfo info = new PXAnimationInfo();

        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && PXAnimationTimingFunction.ofCssValue((String) currentLexeme.getValue()) == null) {
            info.animationName = (String) currentLexeme.getValue();
            advance();
        }
        if (isType(PXStylesheetTokenType.TIME)) {
            info.animationDuration = this.getSecondsValue();
        }
        if (isType(PXStylesheetTokenType.IDENTIFIER)
                && PXAnimationTimingFunction.ofCssValue((String) currentLexeme.getValue()) != null) {
            info.animationTimingFunction = this.getAnimationTimingFunction();
        }
        if (isType(PXStylesheetTokenType.TIME)) {
            info.animationDelay = this.getSecondsValue();
        }

        return info;
    }

    private Uri parseURL() {
        Uri result = null;

        try {
            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                advance();

            } else {
                assertType(PXStylesheetTokenType.URL);
                String path = (String) currentLexeme.getValue();
                advance();
                result = Uri.parse(path.replace(" ", "%20"));
            }

        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    private PXLinearGradient getLinearGradient() {
        PXLinearGradient result = new PXLinearGradient();

        assertTypeAndAdvance(PXStylesheetTokenType.LINEAR_GRADIENT);

        if (isType(PXStylesheetTokenType.ANGLE)) {
            result.setCssAngle(getAngleValue());
            // Skip optional comma
            advanceIfIsType(PXStylesheetTokenType.COMMA);

        } else if (isIdentifierWithName("to")) {
            // advance over 'to'
            advance();

            if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                String text = (String) currentLexeme.getValue();

                if ("left".equals(text)) {
                    advance();

                    if (isIdentifierWithName("top")) {
                        // advance over 'top'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_TOP_LEFT);

                    } else if (isIdentifierWithName("bottom")) {
                        // advance over 'bottom'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_BOTTOM_LEFT);

                    } else {
                        result.setGradientDirection(PXLinearGradientDirection.TO_LEFT);

                    }

                } else if ("right".equals(text)) {
                    advance();

                    if (isIdentifierWithName("top")) {
                        // advance over 'top'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_TOP_RIGHT);

                    } else if (isIdentifierWithName("bottom")) {
                        // advance over 'bottom'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_BOTTOM_RIGHT);

                    } else {
                        result.setGradientDirection(PXLinearGradientDirection.TO_RIGHT);

                    }

                } else if ("top".equals(text)) {
                    advance();

                    if (isIdentifierWithName("left")) {
                        // advance over 'left'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_TOP_LEFT);

                    } else if (isIdentifierWithName("right")) {
                        // advance over 'right'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_TOP_RIGHT);

                    } else {
                        result.setGradientDirection(PXLinearGradientDirection.TO_TOP);

                    }

                } else if ("bottom".equals(text)) {
                    advance();

                    if (isIdentifierWithName("left")) {
                        // advance over 'left'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_BOTTOM_LEFT);

                    } else if (isIdentifierWithName("right")) {
                        // advance over 'right'
                        advance();

                        result.setGradientDirection(PXLinearGradientDirection.TO_BOTTOM_RIGHT);

                    } else {
                        result.setGradientDirection(PXLinearGradientDirection.TO_BOTTOM);

                    }

                } else {
                    exceptionWithMessage("Expected 'top', 'right', 'bottom', or 'left' keyword "
                            + "after 'to' when defining a linear gradient angle");
                }

                // Skip optional comma
                advanceIfIsType(PXStylesheetTokenType.COMMA);

            } else {
                exceptionWithMessage("Expected 'top', 'right', 'bottom', or 'left' keyword "
                        + "after 'to' when defining a linear gradient angle");
            }
        }

        // collect colors
        do {
            int color = getColor();

            if (isType(PXStylesheetTokenType.PERCENTAGE)) {
                PXDimension percent = (PXDimension) currentLexeme.getValue();
                float offset = percent.getNumber() / 100.0f;

                advance();

                result.addColor(color, offset);
            } else {
                result.addColor(color);
            }

            // Skip optional comma
            advanceIfIsType(PXStylesheetTokenType.COMMA);
        } while (isInTypeSet(COLOR_SET));

        // advance over ')'
        advanceIfIsType(PXStylesheetTokenType.RPAREN);

        return result;
    }

    private PXRadialGradient getRadialGradient() {
        PXRadialGradient result = new PXRadialGradient();

        assertTypeAndAdvance(PXStylesheetTokenType.RADIAL_GRADIENT);

        // Collect colors
        do {
            int color = getColor();

            if (isType(PXStylesheetTokenType.PERCENTAGE)) {
                result.addColor(color, getPercentageValue());
            } else {
                result.addColor(color);
            }

            // skip optional comma
            advanceIfIsType(PXStylesheetTokenType.COMMA);

        } while (isInTypeSet(COLOR_SET));

        // Advance over ")"
        assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);

        return result;
    }

    private float getPercentageValue() {
        float result = 0.0f;

        assertType(PXStylesheetTokenType.PERCENTAGE);

        Object value = currentLexeme.getValue();

        if (value instanceof PXDimension) {
            result = ((PXDimension) value).getNumber() / 100.0f;

        } else {
            exceptionWithMessage("PERCENTAGE lexeme did not have PXDimension value");
        }

        advance();

        return result;
    }

    private float getSecondsValue() {
        float result = 0.0f;
        assertType(PXStylesheetTokenType.TIME);

        Object value = currentLexeme.getValue();

        if (value instanceof PXDimension) {
            PXDimension dimension = (PXDimension) value;

            if (dimension.isSeconds()) {
                result = dimension.getNumber();

            } else if (dimension.isMilliseconds()) {
                result = dimension.getNumber() / 1000.0f;

            } else {
                exceptionWithMessage(MessageFormat.format("Unrecognized time unit: {0}", dimension));
            }

        } else {
            exceptionWithMessage("TIME lexeme did not have PXDimension value");
        }

        advance();

        return result;
    }

    private PXAnimationTimingFunction getAnimationTimingFunction() {
        PXAnimationTimingFunction result = PXAnimationTimingFunction.UNDEFINED;
        if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            String text = (String) currentLexeme.getValue();
            try {
                result = PXAnimationTimingFunction.valueOf(text.toLowerCase());
            } catch (IllegalArgumentException e) {
                // no-op
            }
        } else {
            exceptionWithMessage("Expected identifier for animation timing function");
        }

        advance();
        return result;
    }

    private float getFloatValue(DisplayMetrics displayMetrics) {
        Object value = currentLexeme.getValue();
        float result = 0.0f;

        if (value instanceof Number) {
            result = ((Number) value).floatValue();
        } else if (value instanceof String) {
            result = Float.parseFloat((String) value);
        } else if (value instanceof PXDimension) {
            PXDimension dimension = (PXDimension) value;
            result = dimension.points(displayMetrics).getNumber();
        }

        advance();
        return result;

    }

    private int getColumnCount() {
        int result = GridView.AUTO_FIT;
        PXStylesheetTokenType currentType = currentLexeme.getType();
        Object value = currentLexeme.getValue();

        // If currentType is a number, that's the number of columns. If it's an
        // IDENTIFIER, then the only valid value is "auto". If it's anything
        // else, it's an invalid value and we'll anyway use the default of
        // "auto".
        switch (currentType) {
            case NUMBER:
                if (value instanceof Number) {
                    result = ((Number) value).intValue();
                } else {
                    result = Integer.parseInt((String) value);
                }
                break;
            case IDENTIFIER:
                // Check is here to just show the warning if applicable. Default
                // of "auto" is only valid value.
                if (!("auto".equals(value))) {
                    exceptionWithMessage("'" + value + "' unrecognized as value for column count.");
                }
                break;
            default:
                exceptionWithMessage("'" + value + "' unrecognized as value for column count.");

        }

        advance();
        return result;
    }

    private int getColumnGap(DisplayMetrics displayMetrics) {
        Object value = currentLexeme.getValue();
        int result = 0; // "normal"
        PXStylesheetTokenType currentType = currentLexeme.getType();

        switch (currentType) {
            case IDENTIFIER:
                if (!("normal".equals(value))) {
                    exceptionWithMessage("'" + value + "' unrecognized as value for column gap");
                }
                break;
            default:
                if (value instanceof Number) {
                    result = ((Number) value).intValue();
                } else if (value instanceof String) {
                    result = Integer.parseInt((String) value);
                } else if (value instanceof PXDimension) {
                    PXDimension dimension = (PXDimension) value;
                    result = (int) Math.ceil(dimension.points(displayMetrics).getNumber());
                } else {
                    exceptionWithMessage("'" + value + "' unrecognized as value for column gap");
                }

        }

        advance();
        return result;
    }

    private PXColumnStretchMode getColumnStretchMode() {
        PXColumnStretchMode result = PXColumnStretchMode.COLUMN_WIDTH; // Android
                                                                       // default.
        PXStylesheetTokenType currentType = currentLexeme.getType();
        Object value = currentLexeme.getValue();

        if (currentType != PXStylesheetTokenType.IDENTIFIER) {
            exceptionWithMessage("Expected a column stretch mode identifier. '" + value
                    + "' is unrecognized.");
        } else {
            result = PXColumnStretchMode.ofCssValue((String) value);
            if (result == null) {
                exceptionWithMessage("Expected a column stretch mode identifier. '" + value
                        + "' is unrecognized.");
            }
        }

        advance();
        return result;
    }

    private int getRowGap(DisplayMetrics displayMetrics) {
        Object value = currentLexeme.getValue();
        int result = 0; // "normal"
        PXStylesheetTokenType currentType = currentLexeme.getType();

        switch (currentType) {
            case IDENTIFIER:
                if (!("normal".equals(value))) {
                    exceptionWithMessage("'" + value + "' unrecognized as value for row gap");
                }
                break;
            default:
                if (value instanceof Number) {
                    result = ((Number) value).intValue();
                } else if (value instanceof String) {
                    result = Integer.parseInt((String) value);
                } else if (value instanceof PXDimension) {
                    PXDimension dimension = (PXDimension) value;
                    result = (int) Math.ceil(dimension.points(displayMetrics).getNumber());
                } else {
                    exceptionWithMessage("'" + value + "' unrecognized as value for row gap");
                }

        }

        advance();
        return result;
    }

    private float getColumnWidth(DisplayMetrics displayMetrics) {
        Object value = currentLexeme.getValue();
        float result = 0.0f; // "auto"
        PXStylesheetTokenType currentType = currentLexeme.getType();

        switch (currentType) {
            case IDENTIFIER:
                if (!("auto".equals(value))) {
                    exceptionWithMessage("'" + value + "' unrecognized as value for column width");
                }
                break;
            default:
                if (value instanceof Number) {
                    result = ((Number) value).floatValue();
                } else if (value instanceof String) {
                    result = Float.parseFloat((String) value);
                } else if (value instanceof PXDimension) {
                    PXDimension dimension = (PXDimension) value;
                    result = dimension.points(displayMetrics).getNumber();
                }

        }

        advance();
        return result;
    }

    private Integer getColor() {
        Integer result = null;

        PXStylesheetTokenType currentType = currentLexeme.getType();

        switch (currentType) {
            case RGB:
                advance();

                int red = (int) readByteOrPercent(1.0f),
                green = (int) readByteOrPercent(1.0f),
                blue = (int) readByteOrPercent(1.0f);

                result = Color.argb(255, red, green, blue);
                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;

            case RGBA: {
                float r, g, b, a;

                advance();

                if (isType(PXStylesheetTokenType.HEX_COLOR)
                        || isType(PXStylesheetTokenType.IDENTIFIER)) {
                    int color = Color.parseColor((String) currentLexeme.getValue());
                    r = Color.red(color);
                    g = Color.green(color);
                    b = Color.blue(color);
                    a = Color.alpha(color);

                    advance();
                    advanceIfIsType(PXStylesheetTokenType.COMMA);

                } else {
                    r = readByteOrPercent(1.0f);
                    g = readByteOrPercent(1.0f);
                    b = readByteOrPercent(1.0f);
                    a = readByteOrPercent(1.0f);
                }

                result = Color.argb((int) a, (int) r, (int) g, (int) b);

                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;
            }

            case HSL: {
                advance();

                float hue = readAngle(), saturation = readByteOrPercent(255.0f), lightness = readByteOrPercent(255.0f);

                // TODO HSL -> argb calculation. Here we're pretending we
                // actually
                // got HSV. CHANGE THIS
                result = Color.HSVToColor(new float[] { hue, saturation, lightness });

                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;
            }

            case HSLA: {
                advance();

                float hue = readAngle(), saturation = readByteOrPercent(255.0f), lightness = readByteOrPercent(255.0f), alpha = readByteOrPercent(1.0f);

                // TODO HSLA -> argb calculation. Here we're pretending we
                // actually
                // got HSV. CHANGE THIS
                result = Color.HSVToColor((int) (alpha * 255.0f), new float[] { hue, saturation,
                        lightness });

                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;
            }

            case HSB: {
                advance();

                float hue = readAngle(), saturation = readByteOrPercent(255.0f), brightness = readByteOrPercent(255.0f);

                result = Color.HSVToColor(new float[] { hue, saturation, brightness });

                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;
            }

            case HSBA: {
                advance();

                float hue = readAngle(), saturation = readByteOrPercent(255.0f), brightness = readByteOrPercent(255.0f), alpha = readByteOrPercent(1.0f);

                result = Color.HSVToColor((int) (alpha * 255.0f), new float[] { hue, saturation,
                        brightness });

                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;
            }

            case ID:
                result = Color.parseColor((String) currentLexeme.getValue());
                advance();
                break;
            case IDENTIFIER:
                result = SVGColors.get((String) currentLexeme.getValue());
                advance();
                break;

            default:
                exceptionWithMessage("Expected RGB, RGBA, HSB, HSBA, HSL, HSLA, COLOR (hex color), "
                        + "or IDENTIFIER (named color)");
        }

        return result;
    }

    private float getAngleValue() {
        float result = 0.0f;

        assertType(PXStylesheetTokenType.ANGLE);

        Object value = currentLexeme.getValue();

        if (value instanceof PXDimension) {
            result = ((PXDimension) value).getNumber();
        } else {
            exceptionWithMessage("ANGLE lexeme did not have PXDimension value");
        }

        advance();

        return result;
    }

    private PXAnimationDirection getAnimationDirection() {
        PXAnimationDirection result = PXAnimationDirection.UNDEFINED;
        if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            String text = (String) currentLexeme.getValue();
            try {
                result = PXAnimationDirection.valueOf(text.toLowerCase());
            } catch (IllegalArgumentException e) {
                // no-op
            }
        } else {
            exceptionWithMessage("Expected identifier for animation direction");
        }

        advance();
        return result;
    }

    private PXAnimationFillMode getAnimationFillMode() {
        PXAnimationFillMode result = PXAnimationFillMode.UNDEFINED;
        if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            String text = (String) currentLexeme.getValue();
            try {
                result = PXAnimationFillMode.valueOf(text.toLowerCase());
            } catch (IllegalArgumentException e) {
                // no-op
            }
        } else {
            exceptionWithMessage("Expected identifier for animation fill mode");
        }

        advance();
        return result;
    }

    private PXAnimationPlayState getAnimationPlayState() {
        PXAnimationPlayState result = PXAnimationPlayState.UNDEFINED;
        if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            String text = (String) currentLexeme.getValue();
            try {
                result = PXAnimationPlayState.valueOf(text.toLowerCase());
            } catch (IllegalArgumentException e) {
                // no-op
            }
        } else {
            exceptionWithMessage("Expected identifier for animation play state");
        }

        advance();
        return result;
    }

    // read a value from [0,255] or a percentage and return in range [0,1]
    private float readByteOrPercent(float divisor) {

        float result = 0.0f;

        if (isType(PXStylesheetTokenType.NUMBER)) {
            result = Float.parseFloat((String) currentLexeme.getValue()) / divisor;

            advance();
        } else if (isType(PXStylesheetTokenType.PERCENTAGE)) {
            float percent = ((PXDimension) currentLexeme.getValue()).getNumber();

            result = percent / 100.0f;

            advance();
        }

        advanceIfIsType(PXStylesheetTokenType.COMMA);

        return result;
    }

    // read a value from [0,360] or an angle and return in range [0,1]
    private float readAngle() {

        float result = 0.0f;

        if (isType(PXStylesheetTokenType.NUMBER)) {
            result = Float.parseFloat((String) currentLexeme.getValue()) / 360.0f;

            advance();
        } else if (isType(PXStylesheetTokenType.ANGLE)) {
            PXDimension degrees = ((PXDimension) currentLexeme.getValue()).degrees();

            result = degrees.getNumber() / 360.0f;

            advance();
        }

        advanceIfIsType(PXStylesheetTokenType.COMMA);

        return result;
    }

    private float readNumber(DisplayMetrics displayMetrics) {
        float result = 0.0f;

        if (isType(PXStylesheetTokenType.NUMBER)) {
            result = Float.parseFloat((String) currentLexeme.getValue());
            advance();
        } else if (isType(PXStylesheetTokenType.LENGTH)) {
            PXDimension length = (PXDimension) currentLexeme.getValue();
            result = length.points(displayMetrics).getNumber();
            advance();
        }

        advanceIfIsType(PXStylesheetTokenType.COMMA);

        return result;
    }

    private void assertType(PXStylesheetTokenType tokenType) {
        if (!isType(tokenType)) {
            exceptionWithMessage("Expected a " + tokenType.name() + " token");
        }
    }

    private void exceptionWithMessage(String message) {
        throw new RuntimeException("Unexpected token type. " + message + ". Found "
                + (currentLexeme != null ? currentLexeme.getTypeName() : "null"));
    }

}
