package parser;

import java.io.PrintWriter;

import lexer.Lexeme;
import symbolTable.*;
public class SemanticAnalysis {

	public final PrintWriter writer;
	private final SymbolTable symbols;
	private int labelCounter = 1;
	
	public SemanticAnalysis(PrintWriter writer, SymbolTable symbols){
		this.writer = writer;
		this.symbols = symbols;
		
		
	}
	//symbol = src
	//pushes a value onto the stack
	public Type load(Lexeme symbol){
		Entry result = symbols.lookup(symbol.getLexemeContent());
		if (result == null){
			throw new SemanticError( "variable " + symbol.getLexemeContent() + " is not defined ", symbol);
		}
		if (result.getKind() == Kind.Function) {
			throw new SemanticError("Expected a variable but found function ", symbol);
		}
		if (result.getKind() == Kind.Procedure) {
			throw new SemanticError("Expected a variable but found function ", symbol);
		}
		if (result.getKind() == Kind.Reference) {
			writer.println("PUSH @" + result.getOffset() + "(D" + symbols.lookupNesting(symbol.getLexemeContent()) + ")");
		}
		else /* if result kind is Value */ {
			writer.println("PUSH " + result.getOffset() + "(D" + symbols.lookupNesting(symbol.getLexemeContent()) + ")");
		}
		return result.getReturnType();
	}
	
	public Type loadReference(Lexeme symbol, Type expected) {
		Entry result = symbols.lookup(symbol.getLexemeContent());
		if (result == null){
			throw new SemanticError( "variable " + symbol.getLexemeContent() + " is not defined ", symbol);
		}
		if (result.getKind() == Kind.Function) {
			throw new SemanticError("Expected a variable but found function ", symbol);
		}
		if (result.getKind() == Kind.Procedure) {
			throw new SemanticError("Expected a variable but found function ", symbol);
		}
		if (expected != result.getReturnType()) {
			throw new SemanticError(expected, result.getReturnType(), symbol);
		}
		if (result.getKind() == Kind.Reference) {
			writer.println("PUSH " + result.getOffset() + "(D" + symbols.lookupNesting(symbol.getLexemeContent()) + ")");
		}
		else /* if result kind is Value */ {
			writer.println("PUSH D" + symbols.lookupNesting(symbol.getLexemeContent()));
			writer.println("ADD -1(SP) #" + result.getOffset() + " -1(SP)");
		}
		return result.getReturnType();
	}

	//creates a label
	public Label generateLabel() {
		return new Label(labelCounter++);
	}
	
	//writes label destination
	public void writeLabel(Label label){
		label.write();
		writer.println(label + ":");
	}
	
	//goto a target label
	public void goTo(Label target){
		writer.println("BR " + target);
	}
	
	//goTo if False
	public void goToFalse(Label target){
		writer.println("BRFS " + target);
	}
	
	
	
	// symbol = dst
	public void store(Lexeme symbol, Type stackType){
		Entry result = symbols.lookup(symbol.getLexemeContent());
		String storeLocation;
		if (result == null){
			throw new SemanticError("variable: " + symbol.getLexemeContent() + " Is not Defined, cannot pop value ", symbol);
		}
		if (result.getKind() == Kind.Value){
			storeLocation = result.getOffset() + "(D" + symbols.lookupNesting(symbol.getLexemeContent()) + ")";
		}
		else if (result.getKind() == Kind.Reference){
			storeLocation = "@" + result.getOffset() + "(D" + symbols.lookupNesting(symbol.getLexemeContent()) + ")";
		}
		else if (result.getKind() == Kind.Function) {
			if (!symbols.getCurrentName().equals(symbol.getLexemeContent())) {
				throw new SemanticError("Can only assign to return values of innermost function ", symbol);
			}
			storeLocation = "-2(D" + symbols.getNestingLevel() + ")";
		}
		else {
			throw new SemanticError("Cannot assign to a procedure ", symbol);
		}
		if(!cast(stackType, result.getReturnType())){
			throw new SemanticError(stackType, result.getReturnType(), symbol);
		}
		writer.println("POP " + storeLocation);
	}
	
	public void createSemanticRecord(Lexeme matched){
		int offset = symbols.getScopeSize();
		int nesting = symbols.getNestingLevel();
		if (nesting > 9) {
			throw new SemanticError("Cannot have more then 10 levels of nesting ", matched);
		}
		writer.println("MOV SP D" + nesting);
		shiftStack(offset);
	}
	
	public void startCalled(Label location) {
		// jump point for scope body
		writeLabel(location);
		
		// how many parameters the function/proc has
		int params = symbols.getSizeParams();
		// how many non-parameter local variables it has
		int locals = symbols.getScopeSize() - params;
		
		// what register to use for the symbol table
		int nesting = symbols.getNestingLevel();
		
		if (symbols.getKind() != Kind.Program) {
			// Pop the old PC to just below the future symbol table (the activation record?)
			writer.println("POP -" + (params + 1) + "(SP)");
		}
		// Move stack pointer above the locals, to above the symbol table
		shiftStack(locals);
		// Push old symbol table to save it
		writer.println("PUSH D" + nesting);
		// Compute the new symbol table... extra 1 because we pushed the old register already
		writer.println("SUB SP #" + (symbols.getScopeSize() + 1) + " D" + nesting);
	}
	
	public void endCalled() {
		// Pop the old symbol table
		writer.println("POP D" + symbols.getNestingLevel());
		// remove the symbol table offset
		shiftStack(-symbols.getScopeSize());
		
		if (symbols.getKind() != Kind.Program) {
			// because the PC was moved to below the symbol table, we can just return right now
			writer.println("RET");
		}
		else {
			writer.println("HLT");
		}
	}
	
	public void funcActivationRecord() {
		// Make space for return value at -2(DX) and PC at -1(DX)
		shiftStack(2);
	}
	
	public void procActivationRecord() {
		// Make space for the PC at -1(DX)
		shiftStack(1);
	}
	
	public Type call(Lexeme funcName) {
		Entry formalParameters = symbols.lookup(funcName.getLexemeContent());
		
		writer.println("CALL " + formalParameters.getLocation());
		return formalParameters.getReturnType();
	}
	
	//return true if cast is successful
	//return false if not.
	public boolean cast(Type stackType, Type targetType){
		if (stackType == targetType){
			return true;
		}
		else if (stackType.equals(Type.Float) && targetType.equals(Type.Integer)){
			//changes stackType to be the same as targetType (float -> int)
			writer.println("CASTSI");
			return true;
		}
		else if (stackType.equals(Type.Integer) && targetType.equals(Type.Float)){
			//changes stackType to be the same as targetType (int --> float)
			writer.println("CASTSF");
			return true;
		}
		else{
			return false;
		}
	}
	
	//operation = String containing either ADDS, MULS, SUBS,DIVS,MODS 
	// op1Type = type of the top thing on stack
	//op2Type = type of the second thing on stack
	public Type numericExpression(String operation,Type op2Type, Type op1Type, Lexeme symbol){
		if (op1Type.equals(Type.Integer) && op2Type.equals(Type.Integer)){
			writer.println(operation);
			return Type.Integer;
		}
		else if(op1Type.equals(Type.Integer) && op2Type.equals(Type.Float)){
			writer.println("CASTSF");
			// convert to a float operation
			writer.println(operation + "F");
			return Type.Float;
		}
		else if(op1Type.equals(Type.Float) && op2Type.equals(Type.Integer)){
			//moves op2 to top of stack, casts it as a float, and moves it back to original possition. 
			writer.println("PUSH -2(SP)");
			writer.println("CASTSF");
			writer.println("POP -2(SP)");
			// convert to a float operation
			writer.println(operation + "F");
			return Type.Float;
		}
		else if(op1Type.equals(Type.Float) && op2Type.equals(Type.Float)){
			writer.println(operation + "F");
			return Type.Float;
		}else {
			//if it is a boolean or string
			throw new SemanticError("First parameter is " + op1Type + " and second is " + op2Type + " but expected numberic types ", symbol);
		}
	}
	
	//operation = String containing either ADDS, MULS, SUBS,DIVS,MODS 
		// op1Type = type of the top thing on stack
		//op2Type = type of the second thing on stack
		public Type relationalExpression(String operation,Type op1Type, Type op2Type, Lexeme symbol){
			numericExpression(operation, op1Type, op2Type, symbol);
			return Type.Boolean;
		}
	
	public Type booleanExpression(String operation,Type op1Type, Type op2Type, Lexeme symbol){
		if (op1Type.equals(Type.Boolean) && op2Type.equals(Type.Boolean)) {
			writer.println(operation);
			return Type.Boolean;
		} else {
			//if it is a boolean or string
			throw new SemanticError("First parameter is " + op1Type + " and second is " + op2Type + " but expected boolean types ", symbol);
		}
	}
	
	//generates read statement code
	public void read(Lexeme symbol){
		Entry type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new SemanticError("Must define " + symbol.getLexemeContent() + " before reading into it ", symbol); // Unsure what to happen
		}
		if (type.getKind() == Kind.Function){
			throw new SemanticError("Expected a variable, found: Function ", symbol);
		}
		if (type.getKind() == Kind.Procedure) {
			throw new SemanticError("Expected a variable, found: Procedure ", symbol);
		}
		
		String address = type.getOffset() + "(D" + symbols.lookupNesting(symbol.getLexemeContent()) + ")";
		if (type.getKind() == Kind.Reference) {
			address = "@" + address;
		}
		
		if (type.getReturnType() == Type.String){
			writer.println("RDS " + address);
		}
		else if (type.getReturnType() == Type.Integer){
			writer.println("RD " + address);
		}
		else if (type.getReturnType() == Type.Float){
			writer.println("RDF " + address);
		}
		else {
			throw new SemanticError("could not read in specified value ", symbol);
		}
	}
	
	
	
	
	//generates write statement code
	public void write(Type stackType){
		if (stackType == Type.Boolean){
			writer.println("MOV #\"False\" 0(SP)");
			Label falseTarget = this.generateLabel();
			this.goToFalse(falseTarget);
			writer.println("MOV #\"True\" 1(SP)");
			this.writeLabel(falseTarget);
			writer.println("WRT 1(SP)");
		}
		else {
			writer.println("WRTS");
		}
	}
	public void newLine(){
		writer.println("WRT #\"\\n\"");
	}
	
	
	public void negate(Type stackType, Lexeme symbol) {
		if (stackType == Type.Integer) {
			writer.println("NEGS");
		}
		else if (stackType == Type.Float) {
			writer.println("NEGSF");
		}
		else {
			throw new SemanticError("negative sign is only applicable for non numberic values at ", symbol);
		}
	}
	

	public void not() {
		writer.println("NOTS");
	}
	
	public Type loadLiteral(Lexeme literalLexeme) {
		String literal;
		switch (literalLexeme.getToken()) {
		case MP_INTEGER_LIT:
			literal = literalLexeme.getLexemeContent();
			writer.println("PUSH #" + literal);
			return Type.Integer;
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
			literal = literalLexeme.getLexemeContent();
			writer.println("PUSH #" + literal);
			return Type.Float;
		case MP_STRING_LIT:
			// converting string format from pascal strings to asm formatted strings
			literal = literalLexeme.getLexemeContent();
			literal = literal.replace("\\", "\\\\");
			literal = "\"" + literal + "\"";
			writer.println("PUSH #" + literal);
			return Type.String;
		case MP_TRUE:
			writer.println("PUSH #1");
			return Type.Boolean;
		case MP_FALSE:
			writer.println("PUSH #0");
			return Type.Boolean;
		default:
			throw new SemanticError("could not read in specified value ", literalLexeme);
		}
	}

	public void addTo(Lexeme variable, int increment) {
		Entry result = symbols.lookup(variable.getLexemeContent());
		String address = result.getOffset() + "(D" + symbols.lookupNesting(variable.getLexemeContent()) + ")";
		writer.println("ADD " + address + " #" + increment + " " + address);
	}
	
	public void duplicate() {
		writer.println("PUSH -1(SP)");
	}
	
	public void shiftStack(int offset) {
		writer.println("ADD SP #" + offset + " SP");
	}
}