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
		}
		catch (InvalidPathException e) {
			System.out.println("Invalid filename");
			return;
		}
		Scanner scan;
		try {
			scan = Scanner.openFile(path);
		}
		catch (IOException e) {
			System.out.println("Unable to open file");
			return;
		}
		printTokens(scan); 
	}
	
	//print method, Im not sure if this is what you were thinking, but I just sorta winged it. Feel free to make any changes you deem necisary.
	//things that will need to be changed for sure. The token will need to be printed as a string (unless there is a way to do a fancy enum print. Also we will need to have a tag on the EOF token so the loop can end.
	public static void printTokens(Scanner lexScan){
		boolean scannerIsDone = false;
		System.out.println("The following is a list of Lexemes and their content from the given input file.");
		System.out.println("Format: Token Name, Line Number, Column Number, Lexeme content");
		while (!scannerIsDone){ //this condition will most likely need to be altered, but this loop goes untill we have scanned all tokens / file is empty?
		Lexeme curentLexeme = lexScan.getNext(); //gets next lexeme
		System.out.format("%s %d %d %s%n", curentLexeme.getToken(), curentLexeme.getRow(), curentLexeme.getColumn(), curentLexeme.getLexemeContent());
		
		//This is where errors are checked for / printed out.
		if (curentLexeme.getToken() == Token.MP_RUN_STRING){ //if a run-on String is encountered
			//prints out the fact that an run-on String error was encountered at this location
			//print out where the String started, followed by the location of the EOL character, then returns the correct error token
			System.out.print("ERROR: Run-On String Starting on Line: " + curentLexeme.getRow() + ", Column: " +(curentLexeme.getColumn() - curentLexeme.getLexemeContent().length()) + " and ending at the ");
			System.out.println("EOL char on Line: " +curentLexeme.getRow() + ", Column: " + curentLexeme.getColumn());
		}
		
		//this checks for End of File token, if EOF is found, stop scanning.
		if (curentLexeme.getToken() == Token.EOF){ //This will need to be changed so it works, but the logic is there, when the token "end of file" is found, the printing loop stops
			scannerIsDone = true;
		}
		}
	}
}