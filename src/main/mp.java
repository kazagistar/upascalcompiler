/**
 * @author andrew.wilson9
 *
 */
package main;

import lexer.Lexeme;
import lexer.Scanner;
import lexer.Token;

public class mp {
	

	public static void main(String args[]) {
		//creates scanner
		//Scanner lexScan = new Scanner(byte[]);
	}
	
	//print method, Im not sure if this is what you were thinking, but I just sorta winged it. Feel free to make any changes you deem necisary.
	//things that will need to be changed for sure. The token will need to be printed as a string (unless there is a way to do a fancy enum print. Also we will need to have a tag on the EOF token so the loop can end.
	public void printTokens(Scanner lexScan){
		boolean scannerIsDone = false;
		System.out.println("The following is a list of Lexemes and their content from the given input file.");
		//I dont really know how to format this output, because the milestone no longer has a printout example
		
		while (!scannerIsDone){ //this condition will most likely need to be altered, but this loop goes untill we have scanned all tokens / file is empty?
		Lexeme curentLexeme = lexScan.getNext(); //gets next lexeme
		System.out.format("Token: 'print token name', Lexeme content: %s, Row Number: %d, Column Number: %d%n", curentLexeme.getLexemeContent(), curentLexeme.getRow(), curentLexeme.getColumn());
		
		if (currentLexeme.getToken() = EOF){ //This will need to be changed so it works, but the logic is there, when the token "end of file" is found, the printing loop stops
			scannerIsDone = true;
		}
		}
	}
}