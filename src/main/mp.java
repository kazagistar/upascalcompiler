/**
 * @author andrew.wilson9
 *
 */
package main;

import java.io.IOException;
import java.nio.file.*;

import lexer.Lexeme;
import lexer.Scanner;
import lexer.Token;

public class mp {

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Please pass a file to parse");
			return;
		}
		Path path;
		try {
			path = Paths.get(args[0]);
		} catch (InvalidPathException e) {
			System.out.println("Invalid filename");
			return;
		}
		Scanner scan;
		try {
			scan = Scanner.openFile(path);
		} catch (IOException e) {
			System.out.println("Unable to open file");
			return;
		}
		printTokens(scan);
	}

	/*
	 * prints out (and iterates through all) lexemes / tokens, it runs until EOF
	 * token is found
	 * 
	 * Input: Takes a Scanner object output: none
	 */
	public static void printTokens(Scanner lexScan) {
		boolean scannerIsDone = false;

		System.out.println("The following is a list of Lexemes and their content from the given input file.");
		System.out.println("Format: Token Name, Line Number, Column Number, Lexeme content");
		while (!scannerIsDone){ //this condition will most likely need to be altered, but this loop goes untill we have scanned all tokens / file is empty?
		Lexeme curentLexeme = lexScan.getNext(); //gets next lexeme
		System.out.format("%19s  %4d  %3d  %s%n", curentLexeme.getToken(), curentLexeme.getRow(), curentLexeme.getColumn(), curentLexeme.getLexemeContent());
		
		//This is where errors are checked for / printed out.
		if (curentLexeme.getToken() == Token.MP_RUN_STRING){ //if a run-on String is encountered
			//prints out the fact that an run-on String error was encountered at this location
			//print out where the String started, followed by the location of the EOL character, then returns the correct error token
			System.out.print("ERROR: Run-On String Starting on Line: " + curentLexeme.getRow() + ", Column: " +(curentLexeme.getColumn() - curentLexeme.getLexemeContent().length()) + " and ending at the ");
			System.out.println("EOL char on Line: " +curentLexeme.getRow() + ", Column: " + curentLexeme.getColumn());
		}//print out the following if MP_ERROR is found
		else if (curentLexeme.getToken() == Token.MP_ERROR){
			System.out.println("ERROR: No accept states were found before bad input was encountered. Bad char on Line: "+ curentLexeme.getRow()+ " Column: " + curentLexeme.getColumn());
		} // print out an error message for a MP_RUN_COMMENT 
		else if (curentLexeme.getToken() == Token.MP_RUN_COMMENT) {
			System.out.println("ERROR: Run-On Comment Starting on Line: " + curentLexeme.getRow() + ", Column: " + curentLexeme.getColumn());
		}
		
		//this checks for End of File token, if EOF is found, stop scanning.
		if (curentLexeme.getToken() == Token.MP_EOF){ //This will need to be changed so it works, but the logic is there, when the token "end of file" is found, the printing loop stops
			scannerIsDone = true;
		}

		
		}
	}
}