package symbolTable;

import parser.Label;

public interface Typeclass {
	public boolean matches(Typeclass other);
	
	public int getSize();
	
	//procedure returns null
	//function returns its return type
	//variable returns its own type
	public Type getReturnType();
	
	public int getParamsSize();
	
	public Label getLocation();
}