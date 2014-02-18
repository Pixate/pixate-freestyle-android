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
/**
 *
 */
package com.pixate.pxengine.styling.parsing;

import android.test.AndroidTestCase;

import com.pixate.pxengine.cg.math.PXDimension;
import com.pixate.pxengine.cg.math.PXDimensionType;

/**
 * @author kevin
 */
public class StylesheetLexerTests extends AndroidTestCase {
    private PXStylesheetLexer _lexer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this._lexer = new PXStylesheetLexer();
    }

    protected void assertDimension(String source, PXStylesheetTokenType tokenType, PXDimensionType dimensionType,
            float expectedValue) {
        PXStylesheetLexeme lexeme = getLexeme(source);

        assertTokenType(lexeme, tokenType, source.length());

        Object value = lexeme.getValue();
        assertNotNull("Expected a non-nil lexeme value", value);

        assertTrue("Expected an instance of a Float", value instanceof PXDimension);
        PXDimension dimensionValue = (PXDimension) value;

        assertEquals("Unexpected dimension type", dimensionType, dimensionValue.getType());
        assertTrue("Unexpected value", dimensionValue.getNumber() == expectedValue);
    }

    protected void assertType(String source, PXStylesheetTokenType type) {
        this.assertType(source, type, source.length());
    }

    protected void assertType(String source, PXStylesheetTokenType type, int endOffset) {
        PXStylesheetLexeme lexeme = getLexeme(source);

        assertTokenType(lexeme, type, endOffset);
    }

    protected void assertTokenType(PXStylesheetLexeme lexeme, PXStylesheetTokenType type, int endOffset) {
        assertEquals("Unexpected token type", type, lexeme.getType());
        assertTrue("Expected token to start at offset 0, but started at " + lexeme.getOffset(), lexeme.getOffset() == 0);
        assertTrue("Expected token to end at " + endOffset + ", but it ended at " + lexeme.getEndingOffset(),
                lexeme.getEndingOffset() == endOffset);
    }

    protected PXStylesheetLexeme getLexeme(String source) {
        this._lexer.setSource(source);

        return this._lexer.nextLexeme();
    }

    public void testInteger() {
        assertType("123", PXStylesheetTokenType.NUMBER);
    }

    public void testFloat() {
        assertType("123.456", PXStylesheetTokenType.NUMBER);
    }

    public void testClass() {
        assertType(".class", PXStylesheetTokenType.CLASS);
    }

    public void testClassWithEscapeSequence() {
        assertType(".one\\ two", PXStylesheetTokenType.CLASS);
    }

    public void testId() {
        assertType("#id", PXStylesheetTokenType.ID);
    }

    public void testIdWithEscapeSequence() {
        String source = "#one\\ two";
        assertType(source, PXStylesheetTokenType.ID, source.length());
    }

    public void testIdentifier() {
        assertType("identifier0-with-dashes-and-numbers", PXStylesheetTokenType.IDENTIFIER);
    }

    public void testIdentifierWithEscapeSequence() {
        assertType("one\\ two", PXStylesheetTokenType.IDENTIFIER);
    }

    public void testIdentifierWithUnderscore() {
        assertType("under_score", PXStylesheetTokenType.IDENTIFIER);
    }

    public void testLCurly() {
        assertType("{", PXStylesheetTokenType.LCURLY);
    }

    public void testRCurly() {
        assertType("}", PXStylesheetTokenType.RCURLY);
    }

    public void testLParen() {
        assertType("(", PXStylesheetTokenType.LPAREN);
    }

    public void testRParen() {
        assertType(")", PXStylesheetTokenType.RPAREN);
    }

    public void testLBracket() {
        assertType("[", PXStylesheetTokenType.LBRACKET);
    }

    public void testRBracket() {
        assertType("]", PXStylesheetTokenType.RBRACKET);
    }

    public void testSemicolon() {
        assertType(";", PXStylesheetTokenType.SEMICOLON);
    }

    public void testGreaterThan() {
        assertType(">", PXStylesheetTokenType.GREATER_THAN);
    }

    public void testPlus() {
        assertType("+", PXStylesheetTokenType.PLUS);
    }

    public void testTilde() {
        assertType("~", PXStylesheetTokenType.TILDE);
    }

    public void testStar() {
        assertType("*", PXStylesheetTokenType.STAR);
    }

    public void testEqual() {
        assertType("=", PXStylesheetTokenType.EQUAL);
    }

    public void testColon() {
        assertType(":", PXStylesheetTokenType.COLON);
    }

    public void testComma() {
        assertType(",", PXStylesheetTokenType.COMMA);
    }

    public void testPipe() {
        assertType("|", PXStylesheetTokenType.PIPE);
    }

    public void testDoubleColon() {
        assertType("::", PXStylesheetTokenType.DOUBLE_COLON);
    }

    public void testStartsWith() {
        assertType("^=", PXStylesheetTokenType.STARTS_WITH);
    }

    public void testEndsWith() {
        assertType("$=", PXStylesheetTokenType.ENDS_WITH);
    }

    public void testContains() {
        assertType("*=", PXStylesheetTokenType.CONTAINS);
    }

    public void testListContains() {
        assertType("~=", PXStylesheetTokenType.LIST_CONTAINS);
    }

    public void testHyphenListContains() {
        assertType("|=", PXStylesheetTokenType.EQUALS_WITH_HYPHEN);
    }

    public void testDoubleQuotedString() {
        assertType("\"abc\"", PXStylesheetTokenType.STRING);
    }

    public void testDoubleQuotedStringWithEscapes() {
        String source = "\"This is a test with a tab \\t and a double-quote \\\"\"";

        assertType(source, PXStylesheetTokenType.STRING, source.length() - 1);
    }

    public void testSingleQuotedString() {
        assertType("'abc'", PXStylesheetTokenType.STRING);
    }

    public void testSingleQuotedStringWithEscapes() {
        String source = "'This is a test with a tab \\t and a single-quote \\\''";
        assertType(source, PXStylesheetTokenType.STRING, source.length() - 1);
    }

    public void testNot() {
        assertType(":not(", PXStylesheetTokenType.NOT_PSEUDO_CLASS);
    }

    public void testLinearGradient() {
        assertType("linear-gradient(", PXStylesheetTokenType.LINEAR_GRADIENT);
    }

    public void testRadialGradient() {
        assertType("radial-gradient(", PXStylesheetTokenType.RADIAL_GRADIENT);
    }

    public void testHSB() {
        assertType("hsb(", PXStylesheetTokenType.HSB);
    }

    public void testHSBA() {
        assertType("hsba(", PXStylesheetTokenType.HSBA);
    }

    public void testRGB() {
        assertType("rgb(", PXStylesheetTokenType.RGB);
    }

    public void testRGBA() {
        assertType("rgba(", PXStylesheetTokenType.RGBA);
    }

    public void test3DigitHexColor() {
        // TODO:
        // assertType("", PXStylesheetTokenType.);
    }

    public void test6DigitHexColor() {
        // TODO:
        // assertType("", PXStylesheetTokenType.);
    }

    public void testEm() {
        assertDimension("10em", PXStylesheetTokenType.EMS, PXDimensionType.EMS, 10.0f);
    }

    public void testEx() {
        assertDimension("10ex", PXStylesheetTokenType.EXS, PXDimensionType.EXS, 10.0f);
    }

    public void testPixels() {
        assertDimension("10px", PXStylesheetTokenType.LENGTH, PXDimensionType.PIXELS, 10.0f);
    }

    public void testDevicePixels() {
        assertDimension("10dpx", PXStylesheetTokenType.LENGTH, PXDimensionType.DEVICE_PIXELS, 10.0f);
    }

    public void testCentimeters() {
        assertDimension("10cm", PXStylesheetTokenType.LENGTH, PXDimensionType.CENTIMETERS, 10.0f);
    }

    public void testMillimeters() {
        assertDimension("10mm", PXStylesheetTokenType.LENGTH, PXDimensionType.MILLIMETERS, 10.0f);
    }

    public void testInches() {
        assertDimension("10in", PXStylesheetTokenType.LENGTH, PXDimensionType.INCHES, 10.0f);
    }

    public void testPoints() {
        assertDimension("10pt", PXStylesheetTokenType.LENGTH, PXDimensionType.POINTS, 10.0f);
    }

    public void testPicas() {
        assertDimension("10pc", PXStylesheetTokenType.LENGTH, PXDimensionType.PICAS, 10.0f);
    }

    public void testDegrees() {
        assertDimension("10deg", PXStylesheetTokenType.ANGLE, PXDimensionType.DEGREES, 10.0f);
    }

    public void testRadians() {
        assertDimension("10rad", PXStylesheetTokenType.ANGLE, PXDimensionType.RADIANS, 10.0f);
    }

    public void testGradians() {
        assertDimension("10grad", PXStylesheetTokenType.ANGLE, PXDimensionType.GRADIANS, 10.0f);
    }

    public void testMilliseconds() {
        assertDimension("10ms", PXStylesheetTokenType.TIME, PXDimensionType.MILLISECONDS, 10.0f);
    }

    public void testSeconds() {
        assertDimension("10s", PXStylesheetTokenType.TIME, PXDimensionType.SECONDS, 10.0f);
    }

    public void testHertz() {
        assertDimension("10Hz", PXStylesheetTokenType.FREQUENCY, PXDimensionType.HERTZ, 10.0f);
    }

    public void testKilohertz() {
        assertDimension("10KHz", PXStylesheetTokenType.FREQUENCY, PXDimensionType.KILOHERTZ, 10.0f);
    }

    public void testPercentage() {
        assertDimension("10%", PXStylesheetTokenType.PERCENTAGE, PXDimensionType.PERCENTAGE, 10.0f);
    }

    public void testUserDefinedDimension() {
        assertType("10units", PXStylesheetTokenType.DIMENSION);
    }

    public void testKeyframes() {
        assertType("@keyframes", PXStylesheetTokenType.KEYFRAMES);
    }

    public void testError() {
        assertType("&", PXStylesheetTokenType.ERROR);
    }

    public void testIdLooksLikeHexColor() {
        assertType("#abc", PXStylesheetTokenType.ID);
    }

    public void testIdLooksLikeHexColor2() {
        assertType("#back", PXStylesheetTokenType.ID);
    }

    public void testIdLooksLikeHexColor3() {
        assertType("#background", PXStylesheetTokenType.ID);
    }

    public void testURLWithDoubleQuotedString() {
        assertType("url(\"http://www.pixate.com\")", PXStylesheetTokenType.URL);
    }

    public void testURLWithDoubleQuotedStringAndLeadingSpaces() {
        assertType("url(  \"http://www.pixate.com\")", PXStylesheetTokenType.URL);
    }

    public void testURLWithDoubleQuotedStringAndTrailingSpaces() {
        assertType("url(\"http://www.pixate.com\"  )", PXStylesheetTokenType.URL);
    }

    public void testURLWithDoubleQuotedStringAndSpaces() {
        assertType("url(  \"http://www.pixate.com\"  )", PXStylesheetTokenType.URL);
    }

    public void testURLWithSingleQuotedString() {
        assertType("url('http://www.pixate.com')", PXStylesheetTokenType.URL);
    }

    public void testURLWithSingleQuotedStringAndLeadingSpaces() {
        assertType("url(  'http://www.pixate.com')", PXStylesheetTokenType.URL);
    }

    public void testURLWithSingleQuotedStringAndTrailingSpaces() {
        assertType("url('http://www.pixate.com'  )", PXStylesheetTokenType.URL);
    }

    public void testURLWithSingleQuotedStringAndSpaces() {
        assertType("url(  'http://www.pixate.com'  )", PXStylesheetTokenType.URL);
    }

    public void testURLWithInvalidQuotedString() {
        assertType("url(  'http://www.pixate.com\"  )", PXStylesheetTokenType.URL);
    }

    public void testURLWithURI() {
        assertType("url(http://www.pixate.com)", PXStylesheetTokenType.URL);
    }

    public void testNamespace() {
        assertType("@namespace", PXStylesheetTokenType.NAMESPACE);
    }

    public void testLinkPseudoClass() {
        assertType(":link", PXStylesheetTokenType.LINK_PSEUDO_CLASS);
    }

    public void testVisitedPseudoClass() {
        assertType(":visited", PXStylesheetTokenType.VISITED_PSEUDO_CLASS);
    }

    public void testHoverPseudoClass() {
        assertType(":hover", PXStylesheetTokenType.HOVER_PSEUDO_CLASS);
    }

    public void testActivePseudoClass() {
        assertType(":active", PXStylesheetTokenType.ACTIVE_PSEUDO_CLASS);
    }

    public void testFocusPseudoClass() {
        assertType(":focus", PXStylesheetTokenType.FOCUS_PSEUDO_CLASS);
    }

    public void testTargetPseudoClass() {
        assertType(":target", PXStylesheetTokenType.TARGET_PSEUDO_CLASS);
    }

    public void testLangPseudoClass() {
        assertType(":lang(", PXStylesheetTokenType.LANG_PSEUDO_CLASS);
    }

    public void testEnabledPseudoClass() {
        assertType(":enabled", PXStylesheetTokenType.ENABLED_PSEUDO_CLASS);
    }

    public void testCheckedPseudoClass() {
        assertType(":checked", PXStylesheetTokenType.CHECKED_PSEUDO_CLASS);
    }

    public void testIndeterminatePseudoClass() {
        assertType(":indeterminate", PXStylesheetTokenType.INDETERMINATE_PSEUDO_CLASS);
    }

    public void testRootPseudoClass() {
        assertType(":root", PXStylesheetTokenType.ROOT_PSEUDO_CLASS);
    }

    public void testNthChildPseudoClass() {
        assertType(":nth-child(", PXStylesheetTokenType.NTH_CHILD_PSEUDO_CLASS);
    }

    public void testNthLastChildPseudoClass() {
        assertType(":nth-last-child(", PXStylesheetTokenType.NTH_LAST_CHILD_PSEUDO_CLASS);
    }

    public void testNthOfTypePseudoClass() {
        assertType(":nth-of-type(", PXStylesheetTokenType.NTH_OF_TYPE_PSEUDO_CLASS);
    }

    public void testNthLastOfTypePseudoClass() {
        assertType(":nth-last-of-type(", PXStylesheetTokenType.NTH_LAST_OF_TYPE_PSEUDO_CLASS);
    }

    public void testFirstChildPseudoClass() {
        assertType(":first-child", PXStylesheetTokenType.FIRST_CHILD_PSEUDO_CLASS);
    }

    public void testLastChildPseudoClass() {
        assertType(":last-child", PXStylesheetTokenType.LAST_CHILD_PSEUDO_CLASS);
    }

    public void testFirstOfTypePseudoClass() {
        assertType(":first-of-type", PXStylesheetTokenType.FIRST_OF_TYPE_PSEUDO_CLASS);
    }

    public void testLastOfTypePseudoClass() {
        assertType(":last-of-type", PXStylesheetTokenType.LAST_OF_TYPE_PSEUDO_CLASS);
    }

    public void testOnlyChildPseudoClass() {
        assertType(":only-child", PXStylesheetTokenType.ONLY_CHILD_PSEUDO_CLASS);
    }

    public void testOnlyOfTypePseudoClass() {
        assertType(":only-of-type", PXStylesheetTokenType.ONLY_OF_TYPE_PSEUDO_CLASS);
    }

    public void testEmpty() {
        assertType(":empty", PXStylesheetTokenType.EMPTY_PSEUDO_CLASS);
    }

    public void testNthNOnly() {
        assertType("n", PXStylesheetTokenType.NTH);
    }

    public void testNthMinusNOnly() {
        assertType("-n", PXStylesheetTokenType.NTH);
    }

    public void testNthPlusNOnly() {
        assertType("+n", PXStylesheetTokenType.NTH);
    }

    public void testNthMultiplier() {
        assertType("2n", PXStylesheetTokenType.NTH);
    }

    public void testPositiveMultiplier() {
        assertType("+2n", PXStylesheetTokenType.NTH);
    }

    public void testNegativeMultiplier() {
        assertType("-2n", PXStylesheetTokenType.NTH);
    }

    public void testImportant() {
        assertType("!important", PXStylesheetTokenType.IMPORTANT);
    }

    public void testImportantWithWhitespace() {
        assertType("! important", PXStylesheetTokenType.IMPORTANT);
    }

    public void testImport() {
        assertType("@import", PXStylesheetTokenType.IMPORT);
    }

    public void testMedia() {
        assertType("@media", PXStylesheetTokenType.MEDIA);
    }

    public void testAnd() {
        assertType("and", PXStylesheetTokenType.AND);
    }

    public void testPushSource() {
        // TODO:
    }

    public void testRgbBug() {
        String source = "rgb(255,255,17)";
        this._lexer.setSource(source);

        assertEquals(PXStylesheetTokenType.RGB, this._lexer.nextLexeme().getType());
        assertEquals(PXStylesheetTokenType.NUMBER, this._lexer.nextLexeme().getType());
        assertEquals(PXStylesheetTokenType.COMMA, this._lexer.nextLexeme().getType());
        assertEquals(PXStylesheetTokenType.NUMBER, this._lexer.nextLexeme().getType());
        assertEquals(PXStylesheetTokenType.COMMA, this._lexer.nextLexeme().getType());
        assertEquals(PXStylesheetTokenType.NUMBER, this._lexer.nextLexeme().getType());
        assertEquals(PXStylesheetTokenType.RPAREN, this._lexer.nextLexeme().getType());
    }

    public void testHexColor() {
        String source = "#FF3F17";
        this._lexer.setSource(source);

        assertEquals(PXStylesheetTokenType.ID, this._lexer.nextLexeme().getType());
    }

    public void testClassBug() {
        assertType(".5cm", PXStylesheetTokenType.LENGTH);
    }

    public void testEscapedClassBug() {
        assertType(".one\\.word", PXStylesheetTokenType.CLASS);
    }
}
