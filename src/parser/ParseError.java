package parser;

import lexer.Lexeme;
import lexer.Token;

public class ParseError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String message;

	ParseError(Token expected, Lexeme bad) {
		this("Unexpected token found. Expected " + expected.toString(), bad);
	}
	
	ParseError(String message, Lexeme bad) {
		String s = "PARSE ERROR: " + bad.getRow() + ": " + bad.getColumn() + ": ";
		s += message;
		s += " but got a " + bad.getToken() + " \"" + bad.getLexemeContent() + "\"";
		this.message = s;
	}
	ParseError(String symbol, String message, Lexeme bad) {
		String s = symbol + message + "at row" + bad.getRow() + ": column " + bad.getColumn() + ".";
		this.message = s;
	}
	
	public String toString() {
		return message;
	}
}