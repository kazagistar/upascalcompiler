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
	//lookaheads for block include: {MP_FUNCTION, MP_BEGIN, MP_PROCEDURE, MP_VAR}
	private void block() {
		switch (lookahead) {
		// rule 4
		case MP_VAR:
			variableDeclarationPart();
			procedureAndFunctionDeclarationPart();
			statementPart();
			return;
		case MP_FUNCTION:
			variableDeclarationPart();
			procedureAndFunctionDeclarationPart();
			statementPart();
			return;
		case MP_BEGIN:
			variableDeclarationPart();
			procedureAndFunctionDeclarationPart();
			statementPart();
			return;
		case MP_PROCEDURE:
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
	//Lookaheads for rule 31 include {MP_IDENTIFIER, MP_BEGIN, MP_END, MP_FOR, MP_IF, MP_READ, MP_REPEAT, MP_UNTIL, MP_WHILE, MP_WRITE, MP_WRITELN}
	//31	31	StatementSequence => 	Statement StatementTail
	private void statementSequence() {
		switch (lookahead) {
		// rule 31
		case MP_IDENTIFIER: 
			statement();
			statementTail();
			return;
		case MP_BEGIN: 
			statement();
			statementTail();
			return;
		case MP_END: 
			statement();
			statementTail();
			return;
		case MP_FOR: 
			statement();
			statementTail();
			return;
		case MP_IF: 
			statement();
			statementTail();
			return;
		case MP_READ: 
			statement();
			statementTail();
			return;
		case MP_REPEAT: 
			statement();
			statementTail();
			return;
		case MP_UNTIL: 
			statement();
			statementTail();
			return;
		case MP_WHILE: 
			statement();
			statementTail();
			return;
		case MP_WRITE: 
			statement();
			statementTail();
			return;
		case MP_WRITELN: 
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
		//rule 37 MP_write or MP_WRITELN
		case MP_WRITE:
			writeStatement();
			return;
		case MP_WRITELN:
			writeStatement();
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
	// 45 ReadStatement
	private void readStatement() {
		switch (lookahead) {
		// rule 45
		case MP_READ: 
			match();
			readParameter();
			readParameterTail();
			return;
		default:
			error();
		}
	}

	// 46 ReadParameterTail
	private void readParameterTail() {
		switch (lookahead) {
		// rule 46
		case MP_COMMA: 
			match();
			readParameter();
			readParameterTail();
			return;
			// EmptyString Rule 47
		default:
			return;
		}
	}

	// 48 ReadParameterTail
	private void readParameter() {
		switch (lookahead) {
		// rule 48
		case MP_IDENTIFIER: 
			match();
			variableIdentifier();
			return;
		default:
			error();
		}
	}

	// 49 WriteStatement
	private void writeStatement() {
		switch (lookahead) {
		// rule 49
		case MP_WRITE: 
			match();
			match(Token.MP_LPAREN);
			writeParameter();
			writeParameterTail();
			match(Token.MP_RPAREN);
			return;
		// rule 50
		case MP_WRITELN:
			match();
			match(Token.MP_LPAREN);
			writeParameter();
			writeParameterTail();
			match(Token.MP_RPAREN);
			return;
		default:
			error();
		}
	}
	
	// 51 WriteParameterTail
	private void writeParameterTail() {
		switch (lookahead) {
		// rule 51
		case MP_COMMA: 
			match();
			writeParameter();
			writeParameterTail();
			return;
			// Rule 52 empty string
		case MP_RPAREN:
			match();
			writeParameter();
			writeParameterTail();
			return;
		default: 
			return;
		}
	}
	// 53 WriteParameter
	private void writeParameter() {
		switch (lookahead) {
		// rule 53
		case MP_IDENTIFIER: 
			match();
			ordinalExpression();
			return;
		case MP_PLUS:
			match();
			ordinalExpression();
			return;
		case MP_FALSE:
			match();
			ordinalExpression();
			return;
		case MP_NOT:
			match();
			ordinalExpression();
			return;
		case MP_TRUE:
			match();
			ordinalExpression();
			return;
		case MP_INTEGER_LIT:
			match();
			ordinalExpression();
			return;
		case MP_FLOAT_LIT:
			match();
			ordinalExpression();
			return;
		case MP_STRING_LIT:
			match();
			ordinalExpression();
			return;
		case MP_LPAREN:
			match();
			ordinalExpression();
			return;
		case MP_MINUS:
			match();
			ordinalExpression();
			return;
		default: 
			error();
		}
	}
	// 54 AssignmentStatement
	private void assignmentStatement() {
		switch (lookahead) {
		// rule 54
		case MP_IDENTIFIER: 
			match();
			variableIdentifier();
			match(Token.MP_ASSIGN);
			expression();
			return;
		case MP_FUNCTION:
			match();
			functionIdentifier();
			match(Token.MP_ASSIGN);
			expression();
			return;
		default:
			error();
		}
	}

	// 56 ifStatement
	private void ifStatement() {
		switch(lookahead) {
		// rule 56
		case MP_IF:
			match();
			booleanExpression();
			match(Token.MP_THEN);
			statement();
			optionalElsePart();
			return;
		default:
			error();
		}
	}

	// 57 optionalElsePart
	private void optionalElsePart() {
		switch(lookahead) {
		// rule 57
		case MP_ELSE:
			match();
			statement();
			return;
		default:
			return;
		}
	}

	// 59 repeatStatement
	private void repeatStatement() {
		switch(lookahead) {
		// rule 59
		case MP_REPEAT:
			match();
			statementSequence();
			match(Token.MP_UNTIL);
			booleanExpression();
			return;
		default:
			error();
		}
	}

	// 60 whileStatement
	private void whileStatement() {
		switch(lookahead) {
		// rule 60
		case MP_WHILE:
			match();
			booleanExpression();
			match(Token.MP_DO);
			statement();
			return;
		default:
			error();
		}
	}

	// 61 forStatement
	private void forStatement() {
		switch(lookahead) {
		// rule 56
		case MP_FOR:
			match();
			controlVariable();
			match(Token.MP_ASSIGN);
			initialValue();
			stepValue();
			finalValue();
			match(Token.MP_DO);
			statement();
			return;
		default:
			error();
		}
	}

	// 62 controlVariable
	private void controlVariable() {
		switch(lookahead) {
		// rule 59
		case MP_IDENTIFIER:
			match();
			variableIdentifier();
			return;
		default:
			error();
		}
	}

	// 63 initialValue
	private void initialValue() {
		switch(lookahead) {
		// Rule 63
		case MP_IDENTIFIER:
			match();
			ordinalExpression();
			return;
		case MP_FALSE: 
			match();
			ordinalExpression();
			return;
		case MP_NOT:
			match();
			ordinalExpression();
			return;
		case MP_TRUE:
			match();
			ordinalExpression();
			return;
		case MP_INTEGER_LIT:
			match();
			ordinalExpression();
			return;
		case MP_FLOAT_LIT:
			match();
			ordinalExpression();
			return;
		case MP_STRING_LIT:
			match();
			ordinalExpression();
			return;
		case MP_LPAREN:
			match();
			ordinalExpression();
			return;
		case MP_MINUS:
			match();
			ordinalExpression();
			return;
		case MP_PLUS:
			match();
			ordinalExpression();
			return;
		default:
			error();
		}
	}

	// 64 stepValue
	private void stepValue() {
		switch(lookahead) {
		// Rule 63
		case MP_TO: 
			match();
			return;
		case MP_DOWNTO:
			match();
			return;
		default:
			error();
		}
	}

	// 66 finalValue
	private void finalValue() {
		switch(lookahead) {
		// Rule 66
		case MP_IDENTIFIER: 
			match();
			ordinalExpression();
			return;
		case MP_FALSE: 
			match();
			ordinalExpression();
			return;
		case MP_NOT:
			match();
			ordinalExpression();
			return;
		case MP_TRUE:
			match();
			ordinalExpression();
			return;
		case MP_INTEGER_LIT:
			match();
			ordinalExpression();
			return;
		case MP_FLOAT_LIT:
			match();
			ordinalExpression();
			return;
		case MP_STRING_LIT:
			match();
			ordinalExpression();
			return;
		case MP_LPAREN:
			match();
			ordinalExpression();
			return;
		case MP_MINUS:
			match();
			ordinalExpression();
			return;
		case MP_PLUS:
			match();
			ordinalExpression();
			return;
		default:
			error();
		}
	}
	// 67 procedureStatement
	private void procedureStatement() {
		switch(lookahead) {
		// rule 67
		case MP_PROCEDURE:
			match();
			procedureIdentifier();
			optionalActualParameterList();
			return;
		default:
			error();
		}
	}

	// 68 optionalActualParameterList
	private void optionalActualParameterList() {
		switch(lookahead) {
		// Rule 69
		case MP_LPAREN:
			match();
			actualParameter();
			actualParameterTail();
			match(Token.MP_RPAREN);
			return;
		default:
			return;
		}
	}

	// 70 actualParameterTail
	private void actualParameterTail() {
		switch(lookahead) {
		// Rule 70
		case MP_COMMA:
			match();
			actualParameter();
			actualParameterTail();
			match(Token.MP_RPAREN);
			return;
		default:
			return;
		}
	}

	// 72 actualParameter
	private void actualParameter() {
		switch(lookahead) {
		// Rule 72
		case MP_IDENTIFIER: // may need fixing
			match();
			ordinalExpression();
			return;
		case MP_FALSE: 
			match();
			ordinalExpression();
			return;
		case MP_NOT:
			match();
			ordinalExpression();
			return;
		case MP_TRUE:
			match();
			ordinalExpression();
			return;
		case MP_INTEGER_LIT:
			match();
			ordinalExpression();
			return;
		case MP_FLOAT_LIT:
			match();
			ordinalExpression();
			return;
		case MP_STRING_LIT:
			match();
			ordinalExpression();
			return;
		case MP_LPAREN:
			match();
			ordinalExpression();
			return;
		case MP_MINUS:
			match();
			ordinalExpression();
			return;
		case MP_PLUS:
			match();
			ordinalExpression();
			return;
		default:
			error();
		}
	}

	// 73 expression
	private void expression() {
		switch(lookahead) {
		// Rule 73
		case MP_IDENTIFIER: 
			match(); 
			simpleExpression();
			optionalRelationalPart();
			return;
		case MP_MINUS:
			match(); 
			simpleExpression();
			optionalRelationalPart();
			return;
		case MP_PLUS:
			match(); 
			simpleExpression();
			optionalRelationalPart();
			return;
		default:
			error();
		}
	}

	// 74 optionalRelationalPart
	private void optionalRelationalPart() {
		switch(lookahead) {
		// Rule 74
		case MP_IDENTIFIER:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_EQUAL:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_GEQUAL:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_GTHAN:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_LEQUAL:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_LTHAN:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_NEQUAL:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		case MP_RPAREN:
			match(); 
			relationalOperator();
			simpleExpression();
			return;
		default:
			return;
		}
	}
	// 76 relationalOperator
	private void relationalOperator() {
		switch (lookahead) {
		//rule 76
		case MP_EQUAL:
			match();
			return;
			//rule 77
		case MP_LTHAN:
			match();
			return;
			//rule 78
		case MP_GTHAN:
			match();
			return;
			//rule 79
		case MP_LEQUAL:
			match();
			return;
			//rule 80
		case MP_GEQUAL:
			match();
			return;
			//rule 81
		case MP_NEQUAL:
			match();
			return;
		default:
			error();
		}
	}

	// 82 SimpleExpression
	private void simpleExpression() {
		switch(lookahead) {
		// Rule 82
		case MP_MINUS:
			match(); 
			optionalSign();
			term();
			termTail();
			return;
		case MP_PLUS:
			match(); 
			optionalSign();
			term();
			termTail();
			return;
		default:
			error();
		}
	}

	// 83 Term Tail
	private void termTail() {
		switch(lookahead) {
		// Rule 83
		case MP_OR: 
			match(); 
			addingOperator();
			term();
			termTail();
			return;
		case MP_MINUS:
			match(); 
			addingOperator();
			term();
			termTail();
			return;
		case MP_PLUS:
			match(); 
			addingOperator();
			term();
			termTail();
			return;
		default:
			return;
		}
	}

	// 85 optionalSign
	private void optionalSign() {
		switch(lookahead) {
		// Rule 85
		case MP_PLUS: 
			match(); 
			return;
		case MP_MINUS: 
			match(); 
			return;
		default:
			return;
		}
	}

	// 88 addingOperator
	private void addingOperator() {
		switch(lookahead) {
		// Rule 88
		case MP_PLUS: 
			match(); 
			return;
		case MP_MINUS: 
			match(); 
			return;
		case MP_OR: 
			match(); 
			return;
		default:
			error();
		}
	}

	// 91 term
	private void term() {
		switch(lookahead) {
		// Rule 91
		case MP_FALSE:
			match(); 
			factor();
			factorTail();
			return;
		case MP_NOT:
			match(); 
			factor();
			factorTail();
			return;
		case MP_TRUE:
			match(); 
			factor();
			factorTail();
			return;
		case MP_IDENTIFIER:
			match(); 
			factor();
			factorTail();
			return;
		case MP_INTEGER_LIT:
			match(); 
			factor();
			factorTail();
			return;
		case MP_FLOAT_LIT:
			match(); 
			factor();
			factorTail();
			return;
		case MP_STRING_LIT:
			match(); 
			factor();
			factorTail();
			return;
		case MP_LPAREN:
			match(); 
			factor();
			factorTail();
			return;
		default:
			error();
		}
	}

	// 92 FactorTail
	private void factorTail() {
		switch(lookahead) {
		// Rule 92
		case MP_AND:  
			match(); 
			multiplyingOperator();
			factor();
			factorTail();
			return;
		case MP_DIV:
			match(); 
			multiplyingOperator();
			factor();
			factorTail();
			return;
		case MP_MOD:
			match(); 
			multiplyingOperator();
			factor();
			factorTail();
			return;
		case MP_FLOAT_DIVIDE:
			match(); 
			multiplyingOperator();
			factor();
			factorTail();
			return;
		case MP_TIMES:
			match(); 
			multiplyingOperator();
			factor();
			factorTail();
			return;
		default:
			return;
		}
	}

	// 94 multiplying operator
	private void multiplyingOperator() {
		switch(lookahead) {
		// Rule 94
		case MP_TIMES: 
			match(); 
			return;
		case MP_FLOAT_DIVIDE: 
			match(); 
			return;
		case MP_DIV: 
			match(); 
			return;
		case MP_MOD:
			match();
			return;
		case MP_AND:
			match();
			return;
		default:
			error();
		}
	}

	// 99 factor
	private void factor() {
		switch(lookahead) {
		// Rule 99 (How do we handle these next 3 rules 99-102?)
		case MP_INTEGER_LIT: 
			match(); 
			return;
		// Rule 100
		case MP_FLOAT_LIT: 
			match(); 
			return;
		// Rule 101
		case MP_STRING_LIT: 
			match(); 
			return;
		// Rule 102
		case MP_TRUE:
			match();
			return;
		// Rule 103
		case MP_FALSE:
			match();
			return;
		// Rule 104
		case MP_NOT:
			match();
			factor();
			return;
		// Rule 105
		case MP_LPAREN:
			match();
			expression();
			match(Token.MP_RPAREN);
			return;
		// Rule 106
		case MP_IDENTIFIER:
			match();
			functionIdentifier();
			optionalActualParameterList();
			return;
		default:
			error();
		}
	}

	// 107 Program Identifier
	private void programIdentifier() {
		switch(lookahead) {
		// Rule 107
		case MP_IDENTIFIER: 
			match(); 
			return;
		default:
			error();
		}
	}

	// 108 variable Identifier
	private void variableIdentifier() {
		switch(lookahead) {
		// Rule 108
		case MP_VAR: 
			match(); 
			return;
		default:
			error();
		}
	}

	// 109 procedure Identifier
	private void procedureIdentifier() {
		switch(lookahead) {
		// Rule 109
		case MP_PROCEDURE: 
			match(); 
			return;
		default:
			error();
		}
	}

	// 110 function Identifier
	private void functionIdentifier() {
		switch(lookahead) {
		// Rule 110
		case MP_FUNCTION: 
			match(); 
			return;
		default:
			error();
		}
	}

	// 111 boolean expression
	private void booleanExpression() {
		switch(lookahead) {
		// Rule 111
		case MP_MINUS: 
			match(); 
			expression();
			return;
		// Rule 111
		case MP_PLUS:
			match();
			expression();
			return;
		default:
			error();
		}
	}

	// 112 ordinal expression
	private void ordinalExpression() {
		switch(lookahead) {
		// Rule 112
		case MP_MINUS: 
			match(); 
			expression();
			return;
		// Rule 111
		case MP_PLUS:
			match();
			expression();
			return;
		default:
			error();
		}
	}

	// 113 identifierList
	private void identifierList() {
		switch(lookahead) {
		// Rule 113
		case MP_IDENTIFIER: 
			match(); 
			identifierTail();
			return;
		default:
			error();
		}
	}

	// 114 identifierTail
	private void identifierTail() {
		switch(lookahead) {
		// Rule 113
		case MP_COMMA: 
			match(); 
			match(Token.MP_IDENTIFIER);
			identifierTail();
			return;
		case MP_IDENTIFIER:
			match(); 
			match(Token.MP_IDENTIFIER);
			identifierTail();
			return;
		default:
			return;
		}
	}
}
