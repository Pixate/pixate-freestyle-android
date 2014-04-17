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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import android.content.Context;

import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.parsing.Lexeme;
import com.pixate.freestyle.parsing.PXParserBase;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.PXStylesheet;
import com.pixate.freestyle.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.freestyle.styling.animation.PXKeyframe;
import com.pixate.freestyle.styling.animation.PXKeyframeBlock;
import com.pixate.freestyle.styling.combinators.PXAdjacentSiblingCombinator;
import com.pixate.freestyle.styling.combinators.PXChildCombinator;
import com.pixate.freestyle.styling.combinators.PXCombinatorBase;
import com.pixate.freestyle.styling.combinators.PXDescendantCombinator;
import com.pixate.freestyle.styling.combinators.PXSiblingCombinator;
import com.pixate.freestyle.styling.media.PXMediaExpression;
import com.pixate.freestyle.styling.media.PXMediaExpressionGroup;
import com.pixate.freestyle.styling.media.PXNamedMediaExpression;
import com.pixate.freestyle.styling.selectors.PXAttributeSelector;
import com.pixate.freestyle.styling.selectors.PXAttributeSelectorOperator;
import com.pixate.freestyle.styling.selectors.PXClassSelector;
import com.pixate.freestyle.styling.selectors.PXIdSelector;
import com.pixate.freestyle.styling.selectors.PXNotPseudoClass;
import com.pixate.freestyle.styling.selectors.PXPseudoClassFunction;
import com.pixate.freestyle.styling.selectors.PXPseudoClassPredicate;
import com.pixate.freestyle.styling.selectors.PXPseudoClassSelector;
import com.pixate.freestyle.styling.selectors.PXSelector;
import com.pixate.freestyle.styling.selectors.PXTypeSelector;
import com.pixate.freestyle.styling.selectors.PXAttributeSelectorOperator.PXAttributeSelectorOperatorType;
import com.pixate.freestyle.styling.selectors.PXPseudoClassFunction.PXPseudoClassFunctionType;
import com.pixate.freestyle.styling.selectors.PXPseudoClassPredicate.PXPseudoClassPredicateType;
import com.pixate.freestyle.util.CollectionUtil;
import com.pixate.freestyle.util.IOUtil;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

/**
 * Pixate stylesheet parser.
 */
public class PXStylesheetParser extends PXParserBase<PXStylesheetTokenType> {

    private static String TAG = PXStylesheetParser.class.getSimpleName();
    private static EnumSet<PXStylesheetTokenType> SELECTOR_SEQUENCE_SET;
    private static EnumSet<PXStylesheetTokenType> SELECTOR_OPERATOR_SET;
    private static EnumSet<PXStylesheetTokenType> SELECTOR_SET;
    private static EnumSet<PXStylesheetTokenType> TYPE_SELECTOR_SET;
    private static EnumSet<PXStylesheetTokenType> SELECTOR_EXPRESSION_SET;
    private static EnumSet<PXStylesheetTokenType> TYPE_NAME_SET;
    private static EnumSet<PXStylesheetTokenType> ATTRIBUTE_OPERATOR_SET;
    private static EnumSet<PXStylesheetTokenType> DECLARATION_DELIMITER_SET;
    private static EnumSet<PXStylesheetTokenType> KEYFRAME_SELECTOR_SET;
    private static EnumSet<PXStylesheetTokenType> NAMESPACE_SET;
    private static EnumSet<PXStylesheetTokenType> IMPORT_SET;
    private static EnumSet<PXStylesheetTokenType> QUERY_VALUE_SET;
    private static EnumSet<PXStylesheetTokenType> ARCHAIC_PSEUDO_ELEMENTS_SET;

    //@formatter:off
    static {
        TYPE_NAME_SET = EnumSet.of(
                PXStylesheetTokenType.IDENTIFIER,
                PXStylesheetTokenType.STAR);

        TYPE_SELECTOR_SET = EnumSet.of(PXStylesheetTokenType.PIPE);
        TYPE_SELECTOR_SET.addAll(TYPE_NAME_SET);

        SELECTOR_EXPRESSION_SET = EnumSet.of(
                PXStylesheetTokenType.ID,
                PXStylesheetTokenType.CLASS,
                PXStylesheetTokenType.LBRACKET,
                PXStylesheetTokenType.COLON,
                PXStylesheetTokenType.NOT_PSEUDO_CLASS,
                PXStylesheetTokenType.LINK_PSEUDO_CLASS,
                PXStylesheetTokenType.VISITED_PSEUDO_CLASS,
                PXStylesheetTokenType.HOVER_PSEUDO_CLASS,
                PXStylesheetTokenType.ACTIVE_PSEUDO_CLASS,
                PXStylesheetTokenType.FOCUS_PSEUDO_CLASS,
                PXStylesheetTokenType.TARGET_PSEUDO_CLASS,
                PXStylesheetTokenType.LANG_PSEUDO_CLASS,
                PXStylesheetTokenType.ENABLED_PSEUDO_CLASS,
                PXStylesheetTokenType.CHECKED_PSEUDO_CLASS,
                PXStylesheetTokenType.INDETERMINATE_PSEUDO_CLASS,
                PXStylesheetTokenType.ROOT_PSEUDO_CLASS,
                PXStylesheetTokenType.NTH_CHILD_PSEUDO_CLASS,
                PXStylesheetTokenType.NTH_LAST_CHILD_PSEUDO_CLASS,
                PXStylesheetTokenType.NTH_OF_TYPE_PSEUDO_CLASS,
                PXStylesheetTokenType.NTH_LAST_OF_TYPE_PSEUDO_CLASS,
                PXStylesheetTokenType.FIRST_CHILD_PSEUDO_CLASS,
                PXStylesheetTokenType.LAST_CHILD_PSEUDO_CLASS,
                PXStylesheetTokenType.FIRST_OF_TYPE_PSEUDO_CLASS,
                PXStylesheetTokenType.LAST_OF_TYPE_PSEUDO_CLASS,
                PXStylesheetTokenType.ONLY_CHILD_PSEUDO_CLASS,
                PXStylesheetTokenType.ONLY_OF_TYPE_PSEUDO_CLASS,
                PXStylesheetTokenType.EMPTY_PSEUDO_CLASS);

        SELECTOR_OPERATOR_SET = EnumSet.of(
                PXStylesheetTokenType.PLUS,
                PXStylesheetTokenType.GREATER_THAN,
                PXStylesheetTokenType.TILDE);

        SELECTOR_SEQUENCE_SET = EnumSet.of(
                PXStylesheetTokenType.PIPE,
                PXStylesheetTokenType.IDENTIFIER,
                PXStylesheetTokenType.STAR);
        SELECTOR_SEQUENCE_SET.addAll(SELECTOR_EXPRESSION_SET);
        SELECTOR_SEQUENCE_SET.addAll(SELECTOR_OPERATOR_SET);

        SELECTOR_SET = EnumSet.of(PXStylesheetTokenType.PIPE);
        SELECTOR_SET.addAll(TYPE_NAME_SET);
        SELECTOR_SET.addAll(SELECTOR_EXPRESSION_SET);

        ATTRIBUTE_OPERATOR_SET = EnumSet.of(
                PXStylesheetTokenType.STARTS_WITH,
                PXStylesheetTokenType.ENDS_WITH,
                PXStylesheetTokenType.CONTAINS,
                PXStylesheetTokenType.EQUAL,
                PXStylesheetTokenType.LIST_CONTAINS,
                PXStylesheetTokenType.EQUALS_WITH_HYPHEN);

        DECLARATION_DELIMITER_SET = EnumSet.of(
                PXStylesheetTokenType.SEMICOLON,
                PXStylesheetTokenType.RCURLY);

        KEYFRAME_SELECTOR_SET = EnumSet.of(
                PXStylesheetTokenType.IDENTIFIER,
                PXStylesheetTokenType.PERCENTAGE);

        NAMESPACE_SET = EnumSet.of(
                PXStylesheetTokenType.STRING,
                PXStylesheetTokenType.URL);

        IMPORT_SET = EnumSet.of(
                PXStylesheetTokenType.STRING,
                PXStylesheetTokenType.URL);

        QUERY_VALUE_SET = EnumSet.of(
                PXStylesheetTokenType.IDENTIFIER,
                PXStylesheetTokenType.NUMBER,
                PXStylesheetTokenType.LENGTH,
                PXStylesheetTokenType.STRING);

        ARCHAIC_PSEUDO_ELEMENTS_SET = EnumSet.of(
                PXStylesheetTokenType.FIRST_LINE_PSEUDO_ELEMENT,
                PXStylesheetTokenType.FIRST_LETTER_PSEUDO_ELEMENT,
                PXStylesheetTokenType.BEFORE_PSEUDO_ELEMENT,
                PXStylesheetTokenType.AFTER_PSEUDO_ELEMENT);
    }
    //@formatter:on

    private PXStylesheetLexer lexer;
    private PXStylesheet currentStyleSheet;
    private Stack<String> activeImports;
    private Stack<PXStylesheetLexer> lexerStack;

    // Application context
    private Context context;

    /**
     * Constructs a new parser.
     */
    public PXStylesheetParser() {
        this(null);
    }

    /**
     * Constructs a new parser with a {@link Context}.
     * 
     * @param context
     */
    public PXStylesheetParser(Context context) {
        lexer = new PXStylesheetLexer();
        this.context = context;
    }

    /**
     * Sets the {@link Context} that will be used by this parser (e.g. the
     * application context).
     * 
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Returns the {@link Context} that is used by this parser.
     * 
     * @return A {@link Context}
     */
    public Context getContext() {
        return context;
    }

    /**
     * Parse the style-sheet.
     * 
     * @param source
     * @param origin
     * @param fileName
     * @return
     */
    public PXStylesheet parse(String source, PXStyleSheetOrigin origin, String fileName) {
        // add the source file name to prevent @imports from importing it as
        // well
        addImportName(fileName);

        // parse
        PXStylesheet result = parse(source, origin);

        // associate file path on resulting stylesheet
        result.setFilePath(fileName);

        return result;
    }

    /**
     * Parse the style-sheet.
     * 
     * @param source
     * @param origin
     * @return
     */
    public PXStylesheet parse(String source, PXStyleSheetOrigin origin) {
        // clear errors
        clearErrors();

        // create stylesheet
        currentStyleSheet = new PXStylesheet(origin);

        // setup lexer and prime it
        lexer.setSource(source);
        advance();

        try {
            while (currentLexeme != null && currentLexeme.getType() != PXStylesheetTokenType.EOF) {
                switch (currentLexeme.getType()) {
                    case IMPORT:
                        parseImport();
                        break;

                    case NAMESPACE:
                        parseNamespace();
                        break;

                    case KEYFRAMES:
                        parseKeyframes();
                        break;

                    case MEDIA:
                        parseMedia();
                        break;

                    case FONT_FACE:
                        parseFontFace();
                        break;

                    default:
                        // TODO: check for valid tokens to error out sooner?
                        parseRuleSet();
                        break;
                }
            }
        } catch (Exception e) {
            addError(e.getMessage());
        }

        // clear out any import refs
        activeImports = null;

        return currentStyleSheet;
    }

    public PXStylesheet parseInlineCSS(String css) {
        // clear errors
        clearErrors();

        // create stylesheet
        currentStyleSheet = new PXStylesheet(PXStyleSheetOrigin.INLINE);

        // setup lexer and prime it
        lexer.setSource(css);
        advance();

        try {
            // build placeholder rule set
            PXRuleSet ruleSet = new PXRuleSet();

            // parse declarations
            List<PXDeclaration> declarations = parseDeclarations();

            // add declarations to rule set
            for (PXDeclaration declaration : declarations) {
                ruleSet.addDeclaration(declaration);
            }

            // save rule set
            currentStyleSheet.addRuleSet(ruleSet);
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return currentStyleSheet;
    }

    @SuppressWarnings("unused")
    /* Unused, consider deletion. */
    private PXSelector parseSelectorString(String source) {
        // clear errors
        clearErrors();

        // setup lexer and prime it
        lexer.setSource(source);
        advance();

        try {
            return parseSelector();
        } catch (Exception e) {
            addError(e.getMessage());
        }
        return null;
    }

    // level 1

    private void parseFontFace() {
        assertTypeAndAdvance(PXStylesheetTokenType.FONT_FACE);

        // process declaration block
        if (isType(PXStylesheetTokenType.LCURLY)) {
            List<PXDeclaration> declarations = parseDeclarationBlock();

            // TODO: we probably shouldn't load font right here
            for (PXDeclaration declaration : declarations) {
                if ("src".equals(declaration.getName())) {
                    // Load a font and hold it in the fonts registry
                    // Shalom FIXME - We need access to the application's
                    // AssetManager!
                    // PXFontRegistry.getTypeface(declaration.getURLValue());
                }
            }
        }
    }

    private void parseImport() {
        assertTypeAndAdvance(PXStylesheetTokenType.IMPORT);
        assertTypeInSet(IMPORT_SET);

        String path = null;

        switch (currentLexeme.getType()) {
            case STRING: {
                String string = currentLexeme.getValue().toString();

                if (string.length() > 2) {
                    path = string.substring(1, string.length() - 2);
                }

                break;
            }

            case URL:
                path = currentLexeme.getValue().toString();
                break;
            default:
                break;
        }

        if (path != null) {
            // advance over @import argument
            advance();

            // calculate resource name and file extension
            // int dotIndex = path.lastIndexOf(".");
            // String pathMinusExtension = dotIndex > -1 ? path.substring(0,
            // dotIndex) : path;
            // String extension = dotIndex > -1 ? path.substring(dotIndex +
            // 1).toLowerCase()
            // : StringUtil.EMPTY;
            if (context == null) {
                addError("Error parsing an import. The application context is null.");
                advance();
            } else if (!activeImports.contains(path)) {
                // we need to go ahead and process the trailing semicolon so we
                // have the current lexeme in case we push it below
                advance();

                addImportName(path);

                // Note: We always take the import css from the assets.
                String source = null;
                try {
                    source = IOUtil.read(context.getAssets().open(path));
                } catch (IOException e) {
                    PXLog.e(TAG, e, e.getMessage());
                }

                if (!StringUtil.isEmpty(source)) {
                    lexer.pushLexeme((PXStylesheetLexeme) currentLexeme);
                    pushSource(source);
                    advance();
                }
            } else {
                String message = String.format(
                        "import cycle detected trying to import '%s':\n%s ->\n%s", path,
                        CollectionUtil.toString(activeImports, " ->\n"), path);

                addError(message);

                // NOTE: we do this here so we'll still have the current file on
                // the active imports stack. This handles the
                // case of a file ending with an @import statement, causing
                // advance to pop it from the active imports stack
                advance();
            }
        }
    }

    private void parseMedia() {
        assertTypeAndAdvance(PXStylesheetTokenType.MEDIA);

        // TODO: support media types, NOT, and ONLY. Skipping for now
        while (isType(PXStylesheetTokenType.IDENTIFIER)) {
            advance();
        }

        // 'and' may appear here
        advanceIfIsType(PXStylesheetTokenType.AND);

        // parse optional expressions
        if (isType(PXStylesheetTokenType.LPAREN)) {
            parseMediaExpressions();
        }

        // parse body
        if (isType(PXStylesheetTokenType.LCURLY)) {
            try {
                advance();

                while (currentLexeme != null
                        && currentLexeme.getType() != PXStylesheetTokenType.EOF
                        && !isType(PXStylesheetTokenType.RCURLY)) {
                    parseRuleSet();
                }

                advanceIfIsType(PXStylesheetTokenType.RCURLY,
                        "Expected @media body closing curly brace");
            } finally {
                // reset active media query to none
                currentStyleSheet.setActiveMediaQuery(null);
            }
        }
    }

    private void parseRuleSet() {
        List<PXSelector> selectors;

        // parse selectors
        try {
            selectors = parseSelectorGroup();
        } catch (Exception e) {
            // emit error
            addError(e.getMessage());

            // use flag to indicate we have no selectors
            selectors = null;

            // advance to '{'
            advanceToType(PXStylesheetTokenType.LCURLY);
        }

        // here for error recovery
        if (!isType(PXStylesheetTokenType.LCURLY)) {
            addError("Expected a left curly brace to begin a declaration block");

            // advance to '{'
            advanceToType(PXStylesheetTokenType.LCURLY);
        }

        // parse declaration block
        if (isType(PXStylesheetTokenType.LCURLY)) {
            List<PXDeclaration> declarations = parseDeclarationBlock();

            if (selectors == null) {
                PXRuleSet ruleSet = new PXRuleSet();

                for (PXDeclaration declaration : declarations) {
                    ruleSet.addDeclaration(declaration);
                }

                // save rule set
                currentStyleSheet.addRuleSet(ruleSet);
            } else {
                for (PXSelector selector : selectors) {
                    // build rule set
                    PXRuleSet ruleSet = new PXRuleSet();

                    // add selector
                    if (selector != null) {
                        ruleSet.addSelector(selector);
                    }

                    for (PXDeclaration declaration : declarations) {
                        ruleSet.addDeclaration(declaration);
                    }

                    // save rule set
                    currentStyleSheet.addRuleSet(ruleSet);
                }
            }
        }
    }

    private void parseKeyframes() {
        // advance over '@keyframes'
        assertTypeAndAdvance(PXStylesheetTokenType.KEYFRAMES);

        // grab keyframe name
        assertType(PXStylesheetTokenType.IDENTIFIER);
        PXKeyframe keyframe = new PXKeyframe(currentLexeme.getValue().toString());
        advance();

        // advance over '{'
        assertTypeAndAdvance(PXStylesheetTokenType.LCURLY);

        // process each block
        while (isInTypeSet(KEYFRAME_SELECTOR_SET)) {
            // grab all offsets
            List<Number> offsets = new ArrayList<Number>();

            offsets.add(parseOffset());

            while (isType(PXStylesheetTokenType.COMMA)) {
                // advance over ','
                advance();

                offsets.add(parseOffset());
            }

            // grab declarations
            List<PXDeclaration> declarations = parseDeclarationBlock();

            // create blocks, one for each offset, using the same declarations
            // for each
            for (Number number : offsets) {
                float offset = number.floatValue();

                // create keyframe block
                PXKeyframeBlock block = new PXKeyframeBlock(offset);

                // add declarations to it
                for (PXDeclaration declaration : declarations) {
                    block.addDeclaration(declaration);
                }

                keyframe.addKeyframeBlock(block);
            }
        }

        // add keyframe to current stylesheet
        currentStyleSheet.addKeyframe(keyframe);

        // advance over '}'
        assertTypeAndAdvance(PXStylesheetTokenType.RCURLY);
    }

    private float parseOffset() {
        float offset = 0.0f;

        assertTypeInSet(KEYFRAME_SELECTOR_SET);

        switch (currentLexeme.getType()) {
            case IDENTIFIER:
                // NOTE: we only check for 'to' since 'from' and unrecognized
                // values will use the default value of 0.0f
                if ("to".equals(currentLexeme.getValue())) {
                    offset = 1.0f;
                }
                advance();
                break;

            case PERCENTAGE: {
                PXDimension percentage = (PXDimension) currentLexeme.getValue();
                offset = percentage.getNumber() / 100.0f;
                offset = Math.min(1.0f, offset);
                offset = Math.max(0.0f, offset);
                advance();
                break;
            }

            default: {
                String message = String.format("Unrecognized keyframe selector type: %s",
                        currentLexeme);
                errorWithMessage(message);
                break;
            }
        }

        return offset;
    }

    private void parseNamespace() {
        assertTypeAndAdvance(PXStylesheetTokenType.NAMESPACE);

        String identifier = null;
        String uri;

        if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            identifier = currentLexeme.getValue().toString();
            advance();
        }

        assertTypeInSet(NAMESPACE_SET);

        // grab value
        uri = currentLexeme.getValue().toString();

        // trim string
        if (isType(PXStylesheetTokenType.STRING)) {
            // this will remove the URI double quotes.
            uri = uri.substring(1, uri.length() - 1);
        }

        advance();

        // set namespace on stylesheet (identifier is the namespace prefix)
        currentStyleSheet.setURI(uri, identifier);

        assertTypeAndAdvance(PXStylesheetTokenType.SEMICOLON);
    }

    // level 2

    private List<PXSelector> parseSelectorGroup() {
        List<PXSelector> selectors = new ArrayList<PXSelector>();

        PXSelector selectorSequence = parseSelectorSequence();

        if (selectorSequence != null) {
            selectors.add(selectorSequence);
        }

        while (currentLexeme.getType() == PXStylesheetTokenType.COMMA) {
            // advance over ','
            advance();

            // grab next selector
            PXSelector nextSelector = parseSelectorSequence();
            if (nextSelector == null) {
                // We have a problem with this selectors group
                errorWithMessage("Expected a Selector or Pseudo-element after a comma");
            } else {
                selectors.add(nextSelector);
            }
        }

        if (selectors.size() == 0) {
            errorWithMessage("Expected a Selector or Pseudo-element");
        }

        return selectors;
    }

    private List<PXDeclaration> parseDeclarationBlock() {
        assertTypeAndAdvance(PXStylesheetTokenType.LCURLY);

        List<PXDeclaration> declarations = parseDeclarations();

        assertTypeAndAdvance(PXStylesheetTokenType.RCURLY);

        return declarations;
    }

    private void parseMediaExpressions() {
        try {
            // create container for zero-or-more expressions
            List<PXMediaExpression> expressions = new ArrayList<PXMediaExpression>();

            // add at least one expression
            expressions.add(parseMediaExpression());

            // and any others
            while (isType(PXStylesheetTokenType.AND)) {
                advance();

                expressions.add(parseMediaExpression());
            }

            // create expression group or use single entry
            if (expressions.size() == 1) {
                currentStyleSheet.setActiveMediaQuery(expressions.get(0));
            } else {
                PXMediaExpressionGroup group = new PXMediaExpressionGroup();

                for (PXMediaExpression expression : expressions) {
                    group.addExpression(expression);
                }

                currentStyleSheet.setActiveMediaQuery(group);
            }
        } catch (Exception e) {
            addError(e.getMessage());
            // TODO: error recovery
        }
    }

    // level 3

    private PXSelector parseSelectorSequence() {
        PXSelector root = parseSelector();

        while (isInTypeSet(SELECTOR_SEQUENCE_SET)) {
            Lexeme<PXStylesheetTokenType> operator = null;

            if (isInTypeSet(SELECTOR_OPERATOR_SET)) {
                operator = currentLexeme;
                advance();
            }

            PXSelector rhs = parseSelector();

            if (operator != null) {
                switch (operator.getType()) {
                    case PLUS:
                        root = new PXAdjacentSiblingCombinator(root, rhs);
                        break;

                    case GREATER_THAN:
                        root = new PXChildCombinator(root, rhs);
                        break;

                    case TILDE:
                        root = new PXSiblingCombinator(root, rhs);
                        break;

                    default:
                        errorWithMessage("Unsupported selector operator (combinator)");
                }
            } else {
                root = new PXDescendantCombinator(root, rhs);
                // advance();
            }
        }

        String pseudoElement = null;

        // grab possible pseudo-element in new and old formats
        if (isType(PXStylesheetTokenType.DOUBLE_COLON)) {
            advance();

            assertType(PXStylesheetTokenType.IDENTIFIER);
            pseudoElement = currentLexeme.getValue().toString();
            advance();
        } else if (isInTypeSet(ARCHAIC_PSEUDO_ELEMENTS_SET)) {
            String stringValue = currentLexeme.getValue().toString();

            pseudoElement = stringValue.substring(1);

            advance();
        }

        if (pseudoElement != null && pseudoElement.length() > 0) {
            if (root == null) {
                PXTypeSelector selector = new PXTypeSelector();

                selector.setPseudoElement(pseudoElement);

                root = selector;
            } else {
                if (root instanceof PXTypeSelector) {
                    PXTypeSelector selector = (PXTypeSelector) root;

                    selector.setPseudoElement(pseudoElement);
                } else if (root instanceof PXCombinatorBase) {
                    PXCombinatorBase combinator = (PXCombinatorBase) root;
                    PXTypeSelector selector = (PXTypeSelector) combinator.getRhs();

                    selector.setPseudoElement(pseudoElement);
                }
            }
        }

        return root;
    }

    private List<PXDeclaration> parseDeclarations() {
        List<PXDeclaration> declarations = new ArrayList<PXDeclaration>();

        // parse properties
        while (currentLexeme != null && currentLexeme.getType() != PXStylesheetTokenType.EOF
                && currentLexeme.getType() != PXStylesheetTokenType.RCURLY) {
            try {
                PXDeclaration declaration = parseDeclaration();
                declarations.add(declaration);
            } catch (Exception e) {
                addError(e.getMessage());

                // TODO: parseDeclaration could do error recovery. If not, this
                // should probably do the same recovery
                while (currentLexeme != null
                        && currentLexeme.getType() != PXStylesheetTokenType.EOF
                        && !isInTypeSet(DECLARATION_DELIMITER_SET)) {
                    advance();
                }

                advanceIfIsType(PXStylesheetTokenType.SEMICOLON);
            }
        }

        return declarations;
    }

    private PXMediaExpression parseMediaExpression() {
        assertTypeAndAdvance(PXStylesheetTokenType.LPAREN);

        // grab name
        assertType(PXStylesheetTokenType.IDENTIFIER);
        String name = currentLexeme.getValue().toString().toLowerCase(Locale.US);
        advance();

        Object value = null;

        // parse optional value
        if (isType(PXStylesheetTokenType.COLON)) {
            // advance over ':'
            assertTypeAndAdvance(PXStylesheetTokenType.COLON);

            // grab value
            assertTypeInSet(QUERY_VALUE_SET);
            value = currentLexeme.getValue();
            boolean isNumber = currentLexeme.getType() == PXStylesheetTokenType.NUMBER;
            advance();

            // make string values lowercase to avoid doing it later
            if (!isNumber && value instanceof String) {
                value = ((String) value).toLowerCase(Locale.US);
            }
            // check for possible ratio syntax
            else if (isNumber && isType(PXStylesheetTokenType.SLASH)) {

                Float numerator = getFloatValue(value);

                // advance over '/'
                advance();

                // grab denominator
                assertType(PXStylesheetTokenType.NUMBER);
                Float denom = getFloatValue(currentLexeme.getValue());
                advance();

                if (numerator.floatValue() == 0.0f) {
                    // do nothing, leave result as 0.0
                } else if (denom.floatValue() == 0.0f) {
                    value = Double.NaN;
                } else {
                    value = numerator.floatValue() / denom.floatValue();
                }
            }
        }

        advanceIfIsType(PXStylesheetTokenType.RPAREN, "Expected closing parenthesis in media query");

        // create query expression and activate it in current stylesheet
        return new PXNamedMediaExpression(name, value);
    }

    /**
     * Tries to convert the given value into a {@link Float}. In case the value
     * is already instance of Number, the method simply cast it to one.
     * 
     * @param value
     * @return A {@link Float} instance; <code>null</code> in case the value
     *         cannot be converted.
     */
    private Float getFloatValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // level 4

    private PXSelector parseSelector() {
        PXTypeSelector result = null;

        if (isInTypeSet(SELECTOR_SET)) {
            if (isInTypeSet(TYPE_SELECTOR_SET)) {
                result = parseTypeSelector();
            } else {
                // match any element
                result = new PXTypeSelector();

                // clear whitespace flag, so first expression will not fail in
                // this case
                currentLexeme.clearFlag(Lexeme.FLAG_TYPE_FOLLOWS_WHITESPACE);
            }

            if (isInTypeSet(SELECTOR_EXPRESSION_SET)) {
                for (PXSelector expression : parseSelectorExpressions()) {
                    result.addAttributeExpression(expression);
                }
            }
        }
        // else, fail silently in case a pseudo-element follows

        return result;
    }

    private PXDeclaration parseDeclaration() {
        // process property name
        assertType(PXStylesheetTokenType.IDENTIFIER);
        PXDeclaration declaration = new PXDeclaration(currentLexeme.getValue().toString());
        advance();

        // colon
        assertTypeAndAdvance(PXStylesheetTokenType.COLON);

        // collect values
        Stack<PXStylesheetLexeme> lexemes = new Stack<PXStylesheetLexeme>();

        while (currentLexeme != null && currentLexeme.getType() != PXStylesheetTokenType.EOF
                && !isInTypeSet(DECLARATION_DELIMITER_SET)) {
            if (!lexemes.isEmpty() && currentLexeme.getType() == PXStylesheetTokenType.COLON
                    && lexemes.lastElement().getType() == PXStylesheetTokenType.IDENTIFIER) {
                // assume we've moved into a new declaration, so push last
                // lexeme back into the lexeme stream
                Lexeme<PXStylesheetTokenType> propertyName = lexemes.pop();

                // this pushes the colon back to the lexer and makes the
                // property name the current lexeme
                pushLexeme(propertyName);

                // signal end of this declaration
                break;
            } else {
                lexemes.add((PXStylesheetLexeme) currentLexeme);
                advance();
            }
        }

        // let semicolons be optional
        advanceIfIsType(PXStylesheetTokenType.SEMICOLON);

        // grab original source, for error messages and hashing
        String source;

        if (lexemes.size() > 0) {
            Lexeme<PXStylesheetTokenType> firstLexeme = lexemes.firstElement();
            Lexeme<PXStylesheetTokenType> lastLexeme = lexemes.firstElement();
            int start = firstLexeme.getOffset();
            int end = lastLexeme.getEndingOffset();
            source = lexer.getSource().substring(start, end);
        } else {
            source = StringUtil.EMPTY;
        }

        // check for !important
        Lexeme<PXStylesheetTokenType> lastLexeme = lexemes.isEmpty() ? null : lexemes.lastElement();

        if (lastLexeme != null && lastLexeme.getType() == PXStylesheetTokenType.IMPORTANT) {
            // drop !important and tag declaration as important
            lexemes.pop();
            declaration.setImportant(true);
        }

        // associate lexemes with declaration
        declaration.setSource(source, getCurrentFilename(), new ArrayList<PXStylesheetLexeme>(
                lexemes));
        return declaration;
    }

    // level 5

    private PXTypeSelector parseTypeSelector() {
        PXTypeSelector result = null;

        if (isInTypeSet(TYPE_SELECTOR_SET)) {
            String namespace = null;
            String name = null;

            // namespace or type
            if (isInTypeSet(TYPE_NAME_SET)) {
                // assume we have a name only
                name = currentLexeme.getValue().toString();
                advance();
            }

            // if pipe, then we had a namespace, now process type
            if (isType(PXStylesheetTokenType.PIPE)) {
                namespace = name;

                // advance over '|'
                advance();

                if (isInTypeSet(TYPE_NAME_SET)) {
                    // set name
                    name = currentLexeme.getValue().toString();
                    advance();
                } else {
                    errorWithMessage("Expected IDENTIFIER or STAR");
                }
            } else {
                namespace = "*";
            }

            // find namespace URI from namespace prefix

            String namespaceURI = null;

            if (namespace != null) {
                if ("*".equals(namespace)) {
                    namespaceURI = namespace;
                } else {
                    namespaceURI = currentStyleSheet.getNamespaceForPrefix(namespace);
                }
            }

            result = new PXTypeSelector(namespaceURI, name);
        } else {
            errorWithMessage("Expected IDENTIFIER, STAR, or PIPE");
        }

        return result;
    }

    private List<PXSelector> parseSelectorExpressions() {
        List<PXSelector> expressions = new ArrayList<PXSelector>();

        while (!currentLexeme.isFlagSet(Lexeme.FLAG_TYPE_FOLLOWS_WHITESPACE)
                && isInTypeSet(SELECTOR_EXPRESSION_SET)) {
            switch (currentLexeme.getType()) {
                case ID: {
                    String name = currentLexeme.getValue().toString().substring(1);
                    expressions.add(new PXIdSelector(name));
                    advance();
                    break;
                }

                case CLASS: {
                    String name = currentLexeme.getValue().toString().substring(1);
                    expressions.add(new PXClassSelector(name));
                    advance();
                    break;
                }

                case LBRACKET:
                    expressions.add(parseAttributeSelector());
                    break;

                case COLON:
                    expressions.add(parsePseudoClass());
                    break;

                case NOT_PSEUDO_CLASS:
                    expressions.add(parseNotSelector());
                    break;

                case ROOT_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_ROOT));
                    advance();
                    break;

                case FIRST_CHILD_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_FIRST_CHILD));
                    advance();
                    break;

                case LAST_CHILD_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_LAST_CHILD));
                    advance();
                    break;

                case FIRST_OF_TYPE_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_FIRST_OF_TYPE));
                    advance();
                    break;

                case LAST_OF_TYPE_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_LAST_OF_TYPE));
                    advance();
                    break;

                case ONLY_CHILD_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_ONLY_CHILD));
                    advance();
                    break;

                case ONLY_OF_TYPE_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_ONLY_OF_TYPE));
                    advance();
                    break;

                case EMPTY_PSEUDO_CLASS:
                    expressions.add(new PXPseudoClassPredicate(
                            PXPseudoClassPredicateType.PREDICATE_EMPTY));
                    advance();
                    break;

                case NTH_CHILD_PSEUDO_CLASS:
                case NTH_LAST_CHILD_PSEUDO_CLASS:
                case NTH_OF_TYPE_PSEUDO_CLASS:
                case NTH_LAST_OF_TYPE_PSEUDO_CLASS:
                    expressions.add(parsePseudoClassFunction());
                    assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                    break;

                // TODO: implement
                case LINK_PSEUDO_CLASS:
                case VISITED_PSEUDO_CLASS:
                case HOVER_PSEUDO_CLASS:
                case ACTIVE_PSEUDO_CLASS:
                case FOCUS_PSEUDO_CLASS:
                case TARGET_PSEUDO_CLASS:
                case ENABLED_PSEUDO_CLASS:
                case CHECKED_PSEUDO_CLASS:
                case INDETERMINATE_PSEUDO_CLASS:
                    String className = currentLexeme.getValue().toString();
                    if (className.startsWith(":")) {
                        className = className.substring(1);
                    }
                    expressions.add(new PXPseudoClassSelector(className));
                    advance();
                    break;

                // TODO: implement
                case LANG_PSEUDO_CLASS:
                    className = currentLexeme.getValue().toString();
                    if (className.startsWith(":")) {
                        className = className.substring(1);
                    }
                    expressions.add(new PXPseudoClassSelector(className));
                    advanceToType(PXStylesheetTokenType.RPAREN);
                    advance();
                    break;

                default:
                    break;
            }
        }

        if (expressions.size() == 0
                && !currentLexeme.isFlagSet(Lexeme.FLAG_TYPE_FOLLOWS_WHITESPACE)) {
            errorWithMessage("Expected ID, CLASS, LBRACKET, or PseudoClass");
        }

        return expressions;
    }

    // level 6

    private PXPseudoClassFunction parsePseudoClassFunction() {
        // initialize to something to remove analyzer warnings, but the switch
        // below has to cover all cases to prevent a
        // bug here
        PXPseudoClassFunctionType type = PXPseudoClassFunctionType.NTH_CHILD;

        switch (currentLexeme.getType()) {
            case NTH_CHILD_PSEUDO_CLASS:
                type = PXPseudoClassFunctionType.NTH_CHILD;
                break;

            case NTH_LAST_CHILD_PSEUDO_CLASS:
                type = PXPseudoClassFunctionType.NTH_LAST_CHILD;
                break;

            case NTH_OF_TYPE_PSEUDO_CLASS:
                type = PXPseudoClassFunctionType.NTH_OF_TYPE;
                break;

            case NTH_LAST_OF_TYPE_PSEUDO_CLASS:
                type = PXPseudoClassFunctionType.NTH_LAST_OF_TYPE;
                break;
            default:
                break;
        }

        // advance over function name and left paren
        advance();

        int modulus = 0;
        int remainder = 0;

        // parse modulus
        if (isType(PXStylesheetTokenType.NTH)) {
            String numberString = currentLexeme.getValue().toString();
            int length = numberString.length();

            // extract modulus
            if (length == 1) {
                // we have 'n'
                modulus = 1;
            } else if (length == 2 && numberString.startsWith("-")) {
                // we have '-n'
                modulus = -1;
            } else if (length == 2 && numberString.startsWith("+")) {
                // we have '+n'
                modulus = 1;
            } else {
                // a number precedes 'n'
                modulus = Integer.parseInt(numberString.substring(0, numberString.length() - 1));
            }

            advance();

            if (isType(PXStylesheetTokenType.PLUS)) {
                advance();

                // grab remainder
                assertType(PXStylesheetTokenType.NUMBER);
                Number remainderNumber = getFloatValue(currentLexeme.getValue());
                remainder = remainderNumber.intValue();
                advance();
            } else if (isType(PXStylesheetTokenType.NUMBER)) {
                numberString = lexer.getSource().substring(currentLexeme.getOffset(),
                        currentLexeme.getEndingOffset());

                if (numberString.startsWith("-") || numberString.startsWith("+")) {
                    Number remainderNumber = getFloatValue(currentLexeme.getValue());
                    remainder = remainderNumber.intValue();
                    advance();
                } else {
                    errorWithMessage("Expected NUMBER with leading '-' or '+'");
                }
            }
        } else if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            String stringValue = currentLexeme.getValue().toString();

            if ("odd".equals(stringValue)) {
                modulus = 2;
                remainder = 1;
            } else if ("even".equals(stringValue)) {
                modulus = 2;
            } else {
                errorWithMessage(String.format(
                        "Unrecognized identifier '%s'. Expected 'odd' or 'even'", stringValue));
            }

            advance();
        } else if (isType(PXStylesheetTokenType.NUMBER)) {
            modulus = 1;
            Number remainderNumber = getFloatValue(currentLexeme.getValue());
            remainder = remainderNumber.intValue();

            advance();
        } else {
            errorWithMessage("Expected NTH, NUMBER, 'odd', or 'even'");
        }

        return new PXPseudoClassFunction(type, modulus, remainder);
    }

    private PXSelector parseAttributeSelector() {
        PXSelector result = null;

        assertTypeAndAdvance(PXStylesheetTokenType.LBRACKET);

        result = parseAttributeTypeSelector();

        if (isInTypeSet(ATTRIBUTE_OPERATOR_SET)) {
            PXAttributeSelectorOperatorType operatorType = PXAttributeSelectorOperatorType.EQUAL; // make
                                                                                                  // anaylzer
                                                                                                  // happy

            switch (currentLexeme.getType()) {
                case STARTS_WITH:
                    operatorType = PXAttributeSelectorOperatorType.STARTS_WITH;
                    break;
                case ENDS_WITH:
                    operatorType = PXAttributeSelectorOperatorType.ENDS_WITH;
                    break;
                case CONTAINS:
                    operatorType = PXAttributeSelectorOperatorType.CONTAINS;
                    break;
                case EQUAL:
                    operatorType = PXAttributeSelectorOperatorType.EQUAL;
                    break;
                case LIST_CONTAINS:
                    operatorType = PXAttributeSelectorOperatorType.LIST_CONTAINS;
                    break;
                case EQUALS_WITH_HYPHEN:
                    operatorType = PXAttributeSelectorOperatorType.EQUAL_WITH_HYPHEN;
                    break;

                default:
                    errorWithMessage("Unsupported attribute operator type");
                    break;
            }

            advance();

            if (isType(PXStylesheetTokenType.STRING)) {
                String value = currentLexeme.getValue().toString();

                // process string
                result = new PXAttributeSelectorOperator(operatorType,
                        (PXAttributeSelector) result, value.substring(1, value.length() - 1));

                advance();
            } else if (isType(PXStylesheetTokenType.IDENTIFIER)) {
                // process string
                result = new PXAttributeSelectorOperator(operatorType,
                        (PXAttributeSelector) result, currentLexeme.getValue().toString());

                advance();
            } else {
                errorWithMessage("Expected STRING or IDENTIFIER");
            }
        }

        assertTypeAndAdvance(PXStylesheetTokenType.RBRACKET);

        return result;
    }

    private PXSelector parsePseudoClass() {
        PXSelector result = null;

        assertType(PXStylesheetTokenType.COLON);
        advance();

        if (isType(PXStylesheetTokenType.IDENTIFIER)) {
            // process identifier
            result = new PXPseudoClassSelector(currentLexeme.getValue().toString());
            advance();
        } else {
            errorWithMessage("Expected IDENTIFIER");
        }

        // TODO: support an+b notation

        return result;
    }

    private PXSelector parseNotSelector() {
        // advance over 'not'
        assertType(PXStylesheetTokenType.NOT_PSEUDO_CLASS);
        advance();

        PXSelector result = new PXNotPseudoClass(parseNegationArgument());

        // advance over ')'
        assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);

        return result;
    }

    // level 7

    private PXAttributeSelector parseAttributeTypeSelector() {
        PXAttributeSelector result = null;

        if (isInTypeSet(TYPE_SELECTOR_SET)) {
            String namespace = null;
            String name = null;

            // namespace or type
            if (isInTypeSet(TYPE_NAME_SET)) {
                // assume we have a name only
                name = currentLexeme.getValue().toString();
                advance();
            }

            // if pipe, then we had a namespace, now process type
            if (isType(PXStylesheetTokenType.PIPE)) {
                namespace = name;

                // advance over '|'
                advance();

                if (isInTypeSet(TYPE_NAME_SET)) {
                    // set name
                    name = currentLexeme.getValue().toString();
                    advance();
                } else {
                    errorWithMessage("Expected IDENTIFIER or STAR");
                }
            }
            // NOTE: default namepace is null indicating no namespace should
            // exist when matching with this selector. This
            // differs from the interpretation used on type selectors

            // find namespace URI from namespace prefix

            String namespaceURI = null;

            if (namespace != null) {
                if (namespace.equals("*")) {
                    namespaceURI = namespace;
                } else {
                    namespaceURI = currentStyleSheet.getNamespaceForPrefix(namespace);
                }
            }

            result = new PXAttributeSelector(namespaceURI, name);
        } else {
            errorWithMessage("Expected IDENTIFIER, STAR, or PIPE");
        }

        return result;
    }

    private PXSelector parseNegationArgument() {
        PXSelector result = null;

        switch (currentLexeme.getType()) {
            case ID: {
                String name = currentLexeme.getValue().toString().substring(1);
                result = new PXIdSelector(name);
                advance();
                break;
            }

            case CLASS: {
                String name = currentLexeme.getValue().toString().substring(1);
                result = new PXClassSelector(name);
                advance();
                break;
            }

            case LBRACKET:
                result = parseAttributeSelector();
                break;

            case COLON:
                result = parsePseudoClass();
                break;

            case ROOT_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(PXPseudoClassPredicateType.PREDICATE_ROOT);
                advance();
                break;

            case FIRST_CHILD_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(
                        PXPseudoClassPredicateType.PREDICATE_FIRST_CHILD);
                advance();
                break;

            case LAST_CHILD_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(PXPseudoClassPredicateType.PREDICATE_LAST_CHILD);
                advance();
                break;

            case FIRST_OF_TYPE_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(
                        PXPseudoClassPredicateType.PREDICATE_FIRST_OF_TYPE);
                advance();
                break;

            case LAST_OF_TYPE_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(
                        PXPseudoClassPredicateType.PREDICATE_LAST_OF_TYPE);
                advance();
                break;

            case ONLY_CHILD_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(PXPseudoClassPredicateType.PREDICATE_ONLY_CHILD);
                advance();
                break;

            case ONLY_OF_TYPE_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(
                        PXPseudoClassPredicateType.PREDICATE_ONLY_OF_TYPE);
                advance();
                break;

            case EMPTY_PSEUDO_CLASS:
                result = new PXPseudoClassPredicate(PXPseudoClassPredicateType.PREDICATE_EMPTY);
                advance();
                break;

            case NTH_CHILD_PSEUDO_CLASS:
            case NTH_LAST_CHILD_PSEUDO_CLASS:
            case NTH_OF_TYPE_PSEUDO_CLASS:
            case NTH_LAST_OF_TYPE_PSEUDO_CLASS:
                result = parsePseudoClassFunction();
                assertTypeAndAdvance(PXStylesheetTokenType.RPAREN);
                break;

            // TODO: implement
            case LINK_PSEUDO_CLASS:
            case VISITED_PSEUDO_CLASS:
            case HOVER_PSEUDO_CLASS:
            case ACTIVE_PSEUDO_CLASS:
            case FOCUS_PSEUDO_CLASS:
            case TARGET_PSEUDO_CLASS:
            case ENABLED_PSEUDO_CLASS:
            case CHECKED_PSEUDO_CLASS:
            case INDETERMINATE_PSEUDO_CLASS:
                result = new PXPseudoClassSelector(currentLexeme.getValue().toString());
                advance();
                break;

            // TODO: implement
            case LANG_PSEUDO_CLASS:
                result = new PXPseudoClassSelector(currentLexeme.getValue().toString());
                advanceToType(PXStylesheetTokenType.RPAREN);
                advance();
                break;

            case RPAREN:
                // empty body
                break;

            default:
                if (isInTypeSet(TYPE_SELECTOR_SET)) {
                    result = parseTypeSelector();
                } else {
                    errorWithMessage("Expected ID, CLASS, AttributeSelector, PseudoClass, or TypeSelect as negation argument");
                }
                break;
        }

        return result;
    }

    private void lexerDidPopSource() {
        if (activeImports.size() > 0) {
            activeImports.pop();
        } else {
            PXLog.e(TAG, "Tried to pop an empty activeImports array");
        }
    }

    /*
     * Overrides the super implementation. (non-Javadoc)
     * @see com.pixate.freestyle.parsing.PXParserBase#advance()
     */
    @Override
    public Lexeme<PXStylesheetTokenType> advance() {
        Lexeme<PXStylesheetTokenType> candidate = lexer.nextLexeme();

        while (candidate == null && lexerStack != null && !lexerStack.isEmpty()) {
            // pop lexer
            lexer = lexerStack.pop();

            // notify the parser that we've done so
            lexerDidPopSource();

            // try getting the next lexeme from the newly activated lexer
            candidate = lexer.nextLexeme();
        }

        return currentLexeme = candidate;
    }

    // Helpers

    private void addImportName(String name) {
        if (!StringUtil.isEmpty(name)) {
            if (activeImports == null) {
                activeImports = new Stack<String>();
            }
            activeImports.push(name);
        }
    }

    private void advanceToType(PXStylesheetTokenType type) {
        while (currentLexeme != null && currentLexeme.getType() != type
                && currentLexeme.getType() != PXStylesheetTokenType.EOF) {
            advance();
        }
    }

    private void pushLexeme(Lexeme<PXStylesheetTokenType> lexeme) {
        lexer.pushLexeme((PXStylesheetLexeme) currentLexeme);

        currentLexeme = lexeme;
    }

    private void pushSource(String source) {
        if (lexerStack == null) {
            lexerStack = new Stack<PXStylesheetLexer>();
        }

        // push current lexer
        lexerStack.push(lexer);

        // create new lexer and activate it
        lexer = new PXStylesheetLexer();
        lexer.setSource(source);
    }

    private String getCurrentFilename() {
        return (activeImports != null && activeImports.size() > 0) ? (new File(
                activeImports.lastElement())).getName() : null;
    }

    @Override
    public void addError(String error, String filename, String offset) {
        offset = (currentLexeme.getType() != PXStylesheetTokenType.EOF) ? String
                .valueOf(currentLexeme.getOffset()) : "EOF";
        super.addError(error, filename, offset);
    }
}
