package symbolTable;

import java.util.HashMap;

public class Scope {
	private final Scope parent; //doubles as next table pointer
	private final String label; //the name of the scope
	private final HashMap<String, ScopeEntry> symbolTableScope; //stores lexeme name and Type var for each entry in scope
	private final int nestingLevel; //(the nesting level of the scope)
	private int sizeInBytes = 0; //the size (in Bytes of the scope table)
	private int offsetCounter = 0;
	private final ScopeSort sort; //the type of scope table
	
	//creates a new scope with the given name and parent scope.
	Scope(String label, ScopeSort sort, Scope parent){
		this.label = label;
		this.parent = parent;
		this.sort = sort;
		//if parent is not null, then nesting level = parents nestinglevel + 1, otherwise nesting level is 0
		this.nestingLevel = parent == null ? 0 : parent.getNestingLevel() + 1;
		symbolTableScope = new HashMap<String, ScopeEntry>();
	}
	
	public int getNestingLevel(){
		return nestingLevel;
	}
	public Scope getParent(){
		return parent;
	}
	
	//method that adds an identifier to the scope.
	//takes a Type which holds the identifiers name (string) and other attributes.
	public void add(String identName, Typeclass type){
		// checks to see if the given identifier is already used in this scope
		// (or a parent scope), if so, an error is thrown, else the identifier
		// is added to the scope.
		
		if (lookup(identName) != null) {
			throw new RuntimeException("ERROR, the identifier " + identName + " is already used in a parent scope, please change the name of the identifier!");

		} else {
			// where type.getIdentifier() returns a string containing the name
			// of the identifier
			ScopeEntry wrapper = new ScopeEntry(type, offsetCounter);
			offsetCounter += type.getSize();
			symbolTableScope.put(identName, wrapper);
			sizeInBytes += type.getSize(); //increments the sizeInBytes by the added value
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
			return symbolTableScope.get(identName).type;
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
	
	
	//looks up and returns the address of an identifier from symbol table, based on the identName input.
	public String lookupAddress(String identName){
		if (symbolTableScope.containsKey(identName)) {
			int offset = symbolTableScope.get(identName).offset;
			return offset + "(D" + nestingLevel + ")";
		}else if (this.parent != null && this.parent.lookup(identName) != null){
			return this.parent.lookupAddress(identName);
		}
		else return null;
	}
	
	public int getScopeSize(){
		return sizeInBytes;
	}
	class ScopeEntry {
		final Typeclass type;
		final int offset;

		ScopeEntry (Typeclass type, int offset) {
			this.type = type;
			this.offset = offset;
		}
	}

}
