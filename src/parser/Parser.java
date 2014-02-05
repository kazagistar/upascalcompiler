package parser;

import lexer.Lexeme;
import lexer.LexemeProvider;
import lexer.Token;

public class Parser {
	private LexemeProvider in;
	private Token lookahead;

	public Parser(LexemeProvider in) {
		this.in = in;
		// load the first lookahead
		lookahead = in.getNext().getToken();
	}
	
	// Gets next lookahead item
	private void match(Token matched) {
		if (matched == lookahead)
			match();
		else
			error();
	}

	private void match() {
		lookahead = in.getNext().getToken();
	}
	
	private void error() {
		throw new RuntimeException("ERROOORRRRR");
	}
	
	
	/*
	 * The lookaheads do not need to be correct at this time, so the gutz of the if statements can just be set to if(TRUE) ... 
	 * this way it will still compile, and we can set the lookaheads later on.
	 * 
	 * At this point all we want is to have the stuff that is inside the if / if-else statements. The following is an example of how we will make all the rules / stubs.
	 */
	private void variableDeclarationTail() {
		switch (lookahead) {
		// rule 7
		case MP_IDENTIFIER:
			variableDeclaration();
			match(Token.MP_SCOLON);
			variableDeclarationTail();
			break;
		// rule 8
		default:
			return;
		}
	}
	
	private void type() {
		switch (lookahead) {
		// rule 10
		case MP_INTEGER:
			match();
			return;
			// rule 11
		case MP_FLOAT:
			match();
			return;
			// rule 12
		case MP_STRING:
			match();
			return;
			// rule 13
		case MP_BOOLEAN:
			match();
			return;
		// rule 8
		default:
			error();
		}
	}
}
