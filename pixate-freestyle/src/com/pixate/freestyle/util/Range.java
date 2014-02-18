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
package com.pixate.freestyle.util;

public class Range {
	/**
	 * A range that mark a non-found range.
	 */
	public static Range NOT_FOUND = new Range(-1, 0);

	public int location;
	public int length;

	public Range() {
		this(0, 0);
	}

	public Range(int offset, int length) {
		this.location = offset;
		this.length = length;
	}

	/**
	 * @return Returns the end location, which is location + length;
	 */
	public int getEnd() {
		return location + length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Range) {
			Range other = (Range) o;
			return location == other.location && length == other.length;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + location;
		hash = hash * 31 + length;
		return hash;
	}

	/**
	 * Returns true if this range contains the given range.
	 * 
	 * @param range
	 * @return <code>true</code> if this range contains the other;
	 *         <code>false</code> otherwise.
	 */
	public boolean contains(Range range) {
		if (range == null) {
			return false;
		}
		return (location <= range.location && location + length >= range.location + range.length);
	}
}
