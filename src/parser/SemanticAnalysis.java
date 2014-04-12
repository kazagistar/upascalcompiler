package parser;

import java.io.PrintWriter;

import lexer.Lexeme;
import symbolTable.*;
public class SemanticAnalysis {

	private final PrintWriter writer;
	private final SymbolTable symbols;
	private int labelCounter = 1;
	
	public SemanticAnalysis(PrintWriter writer, SymbolTable symbols){
		this.writer = writer;
		this.symbols = symbols;
		
		
	}
	//symbol = src
	//pushes a value onto the stack
	public Type load(Lexeme symbol){
		Typeclass type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new SemanticError( "variable: " + symbol.getLexemeContent() + " Is not Defined ", symbol);
		}
		if (!Variable.isClassOf(type)){
			if(Function.isClassOf(type))
			{
				throw new SemanticError("Expected a Variable, found: Function ", symbol);
			} else {
				throw new SemanticError("Expected a Variable, found: Procedure ", symbol);
			}
		}
		writer.println("PUSH " + symbols.lookupAddress(symbol.getLexemeContent()));
		return type.getReturnType();
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
		Typeclass type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new SemanticError("variable: " + symbol.getLexemeContent() + " Is not Defined, cannot pop value ", symbol);
		}
		if (!Variable.isClassOf(type)){
			if(Function.isClassOf(type))
			{
				throw new SemanticError("Expected a Variable, found: Function ", symbol);
			} else {
				throw new SemanticError("Expected a Variable, found: Procedure ", symbol);
			}
		}
		if(!cast(stackType, type.getReturnType())){
			throw new SemanticError(stackType, type.getReturnType(), symbol);
		}
		writer.println("POP " + symbols.lookupAddress(symbol.getLexemeContent()));
	}
	
	public void createSemanticRecord(){
		int scopeOffSet = symbols.getScopeSize();
		writer.println("MOV SP D0");
		writer.println("ADD SP #" + scopeOffSet + " SP");
	}
	
	public void destroySemanticRecord(){
		int scopeOffSet = symbols.getScopeSize();
		writer.println("SUB SP #" + scopeOffSet + " SP");
	}
	
	public void halt(){
		writer.println("HLT");
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
		Typeclass type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new SemanticError("Must define " + symbol.getLexemeContent() + " before reading into it ", symbol); // Unsure what to happen
		}
		if (!Variable.isClassOf(type)){
			if(Function.isClassOf(type))
			{
				throw new SemanticError("Expected a Variable, found: Function ", symbol);
			} else {
				throw new SemanticError("Expected a Variable, found: Procedure ", symbol);
			}
		}
		if (type.getReturnType().equals(Type.String)){
			writer.println("RDS "+symbols.lookupAddress(symbol.getLexemeContent()));
		}
		else if (type.getReturnType().equals(Type.Integer)){
			writer.println("RD "+symbols.lookupAddress(symbol.getLexemeContent()));
		}
		else if (type.getReturnType().equals(Type.Float)){
			writer.println("RDF "+symbols.lookupAddress(symbol.getLexemeContent()));
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
	

	public void not(Type stackType) {
		if (stackType == Type.Integer) {
			writer.println("NOTS");
		}
		else if (stackType == Type.Float) {
			writer.println("NOTS");
		}
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
		String address = symbols.lookupAddress(variable.getLexemeContent());
		writer.println("ADD " + address + " #" + increment + " " + address);
	}
}