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
				stream.mark(Token.INTEGER);
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
				stream.mark(Token.FIXED);
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
				stream.mark(Token.FLOAT);
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
				else return stream.emit();
				break;
			case 1:
				if (next == '"'){ //im unclear as to what the definition for string says it looks like '''' 
					state = 1;
				}
				else if (next != '\n' && next != '\''){
					state = 1;
				}else if (next == '\''){
					stream.mark(Token.STRING);
					state = 2;
				}else return stream.emit();
			}
		}
		return stream.emit();
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
