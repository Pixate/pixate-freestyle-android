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
package com.pixate.pxengine.cg.parsing;

import android.test.AndroidTestCase;

import com.pixate.pxengine.cg.math.PXDimension;
import com.pixate.pxengine.cg.math.PXDimensionType;

/**
 * @author kevin
 */
public class TransformLexerTests extends AndroidTestCase {
    private PXTransformLexer _lexer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this._lexer = new PXTransformLexer();
    }

    protected void assertDimension(String source, PXTransformTokenType tokenType, PXDimensionType dimensionType,
            float expectedValue) {
        PXTransformLexeme lexeme = getLexeme(source);

        assertTokenType(lexeme, tokenType, source.length());

        Object value = lexeme.getValue();
        assertNotNull("Expected a non-nil lexeme value", value);
        
        assertTrue("Expected an instance of a Float", value instanceof PXDimension);
        PXDimension dimensionValue = (PXDimension) value;
        
        assertEquals("Unexpected dimension type", dimensionType, dimensionValue.getType());
        assertTrue("Unexpected value", dimensionValue.getNumber() == expectedValue);
    }

    protected void assertType(String source, PXTransformTokenType type) {
        this.assertType(source, type, source.length());
    }

    protected void assertType(String source, PXTransformTokenType type, int endOffset) {
        PXTransformLexeme lexeme = getLexeme(source);

        assertTokenType(lexeme, type, endOffset);
    }

    protected void assertTokenType(PXTransformLexeme lexeme, PXTransformTokenType type, int endOffset) {
        assertEquals("Unexpected token type", type, lexeme.getType());
        assertTrue("Expected token to start at offset 0, but started at " + lexeme.getOffset(), lexeme.getOffset() == 0);
        assertTrue("Expected token to end at " + endOffset + ", but it ended at " + lexeme.getEndingOffset(),
                lexeme.getEndingOffset() == endOffset);
    }

    protected PXTransformLexeme getLexeme(String source) {
        this._lexer.setSource(source);

        return this._lexer.nextLexeme();
    }

    public void testEm() {
        assertDimension("10em", PXTransformTokenType.EMS, PXDimensionType.EMS, 10.0f);
    }

    public void testEx() {
        assertDimension("10ex", PXTransformTokenType.EXS, PXDimensionType.EXS, 10.0f);
    }

    public void testPixels() {
        assertDimension("10px", PXTransformTokenType.LENGTH, PXDimensionType.PIXELS, 10.0f);
    }

    public void testDevicePixels() {
        assertDimension("10dpx", PXTransformTokenType.LENGTH, PXDimensionType.DEVICE_PIXELS, 10.0f);
    }

    public void testCentimeters() {
        assertDimension("10cm", PXTransformTokenType.LENGTH, PXDimensionType.CENTIMETERS, 10.0f);
    }

    public void testMillimeters() {
        assertDimension("10mm", PXTransformTokenType.LENGTH, PXDimensionType.MILLIMETERS, 10.0f);
    }

    public void testInches() {
        assertDimension("10in", PXTransformTokenType.LENGTH, PXDimensionType.INCHES, 10.0f);
    }

    public void testPoints() {
        assertDimension("10pt", PXTransformTokenType.LENGTH, PXDimensionType.POINTS, 10.0f);
    }

    public void testPicas() {
        assertDimension("10pc", PXTransformTokenType.LENGTH, PXDimensionType.PICAS, 10.0f);
    }

    public void testDegrees() {
        assertDimension("10deg", PXTransformTokenType.ANGLE, PXDimensionType.DEGREES, 10.0f);
    }

    public void testRadians() {
        assertDimension("10rad", PXTransformTokenType.ANGLE, PXDimensionType.RADIANS, 10.0f);
    }

    public void testGradians() {
        assertDimension("10grad", PXTransformTokenType.ANGLE, PXDimensionType.GRADIANS, 10.0f);
    }

    public void testMilliseconds() {
        assertDimension("10ms", PXTransformTokenType.TIME, PXDimensionType.MILLISECONDS, 10.0f);
    }

    public void testSeconds() {
        assertDimension("10s", PXTransformTokenType.TIME, PXDimensionType.SECONDS, 10.0f);
    }

    public void testHertz() {
        assertDimension("10Hz", PXTransformTokenType.FREQUENCY, PXDimensionType.HERTZ, 10.0f);
    }

    public void testKilohertz() {
        assertDimension("10KHz", PXTransformTokenType.FREQUENCY, PXDimensionType.KILOHERTZ, 10.0f);
    }

    public void testPercentage() {
        assertDimension("10%", PXTransformTokenType.PERCENTAGE, PXDimensionType.PERCENTAGE, 10.0f);
    }

    /*
     * public void testUserDefinedDimension() { assertType("10units",
     * PXTransformTokenType.DIMENSION); }
     */

    public void testInteger() {
        assertType("123", PXTransformTokenType.NUMBER);
    }

    public void testLParen() {
        assertType("(", PXTransformTokenType.LPAREN);
    }

    public void testRParen() {
        assertType(")", PXTransformTokenType.RPAREN);
    }

    public void testComma() {
        assertType(",", PXTransformTokenType.COMMA);
    }

    public void testTranslate() {
        assertType("translate", PXTransformTokenType.TRANSLATE);
    }

    public void testTranslateX() {
        assertType("translateX", PXTransformTokenType.TRANSLATEX);
    }

    public void testTranslateY() {
        assertType("translateY", PXTransformTokenType.TRANSLATEY);
    }

    public void testScale() {
        assertType("scale", PXTransformTokenType.SCALE);
    }

    public void testScaleX() {
        assertType("scaleX", PXTransformTokenType.SCALEX);
    }

    public void testScaleY() {
        assertType("scaleY", PXTransformTokenType.SCALEY);
    }

    public void testSkew() {
        assertType("skew", PXTransformTokenType.SKEW);
    }

    public void testSkewX() {
        assertType("skewX", PXTransformTokenType.SKEWX);
    }

    public void testSkewY() {
        assertType("skewY", PXTransformTokenType.SKEWY);
    }

    public void testRotate() {
        assertType("rotate", PXTransformTokenType.ROTATE);
    }

    public void testMatrix() {
        assertType("matrix", PXTransformTokenType.MATRIX);
    }

    public void testNumberValue() {
        String source = "123";

        this._lexer.setSource(source);

        PXTransformLexeme lexeme = this._lexer.nextLexeme();
        assertEquals("Unexpected token type", PXTransformTokenType.NUMBER, lexeme.getType());

        Object value = lexeme.getValue();
        assertNotNull("Expected a non-nil lexeme value", value);
        assertTrue("Expected an instance of a Float", value instanceof Float);
        Float floatValue = (Float) value;
        assertTrue("Expected the number 123", floatValue.floatValue() == 123.0);
    }
}
