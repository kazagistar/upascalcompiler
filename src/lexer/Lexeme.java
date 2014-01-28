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
	//added seter method for content, so that you can alter the Lexeme content for strings (remove leading / trailing apostrphe)
	public void setLexemeContent(String newContent){
		content = newContent;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return col;
	}
}
