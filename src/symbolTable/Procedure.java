package symbolTable;

import java.util.Iterator;
import java.util.List;
import parser.Label;

public class Procedure implements Typeclass {
	public final List<Type> params;
	public final Label location;
	
	public Procedure(List<Type> params, Label location) {
		this.params = params;
		this.location = location;
	}
	
	public static boolean isClassOf(Typeclass other) {
		try {
			@SuppressWarnings("unused")
			Procedure cast = (Procedure) other;
		} catch (ClassCastException e) {
			return false;
		}
		return other != null;
	}

	public boolean matches(Typeclass other) {
		if (! Procedure.isClassOf(other)) return false;
		Procedure cast = (Procedure) other;
		boolean typesMatch = true;
		// Check if parameters match types
		Iterator<Type> ti = this.params.iterator();
		Iterator<Type> to = cast.params.iterator();
		while (ti.hasNext() && to.hasNext())
			typesMatch &= ti.next().compareTo(to.next()) == 0;
		// Check if parameter lists are the same length
		typesMatch &= this.params.size() == cast.params.size();
		return typesMatch;
	}
	
	
	@Override
	public int getSize(){
		return 0;
	}
	
	@Override
	public Label getLocation() {
		return location;
	}

	@Override
	public Type getReturnType() {
		return null;
	}

	@Override
	public List<Type> getParamTypes() {
		return params;
	}

}
