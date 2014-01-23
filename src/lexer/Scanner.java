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
		// if (isAlpha(first))
		//   return scanIdentifier();
		// if (isNumeric(first)) //then call numbersFSA method
		//   return scanNumeric();
		// if (first = '"')
		//   return scanString();
		// ...
		// else
		throw new RuntimeException("Unable to find valid machine, and error handling does not work yet");
	}
	
//	
//	   scanSomething() {
//	   	int state = 0; // or enum
//	   	for (byte next : stream) {
//	   		switch state {
//	   		case 0:
//	   			if (isNumeric(byte)) 
//	   				...
//	   		case (valid):
//	   			stream.mark(MyToken);
//	   			break;
//	   		case (terminating):
//	   			return stream.emit(); // it returns to the last marked spot
//	   		}
//	   	}
//	   }
	 
	
	
	
//	/*-brad
//	 * FSA implementation for Numbers (combined, Integer_literal, Fixed_literal, and Float_Literal
//	 * */
//	//Questions to be answered:+_+_+_+_++
//	// How do we do logic with byte? wouldent it be easier to just use char instaed of byte?
//	//when getNext() is called, where does the returned stuff go to?
//	//is this case statement looking correct?
//	//where does the print statement for our tokens go?
//	
//	//i dont know what the fSA methods should return, so I just left it void
//	//Note, in the following method, all boolean methods "isBlank(next)" are there as placeholders for actual logic.
//	private Lexeme scanNumbers(){
//		int state = 0;
//		for (byte next : stream){
//			switch (state){
//			case 0: //FSA start state
//				if (isDigit(next)){ //if next is a digit
//					state = 1;
//				}
//				else state = 7;
//				break;
//			case 1: //state 1 in FSA
//				stream.mark(Integer_Literal);
//				if (isDigit(next)){
//					state = 1;
//				}
//				else if (isPeriod(next)){
//					state = 2;
//				}
//				else if (isExponent(next)){
//					state = 4;
//				}
//				else state = 7;
//				break;
//			case 2: //state 2 in FSA
//				if (isDigit(next)){
//					state = 3;
//				}else state = 8;
//				break;
//			case 3: //state 3 in FSA accept state for fixed literal
//				stream.mark(Fixed_Literal);
//				if (isDigit(next)){
//					state = 3;
//				}
//				else if (isExponent(next)){
//					state = 4;
//				}
//				else state = 9;
//				break;
//			case 4://state 4
//				if (isPlusOrMinus(next)){
//					state = 5;
//				}
//				else if (isDigit(next)){ //if no + or - (empty string in FSA)
//					state = 6;
//				}
//				else state = 11;
//				break;
//			case 5: //state 5
//				if (isDigit(next)){
//					state = 6;
//				}
//				else state = 11;
//				break;
//			case 6: // state 6 in FSA / accept state for Float
//				stream.mark(Float_Literal);
//				if (isDigit(next)){
//					state = 6;
//				}
//				else state = 12;
//				break;
//			case 7: // "other" state for state 1
//				return stream.emit(); // it returns to the last marked spot (-1)
//				break;
//			case 8: // "other" state for state 2
//				return stream.emit(); // it returns to the last marked spot (-2)
//				break;
//			case 9: // "other" state for state 3
//				return stream.emit(); // it returns to the last marked spot (-1)
//				break;
//			case 10: // "other" state for state 4
//				return stream.emit(); // it returns to the last marked spot (-2)
//				break;
//			case 11: // "other" state for state 5
//				return stream.emit(); // it returns to the last marked spot (-3)
//				break;
//			case 12: // "other" state for state 6
//				return stream.emit(); // it returns to the last marked spot (-1)
//				break;
//			}
//		}
//		
//	}

	
	
	
	public static Scanner openFile(Path path) throws IOException {
		return new Scanner(Files.readAllBytes(path));
	}
	
	private static boolean isWhitespace(byte b) {
		return b == ' ' || b == '\t' || b == '\n' || b == '\r';
	}
}
