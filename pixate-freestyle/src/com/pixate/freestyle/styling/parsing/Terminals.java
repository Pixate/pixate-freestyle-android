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

/**
 * This class lists terminals used by the
 * grammar specified in "CSS.grammar".
 */
public class Terminals {
	static public final short EOF = 0;
	static public final short NUMBER = 1;
	static public final short CLASS = 2;
	static public final short ID = 3;
	static public final short IDENTIFIER = 4;
	static public final short LCURLY = 5;
	static public final short RCURLY = 6;
	static public final short LPAREN = 7;
	static public final short RPAREN = 8;
	static public final short LBRACKET = 9;
	static public final short RBRACKET = 10;
	static public final short SEMICOLON = 11;
	static public final short GREATER_THAN = 12;
	static public final short PLUS = 13;
	static public final short TILDE = 14;
	static public final short STAR = 15;
	static public final short EQUAL = 16;
	static public final short COLON = 17;
	static public final short COMMA = 18;
	static public final short PIPE = 19;
	static public final short SLASH = 20;
	static public final short DOUBLE_COLON = 21;
	static public final short STARTS_WITH = 22;
	static public final short ENDS_WITH = 23;
	static public final short CONTAINS = 24;
	static public final short LIST_CONTAINS = 25;
	static public final short EQUALS_WITH_HYPHEN = 26;
	static public final short STRING = 27;
	static public final short LINEAR_GRADIENT = 28;
	static public final short RADIAL_GRADIENT = 29;
	static public final short HSL = 30;
	static public final short HSLA = 31;
	static public final short HSB = 32;
	static public final short HSBA = 33;
	static public final short RGB = 34;
	static public final short RGBA = 35;
	static public final short HEX_COLOR = 36;
	static public final short URL = 37;
	static public final short NAMESPACE = 38;
	static public final short NOT_PSEUDO_CLASS = 39;
	static public final short LINK_PSEUDO_CLASS = 40;
	static public final short VISITED_PSEUDO_CLASS = 41;
	static public final short HOVER_PSEUDO_CLASS = 42;
	static public final short ACTIVE_PSEUDO_CLASS = 43;
	static public final short FOCUS_PSEUDO_CLASS = 44;
	static public final short TARGET_PSEUDO_CLASS = 45;
	static public final short LANG_PSEUDO_CLASS = 46;
	static public final short ENABLED_PSEUDO_CLASS = 47;
	static public final short CHECKED_PSEUDO_CLASS = 48;
	static public final short INDETERMINATE_PSEUDO_CLASS = 49;
	static public final short ROOT_PSEUDO_CLASS = 50;
	static public final short NTH_CHILD_PSEUDO_CLASS = 51;
	static public final short NTH_LAST_CHILD_PSEUDO_CLASS = 52;
	static public final short NTH_OF_TYPE_PSEUDO_CLASS = 53;
	static public final short NTH_LAST_OF_TYPE_PSEUDO_CLASS = 54;
	static public final short FIRST_CHILD_PSEUDO_CLASS = 55;
	static public final short LAST_CHILD_PSEUDO_CLASS = 56;
	static public final short FIRST_OF_TYPE_PSEUDO_CLASS = 57;
	static public final short LAST_OF_TYPE_PSEUDO_CLASS = 58;
	static public final short ONLY_CHILD_PSEUDO_CLASS = 59;
	static public final short ONLY_OF_TYPE_PSEUDO_CLASS = 60;
	static public final short EMPTY_PSEUDO_CLASS = 61;
	static public final short NTH = 62;
	static public final short FIRST_LINE_PSEUDO_ELEMENT = 63;
	static public final short FIRST_LETTER_PSEUDO_ELEMENT = 64;
	static public final short BEFORE_PSEUDO_ELEMENT = 65;
	static public final short AFTER_PSEUDO_ELEMENT = 66;
	static public final short KEYFRAMES = 67;
	static public final short IMPORTANT = 68;
	static public final short IMPORT = 69;
	static public final short MEDIA = 70;
	static public final short FONT_FACE = 71;
	static public final short AND = 72;
	static public final short EMS = 73;
	static public final short EXS = 74;
	static public final short LENGTH = 75;
	static public final short ANGLE = 76;
	static public final short TIME = 77;
	static public final short FREQUENCY = 78;
	static public final short DIMENSION = 79;
	static public final short PERCENTAGE = 80;
}
