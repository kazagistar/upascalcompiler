package lexer;

import java.io.PrintWriter;

public class LexemePrinter implements LexemeProvider {
	private final LexemeProvider source;
	private final PrintWriter writer;
	
	public LexemePrinter(LexemeProvider source, PrintWriter writer) {
		this.source = source;
		this.writer = writer;
	}
	
	@Override
	public Lexeme getNext() {
		Lexeme current = source.getNext();
		writer.format("%-19s  %-4d  %-3d  %s%n", current.getToken(), current.getRow(), current.getColumn(), current.getLexemeContent());
		return current;
	}
}
