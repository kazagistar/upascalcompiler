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
			throw new ParseError(symbol.getLexemeContent(), " Is not Defined ", symbol);
		}
		if (!Variable.isClassOf(type)){
			throw new ParseError("Expected a Variable", symbol);
		}
		writer.println("PUSH " + symbols.lookupAddress(symbol.getLexemeContent()));
		return type.getReturnType();
	}
	
	// symbol = dst
	public void store(Lexeme symbol, Type stackType){
		Typeclass type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new ParseError(symbol.getLexemeContent(), " Is not Defined, cannot pop value ", symbol);
		}
		if (!Variable.isClassOf(type)){
			throw new ParseError("Expected a Variable", symbol);
		}
		if(!cast(stackType, type.getReturnType())){
			throw new ParseError(symbol.getLexemeContent() + " Has a type mis-match, could not be poped ", symbol);
		}
		writer.println("POP " + symbols.lookupAddress(symbol.getLexemeContent()));
	}
	
	//return true if cast is successfull
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
			throw new ParseError("", " Mistmatched types for operator ", symbol);
		}
	}
	//generates read statement code
	public void read(Lexeme symbol){
		Typeclass type = symbols.lookup(symbol.getLexemeContent());
		if (type == null){
			throw new ParseError(symbol.getLexemeContent(), " Is not Defined, cannot pop value ", symbol);
		}
		if (!Variable.isClassOf(type)){
			throw new ParseError("Expected a Variable", symbol);
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
			throw new ParseError("could not ",  "read in, specified value ", symbol);
		}
	}
	
	//generates write statement code
	public void write(){
		
	}

}
