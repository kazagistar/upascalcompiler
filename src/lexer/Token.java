package lexer;

public enum Token {
	// Reserved
	MP_AND,
	MP_BEGIN,
	MP_BOOLEAN, //
	MP_DIV,
	MP_DO,
	MP_DOWNTO,
	MP_ELSE,
	MP_END,
	MP_FALSE, //
	MP_FIXED,
	MP_FLOAT,
	MP_FOR,
	MP_FUNCTION,
	MP_IF,
	MP_INTEGER,
	MP_MOD,
	MP_NOT,
	MP_OR,
	MP_PROCEDURE,
	MP_PROGRAM,
	MP_READ,
	MP_REPEAT,
	MP_STRING, //
	MP_THEN,
	MP_TRUE, //
	MP_TO,
	MP_TYPE,
	MP_UNTIL,
	MP_VAR,
	MP_WHILE,
	MP_WRITE,
	MP_WRITELN, //

	// Literals
	MP_IDENTIFIER,
	MP_STRING_LIT,
	MP_FIXED_LIT,
	MP_FLOAT_LIT,
	MP_INTEGER_LIT,
	
	// Special
	MP_COMMENT,
	MP_EOF, //end of file
	
	// Errors
	MP_ERROR, //error if cannot find FSA to use, or get an "other" before an accept state in FSA
	MP_RUN_STRING, // run-on string Error
	MP_RUN_COMMENT, // comment runs past end of file
	
	// Symbols
	MP_PERIOD,
	MP_COMMA,
	MP_SCOLON,
	MP_LPAREN,
	MP_RPAREN,
	MP_EQUAL,
	MP_FLOAT_DIVIDE,
	MP_GTHAN,
	MP_GEQUAL,
	MP_LTHAN,
	MP_LEQUAL,
	MP_NEQUAL,
	MP_ASSIGN,
	MP_PLUS,
	MP_MINUS,
	MP_TIMES,
	MP_COLON
}