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
package com.pixate.freestyle.styling.selectors;

public class PXSpecificity implements Comparable<PXSpecificity> {

	// @formatter:off
	public enum PXSpecificityType {
		STYLE_ATTRIBUTE,
		ORIGIN,
		ID,
		CLASS_OR_ATTRIBUTE,
		ELEMENT
	}
	// @formatter:on

	private int[] values;
	private int length;

	public PXSpecificity() {
		length = PXSpecificityType.values().length;
		values = new int[length];
	}

	public void incrementSpecifity(PXSpecificityType specificity) {
		values[specificity.ordinal()]++;
	}

	public void setSpecificity(PXSpecificityType specificity, int value) {
		values[specificity.ordinal()] = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(PXSpecificity another) {
		for (int i = 0; i < length; i++) {
			if (this.values[i] < another.values[i]) {
				return -1;
			} else if (this.values[i] > another.values[i]) {
				return 1;
			}
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder('(');
		for (int i = 0; i < length; i++) {
			builder.append(values[i]);
			if (i + 1 < length) {
				builder.append(',');
			}
		}
		builder.append(')');
		return builder.toString();
	}
}
