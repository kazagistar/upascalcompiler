package symbolTable;

import java.util.List;

import parser.Label;

public interface Typeclass {
	public int getSize();
	
	//procedure returns null
	//function returns its return type
	//variable returns its own type
	public Type getReturnType();
	
	public List<Type> getParamTypes();
	
	public Label getLocation();
}