package lexer;

public class LexemeFilter implements LexemeProvider {
	private final LexemeProvider source;
	private final Token target;

	public LexemeFilter(LexemeProvider source, Token target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public Lexeme getNext() {
		while(true) {
			Lexeme current = source.getNext();
			if (current.getToken() != target) {
				return current;
			}
		}
	}
}