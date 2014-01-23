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
		// if (isNumeric(first))
		//   return scanNumeric();
		// if (first = '"')
		//   return scanString();
		// ...
		// else
		throw new RuntimeException("Unable to find valid machine, and error handling does not work yet");
	}
	
	/*
	 *  scanSomething() {
	 *  	int state = 0; // or enum
	 *  	for (byte next : stream) {
	 *  		switch state {
	 *  		case 0:
	 *  			if (isNumeric(byte))
	 *  				...
	 *  		case (valid):
	 *  			stream.mark(MyToken);
	 *  			break;
	 *  		case (terminating):
	 *  			return stream.emit(); // it returns to the last marked spot
	 *  		}
	 *  	}
	 *  }
	 */
	
	public static Scanner openFile(Path path) throws IOException {
		return new Scanner(Files.readAllBytes(path));
	}
	
	private static boolean isWhitespace(byte b) {
		return b == ' ' || b == '\t' || b == '\n' || b == '\r';
	}
}
