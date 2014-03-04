package symbolTable;

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
	public boolean matches(Typeclass other) {
		if (! Variable.isClassOf(other)) return false;
		Variable cast = (Variable) other;
		return this.type == cast.type;
	}
	
	@Override
	public int getSize(){
		return 1;
	}

}
