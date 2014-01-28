package lexer;

public enum Token {
	MP_STRING_LIT,
	MP_RUN_STRING, //run-on string Error
	MP_FIXED_LIT,
	MP_FLOAT_LIT,
	MP_INTEGER_LIT,
	EOF, //end of file
	EOL, //end of line
	MP_ERROR //error if cannot find FSA to use, or get an "other" before an accept state in FSA
}
