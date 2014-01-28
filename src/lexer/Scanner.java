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
		while (isWhitespace(stream.peek())) {
			stream.next();
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
	 * Skips through comments and jumps past a line
	 */
	private Lexeme scanComment() {
		for (byte next : stream) { // Need something like this or maybe I'm missing something
			switch (stream.peek()) {
			default:
				getNext();
				break;
			case '\n':      // Skips a line
				return;
			}
		}
	}

	/*
	 * Scans an identifier. The byte is checked to see if it is a digit or String. 
	 * This is a simple FSA that identifies the next byte and concatenates them
	 * all together.
	 */
	private Lexeme scanIdentifier(byte[] string) {  
		int state = 0;
		for (int i = 0; i < string.length;){
			switch (state){
			case 0: //FSA start state
				if (isLetter(string)) { 
					state = 1;
				} else {
					return stream.emit();
					break; 
				}
			case 1:
				if (Character.isLetter(string) || isDigit(string) || string[i] == "_"){ 
					state = 1;
				} else { 
					return stream.emit();
				}
			}
		}
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
				}
				else return stream.emit();
				break;
			case 1: //state 1 in FSA
				stream.mark(Token.MP_INTEGER_LIT);
				if (Character.isDigit(next)){
					state = 1;
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
				}else return stream.emit();
				break;
			case 3: //state 3 in FSA accept state for fixed literal
				stream.mark(Token.MP_FIXED_LIT);
				if (Character.isDigit(next)){
					state = 3;
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
				}
				else return stream.emit();;
				break;
			case 5: //state 5
				if (Character.isDigit(next)){
					state = 6;
				}
				else return stream.emit();
				break;
			case 6: // state 6 in FSA / accept state for Float
				stream.mark(Token.MP_FLOAT_LIT);
				if (Character.isDigit(next)){
					state = 6;
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
	private Lexeme scanString(){
		int state = 0;
		for (byte next : stream){
			switch (state){
			case 0: //FSA start state
				if (next == '\''){ //how do you denot apostraphy and make it work?
					state = 1;
				}
				else return alterStringContents(stream.emit());
				break;
			case 1:
				if (next == '\''){ 
					state = 2;
				}
				else if (next != '\n' && next != '\''){
					state = 1;
				} else if(next == '\n'){ //if EOL char is found before string is closed
					state = 3;
				}
				else return stream.emit();
				break;
			case 2:
				stream.mark(Token.MP_STRING_LIT);
				if (next == '\''){
					state = 1;
				}else{ //remove leading and trailing "'" mark
					return alterStringContents(stream.emit());
				}
				break;
			case 3: //if EOL is found before closing of string, token is an run-on string error
				//NOTE: I just made up error handleing procedure, when we find an error we need to be consistant on how we print it out. the following can be replaced with some other procedure easily.
				stream.mark(Token.MP_RUN_STRING);
				Lexeme temp = stream.emit();
				//prints out the fact that an run-on String error was encountered at this location
				//print out where the String started, followed by the location of the EOL character, then returns the correct error token
				System.out.print("ERROR: Run-On String Starting on Line: " + temp.getRow() + ", Column: " +(temp.getColumn() - temp.getLexemeContent().length()) + " and ending at the ");
				System.out.println("EOL char on Line: " +temp.getRow() + ", Column: " + temp.getColumn());
				return temp;
				}
				
			}
		return stream.emit();
	}
	
	//this method, takes a Lexeme, and alters the lexemeContents to exclude the leading and trailing apostrophes, as well as the double apostrophes in middle of string
	//returns lexeme with altered internal content.
	private Lexeme alterStringContents(Lexeme inLexeme){
		String curLexemeContent = inLexeme.getLexemeContent();
		//sets the lexemeContent of the current lexeme, to the altered string (after removal of leading and trailing "'")
		curLexemeContent = curLexemeContent.substring(1, curLexemeContent.length());
		//replaces all occurences of "''" with "'" (a single apostrophe instead of two of them)
		inLexeme.setLexemeContent(curLexemeContent.replace("''", "'"));
		return inLexeme;
	}

	private Lexeme scanSymbol() {
		throw new RuntimeException("Detected misc, but no FSA implemented yet");
	}

	public static Scanner openFile(Path path) throws IOException {
		return new Scanner(Files.readAllBytes(path));
	}

	private static boolean isWhitespace(byte b) {
		return b == ' ' || b == '\t' || b == '\n' || b == '\r';
	}
}
