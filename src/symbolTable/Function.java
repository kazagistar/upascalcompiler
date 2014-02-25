package symbolTable;

import java.util.Arrays;

public class Function implements Typeclass {
	public final Type[] params;
	
	public Function(Type[] params) {
		this.params = params;
	}
	
	public static boolean isClassOf(Typeclass other) {
		try {
			@SuppressWarnings("unused")
			Function cast = (Function) other;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean matches(Typeclass other) {
		if (! Function.isClassOf(other)) return false;
		Function cast = (Function) other;
		return Arrays.equals(this.params, cast.params);
	}
}