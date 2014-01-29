package lexer;

public class Lexeme {
	Token token;
	String content;
	int row;
	int col;
	
	public Token getToken() {
		return token;
	}
	public String getLexemeContent() {
		return content;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return col;
	}
}
