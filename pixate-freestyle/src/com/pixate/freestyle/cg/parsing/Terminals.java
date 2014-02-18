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
package com.pixate.freestyle.cg.parsing;

/**
 * This class lists terminals used by the
 * grammar specified in "Transforms.grammar".
 */
public class Terminals {
	static public final short EOF = 0;
	static public final short EMS = 1;
	static public final short EXS = 2;
	static public final short LENGTH = 3;
	static public final short ANGLE = 4;
	static public final short TIME = 5;
	static public final short FREQUENCY = 6;
	static public final short PERCENTAGE = 7;
	static public final short DIMENSION = 8;
	static public final short NUMBER = 9;
	static public final short LPAREN = 10;
	static public final short RPAREN = 11;
	static public final short COMMA = 12;
	static public final short TRANSLATE = 13;
	static public final short TRANSLATEX = 14;
	static public final short TRANSLATEY = 15;
	static public final short SCALE = 16;
	static public final short SCALEX = 17;
	static public final short SCALEY = 18;
	static public final short SKEW = 19;
	static public final short SKEWX = 20;
	static public final short SKEWY = 21;
	static public final short ROTATE = 22;
	static public final short MATRIX = 23;
}
