package parser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import symbolTable.*;
import lexer.Lexeme;
import lexer.LexemeProvider;
import lexer.Token;

public class Parser {
	private LexemeProvider in;
	private Lexeme lookaheadLexeme;
	private Token lookahead;
	private Lexeme matched;
	private final SymbolTable table;
	private final SemanticAnalysis semantic;
	private final PrintWriter errors;

	public Parser(LexemeProvider in, PrintWriter code, PrintWriter errors) {
		this.in = in;
		this.errors = errors;
		// load the first lookahead
		match();
		table = new SymbolTable();
		semantic = new SemanticAnalysis(code, table);
	}

	public void run() {
		try {
			systemGoal();
		} catch (ParseError e) {
			errors.println(e);
		} catch (SemanticError e) {
			errors.println(e);
		}
	}

	// Gets next lookahead item if the specified token matches the lookahead,
	// otherwise throws error.
	private void match(Token matched) {
		if (matched == lookahead)
			match();
		else
			error(matched);
	}

	// gets next lookahead item
	private void match() {
		matched = lookaheadLexeme;
		lookaheadLexeme = in.getNext();
		lookahead = lookaheadLexeme.getToken();
	}

	// Error expecting a single token
	private void error(Token expected) {
		throw new ParseError(expected, lookaheadLexeme);
	}

	// Error with a custom message
	private void error(String message) {
		throw new ParseError(message, lookaheadLexeme);
	}

	/*
	 * The lookaheads do not need to be correct at this time, so the gutz of the
	 * if statements can just be set to if(TRUE) ... this way it will still
	 * compile, and we can set the lookaheads later on.
	 * 
	 * At this point all we want is to have the stuff that is inside the case
	 * statements. The following is an example of how we will make all the rules
	 * / stubs.
	 */

	private void systemGoal() {
		// rule 1
		program();
		match(Token.MP_EOF);
	}

	// ProgramHeading ";" Block "."
	private void program() {
		// rule 2
		programHeading();
		match(Token.MP_SCOLON);
		Label skipLocation = semantic.generateLabel();
		semantic.goTo(skipLocation);
		block(skipLocation);
		match(Token.MP_PERIOD);
	}

	// "program" ProgramIdentifier
	private void programHeading() {
		// rule 3
		match(Token.MP_PROGRAM);
		programIdentifier();
	}

	// VariableDeclarationPart ProcedureAndFunctionDeclarationPart StatementPart
	// lookaheads for block include: {MP_FUNCTION, MP_BEGIN, MP_PROCEDURE,
	// MP_VAR}
	private void block(Label start) {
		// rule 4
		variableDeclarationPart();
		procedureAndFunctionDeclarationPart();
		//sets the D(n) pointer for each scope, and moves the stack above the symbol table
		semantic.startCalled(start);
		statementPart();
		semantic.endCalled();
		table.destroyScope();
	}

	// 5 5 VariableDeclarationPart => "var" VariableDeclaration ";"
	// VariableDeclarationTail
	private void variableDeclarationPart() {
		switch (lookahead) {
		// rule 5
		case MP_VAR:
			match();
			variableDeclaration();
			match(Token.MP_SCOLON);
			variableDeclarationTail();
			return;
			// rule 6 (empty string)
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

	// 9 9 VariableDeclaration => Identifierlist ":" Type
	private void variableDeclaration() {
		// rule 9
		List<String> idList = identifierList();
		match(Token.MP_COLON);
		Type newType = type();
		
		for (String id : idList) {
			table.addValue(id, newType);
		}
	}

	private Type type() {
		switch (lookahead) {
		// rule 10
		case MP_INTEGER:
			match();
			return Type.Integer;
			// rule 11
		case MP_FLOAT:
			match();
			return Type.Float;
			// rule 12
		case MP_STRING:
			match();
			return Type.String;
			// rule 13
		case MP_BOOLEAN:
			match();
			return Type.Boolean;
			// error call
		default:
			error("Needed to find a type declaration");
			return null;
		}
	}

	private void procedureAndFunctionDeclarationPart() {
		switch (lookahead) {
		// rule 14
		case MP_PROCEDURE:
			procedureDeclaration();
			procedureAndFunctionDeclarationPart();
			return;
			// rule 15
		case MP_FUNCTION:
			functionDeclaration();
			procedureAndFunctionDeclarationPart();
			return;
			// rule 16 (empty string)
		default:
			return;
		}
	}

	private void procedureDeclaration() {
		// rule 17
		procedureHeading();
		match(Token.MP_SCOLON);
		block(table.lookup(table.getCurrentName()).getLocation());
		match(Token.MP_SCOLON);
		// return from procedure: 
	}

	private void functionDeclaration() {
		// rule 18
		functionHeading();
		match(Token.MP_SCOLON);
		block(table.lookup(table.getCurrentName()).getLocation());
		match(Token.MP_SCOLON);
	}

	private void procedureHeading() {
		// rule 19
		match(Token.MP_PROCEDURE);
		String id = procedureIdentifier().getLexemeContent();
		table.addScope(id, Kind.Procedure);
		List<Entry> typeList = new ArrayList<Entry>();
		optionalFormalParameterList(typeList);
		// Create and store label in scope, and then write the "start of call" content
		table.addProcedureParent(id, semantic.generateLabel(), typeList);
	}

	private void functionHeading() {
		// rule 20
		match(Token.MP_FUNCTION);
		String id = functionIdentifier().getLexemeContent();
		table.addScope(id, Kind.Function);
		List<Entry> typeList = new ArrayList<Entry>();
		optionalFormalParameterList(typeList);
		match(Token.MP_COLON);
		Type returnType = type();
		// Create and store label in scope, and then write the "start of call" content
		table.addFunctionParent(id, returnType, semantic.generateLabel(), typeList);
	}

	private void optionalFormalParameterList(List<Entry> typeList) {
		switch (lookahead) {
		// rule 21
		case MP_LPAREN:
			match();
			formalParameterSection(typeList);
			formalParameterSectionTail(typeList);
			match(Token.MP_RPAREN);
			return;
			// rule 22 (empty String)
		default:
			return;
		}
	}

	private void formalParameterSectionTail(List<Entry> typeList) {
		switch (lookahead) {
		// rule 23
		case MP_SCOLON:
			match();
			formalParameterSection(typeList);
			formalParameterSectionTail(typeList);
			return;
			// rule 24 (empty String)
		default:
			return;
		}
	}

	private void formalParameterSection(List<Entry> typeList) {
		switch (lookahead) {
		// rule 25
		case MP_IDENTIFIER:
			valueParameterSection(typeList);
			return;
			// rule 26
		case MP_VAR:
			variableParameterSection(typeList);
			return;
		default:
			error("Expected an identifier or variable");
		}
	}

	private void valueParameterSection(List<Entry> typeList) {
		// rule 27
		List<String> idList = identifierList();
		match(Token.MP_COLON);
		Type newType = type();

		for (String id : idList) {
			typeList.add(table.addValue(id, newType));
		}
	}

	private void variableParameterSection(List<Entry> typeList) {
		match(Token.MP_VAR);
		List<String> idList = identifierList();
		match(Token.MP_COLON);
		Type newType = type();

		for (String id : idList) {
			typeList.add(table.addReference(id, newType));
		}
	}

	private void statementPart() {
		compoundStatement();
	}

	private void compoundStatement() {
		// rule 30
		match(Token.MP_BEGIN);
		
		statementSequence();
		match(Token.MP_END);
	}

	private void statementSequence() {
		statement();
		statementTail();
	}

	private void statementTail() {
		switch (lookahead) {
		// rule 32
		case MP_SCOLON:
			match();
			statement();
			statementTail();
			return;
			// rule 33 (empty string / epsilon)
		default:
			return;
		}
	}

	private void statement() {
		switch (lookahead) {
		// rule 35
		case MP_BEGIN:
			compoundStatement();
			return;
			// rule 36
		case MP_READ:
			readStatement();
			return;
			// rule 37 MP_write or MP_WRITELN
		case MP_WRITE:
		case MP_WRITELN:
			writeStatement();
			return;
			// rule 38/43
			// these two rules have the same lookahead token, but differ by
			// symbol table context
		case MP_IDENTIFIER:
			// if the identifier is a function
			Entry ident = table.lookup(lookaheadLexeme.getLexemeContent());
			if (ident == null) {
				throw new SemanticError("Undeclared identifier ", lookaheadLexeme);
			}
			else if (ident.getKind() == Kind.Procedure){
				procedureStatement(ident);
			}
			else {
			// elseif the identifier is a procedure
				assignmentStatement();
			}
			return;
			// rule 39
		case MP_IF:
			ifStatement();
			return;
			// rule 40
		case MP_WHILE:
			whileStatement();
			return;
			// rule 41
		case MP_REPEAT:
			repeatStatement();
			return;
			// rule 42
		case MP_FOR:
			forStatement();
			return;
			// rule 34 (empty statement)
		default:
			emptyStatement();
			return;
		}
	}

	private void emptyStatement() {
		return;
	}

	private void readStatement() {
		// rule 45
		match(Token.MP_READ);
		match(Token.MP_LPAREN);
		readParameter();
		readParameterTail();
		match(Token.MP_RPAREN);
	}

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

	private void readParameter() {
		// rule 48
		semantic.read(variableIdentifier());
	}

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
			semantic.newLine();
			return;
		default:
			error("Expected write statement");
		}
	}

	private void writeParameterTail() {
		switch (lookahead) {
		// rule 51
		case MP_COMMA:
			match();
			writeParameter();
			writeParameterTail();
			return;
			// Rule 52 empty string
		default:
			return;
		}
	}

	private void writeParameter() {
		// rule 53
		semantic.write(ordinalExpression());
	}

	private void assignmentStatement() {
		// rule 54 or 55
		Lexeme target = variableIdentifier(); // or functionIdentifier
		match(Token.MP_ASSIGN);
		Type resultType = expression();
		semantic.store(target, resultType);
	}

	private void ifStatement() {
		// rule 56
		match(Token.MP_IF);
		Label falseTarget = semantic.generateLabel();
		booleanExpression();
		match(Token.MP_THEN);
		semantic.goToFalse(falseTarget);
		statement();
		optionalElsePart(falseTarget);
	}

	private void optionalElsePart(Label falseTarget) {
		switch (lookahead) {
		// rule 57
		case MP_ELSE:
			match();
			Label endTarget = semantic.generateLabel();
			semantic.goTo(endTarget);
			semantic.writeLabel(falseTarget);
			statement();
			semantic.writeLabel(endTarget);
			return;
			// rule 58
		default:
			semantic.writeLabel(falseTarget);
			return;
		}
	}

	private void repeatStatement() {
		// rule 59
		match(Token.MP_REPEAT);
		Label begTarget = semantic.generateLabel();
		semantic.writeLabel(begTarget);
		statementSequence();
		match(Token.MP_UNTIL);
		booleanExpression();
		semantic.goToFalse(begTarget);
	}

	private void whileStatement() {
		// rule 60
		match(Token.MP_WHILE);
		Label begTarget = semantic.generateLabel();
		semantic.writeLabel(begTarget);
		booleanExpression();
		Label exitTarget = semantic.generateLabel();
		semantic.goToFalse(exitTarget);
		match(Token.MP_DO);
		statement();
		semantic.goTo(begTarget);
		semantic.writeLabel(exitTarget);
	}

	private void forStatement() {
		// rule 61
		match(Token.MP_FOR);
		Lexeme controlVar = controlVariable();
		match(Token.MP_ASSIGN);
		initialValue();
		semantic.store(controlVar, Type.Integer);
		boolean goesUp = stepValue();
		finalValue();
		Label begTarget = semantic.generateLabel();
		semantic.writeLabel(begTarget);
		semantic.duplicate();
		semantic.load(controlVar);
		if(goesUp) {
			semantic.relationalExpression("CMPGES", Type.Integer, Type.Integer, controlVar);
		} else {
			semantic.relationalExpression("CMPLES", Type.Integer, Type.Integer, controlVar);
		}
		Label exitTarget = semantic.generateLabel();
		semantic.goToFalse(exitTarget);
		match(Token.MP_DO);
		statement();
		if(goesUp) {
			semantic.addTo(controlVar, 1);
		} else {
			semantic.addTo(controlVar, -1);
		}
		semantic.goTo(begTarget);
		semantic.writeLabel(exitTarget);
		// Pop final value off stack
		semantic.shiftStack(-1);
	}

	private Lexeme controlVariable() {
		// rule 62
		return variableIdentifier();
	}

	private void initialValue() {
		// Rule 63
		if(ordinalExpression() != Type.Integer) {
			throw new SemanticError("Initial Value in for loop is not an Integer", matched);
		}
	}

	private boolean stepValue() {
		switch (lookahead) {
		// Rule 64
		case MP_TO:
			match();
			return true;
			// Rule 65
		case MP_DOWNTO:
			match();
			return false;
		default:
			error("Expected step value (up to/ down to)");
			return true;
		}
	}

	private void finalValue() {
		// Rule 66
		if(ordinalExpression() != Type.Integer) {
			throw new SemanticError("Final Value in for loop is not an Integer", matched);
		}
		
	}

	private void procedureStatement(Entry entry) {
		// Rule 67
		Lexeme procName = procedureIdentifier();
		semantic.procActivationRecord();
		Iterator<Entry> formalParams = entry.getParamTypes().iterator();
		optionalActualParameterList(formalParams);
		if (formalParams.hasNext()) {
			throw new SemanticError("Not enough parameters for procedure ", procName);
		}
		semantic.call(procName);
	}

	private void optionalActualParameterList(Iterator<Entry> params) {
		switch (lookahead) {
		// Rule 68
		case MP_LPAREN:
			match();
			actualParameter(params);
			actualParameterTail(params);
			match(Token.MP_RPAREN);
			return;
			// Rule 69
		default:
			return;
		}
	}

	private void actualParameterTail(Iterator<Entry> params) {
		switch (lookahead) {
		// Rule 70
		case MP_COMMA:
			match();
			actualParameter(params);
			actualParameterTail(params);
			return;
			// Rule 71
		default:
			return;
		}
	}

	private void actualParameter(Iterator<Entry> params) {
		// Rule 72
		if (!params.hasNext()) {
			throw new SemanticError("Too many parameters ", matched);
		}
		Entry formalParam = params.next();
		if (formalParam.getKind() == Kind.Value) {
			Type result = ordinalExpression();
			if (!semantic.cast(result, formalParam.getReturnType())) {
				throw new SemanticError("Cannot cast " + result + " to a " + formalParam + " ", matched);
			}
		}
		else if (formalParam.getKind() == Kind.Reference) {
			Lexeme var = variableIdentifier();
			semantic.loadReference(var, formalParam.getReturnType());
		}
	}

	private Type expression() {
		// Rule 73
		Type firstType = simpleExpression();
		return optionalRelationalPart(firstType);
	}

	private Type optionalRelationalPart(Type firstType) {
		Lexeme operator;
		Type secondType, castType;
		switch (lookahead) {
		// Rule 74
		case MP_EQUAL:
			operator = relationalOperator();
			secondType = simpleExpression();
			castType = semantic.relationalExpression("CMPEQS", firstType, secondType, operator);
			return castType;
		case MP_GEQUAL:
			operator = relationalOperator();
			secondType = simpleExpression();
			castType = semantic.relationalExpression("CMPGES", firstType, secondType, operator);
			return castType;
		case MP_GTHAN:
			operator = relationalOperator();
			secondType = simpleExpression();
			castType = semantic.relationalExpression("CMPGTS", firstType, secondType, operator);
			return castType;
		case MP_LEQUAL:
			operator = relationalOperator();
			secondType = simpleExpression();
			castType = semantic.relationalExpression("CMPLES", firstType, secondType, operator);
			return castType;
		case MP_NEQUAL:
			operator = relationalOperator();
			secondType = simpleExpression();
			castType = semantic.relationalExpression("CMPNES", firstType, secondType, operator);
			return castType;
		case MP_LTHAN:
			operator = relationalOperator();
			secondType = simpleExpression();
			castType = semantic.relationalExpression("CMPLTS", firstType, secondType, operator);
			return castType;
			// Rule 75
		default:
			return firstType;
		}
	}

	private Lexeme relationalOperator() {
		switch (lookahead) {
		// rule 76
		case MP_EQUAL:
			// rule 77
		case MP_LTHAN:
			// rule 78
		case MP_GTHAN:
			// rule 79
		case MP_LEQUAL:
			// rule 80
		case MP_GEQUAL:
			// rule 81
		case MP_NEQUAL:
			match();
			return matched;
		default:
			error("Needed to find a relational operator");
			return null;
		}
	}

	private Type simpleExpression() {
		// Rule 82
		Lexeme negSign = optionalSign();
		Type firstType = term();
		if (negSign != null) {
			semantic.negate(firstType, negSign);
		}
		return termTail(firstType);
	}

	private Type termTail(Type firstType) {
		Lexeme operator;
		Type secondType, castType;
		
		switch (lookahead) {
		// Rule 83
		case MP_OR:
			operator = addingOperator();
			secondType = term();
			castType = semantic.booleanExpression("ORS", firstType, secondType, operator);
			return termTail(castType);
		case MP_MINUS:
			operator = addingOperator();
			secondType = term();
			castType = semantic.numericExpression("SUBS", firstType, secondType, operator);
			return termTail(castType);
		case MP_PLUS:
			operator = addingOperator();
			secondType = term();
			castType = semantic.numericExpression("ADDS", firstType, secondType, operator);
			return termTail(castType);
			// Rule 84
		default:
			return firstType;
		}
	}

	private Lexeme optionalSign() {
		switch (lookahead) {
		// Rule 85
		case MP_PLUS:
			match();
			return null;
			// Rule 85
		case MP_MINUS:
			match();
			return matched;
			// Rule 85
		default:
			return null;
		}
	}

	private Lexeme addingOperator() {
		switch (lookahead) {
		// Rule 88, 89, 90
		case MP_PLUS:
		case MP_MINUS:
		case MP_OR:
			match();
			return matched;
		default:
			error("Expected adding operator");
			return null;
		}
	}

	// 91 term
	private Type term() {
		// Rule 91
		Type firstType = factor();
		return factorTail(firstType);
	}

	private Type factorTail(Type firstType) {
		Lexeme operator;
		Type secondType, castType;
		switch (lookahead) {
		// Rule 92
		
		case MP_AND:
			operator = multiplyingOperator();
			secondType = factor();
			castType = semantic.booleanExpression("ANDS", firstType, secondType, operator);
			return factorTail(castType);
		case MP_DIV:
			operator = multiplyingOperator();
			secondType = factor();
			castType = semantic.numericExpression("DIVS", firstType, secondType, operator);
			return factorTail(castType);
		case MP_MOD:
			operator = multiplyingOperator();
			secondType = factor();
			if (firstType != Type.Integer || secondType != Type.Integer){
					 throw new SemanticError("Cannot use MOD on non-integer values, ", operator);
				 }
			castType = semantic.numericExpression("MODS", firstType, secondType, operator);
			return factorTail(castType);
		case MP_FLOAT_DIVIDE:
			operator = multiplyingOperator();
			secondType = factor();
			//if the second type is not already a float, cast it as a float.
			if (secondType != Type.Float){
				 if(!semantic.cast(secondType, Type.Float)){
					 throw new SemanticError("Cannot cast " + secondType + " to a Float, ", operator);
				 }
				 }
			castType = semantic.numericExpression("DIVS", firstType, Type.Float, operator);
			return factorTail(castType);
		case MP_TIMES:
			operator = multiplyingOperator();
			secondType = factor();
			castType = semantic.numericExpression("MULS", firstType, secondType, operator);
			return factorTail(castType);
			// Rule 92
		default:
			return firstType;
		}
	}

	private Lexeme multiplyingOperator() {
		switch (lookahead) {
		// Rule 94
		case MP_TIMES:
		case MP_FLOAT_DIVIDE:
		case MP_DIV:
		case MP_MOD:
		case MP_AND:
			match();
			return matched;
		default:
			error("Expected multiplying operator");
			return null;
		}
	}

	private Type factor() {
		Type returnedType;
		
		switch (lookahead) {
		// Rule 99, 100, 101, 102, 103
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_STRING_LIT:
		case MP_TRUE:
		case MP_FALSE:
			match();
			return semantic.loadLiteral(matched);
			// Rule 104
		case MP_NOT:
			match();
			Lexeme notLexeme = matched;
			returnedType = factor();
			if (returnedType != Type.Boolean) {
				throw new SemanticError("NOT is only applicable for non boolean values at ", notLexeme);
			} 
			semantic.not();
			return returnedType;
			// Rule 105
		case MP_LPAREN:
			match();
			returnedType = expression();
			match(Token.MP_RPAREN);
			return returnedType;
			// Rule 106
		case MP_IDENTIFIER:
			Entry idType = table.lookup(lookaheadLexeme.getLexemeContent());
			if(idType.getKind() == Kind.Procedure){
				throw new SemanticError("cannot call procedure in an expression ", lookaheadLexeme);
			}
			else if (idType.getKind() == Kind.Function){
				Lexeme funcName = functionIdentifier();
				semantic.funcActivationRecord();
				Iterator<Entry> formalParams = table.lookup(funcName.getLexemeContent()).getParamTypes().iterator();
				optionalActualParameterList(formalParams);
				if (formalParams.hasNext()) {
					throw new SemanticError("Not enough parameters for function ", funcName);
				}
				return semantic.call(funcName);
			}
			else {
				return semantic.load(variableIdentifier());
			}
			
		default:
			throw new SemanticError("invalid token in expression -> factor ", matched);
		}
	}

	private void programIdentifier() {
		// Rule 107
		match(Token.MP_IDENTIFIER);
	}

	private Lexeme variableIdentifier() {
		// Rule 108
		match(Token.MP_IDENTIFIER);
		return matched;
	}

	private Lexeme procedureIdentifier() {
		// Rule 109
		match(Token.MP_IDENTIFIER);
		return matched;
	}

	private Lexeme functionIdentifier() {
		// Rule 110
		match(Token.MP_IDENTIFIER);
		return matched;
	}

	private void booleanExpression() {
		// Rule 111
		Type type = expression();
		if (type != Type.Boolean){
			throw new SemanticError("Expected boolean in expression, found " + type + " ", matched);
		}
	}

	private Type ordinalExpression() {
		// Rule 112
		return expression();

	}

	private List<String> identifierList() {
		// Rule 113
		List<String> idList = new ArrayList<String>();
		match(Token.MP_IDENTIFIER);
		idList.add(matched.getLexemeContent());
		identifierTail(idList);
		return idList;
	}

	private void identifierTail(List<String> idList) {
		switch (lookahead) {
		// Rule 114
		case MP_COMMA:
			match();
			match(Token.MP_IDENTIFIER);
			idList.add(matched.getLexemeContent());
			identifierTail(idList);
			return;
			// Rule 115
		default:
			return;
		}
	}
}
