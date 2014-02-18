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
import java.util.HashMap;
import java.util.Map;

/**
 * PXTransform token types
 */
public enum PXTransformTokenType {
    ERROR((short) -1),
    EOF(Terminals.EOF),

    WHITESPACE((short) -2),

    EMS(Terminals.EMS),
    EXS(Terminals.EXS),
    LENGTH(Terminals.LENGTH),
    ANGLE(Terminals.ANGLE),
    TIME(Terminals.TIME),
    FREQUENCY(Terminals.FREQUENCY),
    PERCENTAGE(Terminals.PERCENTAGE),
    DIMENSION(Terminals.DIMENSION),
    NUMBER(Terminals.NUMBER),

    LPAREN(Terminals.LPAREN),
    RPAREN(Terminals.RPAREN),
    COMMA(Terminals.COMMA),

    TRANSLATE(Terminals.TRANSLATE),
    TRANSLATEX(Terminals.TRANSLATEX),
    TRANSLATEY(Terminals.TRANSLATEY),
    SCALE(Terminals.SCALE),
    SCALEX(Terminals.SCALEX),
    SCALEY(Terminals.SCALEY),
    SKEW(Terminals.SKEW),
    SKEWX(Terminals.SKEWX),
    SKEWY(Terminals.SKEWY),
    ROTATE(Terminals.ROTATE),
    MATRIX(Terminals.MATRIX);

    private static Map<Short, PXTransformTokenType> nameForIntMap;
    private short _index;

    // prevent instantiation
    private PXTransformTokenType(short index) {
        this._index = index;
    }

    public short getIndex() {
        return this._index;
    }

    static {
        nameForIntMap = new HashMap<Short, PXTransformTokenType>();

        for (PXTransformTokenType type : EnumSet.allOf(PXTransformTokenType.class)) {
            nameForIntMap.put(type.getIndex(), type);
        }
    }

    public static String typeNameForInt(short type) {
        PXTransformTokenType t = nameForIntMap.get(type);

        return (t == null) ? "<unknown>" : t.toString();
    }
};
