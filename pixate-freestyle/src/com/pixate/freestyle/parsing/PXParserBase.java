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
package com.pixate.freestyle.parsing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.StringUtil;

/**
 * Pixate parser base class.
 */
public class PXParserBase<T extends Enum<T>> {

    private static final String TAG = PXParserBase.class.getSimpleName();

    protected Lexeme<T> currentLexeme;
    private List<String> errors;

    /**
     * Returns the errors for this parser.
     * 
     * @return An errors list (may be <code>null</code>);
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Advance to the next lexeme in the lexeme stream. The current lexeme is
     * returned and the currentLexeme is set to that return value.
     */
    public Lexeme<T> advance() {
        // TODO: generalize so descendants don't have to override this method
        currentLexeme = null;
        return currentLexeme;
    }

    /**
     * Add an error message to the list of errors encountered during parsing
     * 
     * @param error The error message to add
     */
    public void addError(String error) {
        if (!StringUtil.isEmpty(error)) {
            if (errors == null) {
                errors = new ArrayList<String>(3);
            }
            errors.add(error);
            PXLog.i(TAG, error);
        }
    }

    /**
     * Add an error message, including the filename and offset where the error
     * occurred
     * 
     * @param error The error message
     * @param filename The filename where the error occurred
     * @param offset A string representing the offset where the error occurred
     */
    public void addError(String error, String filename, String offset) {
        if (StringUtil.isEmpty(filename)) {
            addError(String.format("[PXEngine.ParseError, offset='%s']: %s", offset, error));
        } else {
            addError(String.format("[PXEngine.ParseError, file='%s', offset=%s]: %s", filename,
                    offset, error));
        }
    }

    /**
     * Remove all errors that have been previously reported. This should be
     * called before a parse begins if the parser instance is being re-used.
     */
    public void clearErrors() {
        errors = null;
    }

    /**
     * Throw an {@link PXParserException} and add an error message to the list
     * of errors collected so far.
     * 
     * @param message The error message
     */
    public void errorWithMessage(String message) throws PXParserException {
        throw new PXParserException(String.format("Unexpected token type. %s. Found %s", message,
                ((currentLexeme != null) ? currentLexeme.getType() : "null")));
    }

    /**
     * Assert that the current lexeme matches the specified type. If it does not
     * match, then throw an exception
     * 
     * @param type The lexeme type to test against
     */
    public void assertType(T type) {
        if (currentLexeme == null || currentLexeme.getType() != type) {
            errorWithMessage(MessageFormat.format("Expected a {0} token", type.toString()));
        }
    }

    /**
     * Assert that the current lexeme matches one of the types in the specified
     * set. If it does not match, then throw an exception.
     * 
     * @param types An set containing a collection of types to match against
     */
    public void assertTypeInSet(EnumSet<T> types) {
        if (!isInTypeSet(types)) {
            List<String> typeNames = new ArrayList<String>(types.size());
            for (Enum<T> s : types) {
                typeNames.add(s.toString());
            }
            errorWithMessage("Expected a token of one of these types: " + typeNames);
        }
    }

    /**
     * Assert that the current lexeme matches the specified type. If it does not
     * match, then throw an exception. If the types do match, then advance to
     * the next lexeme.
     * 
     * @param type The lexeme type to test against
     */
    public Lexeme<T> assertTypeAndAdvance(T type) {
        assertType(type);
        return advance();
    }

    /**
     * Advance to the next lexeme if the current lexeme matches the specified
     * type.
     * 
     * @param type The lexeme type to test against
     */
    public void advanceIfIsType(T type) {
        if (currentLexeme != null && currentLexeme.getType() == type) {
            advance();
        }
    }

    /**
     * Advance to the next lexeme if the current lexeme matches the specified
     * type. If the type does not match, then add a warning to the current list
     * of errors, but do not throw an exception
     * 
     * @param type The lexeme type to test against
     * @param warning The warning message to emit
     */
    public void advanceIfIsType(T type, String warning) {
        if (currentLexeme != null && currentLexeme.getType() == type) {
            advance();
        } else {
            errorWithMessage(warning);
        }
    }

    /**
     * Determine if the current lexeme matches the specified type.
     * 
     * @param type
     * @return <code>true</code> if the token is current; <code>false</code>
     *         otherwise.
     */
    public boolean isType(T type) {
        return (currentLexeme != null && currentLexeme.getType() == type);
    }

    /**
     * Determine if the current lexeme matches one of the types in the specified
     * set.
     * 
     * @param set
     * @return <code>true</code> if the current lexeme token is in the set;
     *         <code>false</code> otherwise.
     */
    public boolean isInTypeSet(EnumSet<T> set) {
        return (currentLexeme != null && set != null && set.contains(currentLexeme.getType()));
    }
}
