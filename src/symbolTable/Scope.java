package symbolTable;

import java.util.HashMap;

public class Scope {
	private final Scope parent; //doubles as next table pointer
	private final String name; //the name of the scope
	private final HashMap<String, Entry> symbolTableScope; //stores lexeme name and Type var for each entry in scope
	private final int nestingLevel; //(the nesting level of the scope)
	private int sizeInBytes = 0; //the size (in Bytes of the scope table)
	public final Kind kind; //the type of scope table
	
	//creates a new scope with the given name and parent scope.
	Scope(String name, Kind kind, Scope parent){
		this.name = name;
		this.parent = parent;
		this.kind = kind;
		//if parent is not null, then nesting level = parents nestinglevel + 1, otherwise nesting level is 0
		this.nestingLevel = parent == null ? 0 : parent.getNestingLevel() + 1;
		symbolTableScope = new HashMap<String, Entry>();
	}
	
	public int getNestingLevel(){
		return nestingLevel;
	}
	public Scope getParent(){
		return parent;
	}
	public int getScopeSize(){
		return sizeInBytes;
	}
	public String getName() {
		return name;
	}
	
	//method that adds an identifier to the scope.
	//takes a Type which holds the identifiers name (string) and other attributes.
	public void add(String identName, Entry added){
		// checks to see if the given identifier is already used in this scope
		// if so, an error is thrown, else the identifier is added to the scope.
		if (symbolTableScope.containsKey(identName)) {
			throw new RuntimeException("ERROR, the identifier " + identName + " is already used in a parent scope, please change the name of the identifier!");
		} else {
			symbolTableScope.put(identName, added);
			sizeInBytes += added.getSize();
		}
	}
	
	// lookup() method
	// checks to see if the identifier is already present in the scope (or any
	// parent scope)
	// if found, returns Type (of the identifier)
	// if not found, returns null.
	public Entry lookup(String identName) {

		// if identifier is found in current scope, return the Type of
		// identifier.
		if (symbolTableScope.containsKey(identName)) {
			return symbolTableScope.get(identName);
		}
		// recursively check the parent scopes
		else if (this.parent != null) {
			return this.parent.lookup(identName);
		} else {
			// else, the identifier was not found, return null.
			return null;
		}
	}
	
	public int lookupNesting(String identName) {
		if (symbolTableScope.containsKey(identName)) {
			return nestingLevel;
		}
		else if (this.parent != null) {
			return this.parent.lookupNesting(identName);
		}
		else {
			throw new RuntimeException("ERROR, the identifier " + identName + " has no nesting, make sure you verify its existance first!");
		}
	}

}
