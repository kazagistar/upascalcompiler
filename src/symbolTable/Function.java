package symbolTable;

import java.util.Arrays;

public class Function implements Typeclass {
	public final Type[] params;
	public final Type returned;
	
	public Function(Type returned, Type[] params) {
		this.returned = returned;
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
		return this.returned == cast.returned && Arrays.equals(this.params, cast.params);
	}
	
	@Override
	public int getSize(){
		return 0;
	}
}