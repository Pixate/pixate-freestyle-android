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

import com.pixate.freestyle.cg.math.PXDimension;

/**
 * Lexeme
 */
public class Lexeme<T> {

    public static int FLAG_TYPE_FOLLOWS_WHITESPACE = 1;

    private T _type;
    private int _offset;
    private int _length;
    private Object _value;
    private int flags;

    /**
     * Constructs a new lexeme.
     * 
     * @param type
     * @param offset
     * @param length
     * @param value
     */
    public Lexeme(T type, int offset, int length, Object value) {
        _type = type;
        _offset = offset;
        _length = length;
        _value = value;
    }

    public T getType() {
        return _type;
    }

    public String getTypeName() {
        return _type.toString();
    }

    public int getOffset() {
        return _offset;
    }

    public int getEndingOffset() {
        return _offset + _length;
    }

    public int getLength() {
        return _length;
    }

    /**
     * Can be {@link PXDimension} or {@link Float}
     * 
     * @return The value
     */
    public Object getValue() {
        return _value;
    }

    // Flags

    public void clearFlag(int flagType) {
        flags &= ~flagType;
    }

    public void setFlag(int flagType) {
        flags |= flagType;
    }

    public boolean isFlagSet(int flagType) {
        return ((flags & flagType) == flagType);
    }

    public boolean followsWhitespace() {
        return isFlagSet(FLAG_TYPE_FOLLOWS_WHITESPACE);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MessageFormat.format("{0} {1}-{2}: {3}", getTypeName(), _offset, getEndingOffset(),
                _value);
    }
}
