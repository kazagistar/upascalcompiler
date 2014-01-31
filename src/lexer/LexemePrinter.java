package lexer;

import java.io.PrintWriter;
import java.io.Writer;

public class LexemePrinter implements LexemeProvider {
	private final LexemeProvider source;
	private final PrintWriter writer;
	
	public LexemePrinter(LexemeProvider source, Writer writer) {
		this.source = source;
		this.writer = new PrintWriter(writer);
	}
	
	@Override
	public Lexeme getNext() {
		Lexeme current = source.getNext();
		writer.format("%19s  %4d  %3d  %s%n", current.getToken(), current.getRow(), current.getColumn(), current.getLexemeContent());
		if (current.getToken() == Token.MP_EOF) { writer.flush(); writer.close(); }
		return current;
	}
}
