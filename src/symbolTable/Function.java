package symbolTable;

import java.util.Iterator;
import java.util.List;

public class Function implements Typeclass {
	public final List<Type> params;
	public final Type returned;
	
	public Function(Type returned, List<Type> params) {
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
		// Check if return types match
		boolean typesMatch = this.returned == cast.returned;
		// Check if parameters match types
		Iterator<Type> ti = this.params.iterator();
		Iterator<Type> to = cast.params.iterator();
		while (ti.hasNext() && to.hasNext())
			typesMatch &= ti.next().compareTo(to.next()) == 0;
		// Check if parameter lists are the same length
		typesMatch &= this.params == cast.params;
		return typesMatch;
	}
	
	@Override
	public int getSize(){
		return 0;
	}

	@Override
	public Type getReturnType() {
		
		return returned;
	}
}