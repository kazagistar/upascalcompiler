package symbolTable;

import parser.Label;

public class Variable implements Typeclass {
	public final Type type;
	
	public Variable(Type type) {
		this.type = type;
	}
	
	public static boolean isClassOf(Typeclass other) {
		try {
			@SuppressWarnings("unused")
			Variable cast = (Variable) other;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}
	
	@Override
	public int getParamsSize(){
		throw new RuntimeException("tried to get the size of the params of a variable oops");
	}


	@Override
	public Label getLocation() {
		throw new RuntimeException("tried to get the goto label of a variable oops");
	}
	
	@Override
	public boolean matches(Typeclass other) {
		if (! Variable.isClassOf(other)) return false;
		Variable cast = (Variable) other;
		return this.type == cast.type;
	}
	
	@Override
	public Type getReturnType(){
		return type;
	}
	@Override
	public int getSize(){
		return 1;
	}

}
