package lexer;

import java.io.*;
import java.nio.file.*;

public class Scanner {
	private ScannerStream stream;

	Scanner(byte[] input) {
		stream = new ScannerStream(input);
	}

	public Lexeme getNext() {
		// Clear whitespace
		while (true) {
			if (stream.isFinished()) {
				stream.lexemeStart();
				stream.mark(Token.MP_EOF);
				return stream.emit();
			}
			else if (isWhitespace(stream.peek())) {
				stream.next();
			}
			else {
				break;
			}
		}

		// Start recording lexeme
		stream.lexemeStart();
		// Select fsa to use by first character
		byte first = stream.peek();
		if (first == '\'')
			return scanString();
		else if (Character.isDigit(first))
			return scanNumbers();
		else if (Character.isLetter(first))
			return scanIdentifier();
		else if (first == '{')
			return scanComment();
		else
			return scanSymbol();
	}

	/*
	 * Determines whether there is a run-on comment and if so, the token
	 * is marked as such and an error message is done.
	 */
	private Lexeme scanComment() {
		int state = 0;
		for (byte next : stream) {
			switch (state) {
			case 0:
				// Looks for the beginning comment token
				if (next == '{') {
					state = 1;
					break;
				} else
					return stream.emit();
			case 1:
				// Looks for ending token
				if (next == '}') {
					stream.mark(Token.MP_COMMENT);
					return stream.emit();
				}
				break;
			}
		}
		stream.mark(Token.MP_RUN_COMMENT);
		return stream.emit();
	}

	/*
	 * Scans an identifier. The byte is checked to see if it is a digit or String. 
	 * This is a simple FSA that identifies the next byte and concatenates them
	 * all together.
	 */
	private Lexeme scanIdentifier() {  
		int state = 0;
		for (byte next : stream){
			switch (state){
			case 0: //FSA start state
				if (Character.isLetter(next) || next == '_') { 
					state = 1;
					stream.mark(Token.MP_IDENTIFIER);
				} else return stream.emit();
					break; 
			case 1:
				
				if (Character.isLetter(next) || Character.isDigit(next)){ 
					state = 1;
					stream.mark(Token.MP_IDENTIFIER);
				} else if (next == '_') {
					state = 2;
					stream.mark(Token.MP_IDENTIFIER);
				} else { 
					return stream.emit();
				}
			case 2: 
				if(Character.isLetter(next) || Character.isDigit(next)) {
					state = 2;
					stream.mark(Token.MP_IDENTIFIER);
				} else {
					return stream.emit();
				}
			}
		}
		return stream.emit();
	}
	
	private Lexeme scanReservedWords() {
			switch (Character.toLowerCase(stream.next())){
			case 'a':
				if(Character.toLowerCase(stream.next()) == 'n') {
					if(Character.toLowerCase(stream.next()) == 'd') {
						stream.mark(Token.MP_AND);
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'b':
				if(Character.toLowerCase(stream.next()) == 'e') {
					if(Character.toLowerCase(stream.next()) == 'g') {
						if(Character.toLowerCase(stream.next()) == 'i') {
							if(Character.toLowerCase(stream.next()) == 'n') {
								stream.mark(Token.MP_BEGIN);
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'd':
				if(Character.toLowerCase(stream.next()) == 'i') {
					if(Character.toLowerCase(stream.next()) == 'v') {
						stream.mark(Token.MP_DIV);
					} else return stream.emit();
				} else if (Character.toLowerCase(stream.next()) == 'o') {
					stream.mark(Token.MP_DO);
					if(Character.toLowerCase(stream.next()) == 'w') {
						if(Character.toLowerCase(stream.next()) == 'n') {
							if(Character.toLowerCase(stream.next()) == 't') {
								if(Character.toLowerCase(stream.next()) == 'o') {
									stream.mark(Token.MP_DOWNTO);
								} else return stream.emit();
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'e': 
				if(Character.toLowerCase(stream.next()) == 'l') {
					if(Character.toLowerCase(stream.next()) == 's') {
						if(Character.toLowerCase(stream.next()) == 'e') {
							stream.mark(Token.MP_ELSE);
						} else return stream.emit();
					} else return stream.emit();
				} else if (Character.toLowerCase(stream.next()) == 'n') { 
					if(Character.toLowerCase(stream.next()) == 'd') {
						stream.mark(Token.MP_END);
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'f': 
				if(Character.toLowerCase(stream.next()) == 'i') {
					if(Character.toLowerCase(stream.next()) == 'x') {
						if(Character.toLowerCase(stream.next()) == 'e') {
							if(Character.toLowerCase(stream.next()) == 'd') {
								stream.mark(Token.MP_FIXED);
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else if (Character.toLowerCase(stream.next()) == 'l') {
					if(Character.toLowerCase(stream.next()) == 'o') {
						if(Character.toLowerCase(stream.next()) == 'a') {
							if(Character.toLowerCase(stream.next()) == 't') {
								stream.mark(Token.MP_FLOAT);
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else if (Character.toLowerCase(stream.next()) == 'o') {
					if(Character.toLowerCase(stream.next()) == 'r') {
						stream.mark(Token.MP_FOR); 
					} else return stream.emit();
				} else if (Character.toLowerCase(stream.next()) == 'u') { 
					if(Character.toLowerCase(stream.next()) == 'n') {
						if(Character.toLowerCase(stream.next()) == 'c') {
							if(Character.toLowerCase(stream.next()) == 't') {
								if(Character.toLowerCase(stream.next()) == 'i') {
									if(Character.toLowerCase(stream.next()) == 'o') {
										if(Character.toLowerCase(stream.next()) == 'n') {
											stream.mark(Token.MP_FUNCTION);
										} else return stream.emit();
									} else return stream.emit();
								} else return stream.emit();
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'i': 
				if(Character.toLowerCase(stream.next()) == 'f') {
					stream.mark(Token.MP_IF);
				} else if (Character.toLowerCase(stream.next()) == 'n') {
					if(Character.toLowerCase(stream.next()) == 't') {
						if(Character.toLowerCase(stream.next()) == 'e') {
							if(Character.toLowerCase(stream.next()) == 'g') {
								if(Character.toLowerCase(stream.next()) == 'e') {
									if(Character.toLowerCase(stream.next()) == 'r') {
										stream.mark(Token.MP_INTEGER);
									} else return stream.emit();
								} else return stream.emit();
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'm': 
				if(Character.toLowerCase(stream.next()) == 'o') {
					if(Character.toLowerCase(stream.next()) == 'd') {
						stream.mark(Token.MP_MOD);
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'n': 
				if(Character.toLowerCase(stream.next()) == 'o') {
					if(Character.toLowerCase(stream.next()) == 't') {
						stream.mark(Token.MP_NOT);
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'o': 
				if(Character.toLowerCase(stream.next()) == 'r') {
					stream.mark(Token.MP_OR);
				} else return stream.emit();
				break;
			case 'p': 
				if(Character.toLowerCase(stream.next()) == 'r') {
					if(Character.toLowerCase(stream.next()) == 'o') {
						if(Character.toLowerCase(stream.next()) == 'c') {
							if(Character.toLowerCase(stream.next()) == 'e') {
								if(Character.toLowerCase(stream.next()) == 'd') {
									if(Character.toLowerCase(stream.next()) == 'u') {
										if(Character.toLowerCase(stream.next()) == 'r') {
											if (Character.toLowerCase(stream.next()) == 'e') {
												stream.mark(Token.MP_PROCEDURE);
											} else return stream.emit();
										} else return stream.emit();
									} else return stream.emit();
								} else return stream.emit();
							} else return stream.emit();
						} else if(Character.toLowerCase(stream.next()) == 'g') {
							if(Character.toLowerCase(stream.next()) == 'r') {
								if(Character.toLowerCase(stream.next()) == 'a') {
									if (Character.toLowerCase(stream.next()) == 'm') {
										stream.mark(Token.MP_PROGRAM);
									} else return stream.emit();
								} else return stream.emit();
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'r': 
				if(Character.toLowerCase(stream.next()) == 'e') {
					if(Character.toLowerCase(stream.next()) == 'a') {
						if(Character.toLowerCase(stream.next()) == 'd') {
							stream.mark(Token.MP_READ);
						} else return stream.emit();
					} else if(Character.toLowerCase(stream.next()) == 'p') {
						if(Character.toLowerCase(stream.next()) == 'e') {
							if(Character.toLowerCase(stream.next()) == 'a') {
								if(Character.toLowerCase(stream.next()) == 't') {
									stream.mark(Token.MP_REPEAT);
								} else return stream.emit();
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 't': 
				if(Character.toLowerCase(stream.next()) == 'o') {
					stream.mark(Token.MP_TO);
				} else if(Character.toLowerCase(stream.next()) == 'h') {
					if(Character.toLowerCase(stream.next()) == 'e') {
						if(Character.toLowerCase(stream.next()) == 'n') {
							stream.mark(Token.MP_THEN);
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'u': 
				if(Character.toLowerCase(stream.next()) == 'n') {
					if(Character.toLowerCase(stream.next()) == 't') {
						if(Character.toLowerCase(stream.next()) == 'i') { 
							if(Character.toLowerCase(stream.next()) == 'l') {
								stream.mark(Token.MP_UNTIL);
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'v': 
				if(Character.toLowerCase(stream.next()) == 'a') {
					if(Character.toLowerCase(stream.next()) == 'r') {
						stream.mark(Token.MP_VAR);
					} else return stream.emit();
				} else return stream.emit();
				break;
			case 'w': 
				if(Character.toLowerCase(stream.next()) == 'h') {
					if(Character.toLowerCase(stream.next()) == 'i') {
						if(Character.toLowerCase(stream.next()) == 'l') { 
							if(Character.toLowerCase(stream.next()) == 'e') {
								stream.mark(Token.MP_WHILE);
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else if(Character.toLowerCase(stream.next()) == 'r') {
					if(Character.toLowerCase(stream.next()) == 'i') {
						if(Character.toLowerCase(stream.next()) == 't') { 
							if(Character.toLowerCase(stream.next()) == 'e') {
								stream.mark(Token.MP_WRITE);
							} else return stream.emit();
						} else return stream.emit();
					} else return stream.emit();
				} else return stream.emit();
				break;
			default: stream.mark(Token.MP_ERROR); break;
			}
		
		return stream.emit();
	}

	/*-brad
	 * FSA implementation for Numbers (combined, Integer_literal, Fixed_literal, and Float_Literal
	 * */
	private Lexeme scanNumbers() {
		int state = 0;
		for (byte next : stream){
			switch (state){
			case 0: //FSA start state
				if (Character.isDigit(next)){ //if next is a digit
					state = 1;
					stream.mark(Token.MP_INTEGER_LIT);
				}
				else return stream.emit();
				break;
			case 1: //state 1 in FSA
				
				if (Character.isDigit(next)){
					state = 1;
					stream.mark(Token.MP_INTEGER_LIT);
				}
				else if (next == '.'){
					state = 2;
				}
				else if (next == 'e' || next == 'E'){
					state = 4;
				}
				else return stream.emit();
				break;
			case 2: //state 2 in FSA
				if (Character.isDigit(next)){
					state = 3;
					stream.mark(Token.MP_FIXED_LIT);
				}else return stream.emit();
				break;
			case 3: //state 3 in FSA accept state for fixed literal
				
				if (Character.isDigit(next)){
					state = 3;
					stream.mark(Token.MP_FIXED_LIT);
				}
				else if (next == 'e' || next == 'E'){
					state = 4;
				}
				else return stream.emit();
				break;
			case 4://state 4
				if (next == '+' || next == '-'){
					state = 5;
				}
				else if (Character.isDigit(next)){ //if no + or - (empty string in FSA)
					state = 6;
					stream.mark(Token.MP_FLOAT_LIT);
				}
				else return stream.emit();;
				break;
			case 5: //state 5
				if (Character.isDigit(next)){
					state = 6;
					stream.mark(Token.MP_FLOAT_LIT);
				}
				else return stream.emit();
				break;
			case 6: // state 6 in FSA / accept state for Float
				
				if (Character.isDigit(next)){
					state = 6;
					stream.mark(Token.MP_FLOAT_LIT);
				}
				else return stream.emit();
				break;
			}
		}
		return stream.emit();
	}
	
	/*-brad
	 * FSA implementation for String
	 * */
	//Method for string FSA
	private Lexeme scanString() {
		int state = 0;
		for (byte next : stream) {
			switch (state) {
			case 0: // FSA start state
				if (next == '\'') { // how do you denot apostraphy and make it
									// work?
					state = 1;
				} else
					return alterStringContents(stream.emit());
				break;
			case 1:
				if (next == '\'') {
					stream.mark(Token.MP_STRING_LIT);
					state = 2;
				} else if (next != '\n' && next != '\'') {
					state = 1;
				} else if (next == '\n') { // if EOL char is found before string
											// is closed
					state = 3;
					stream.mark(Token.MP_RUN_STRING);
				} else
					return stream.emit();
				break;
			case 2:
				if (next == '\'') {
					state = 1;
				} else { // remove leading and trailing "'" mark
					return alterStringContents(stream.emit());
				}
				break;
			case 3: // if EOL is found before closing of string, token is a
					// run-on string error
				// the actual printing of error statements and such is done in
				// the Printer method in MP.java
				return stream.emit();
			}

		}
		return stream.emit();
	}
	
	//this method, takes a Lexeme, and alters the lexemeContents to exclude the leading and trailing apostrophes, as well as the double apostrophes in middle of string
	//returns lexeme with altered internal content.
	private Lexeme alterStringContents(Lexeme inLexeme){
		String curLexemeContent = inLexeme.getLexemeContent();
		//sets the lexemeContent of the current lexeme, to the altered string (after removal of leading and trailing "'")
		curLexemeContent = curLexemeContent.substring(1, curLexemeContent.length()-1);
		//replaces all occurence's of "''" with "'" (a single apostrophe instead of two of them)
		inLexeme.content=(curLexemeContent.replace("''", "'"));
		return inLexeme;
	}

	private Lexeme scanSymbol() {
		switch (stream.next()) {
		case '<':
			stream.mark(Token.MP_LTHAN);
			switch (stream.next()) {
			case '>': stream.mark(Token.MP_NEQUAL); break;
			case '=': stream.mark(Token.MP_LEQUAL); break;
			} break;
		case '>':
			stream.mark(Token.MP_GTHAN);
			switch (stream.next()) {
			case '=': stream.mark(Token.MP_GEQUAL); break;
			} break;
		case ':':
			stream.mark(Token.MP_COLON);
			switch (stream.next()) {
			case '=': stream.mark(Token.MP_ASSIGN); break;
			} break;
		case '/': stream.mark(Token.MP_FLOAT_DIVIDE); break;
		case '(': stream.mark(Token.MP_RPAREN); break;
		case ')': stream.mark(Token.MP_LPAREN); break;
		case '=': stream.mark(Token.MP_EQUAL); break;
		case '+': stream.mark(Token.MP_PLUS); break;
		case '-': stream.mark(Token.MP_MINUS); break;
		case '*': stream.mark(Token.MP_TIMES); break;
		case ';': stream.mark(Token.MP_SCOLON); break;
		case '.': stream.mark(Token.MP_PERIOD); break;
		case ',': stream.mark(Token.MP_COMMA); break;
		default:  stream.mark(Token.MP_ERROR); break;
		}
		return stream.emit();
	}

	public static Scanner openFile(Path path) throws IOException {
		return new Scanner(Files.readAllBytes(path));
	}

	private static boolean isWhitespace(byte b) {
		return b == ' ' || b == '\t' || b == '\n' || b == '\r';
	}
}
