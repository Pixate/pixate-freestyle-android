// java -jar JFlex.jar CSS.flex

package com.pixate.freestyle.styling.parsing;

import java.io.Reader;
import java.io.StringReader;

import java.util.Stack;

import com.pixate.freestyle.cg.math.PXDimension;
import com.pixate.freestyle.parsing.Lexeme;

%%

%class PXStylesheetLexer
%type PXStylesheetLexeme
%eofval{
	return new PXStylesheetLexeme(PXStylesheetTokenType.EOF, yychar, 0, "end-of-file");
%eofval}
%unicode
%ignorecase
%char

//%switch
//%table
//%pack

%{
	 /** Holds a source reference */
	private String source;
  	private Stack<PXStylesheetLexeme> lexemeStack;
  	
	public PXStylesheetLexer() {
		this((Reader) null);
	}

	private PXStylesheetLexeme createLexeme(PXStylesheetTokenType type) {
		return createLexeme(type, yytext());
	}
	
	private PXStylesheetLexeme createLexeme(PXStylesheetTokenType type, Object value) {
		PXStylesheetLexeme result = new PXStylesheetLexeme(type, yychar, yylength(), value);

		if (yychar > 0) {
			char c = source.charAt(yychar - 1);

			if (Character.isWhitespace(c)) {
				result.setFlag(Lexeme.FLAG_TYPE_FOLLOWS_WHITESPACE);
			}
		}

		return result;
	}

	private PXDimension createDimension(String source, String units) {
		String numberString = source.substring(0, source.length() - units.length());

		return new PXDimension(Float.parseFloat(numberString), units);
	}

	private PXStylesheetLexeme createURLLexeme() {
		String source = yytext();
		int start = 4;
		int end = source.length() - 1;

		// skip leading whitespace
		while (start < end && Character.isWhitespace(source.charAt(start))) {
			start++;
		}

		// skip trailing whitespace
		while (end >= start && Character.isWhitespace(source.charAt(end - 1))) {
			end--;
		}

		// grab possible leading quote
		char firstChar = source.charAt(start);

		// trim off quotes if we have them and if they match
		if ((firstChar == '\'' || firstChar == '"') && source.charAt(end - 1) == firstChar) {
			start++;
			end--;
		}

		return createLexeme(PXStylesheetTokenType.URL, source.substring(start, end));
	}

	public PXStylesheetLexeme nextLexeme() {
		PXStylesheetLexeme result = null;
		
		if (lexemeStack != null && !lexemeStack.isEmpty()) {
			result = lexemeStack.pop();
		}
		else {
			try {
				result = yylex();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public void setSource(String source) {
		this.source = source;
		yyreset(new StringReader(source));
	}
	
	public String getSource() {
		return source;
	}
	
	public void pushLexeme(PXStylesheetLexeme lexeme) {
		if (lexeme != null) {
			if (lexemeStack == null) {
				lexemeStack = new Stack<PXStylesheetLexeme>();
			}
			
			lexemeStack.push(lexeme);
		}
	}
%}

// based on http://www.w3.org/TR/css3-syntax/

h			= [0-9a-fA-Z]
nonascii	= [\200-\377]
unicode		= \\{h}{1,6}
escape		= {unicode}|\\[ =~.\200-\377]
nmstart		= [a-zA-Z]|{nonascii}|{escape}
nmchar		= [-a-zA-Z0-9_]|{nonascii}|{escape}
nmchar2		= [a-zA-Z0-9]|{nonascii}|{escape}
string1		= \'([^\n\r\f\']|\\{nl}|{escape})*\'
string2		= \"([^\n\r\f\"]|\\{nl}|{escape})*\"

ident		= -?{nmstart}{nmchar}*
dim         = -?{nmstart}{nmchar2}*
name		= {nmchar}+
num			= [-+]?([0-9]+|[0-9]*\.[0-9]+)
string		= {string1}|{string2}
w			= [ \t\r\n\f]
nl			= \r|\n|\r\n|\f
comment		= "/*" ~"*/"
//range		= \?{1,6}|{h}(\?{0,5}|{h}(\?{0,4}|{h}(\?{0,3}|{h}(\?{0,2}|{h}(\??|{h})))))

n			= [-+]?[0-9]*n

%%

{w}+					{ /* ignore */ }

{comment}				{ /* ignore */ }

"~="					{ return createLexeme(PXStylesheetTokenType.LIST_CONTAINS); }
"|="					{ return createLexeme(PXStylesheetTokenType.EQUALS_WITH_HYPHEN); }
"^="					{ return createLexeme(PXStylesheetTokenType.STARTS_WITH); }
"$="					{ return createLexeme(PXStylesheetTokenType.ENDS_WITH); }
"*="					{ return createLexeme(PXStylesheetTokenType.CONTAINS); }

{string}				{ return createLexeme(PXStylesheetTokenType.STRING); }

"and"					{ return createLexeme(PXStylesheetTokenType.AND); }
//"not"					{ return createLexeme(PXStylesheetTokenType.NOT); }
"linear-gradient("		{ return createLexeme(PXStylesheetTokenType.LINEAR_GRADIENT); }
"radial-gradient("		{ return createLexeme(PXStylesheetTokenType.RADIAL_GRADIENT); }
"hsb("					{ return createLexeme(PXStylesheetTokenType.HSB); }
"hsba("					{ return createLexeme(PXStylesheetTokenType.HSBA); }
"hsl("					{ return createLexeme(PXStylesheetTokenType.HSL); }
"hsla("					{ return createLexeme(PXStylesheetTokenType.HSLA); }
"rgb("					{ return createLexeme(PXStylesheetTokenType.RGB); }
"rgba("					{ return createLexeme(PXStylesheetTokenType.RGBA); }
"url("[^)]*")"			{ return createURLLexeme(); }

"#"{name}				{ return createLexeme(PXStylesheetTokenType.ID); }
"."{ident}				{ return createLexeme(PXStylesheetTokenType.CLASS); }

"@import"				{ return createLexeme(PXStylesheetTokenType.IMPORT); }
//"@page"					{ return createLexeme(PXStylesheetTokenType.PAGE); }
"@media"				{ return createLexeme(PXStylesheetTokenType.MEDIA); }
"@font-face"			{ return createLexeme(PXStylesheetTokenType.FONT_FACE); }
//"@charset"				{ return createLexeme(PXStylesheetTokenType.CHARSET); }
"@namespace"			{ return createLexeme(PXStylesheetTokenType.NAMESPACE); }
"@keyframes"			{ return createLexeme(PXStylesheetTokenType.KEYFRAMES); }
//"@"{name}				{ return createLexeme(PXStylesheetTokenType.AT_RULE); }

"!"({w}+|{comment})*"important"
						{ return createLexeme(PXStylesheetTokenType.IMPORTANT); }

{num}"em"				{ return createLexeme(PXStylesheetTokenType.EMS, createDimension(yytext(), "em")); }
{num}"ex"				{ return createLexeme(PXStylesheetTokenType.EXS, createDimension(yytext(), "ex")); }
{num}"px"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "px")); }
{num}"cm"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "cm")); }
{num}"mm"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "mm")); }
{num}"in"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "in")); }
{num}"pt"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "pt")); }
{num}"pc"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "pc")); }
{num}"dpx"				{ return createLexeme(PXStylesheetTokenType.LENGTH, createDimension(yytext(), "dpx")); }
{num}"deg"				{ return createLexeme(PXStylesheetTokenType.ANGLE, createDimension(yytext(), "deg")); }
{num}"rad"				{ return createLexeme(PXStylesheetTokenType.ANGLE, createDimension(yytext(), "rad")); }
{num}"grad"				{ return createLexeme(PXStylesheetTokenType.ANGLE, createDimension(yytext(), "grad")); }
{num}"ms"				{ return createLexeme(PXStylesheetTokenType.TIME, createDimension(yytext(), "ms")); }
{num}"s"				{ return createLexeme(PXStylesheetTokenType.TIME, createDimension(yytext(), "s")); }
{num}"hz"				{ return createLexeme(PXStylesheetTokenType.FREQUENCY, createDimension(yytext(), "Hz")); }
{num}"khz"				{ return createLexeme(PXStylesheetTokenType.FREQUENCY, createDimension(yytext(), "kHz")); }
{n}						{ return createLexeme(PXStylesheetTokenType.NTH); }
{num}{dim}				{ return createLexeme(PXStylesheetTokenType.DIMENSION); }
{num}%					{ return createLexeme(PXStylesheetTokenType.PERCENTAGE, createDimension(yytext(), "%")); }
{num}					{ return createLexeme(PXStylesheetTokenType.NUMBER); }

":not("					{ return createLexeme(PXStylesheetTokenType.NOT_PSEUDO_CLASS); }
":link"					{ return createLexeme(PXStylesheetTokenType.LINK_PSEUDO_CLASS); }
":visited"				{ return createLexeme(PXStylesheetTokenType.VISITED_PSEUDO_CLASS); }
":hover"				{ return createLexeme(PXStylesheetTokenType.HOVER_PSEUDO_CLASS); }
":active"				{ return createLexeme(PXStylesheetTokenType.ACTIVE_PSEUDO_CLASS); }
":focus"				{ return createLexeme(PXStylesheetTokenType.FOCUS_PSEUDO_CLASS); }
":target"				{ return createLexeme(PXStylesheetTokenType.TARGET_PSEUDO_CLASS); }
":lang("				{ return createLexeme(PXStylesheetTokenType.LANG_PSEUDO_CLASS); }
":enabled"				{ return createLexeme(PXStylesheetTokenType.ENABLED_PSEUDO_CLASS); }
":checked"				{ return createLexeme(PXStylesheetTokenType.CHECKED_PSEUDO_CLASS); }
":indeterminate"		{ return createLexeme(PXStylesheetTokenType.INDETERMINATE_PSEUDO_CLASS); }
":root"					{ return createLexeme(PXStylesheetTokenType.ROOT_PSEUDO_CLASS); }
":nth-child("			{ return createLexeme(PXStylesheetTokenType.NTH_CHILD_PSEUDO_CLASS); }
":nth-last-child("		{ return createLexeme(PXStylesheetTokenType.NTH_LAST_CHILD_PSEUDO_CLASS); }
":nth-of-type("			{ return createLexeme(PXStylesheetTokenType.NTH_OF_TYPE_PSEUDO_CLASS); }
":nth-last-of-type("	{ return createLexeme(PXStylesheetTokenType.NTH_LAST_OF_TYPE_PSEUDO_CLASS); }
":first-child"			{ return createLexeme(PXStylesheetTokenType.FIRST_CHILD_PSEUDO_CLASS); }
":last-child"			{ return createLexeme(PXStylesheetTokenType.LAST_CHILD_PSEUDO_CLASS); }
":first-of-type"		{ return createLexeme(PXStylesheetTokenType.FIRST_OF_TYPE_PSEUDO_CLASS); }
":last-of-type"			{ return createLexeme(PXStylesheetTokenType.LAST_OF_TYPE_PSEUDO_CLASS); }
":only-child"			{ return createLexeme(PXStylesheetTokenType.ONLY_CHILD_PSEUDO_CLASS); }
":only-of-type"			{ return createLexeme(PXStylesheetTokenType.ONLY_OF_TYPE_PSEUDO_CLASS); }
":empty"				{ return createLexeme(PXStylesheetTokenType.EMPTY_PSEUDO_CLASS); }

":first-line"			{ return createLexeme(PXStylesheetTokenType.FIRST_LINE_PSEUDO_ELEMENT); }
":first-letter"			{ return createLexeme(PXStylesheetTokenType.FIRST_LETTER_PSEUDO_ELEMENT); }
":before"				{ return createLexeme(PXStylesheetTokenType.BEFORE_PSEUDO_ELEMENT); }
":after"				{ return createLexeme(PXStylesheetTokenType.AFTER_PSEUDO_ELEMENT); }

"{"						{ return createLexeme(PXStylesheetTokenType.LCURLY); }
"}"						{ return createLexeme(PXStylesheetTokenType.RCURLY); }
"("						{ return createLexeme(PXStylesheetTokenType.LPAREN); }
")"						{ return createLexeme(PXStylesheetTokenType.RPAREN); }
"["						{ return createLexeme(PXStylesheetTokenType.LBRACKET); }
"]"						{ return createLexeme(PXStylesheetTokenType.RBRACKET); }
";"						{ return createLexeme(PXStylesheetTokenType.SEMICOLON); }
">"						{ return createLexeme(PXStylesheetTokenType.GREATER_THAN); }
"+"						{ return createLexeme(PXStylesheetTokenType.PLUS); }
"~"						{ return createLexeme(PXStylesheetTokenType.TILDE); }
"*"						{ return createLexeme(PXStylesheetTokenType.STAR); }
"="						{ return createLexeme(PXStylesheetTokenType.EQUAL); }
"::"					{ return createLexeme(PXStylesheetTokenType.DOUBLE_COLON); }
":"						{ return createLexeme(PXStylesheetTokenType.COLON); }
","						{ return createLexeme(PXStylesheetTokenType.COMMA); }
"|"						{ return createLexeme(PXStylesheetTokenType.PIPE); }
"/"						{ return createLexeme(PXStylesheetTokenType.SLASH); }

{ident}					{ return createLexeme(PXStylesheetTokenType.IDENTIFIER); }

.						{ return createLexeme(PXStylesheetTokenType.ERROR); }
