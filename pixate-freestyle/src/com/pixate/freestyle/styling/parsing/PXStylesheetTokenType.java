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

package com.pixate.freestyle.styling.parsing;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin
 */
public enum PXStylesheetTokenType {
    ERROR((short) -1),
    EOF(Terminals.EOF),
    WHITESPACE((short) -2),
    NUMBER(Terminals.NUMBER),
    CLASS(Terminals.CLASS),
    ID(Terminals.ID),
    IDENTIFIER(Terminals.IDENTIFIER),
    LCURLY(Terminals.LCURLY),
    RCURLY(Terminals.RCURLY),
    LPAREN(Terminals.LPAREN),
    RPAREN(Terminals.RPAREN),
    LBRACKET(Terminals.LBRACKET),
    RBRACKET(Terminals.RBRACKET),
    SEMICOLON(Terminals.SEMICOLON),
    GREATER_THAN(Terminals.GREATER_THAN),
    PLUS(Terminals.PLUS),
    TILDE(Terminals.TILDE),
    STAR(Terminals.STAR),
    EQUAL(Terminals.EQUAL),
    COLON(Terminals.COLON),
    COMMA(Terminals.COMMA),
    PIPE(Terminals.PIPE),
    SLASH(Terminals.SLASH),
    DOUBLE_COLON(Terminals.DOUBLE_COLON),
    STARTS_WITH(Terminals.STARTS_WITH),
    ENDS_WITH(Terminals.ENDS_WITH),
    CONTAINS(Terminals.CONTAINS),
    LIST_CONTAINS(Terminals.LIST_CONTAINS),
    EQUALS_WITH_HYPHEN(Terminals.EQUALS_WITH_HYPHEN),
    STRING(Terminals.STRING),
    LINEAR_GRADIENT(Terminals.LINEAR_GRADIENT),
    RADIAL_GRADIENT(Terminals.RADIAL_GRADIENT),
    HSL(Terminals.HSL),
    HSLA(Terminals.HSLA),
    HSB(Terminals.HSB),
    HSBA(Terminals.HSBA),
    RGB(Terminals.RGB),
    RGBA(Terminals.RGBA),
    HEX_COLOR(Terminals.HEX_COLOR),
    URL(Terminals.URL),
    NAMESPACE(Terminals.NAMESPACE),
    NOT_PSEUDO_CLASS(Terminals.NOT_PSEUDO_CLASS),
    LINK_PSEUDO_CLASS(Terminals.LINK_PSEUDO_CLASS),
    VISITED_PSEUDO_CLASS(Terminals.VISITED_PSEUDO_CLASS),
    HOVER_PSEUDO_CLASS(Terminals.HOVER_PSEUDO_CLASS),
    ACTIVE_PSEUDO_CLASS(Terminals.ACTIVE_PSEUDO_CLASS),
    FOCUS_PSEUDO_CLASS(Terminals.FOCUS_PSEUDO_CLASS),
    TARGET_PSEUDO_CLASS(Terminals.TARGET_PSEUDO_CLASS),
    LANG_PSEUDO_CLASS(Terminals.LANG_PSEUDO_CLASS),
    ENABLED_PSEUDO_CLASS(Terminals.ENABLED_PSEUDO_CLASS),
    CHECKED_PSEUDO_CLASS(Terminals.CHECKED_PSEUDO_CLASS),
    INDETERMINATE_PSEUDO_CLASS(Terminals.INDETERMINATE_PSEUDO_CLASS),
    ROOT_PSEUDO_CLASS(Terminals.ROOT_PSEUDO_CLASS),
    NTH_CHILD_PSEUDO_CLASS(Terminals.NTH_CHILD_PSEUDO_CLASS),
    NTH_LAST_CHILD_PSEUDO_CLASS(Terminals.NTH_LAST_CHILD_PSEUDO_CLASS),
    NTH_OF_TYPE_PSEUDO_CLASS(Terminals.NTH_OF_TYPE_PSEUDO_CLASS),
    NTH_LAST_OF_TYPE_PSEUDO_CLASS(Terminals.NTH_LAST_OF_TYPE_PSEUDO_CLASS),
    FIRST_CHILD_PSEUDO_CLASS(Terminals.FIRST_CHILD_PSEUDO_CLASS),
    LAST_CHILD_PSEUDO_CLASS(Terminals.LAST_CHILD_PSEUDO_CLASS),
    FIRST_OF_TYPE_PSEUDO_CLASS(Terminals.FIRST_OF_TYPE_PSEUDO_CLASS),
    LAST_OF_TYPE_PSEUDO_CLASS(Terminals.LAST_OF_TYPE_PSEUDO_CLASS),
    ONLY_CHILD_PSEUDO_CLASS(Terminals.ONLY_CHILD_PSEUDO_CLASS),
    ONLY_OF_TYPE_PSEUDO_CLASS(Terminals.ONLY_OF_TYPE_PSEUDO_CLASS),
    EMPTY_PSEUDO_CLASS(Terminals.EMPTY_PSEUDO_CLASS),
    NTH(Terminals.NTH),
    FIRST_LINE_PSEUDO_ELEMENT(Terminals.FIRST_LINE_PSEUDO_ELEMENT),
    FIRST_LETTER_PSEUDO_ELEMENT(Terminals.FIRST_LETTER_PSEUDO_ELEMENT),
    BEFORE_PSEUDO_ELEMENT(Terminals.BEFORE_PSEUDO_ELEMENT),
    AFTER_PSEUDO_ELEMENT(Terminals.AFTER_PSEUDO_ELEMENT),
    KEYFRAMES(Terminals.KEYFRAMES),
    IMPORTANT(Terminals.IMPORTANT),
    IMPORT(Terminals.IMPORT),
    MEDIA(Terminals.MEDIA),
    FONT_FACE(Terminals.FONT_FACE),
    AND(Terminals.AND),
    EMS(Terminals.EMS),
    EXS(Terminals.EXS),
    LENGTH(Terminals.LENGTH),
    ANGLE(Terminals.ANGLE),
    TIME(Terminals.TIME),
    FREQUENCY(Terminals.FREQUENCY),
    DIMENSION(Terminals.DIMENSION),
    PERCENTAGE(Terminals.PERCENTAGE);

    private static Map<Short, PXStylesheetTokenType> nameForIntMap;
    private short _index;

    private PXStylesheetTokenType(short index) {
        this._index = index;
    }

    public short getIndex() {
        return this._index;
    }

    static {
        nameForIntMap = new HashMap<Short, PXStylesheetTokenType>();

        for (PXStylesheetTokenType type : EnumSet.allOf(PXStylesheetTokenType.class)) {
            nameForIntMap.put(type.getIndex(), type);
        }
    }

    public static String typeNameForInt(short type) {
        PXStylesheetTokenType t = nameForIntMap.get(type);

        return (t == null) ? "<unknown>" : t.toString();
    }
}
