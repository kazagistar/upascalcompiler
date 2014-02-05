package parser;

import lexer.Lexeme;
import lexer.LexemeProvider;
import lexer.Token;

public class Parser {
	private LexemeProvider in;
	private Token lookahead;

	public Parser(LexemeProvider in) {
		this.in = in;
		// load the first lookahead
		lookahead = in.getNext().getToken();
	}
	
	// Gets next lookahead item if the specified token matches the lookahead, otherwise throws error.
	private void match(Token matched) {
		if (matched == lookahead)
			match();
		else
			error();
	}
//gets next lookahead item
	private void match() {
		lookahead = in.getNext().getToken();
	}
	
	private void error() {
		throw new RuntimeException("ERROOORRRRR");
	}
	
	
	/*
	 * The lookaheads do not need to be correct at this time, so the gutz of the if statements can just be set to if(TRUE) ... 
	 * this way it will still compile, and we can set the lookaheads later on.
	 * 
	 * At this point all we want is to have the stuff that is inside the case statements. The following is an example of how we will make all the rules / stubs.
	 */
	
	
	private void systemGoal() {
		switch (lookahead) {
		// rule 1
		case MP_PROGRAM:
			program();
			match(Token.MP_EOF);
			return;
		//error case
		default:
			error();
	}
}
	
	//ProgramHeading ";" Block "."
	private void program() {
		switch (lookahead) {
		// rule 2
		case MP_PROGRAM:
			programHeading();
			match(Token.MP_SCOLON);
			block();
			match(Token.MP_PERIOD);
			return;
		//error case
		default:
			error();
	}
}
	//"program" ProgramIdentifier
	private void programHeading() {
		switch (lookahead) {
		// rule 3
		case MP_PROGRAM:
			match();
			programIdentifier();
			return;
		//error case
		default:
			error();
	}
}
	//VariableDeclarationPart ProcedureAndFunctionDeclarationPart StatementPart
	private void block() {
		switch (lookahead) {
		// rule 4
		case MP_VAR:
			variableDeclarationPart();
			procedureAndFunctionDeclarationPart();
			statementPart();
			return;
		//error case
		default:
			error();
	}
}
	//5	5	VariableDeclarationPart 	=>	"var" VariableDeclaration ";" VariableDeclarationTail
	private void variableDeclarationPart() {
		switch (lookahead) {
		// rule 5
		case MP_VAR:
			match();
			variableDeclaration();
			match(Token.MP_SCOLON);
			variableDeclarationTail();
			return;
		//rule 6 (empty string)
		default:
			return;
	}
}
	private void variableDeclarationTail() {
		switch (lookahead) {
		// rule 7
		case MP_IDENTIFIER:
			variableDeclaration();
			match(Token.MP_SCOLON);
			variableDeclarationTail();
			break;
		// rule 8 (empty string)
		default:
			return;
		}
	}
	
	//9	9	VariableDeclaration =>	Identifierlist ":" Type
	private void variableDeclaration() {
		switch (lookahead) {
		// rule 9
		case MP_IDENTIFIER:
			identifierList();
			match(Token.MP_COLON);
			type();
			return;
		//error case
		default:
			error();
	}
}
	private void type() {
		switch (lookahead) {
		// rule 10
		case MP_INTEGER:
			match();
			return;
			// rule 11
		case MP_FLOAT:
			match();
			return;
			// rule 12
		case MP_STRING:
			match();
			return;
			// rule 13
		case MP_BOOLEAN:
			match();
			return;
		// error call
		default:
			error();
		}
	}
	
	//14	14	ProcedureAndFunctionDeclarationPart	=> 	ProcedureDeclaration ProcedureAndFunctionDeclarationPart
	//15	15		=> 	FunctionDeclaration ProcedureAndFunctionDeclarationPart
	//16	16		=> 	empty string 
	private void procedureAndFunctionDeclarationPart() {
		switch (lookahead) {
		// rule 14
		case MP_PROCEDURE:
			procedureDeclaration();
			procedureAndFunctionDeclarationPart();
			return;
		//rule 15
		case MP_FUNCTION:
			functionDeclaration();
			procedureAndFunctionDeclarationPart();
			return;
		//rule 16 (empty string)
		default:
			return;
	}
}
	//17	17	ProcedureDeclaration     =>	ProcedureHeading ";" Block ";"
	private void procedureDeclaration() {
		switch (lookahead) {
		// rule 17
		case MP_PROCEDURE:
			procedureHeading();
			match(Token.MP_SCOLON);
			block();
			match(Token.MP_SCOLON);
			return;
		default:
			error();
	}
}
	//18	18	FunctionDeclaration    => 	FunctionHeading ";" Block ";"
	private void functionDeclaration() {
		switch (lookahead) {
		// rule 18
		case MP_FUNCTION:
			functionHeading();
			match(Token.MP_SCOLON);
			block();
			match(Token.MP_SCOLON);
			return;
		default:
			error();
	}
}
	//19	19	ProcedureHeading     => 	"procedure" procedureIdentifier OptionalFormalParameterList
	private void procedureHeading() {
		switch (lookahead) {
		// rule 19
		case MP_PROCEDURE:
			match();
			procedureIdentifier();
			optionalFormalParameterList();
			return;
		default:
			error();
	}
}
	//20	20	FunctionHeading     => 	"function" functionIdentifier OptionalFormalParameterList Type
	private void functionHeading() {
		switch (lookahead) {
		// rule 20
		case MP_FUNCTION:
			match();
			functionIdentifier();
			optionalFormalParameterList();
			type();
			return;
		default:
			error();
	}
}
	//21	21	OptionalFormalParameterList =>	"(" FormalParameterSection FormalParameterSectionTail ")"
	//22	22	=> 	empty String             
	private void optionalFormalParameterList() {
		switch (lookahead) {
		// rule 21
		case MP_LPAREN:
			match();
			formalParameterSection();
			formalParameterSectionTail();
			match(Token.MP_RPAREN);
			return;
		//rule 22 (empty String)
		default:
			return;
	}
}
	//23	23	FormalParameterSectionTail 	=> 	";" FormalParameterSection FormalParameterSectionTail
	//24	24	=> 	Empty string
	private void formalParameterSectionTail() {
		switch (lookahead) {
		// rule 23
		case MP_SCOLON:
			match();
			formalParameterSection();
			formalParameterSectionTail();
			return;
		//rule 24 (empty String)
		default:
			return;
	}
}
	//25	25	FormalParameterSection   => 	ValueParameterSection
	//26	26	=> 	VariableParameterSection 
	private void formalParameterSection() {
		switch (lookahead) {
		// rule 25
		case MP_IDENTIFIER:
			valueParameterSection();
			return;
		//rule 26
		case MP_VAR:
			variableParameterSection();
			return;
		default:
			error();
	}
}
	//27	27	ValueParameterSection   =>	IdentifierList ":" Type
	private void valueParameterSection() {
		switch (lookahead) {
		// rule 27
		case MP_IDENTIFIER:
			identifierList();
			match(Token.MP_COLON);
			type();
			return;
		default:
			error();
	}
}
	//28	28	VariableParameterSection  => 	"var" IdentifierList ":" Type            
	private void variableParameterSection() {
		switch (lookahead) {
		// rule 28
		case MP_VAR:
			match();
			identifierList();
			match(Token.MP_COLON);
			type();
			return;
		default:
			error();
	}
}
	//29	29	StatementPart  => 	CompoundStatement   
	private void statementPart() {
		switch (lookahead) {
		// rule 29
		case MP_BEGIN:
			compoundStatement();
			return;
		default:
			error();
	}
}
	//30	30	CompoundStatement => 	"begin" StatementSequence "end"
	private void compoundStatement() {
		switch (lookahead) {
		// rule 30
		case MP_BEGIN:
			match();
			statementSequence();
			match(Token.MP_END);
			return;
		default:
			error();
	}
}
	//I was unable to find the correct lookaheadToken for this method, STATEMENT has to many possible outcomes
	
	//31	31	StatementSequence => 	Statement StatementTail
	private void statementSequence() {
		switch (lookahead) {
		// rule 31
		case MP_IDENTIFIER: //dummy Token its not actually supposed to be an Identifier
			statement();
			statementTail();
			return;
		default:
			error();
	}
}
	//32	32	StatementTail  => 	";" Statement StatementTail
	//33	33				 => 	emptyString
	private void statementTail() {
		switch (lookahead) {
		// rule 32
		case MP_SCOLON: 
			match();
			statement();
			statementTail();
			return;
		//rule 33 (empty string / epsilon)
		default:
			return;
	}
}
	//34	34	Statement   	=> 	EmptyStatement
	//35	35			=> 	CompoundStatement
	//36	36			=> 	ReadStatement
	//37	37			=> 	WriteStatement
	//38	38			=> 	AssignmentStatement
	//39	39			=> 	IfStatement
	//40	40			=> 	WhileStatement
	//41	41			=> 	RepeatStatement
	//42	42			=> 	ForStatement
	//43	43			=> 	ProcedureStatement 
	private void statement() {
		switch (lookahead) {
		//rule 35
		case MP_BEGIN:
			compoundStatement();
			return;
		//rule 36
		case MP_READ:
			readStatement();
			return;
		//rule 37 (unsure as to which lookahead it is (either MP_WRITE or MP_WRITELN)
		case MP_WRITE:
			writeStatement();
			return;
		//rule 38
		case MP_IDENTIFIER: //this has the same token terminal as Rule 43, (they are both Identifier idk what to do)
			assignmentStatement();
			return;
		//rule 39
		case MP_IF:
			ifStatement();
			return;
		//rule 40
		case MP_WHILE:
			whileStatement();
			return;
		//rule 41
		case MP_REPEAT:
			repeatStatement();
			return;
		//rule 42
		case MP_FOR:
			forStatement();
			return;
		//rule 43 (has same token (identifier as rule 38, so im using MP_PROCEDURE for the time being)
		case MP_PROCEDURE: //this has the same token terminal as Rule 38, (they are both Identifier idk what to do)
			procedureStatement();
			return;
		// rule 34 (empty statement)
		default:
			emptyStatement();
			return;
	}
}
	//44	44	EmptyStatement  => 	empty string             
	private void emptyStatement() {
		switch (lookahead) {
		// rule 44 (empty String) 
		default:
			return;
	}
}
}
