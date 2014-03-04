package symbolTable;

import java.util.Arrays;

public class Procedure implements Typeclass {
	public final Type[] params;
	public final Type returned;
	
	public Procedure(Type returned, Type[] params) {
		this.params = params;
		this.returned = returned;
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
		return this.returned == cast.returned && Arrays.equals(this.params, cast.params);
	}
	
	@Override
	public int getSize(){
		return 0;
	}

}
