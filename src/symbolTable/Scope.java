package symbolTable;

import java.util.HashMap;

public class Scope {
	private Scope parent = null;
	private String name;
	private HashMap<String, Typeclass> symbolTableScope;
	
	//creates a new scope with the given name and parent scope.
	Scope(String name, Scope parent){
		this.name = name;
		this.parent = parent;
	}
	
	//method that adds an identifier to the scope.
	//takes a Type which holds the identifiers name (string) and other attributes.
	public void add(String identName, Typeclass type){
		// checks to see if the given identifier is already used in this scope
		// (or a parent scope), if so, an error is thrown, else the identifier
		// is added to the scope.
		
		if (lookup(identName) == null) {
			System.out.println("ERROR, the identifier " + identName + " is already used in a parent scope, please change the name of the identifier!");

		} else {
			// where type.getIdentifier() returns a string containing the name
			// of the identifier
			symbolTableScope.put(identName, type);
		}
	}
	
	// lookup() method
	// checks to see if the identifier is already present in the scope (or any
	// parent scope)
	// if found, returns Type (of the identifier)
	// if not found, returns null.
	public Typeclass lookup(String identName) {

		// if identifier is found in current scope, return the Type of
		// identifier.
		if (symbolTableScope.containsKey(identName)) {
			return symbolTableScope.get(identName);
		}
		// checks to see if the given identifier is already used the parent
		// scope,
		else if (this.parent != null && this.parent.lookup(identName) != null) {
			// if identifier is found in a parents scope returns the Type of the
			// identifier
			return this.parent.lookup(identName);
		} else {
			// else, the identifier was not found, return null.
			return null;
		}
	}

}
