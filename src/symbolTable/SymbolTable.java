package symbolTable;

public class SymbolTable {
	private Scope scopeStack;
	
	public SymbolTable(){
		scopeStack = new Scope("_program", ScopeSort.Program, null);
	}
	public void add(String identName, Typeclass type){
		scopeStack.add(identName, type);
	}

	public void addParent(String identName, Typeclass type){
		scopeStack.getParent().add(identName, type);
	}
	
	// lookup() method
	// checks to see if the identifier is already present in the scope (or any
	// parent scope)
	// if found, returns Type (of the identifier)
	// if not found, returns null.
	public Typeclass lookup(String identName) {
		return scopeStack.lookup(identName);
	}
	
	//gets rid of a scope when its no longer needed
	public void destroyScope(){
		scopeStack = scopeStack.getParent();
	}
	
	//creates a new scope
	public void addScope(String scopeName, ScopeSort sort){
		scopeStack = new Scope(scopeName, sort, scopeStack);
	}
	//returns a String in the form "offset(D#)" for a specified identifierName
	public String lookupAddress(String identName){
		return scopeStack.lookupAddress(identName);
	}
	
	//returns the sizeInBytes of the specified scope
	public int getScopeSize(){
		return scopeStack.getScopeSize();
	}
}
