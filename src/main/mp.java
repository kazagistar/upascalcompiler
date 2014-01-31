/**
 * @author andrew.wilson9
 *
 */
package main;

import java.io.*;
import java.nio.file.*;

import lexer.*;

public class mp {
	

	public static void main(String args[]) {
		Writer tokenWriter = null;
		LexemeProvider scanner = null;
		boolean verbose = false;
		
		// Read in the args one at a time, checking if they match flags
		try {
			int index = 0;
			
			// -v argument: print tokens to stdout
			if (args[index].equals("-v")) {
				verbose = true;
				index++;
			}
			
			// -t argument: print tokens to file
			if (args[index].equals("-t")) {
	    		try {
	        		tokenWriter = new BufferedWriter(new OutputStreamWriter(
	        		          new FileOutputStream(args[++index]), "utf-8"));
	    		}
	    		catch (ArrayIndexOutOfBoundsException e) { errorAndDie("Filename expected following -t"); }
	    	    catch (UnsupportedEncodingException e) { errorAndDie("Could not decode tokenfile at path \"" + args[index] + "\""); }
				catch (FileNotFoundException e) { errorAndDie("Could not find tokenfile at path \"" + args[index] + "\""); }
	    		index++;
			}
			
			// Final argument: input file to parse
			try {
				scanner = Scanner.openFile(Paths.get(args[index]));
			}
			catch (InvalidPathException e) { errorAndDie("Invalid path to input file at path \"" + args[index] + "\""); }
			catch (IOException e) { errorAndDie("Error while reading input file at path  \"" + args[index] + "\""); }
			
		}
		catch (ArrayIndexOutOfBoundsException e) { errorAndDie("Missing input file parameter"); }
		

		scanner = new LexemeFilter(scanner, Token.MP_COMMENT);
		scanner = new LexemeErrorPrinter(scanner, new OutputStreamWriter(System.out));
		if (tokenWriter != null)
			scanner = new LexemePrinter(scanner, tokenWriter);
		if (verbose)
			scanner = new LexemePrinter(scanner, new OutputStreamWriter(System.out));
		
		Lexeme lexeme;
		do {
			lexeme = scanner.getNext();
		} while (lexeme.getToken() != Token.MP_EOF);
	}
	
	private static void errorAndDie(String error) {
		System.out.println(error);
		System.out.println("Usage: mp [-t TOKENFILE] INPUTFILE");
		System.exit(1);
	}
}