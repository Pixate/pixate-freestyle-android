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
package com.pixate.freestyle.styling.parsing;

import com.pixate.freestyle.parsing.Lexeme;
import com.pixate.freestyle.util.ObjectUtil;

/**
 * PXStylesheetLexeme
 */
public class PXStylesheetLexeme extends Lexeme<PXStylesheetTokenType> {

    public PXStylesheetLexeme(PXStylesheetTokenType type, int offset, int length, Object value) {
        super(type, offset, length, value);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + getType().ordinal();
        hash = hash * 31 + getOffset();
        hash = hash * 13 + getLength();
        // value can be a String or PXDimention, but we also check for null,
        // just in case...
        Object value = getValue();
        hash = hash * 7 + (value != null ? value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PXStylesheetLexeme) {
            PXStylesheetLexeme other = (PXStylesheetLexeme) o;
            return getType() == other.getType() && getOffset() == other.getOffset()
                    && getLength() == other.getLength()
                    && ObjectUtil.areEqual(getValue(), other.getValue());
        }
        return false;
    }
}
