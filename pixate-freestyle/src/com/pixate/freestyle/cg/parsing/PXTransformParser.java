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
 * Copyright (c) 2012-2013 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.parsing;

import java.util.EnumSet;

import android.graphics.Matrix;
import android.util.DisplayMetrics;

import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.parsing.Lexeme;
import com.pixate.freestyle.parsing.PXParserBase;

/**
 * PXTransformParser generates a CGAffineTransform by parsing an SVG transform
 * value
 */
public class PXTransformParser extends PXParserBase<PXTransformTokenType> {

    public static Matrix IDENTITY_MATRIX = new Matrix();

    private static EnumSet<PXTransformTokenType> TRANSFORM_KEYWORD_SET;
    private static EnumSet<PXTransformTokenType> ANGLE_SET;
    private static EnumSet<PXTransformTokenType> LENGTH_SET;
    private static EnumSet<PXTransformTokenType> PERCENTAGE_SET;

    static {
        TRANSFORM_KEYWORD_SET = EnumSet.of(PXTransformTokenType.TRANSLATE,
                PXTransformTokenType.TRANSLATEX, PXTransformTokenType.TRANSLATEY,
                PXTransformTokenType.SCALE, PXTransformTokenType.SCALEX,
                PXTransformTokenType.SCALEY, PXTransformTokenType.SKEW, PXTransformTokenType.SKEWX,
                PXTransformTokenType.SKEWY, PXTransformTokenType.ROTATE,
                PXTransformTokenType.MATRIX);
    }
    static {
        ANGLE_SET = EnumSet.of(PXTransformTokenType.NUMBER, PXTransformTokenType.ANGLE);
    }
    static {
        LENGTH_SET = EnumSet.of(PXTransformTokenType.NUMBER, PXTransformTokenType.LENGTH);
    }
    static {
        PERCENTAGE_SET = EnumSet.of(PXTransformTokenType.NUMBER, PXTransformTokenType.PERCENTAGE);
    }

    private PXTransformLexer lexer;

    /**
     * Constructs a new transform parser.
     */
    public PXTransformParser() {
        lexer = new PXTransformLexer();
    }

    /**
     * Parse the specified source, generating a {@link Matrix} transformation as
     * a result
     * 
     * @param source The source to parse
     */
    public Matrix parse(String source) {
        Matrix result = new Matrix();

        // clear errors
        clearErrors();

        // setup lexer and prime lexer stream
        lexer.setSource(source);
        advance();

        // TODO: move try/catch inside while loop after adding some error
        // recovery
        try {
            while (currentLexeme != null && currentLexeme.getType() != PXTransformTokenType.EOF) {
                Matrix transform = parseTransform();
                result.preConcat(transform);
            }
        } catch (Exception e) {
            addError(e.getMessage());
        }

        return result;
    }

    @Override
    public Lexeme<PXTransformTokenType> advance() {
        currentLexeme = lexer.nextLexeme();
        return currentLexeme;
    }

    /**
     * Parse the matrix
     * 
     * @return A parsed Matrix
     */
    private Matrix parseTransform() {
        Matrix result = null;
        // advance over keyword
        assertTypeInSet(TRANSFORM_KEYWORD_SET);
        Lexeme<PXTransformTokenType> transformType = currentLexeme;
        advance();

        // advance over '('
        assertTypeAndAdvance(PXTransformTokenType.LPAREN);

        switch (transformType.getType()) {
            case TRANSLATE:
                result = parseTranslate();
                break;

            case TRANSLATEX:
                result = parseTranslateX();
                break;

            case TRANSLATEY:
                result = parseTranslateY();
                break;

            case SCALE:
                result = parseScale();
                break;

            case SCALEX:
                result = parseScaleX();
                break;

            case SCALEY:
                result = parseScaleY();
                break;

            case SKEW:
                result = parseSkew();
                break;

            case SKEWX:
                result = parseSkewX();
                break;

            case SKEWY:
                result = parseSkewY();
                break;

            case ROTATE:
                result = parseRotate();
                break;

            case MATRIX:
                result = parseMatrix();
                break;

            default:
                result = new Matrix();
                errorWithMessage("Unrecognized transform type");
                break;
        }
        // advance over ')'
        advanceIfIsType(PXTransformTokenType.RPAREN);
        return result;
    }

    private Matrix parseMatrix() {
        Matrix result = new Matrix();
        float[] values = new float[9];
        values[0] = floatValue();
        values[3] = floatValue();
        values[1] = floatValue();
        values[4] = floatValue();
        values[2] = floatValue();
        values[5] = floatValue();
        values[6] = 0;
        values[7] = 0;
        values[8] = 1;
        result.setValues(values);
        return result;
    }

    private Matrix parseRotate() {
        float angle = angleValue();
        Matrix result = new Matrix();
        if (isInTypeSet(LENGTH_SET)) {
            float x = lengthValue();
            float y = lengthValue();
            result.setTranslate(x, y);
            result.setRotate(angle);
            result.setTranslate(-x, -y);
        } else {
            result.setRotate(angle);
        }
        return result;
    }

    private Matrix parseScale() {
        float sx = floatValue();
        float sy = (isType(PXTransformTokenType.NUMBER)) ? floatValue() : sx;
        Matrix result = new Matrix();
        result.setScale(sx, sy);
        return result;
    }

    private Matrix parseScaleX() {
        float sx = floatValue();

        Matrix result = new Matrix();
        result.setScale(sx, 1.0f);
        return result;
    }

    private Matrix parseScaleY() {
        float sy = floatValue();

        Matrix result = new Matrix();
        result.setScale(1.0f, sy);
        return result;
    }

    private Matrix parseSkew() {
        float sx = (float) Math.tan(angleValue());
        float sy = (float) ((isInTypeSet(ANGLE_SET)) ? Math.tan(angleValue()) : 0.0f);

        Matrix result = new Matrix();
        result.setValues(new float[] { 1f, sy, 0f, sx, 1f, 0f, 0f, 0f, 1f });
        return result;
    }

    private Matrix parseSkewX() {
        float sx = (float) Math.tan(angleValue());
        Matrix result = new Matrix();
        result.setValues(new float[] { 1f, 0f, 0f, sx, 1f, 0f, 0f, 0f, 1f });
        return result;
    }

    private Matrix parseSkewY() {
        float sy = (float) Math.tan(angleValue());
        Matrix result = new Matrix();
        result.setValues(new float[] { 1f, sy, 0f, 0f, 1f, 0f, 0f, 0f, 1f });
        return result;
    }

    private Matrix parseTranslate() {
        float tx = lengthValue();
        float ty = (isInTypeSet(LENGTH_SET)) ? lengthValue() : 0.0f;
        Matrix result = new Matrix();
        result.setTranslate(tx, ty);
        return result;
    }

    private Matrix parseTranslateX() {
        float tx = lengthValue();

        Matrix result = new Matrix();
        result.setTranslate(tx, 0.0f);
        return result;
    }

    private Matrix parseTranslateY() {
        float ty = lengthValue();
        Matrix result = new Matrix();
        result.setTranslate(0.0f, ty);
        return result;
    }

    private float angleValue() {
        float result = 0.0f;

        if (isInTypeSet(ANGLE_SET)) {
            switch (currentLexeme.getType()) {
                case NUMBER: {

                    result = (float) Math.toRadians((Float) currentLexeme.getValue());
                    break;
                }

                case ANGLE: {
                    PXDimension angle = (PXDimension) currentLexeme.getValue();

                    result = angle.radians().getNumber();
                    break;
                }

                default: {
                    errorWithMessage("Unrecognized token type in LENGTH_SET: " + currentLexeme);
                    break;
                }
            }

            advance();
            advanceIfIsType(PXTransformTokenType.COMMA);
        }

        return result;
    }

    private float floatValue() {
        float result = 0.0f;

        if (isType(PXTransformTokenType.NUMBER)) {
            result = (Float) currentLexeme.getValue();

            advance();
            advanceIfIsType(PXTransformTokenType.COMMA);
        } else {
            errorWithMessage("Expected a NUMBER token");
        }

        return result;
    }

    private float lengthValue() {
        float result = 0.0f;

        if (isInTypeSet(LENGTH_SET)) {
            switch (currentLexeme.getType()) {
                case NUMBER: {

                    result = (Float) currentLexeme.getValue();
                    break;
                }

                case LENGTH: {
                    PXDimension length = (PXDimension) currentLexeme.getValue();
                    // FIXME - we need the real DisplayMetrics!
                    DisplayMetrics metrics = new DisplayMetrics();
                    metrics.setToDefaults();
                    result = length.points(metrics).getNumber();
                    break;
                }

                default: {
                    errorWithMessage("Unrecognized token type in LENGTH_SET: " + currentLexeme);
                    break;
                }
            }

            advance();
            advanceIfIsType(PXTransformTokenType.COMMA);
        } else {
            errorWithMessage("Expected a LENGTH or NUMBER token");
        }

        return result;
    }

    @SuppressWarnings("unused")
    /* Unused here, and appears to be unused in iOS as well. */
    private float percentageValue() {
        float result = 0.0f;

        if (isInTypeSet(PERCENTAGE_SET)) {
            switch (currentLexeme.getType()) {
                case PERCENTAGE: {
                    PXDimension percentage = (PXDimension) currentLexeme.getValue();

                    result = percentage.getNumber() / 100.0f;
                    break;
                }

                case NUMBER: {

                    result = (Float) currentLexeme.getValue();
                    break;
                }

                default: {
                    errorWithMessage("Unrecognized token type in PERCENTAGE_SET: " + currentLexeme);
                    break;
                }
            }

            advance();
            advanceIfIsType(PXTransformTokenType.COMMA);
        } else {
            errorWithMessage("Expected a PERCENTAGE or NUMBER token");
        }

        return result;
    }
}
