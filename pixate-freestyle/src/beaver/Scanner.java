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
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver;

import java.io.IOException;



/**
 * Defines an interface expected by a generated parser.
 */
public abstract class Scanner
{
	public static class Exception extends java.lang.Exception
	{
    private static final long serialVersionUID = 1L;
    public final int line;
		public final int column;
		
		public Exception(String msg)
		{
			this(0, 0, msg);
		}
		
		public Exception(int line, int column, String msg)
		{
			super(msg);
			this.line = line;
			this.column = column;
		}
	}

	public abstract Symbol nextToken() throws IOException, Scanner.Exception;
}
