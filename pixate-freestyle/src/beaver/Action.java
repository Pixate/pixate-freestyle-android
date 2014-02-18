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

/**
 * An "interface" to Java code executed when a production is reduced.
 */
public abstract class Action
{
	static public final Action NONE = new Action()
	{
		public Symbol reduce(Symbol[] args, int offset)
		{
			return new Symbol(null);
		}
	};
	
	static public final Action RETURN = new Action()
	{
		public Symbol reduce(Symbol[] args, int offset)
		{
			return args[offset + 1];
		}
	};
	
	/**
	 * Am action code that is executed when the production is reduced.
	 *
	 * @param args   an array part of which is filled with this action arguments
	 * @param offset to the last element <b>BEFORE</b> the first argument of this action
	 * @return a symbol or a value of a LHS nonterminal
	 */
	public abstract Symbol reduce(Symbol[] args, int offset);
}
