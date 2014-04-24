package symbolTable;

import java.util.List;

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
	public Label getLocation() {
		throw new RuntimeException("tried to get the goto label of a variable oops");
	}
	
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

	@Override
	public List<Type> getParamTypes() {
		return null;
	}

}
