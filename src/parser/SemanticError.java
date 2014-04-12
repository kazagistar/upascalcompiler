package parser;

import symbolTable.*;

import lexer.Lexeme;

public class SemanticError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String message;
	
	SemanticError(String message, Lexeme bad) {
		String s = "SemanticError: " + message + "at row " + bad.getRow() + ": column " + bad.getColumn() + ".";
		this.message = s;
	}
	
	SemanticError(Type type1, Type type2, Lexeme bad){
		String s = "SemanticError: Mistmatched types for operator. Expected: " + type1 + ". Found " +
	type2 + " at row " + bad.getRow() + ": column " + bad.getColumn() + ".";
		this.message = s;
	}
	
	public String toString() {
		return message;
	}
}
