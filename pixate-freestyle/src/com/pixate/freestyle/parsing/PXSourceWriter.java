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
 * Copyright (c) 2012 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.parsing;

public class PXSourceWriter {

	private String currentIndent;
	private String indentString;
	private StringBuilder builder;

	public PXSourceWriter() {
		currentIndent = "";
		indentString = "  ";
		builder = new StringBuilder();
	}

	public void increaseIndent() {
		currentIndent += indentString;
	}

	public void decreaseIndent() {
		if (currentIndent.length() > indentString.length()) {
			currentIndent = currentIndent.substring(0, currentIndent.length()
					- indentString.length());
		}
	}

	public void printIndent() {
		if (currentIndent.length() > 0) {
			builder.append(currentIndent);
		}
	}

	public void printWithIndent(String text) {
		printIndent();
		print(text);
	}

	public void printWithIndent(char character) {
		printIndent();
		builder.append(character);
	}

	public void print(Object obj) {
		if (obj != null) {
			builder.append(obj);
		}
	}

	public void print(String text) {
		if (text != null) {
			builder.append(text);
		}
	}

	public void print(char character) {
		builder.append(character);
	}

	public void println() {
		builder.append('\n');
	}

	public void println(String text) {
		print(text);
		println();
	}

	public void println(char character) {
		builder.append(character);
		println();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return builder.toString();
	}
}
