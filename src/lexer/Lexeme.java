package lexer;

//we may need to alter the getRow() and getColumn() methods, They are supposed to point to the FIRST char
//of the current Lexeme, not the last (currently it returns the last), this is an easy fix.
//can be done by simply doing column# = col - getLexemeContent().length()
public class Lexeme {
	Token token;
	String content;
	int row;
	int col;
	
	public Token getToken() {
		return token;
	}
	public String getLexemeContent() {
		return content;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return col;
	}
}
