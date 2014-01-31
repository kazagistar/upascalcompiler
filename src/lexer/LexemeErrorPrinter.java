package lexer;

import java.io.PrintWriter;
import java.io.Writer;

public class LexemeErrorPrinter implements LexemeProvider {
	private final LexemeProvider source;
	private final PrintWriter writer;
	
	public LexemeErrorPrinter(LexemeProvider source, Writer writer) {
		this.source = source;
		this.writer = new PrintWriter(writer);
	}
	
	@Override
	public Lexeme getNext() {
		Lexeme current = source.getNext();
		if (current.getToken() == Token.MP_RUN_STRING){ //if a run-on String is encountered
			//prints out the fact that an run-on String error was encountered at this location
			//print out where the String started, followed by the location of the EOL character, then returns the correct error token
			writer.print("SCAN ERROR: Run-On String Starting on Line: " + current.getRow() + ", Column: " +(current.getColumn() - current.getLexemeContent().length()) + " and ending at the ");
			writer.println("EOL char on Line: " +current.getRow() + ", Column: " + current.getColumn());
		}//print out the following if MP_ERROR is found
		else if (current.getToken() == Token.MP_ERROR){
			writer.println("SCAN ERROR: No accept states were found before bad input was encountered. Bad char on Line: "+ current.getRow()+ " Column: " + current.getColumn());
		} // print out an error message for a MP_RUN_COMMENT 
		else if (current.getToken() == Token.MP_RUN_COMMENT) {
			writer.println("SCAN ERROR: Run-On Comment Starting on Line: " + current.getRow() + ", Column: " + current.getColumn());
		}
		if (current.getToken() == Token.MP_EOF) { writer.close(); }
		return current;
	}
}
