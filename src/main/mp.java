/**
 * @author andrew.wilson9
 *
 */
package main;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import parser.Parser;
import lexer.*;

public class mp {
	// known edge case: if -o comes after -p, no conflict will be detected

	public static void main(String args[]) {
		PrintWriter tokenWriter = null;
		String codeOutputPath = null;
		PrintWriter codeOutput = null;
		// Create a print writer to output to stdout
		PrintWriter stdout = new PrintWriter(new BufferedOutputStream(System.out));
		LexemeProvider scanner = null;
		boolean verbose = false;
		boolean lexOnly = false;

		// Read in the args one at a time, checking if they match flags
		for (int index = 0; index < args.length; index++) {
			// -v argument: print tokens to stdout
			if (args[index].equals("-v")) {
				verbose = true;
				continue;
			}

			// -l argument: only write tokens, skip parsing and code generation
			if (args[index].equals("-l")) {
				lexOnly = true;
				continue;
			}

			// -h argument: print help and exit
			if (args[index].equals("-h")) {
				errorAndDie("Printing help information...");
			}

			// -t argument: print tokens to file
			if (args[index].equals("-t")) {
				if (++index == args.length) {
					errorAndDie("Filename for writing tokens expected after -t argument");
				}
				tokenWriter = createWriter(args[index], "writing tokens");
				continue;
			}

			// -p argument: write to standard out instead of a file
			if (args[index].equals("-p")) {
				codeOutput = stdout;
				continue;
			}

			// -o argument: print code to a differently named file
			if (args[index].equals("-o")) {
				if (codeOutput != null) {
					errorAndDie("Writing code to both standard output and an output file not supported");
				}
				if (++index == args.length) {
					errorAndDie("Filename for writing code expected after -o argument");
				}
				codeOutputPath = args[index];
				continue;
			}

			// -i argument: read from stdin instead
			if (args[index].equals("-i")) {
				if (scanner != null) {
					errorAndDie("Cannot read from both file and standard input, aborting...");
				}
				try {
					scanner = Scanner.openFile(System.in);
				} catch (IOException e) {
					errorAndDie("Error while reading standard input");
				}
				continue;
			}

			// Default argument: file to read in from (file with code)
			if (scanner != null) {
				// already made scanner, so it must be that an extra parameter
				// was given
				errorAndDie("Unexpected extra parameter \"" + args[index] + "\" found");
			}
			// If we don't have a file to write to yet, generate a good default
			// candidate name
			if (codeOutputPath == null) {
				codeOutputPath = Paths.get(args[index]).getFileName().toString();
				// remove suffix if any
				codeOutputPath = codeOutputPath.replaceFirst("\\.[^\\.]$", ""); 
				codeOutputPath += ".asm"; // add suffix
			}
			try {
				scanner = Scanner.openFile(Paths.get(args[index]));
			} catch (InvalidPathException e) {
				errorAndDie("Invalid path to input file at path \""
						+ args[index] + "\"");
			} catch (IOException e) {
				errorAndDie("Error while reading input file at path  \""
						+ args[index] + "\"");
			}
		}

		// verify that we have a scanner prepared
		if (scanner == null) {
			errorAndDie("Must provide an input file, or -i to read from standard input");
		}

		// Wrap the scanner in a sequence of classes to process the lexemes
		scanner = new LexemeFilter(scanner, Token.MP_COMMENT);
		if (tokenWriter != null) {
			scanner = new LexemePrinter(scanner, tokenWriter);
		}
		if (verbose) {
			scanner = new LexemePrinter(scanner, stdout);
		}
		scanner = new LexemeErrorPrinter(scanner, stdout);

		// if we are only lexing, just do it now directly
		if (lexOnly) {
			while (scanner.getNext().getToken() != Token.MP_EOF);
		} else {
			// Prepare the code output target
			if (codeOutput == null) {
				if (codeOutputPath == null) {
					codeOutputPath = "a.asm";
				}
				codeOutput = createWriter(codeOutputPath, "writing the generated assembly");
			}

			// Run the parser
			Parser parser = new Parser(scanner, codeOutput, stdout);
			parser.run();
		}

		// Close output streams
		stdout.close();
		if (codeOutput != null) {
			codeOutput.close();
		}
		if (tokenWriter != null) {
			tokenWriter.close();
		}
	}

	// Error message to print if given invalid parameters
	private static void errorAndDie(String error) {
		System.out.println(error);
		System.out.println("Usage: mp INPUTFILE");
		System.out.println("Optional parameters (enter one at a time):");
		System.out.println("    -v             print tokens to stdout");
		System.out.println("    -t TOKEN_OUT   write tokens to given file");
		System.out.println("    -l             lex only, dont parse");
		System.out.println("    -o CODE_OUT    write code to given file (if not provided, file name will be generated)");
		System.out.println("    -p             instead of writing code to a file, write it to standard output");
		System.out.println("    -i             instead of reading from an input file, read from stdin");
		System.out.println("    -h             print this help message");
		System.exit(1);
	}

	private static PrintWriter createWriter(String path, String purpose) {
		try {
			return new PrintWriter(path, "utf-8");
		} catch (UnsupportedEncodingException e) {
			errorAndDie("Could prepare file for " + purpose + " at \"" + path + "\"");
		} catch (FileNotFoundException e) {
			errorAndDie("Could not find file for " + purpose + " at path \"" + path + "\"");
		}
		return null;
	}
}