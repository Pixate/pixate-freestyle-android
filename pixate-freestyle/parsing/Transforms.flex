package com.pixate.freestyle.cg.parsing;

import java.io.Reader;
import java.io.StringReader;

import beaver.Scanner;

import com.pixate.freestyle.cg.math.PXDimension;

%%

%class PXTransformLexer
%type PXTransformLexeme
%eofval{
	return new PXTransformLexeme(PXTransformTokenType.EOF, yychar, 0, "end-of-file");
%eofval}
%unicode
%ignorecase
%char

//%switch
//%table
//%pack

%{
	public PXTransformLexer()
	{
		this((Reader) null);
	}

	private PXTransformLexeme createLexeme(PXTransformTokenType type)
	{
		return createLexeme(type, yytext());
	}
	
	private PXTransformLexeme createLexeme(PXTransformTokenType type, Object value)
	{
		return new PXTransformLexeme(type, yychar, yylength(), value);
	}

	private PXDimension createDimension(String source, String units)
	{
		String numberString = source.substring(0, source.length() - units.length());
		
		return new PXDimension(Float.parseFloat(numberString), units);
	}

	public PXTransformLexeme nextLexeme()
	{
		PXTransformLexeme result = null;
		 
		try
		{
			result = yylex();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	public void setSource(String source)
	{
		yyreset(new StringReader(source));
	}
%}

//nonascii	= [\200-\4177777]
//h			= [0-9a-f]
//unicode		= \\{h}{1,6}
//escape		= {unicode}|\\[ = ~\200-\4177777]
//nmstart		= [a-z]|{nonascii}|{escape}
//nmchar		= [-a-z0-9]|{nonascii}|{escape}
//ident		= -?{nmstart}{nmchar}*
num			= [-+]?([0-9]+|[0-9]*\.[0-9]+)
w			= [ \t\r\n\f]


%%

{w}+					{ /* ignore */ }

"translate"				{ return createLexeme(PXTransformTokenType.TRANSLATE); }
"translateX"			{ return createLexeme(PXTransformTokenType.TRANSLATEX); }
"translateY"			{ return createLexeme(PXTransformTokenType.TRANSLATEY); }
"scale"					{ return createLexeme(PXTransformTokenType.SCALE); }
"scaleX"				{ return createLexeme(PXTransformTokenType.SCALEX); }
"scaleY"				{ return createLexeme(PXTransformTokenType.SCALEY); }
"skew"					{ return createLexeme(PXTransformTokenType.SKEW); }
"skewX"					{ return createLexeme(PXTransformTokenType.SKEWX); }
"skewY"					{ return createLexeme(PXTransformTokenType.SKEWY); }
"rotate"				{ return createLexeme(PXTransformTokenType.ROTATE); }
"matrix"				{ return createLexeme(PXTransformTokenType.MATRIX); }
{num}"em"				{ return createLexeme(PXTransformTokenType.EMS, createDimension(yytext(), "em")); }
{num}"ex"				{ return createLexeme(PXTransformTokenType.EXS, createDimension(yytext(), "ex")); }
{num}"px"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "px")); }
{num}"cm"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "cm")); }
{num}"mm"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "mm")); }
{num}"in"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "in")); }
{num}"pt"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "pt")); }
{num}"pc"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "pc")); }
{num}"dpx"				{ return createLexeme(PXTransformTokenType.LENGTH, createDimension(yytext(), "dpx")); }
{num}"deg"				{ return createLexeme(PXTransformTokenType.ANGLE, createDimension(yytext(), "deg")); }
{num}"rad"				{ return createLexeme(PXTransformTokenType.ANGLE, createDimension(yytext(), "rad")); }
{num}"grad"				{ return createLexeme(PXTransformTokenType.ANGLE, createDimension(yytext(), "grad")); }
{num}"ms"				{ return createLexeme(PXTransformTokenType.TIME, createDimension(yytext(), "ms")); }
{num}"s"				{ return createLexeme(PXTransformTokenType.TIME, createDimension(yytext(), "s")); }
{num}"hz"				{ return createLexeme(PXTransformTokenType.FREQUENCY, createDimension(yytext(), "Hz")); }
{num}"khz"				{ return createLexeme(PXTransformTokenType.FREQUENCY, createDimension(yytext(), "kHz")); }
//{num}{ident}			{ return createLexeme(PXTransformTokenType.DIMENSION); }
{num}%					{ return createLexeme(PXTransformTokenType.PERCENTAGE, createDimension(yytext(), "%")); }
{num}					{ return createLexeme(PXTransformTokenType.NUMBER, Float.parseFloat(yytext())); }

"("						{ return createLexeme(PXTransformTokenType.LPAREN); }
")"						{ return createLexeme(PXTransformTokenType.RPAREN); }
","						{ return createLexeme(PXTransformTokenType.COMMA); }

.						{ return createLexeme(PXTransformTokenType.ERROR); }
