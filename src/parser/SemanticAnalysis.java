package parser;

import java.io.PrintWriter;

import lexer.Lexeme;
import symbolTable.*;
import parser.*;
public class SemanticAnalysis {

	private final PrintWriter writer;
	private final SymbolTable symbols;
	
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
	
	//return true if cast is successful
	//return false if not.
	private boolean cast(Type stackType, Type targetType){
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
	
	//operation = String containtaiging either ADDS, MULS, SUBS,DIVS,MODS 
	// op1Type = type of the top thing on stack
	//op2Type = type of the second thing on stack
	public Type numericExpression(String operation,Type op1Type, Type op2Type, Lexeme symbol){
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
			writer.println("PUSH -1(SP)");
			writer.println("CASTSF");
			writer.println("POP -2(SP)");
			// convert to a float operation
			writer.println(operation + "F");
			return Type.Float;
		}
		else if(op1Type.equals(Type.Float) && op2Type.equals(Type.Float)){
			writer.println(operation + "F");
			return Type.Float;
		}else{
			//if it is a boolean or string
			throw new SemanticError(op1Type, op2Type, symbol);
		}
	}
	//generates read statement code
	public void read(Lexeme symbol){
		Typeclass type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new SemanticError(" Is not Defined, cannot pop value. "
					+ "Unsure what's supposed to happen. This occurs in "
					+ "read when type == null ", symbol); // Unsure what to happen
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
	public void write(){
		
	}

}
