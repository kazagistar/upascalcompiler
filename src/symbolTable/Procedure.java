package symbolTable;

import java.util.Arrays;

public class Procedure implements Typeclass {
	public final Type[] params;
	
	public Procedure(Type[] params) {
		this.params = params;
	}
	
	public static boolean isClassOf(Typeclass other) {
		try {
			@SuppressWarnings("unused")
			Procedure cast = (Procedure) other;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean matches(Typeclass other) {
		if (! Procedure.isClassOf(other)) return false;
		Procedure cast = (Procedure) other;
		return Arrays.equals(this.params, cast.params);
	}
	
	
	@Override
	public int getSize(){
		return 0;
	}

	@Override
	public Type getReturnType() {
		return null;
	}

}
