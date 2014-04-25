package symbolTable;

import java.util.List;

import parser.Label;

public class SymbolTable {
	private Scope scopeStack;
	
	public SymbolTable(){
		scopeStack = new Scope("_program", Kind.Program, null);
	}
	
	// constructors for entries...
	
	public Entry addValue(String identName, Type type) {
		Entry created = new Entry(Kind.Value, type, 1, scopeStack.getScopeSize(), null, null);
		scopeStack.add(identName, created);
		return created;
	}
	
	public Entry addReference(String identName, Type type) {
		Entry created = new Entry(Kind.Reference, type, 1, scopeStack.getScopeSize(), null, null);
		scopeStack.add(identName, created);
		return created;
	}

	public void addProcedureParent(String identName, Label location, List<Entry> params) {
		Entry created = new Entry(Kind.Procedure, null, 0, 0, location, params);
		scopeStack.getParent().add(identName, created);
	}
	
	public void addFunctionParent(String identName, Type type, Label location, List<Entry> params) {
		Entry created = new Entry(Kind.Function, type, 0, 0, location, params);
		scopeStack.getParent().add(identName, created);
	}
	
	// lookup() method
	// checks to see if the identifier is already present in the scope (or any
	// parent scope)
	// if found, returns Type (of the identifier)
	// if not found, returns null.
	public Entry lookup(String identName) {
		return scopeStack.lookup(identName);
	}
	
	// after calling lookup, you can also lookup its nesting
	public int lookupNesting(String identName) {
		return scopeStack.lookupNesting(identName);
	}
	
	//gets rid of a scope when its no longer needed
	public void destroyScope(){
		scopeStack = scopeStack.getParent();
	}
	
	//creates a new scope
	public void addScope(String scopeName, Kind kind){
		scopeStack = new Scope(scopeName, kind, scopeStack);
	}
	
	//returns the sizeInBytes of the specified scope
	public int getScopeSize(){
		return scopeStack.getScopeSize();
	}
	
	public int getNestingLevel(){
		return scopeStack.getNestingLevel();
	}
	
	public int getSizeParams(){
		// Programs have no parameters
		if (scopeStack.kind == Kind.Program) {
			return 0;
		}
		Entry record = lookup(scopeStack.getName());
		return record.getParamTypes().size();
	}
	
	public String getCurrentName() {
		return scopeStack.getName();
	}
	
	public Kind getKind() {
		return scopeStack.kind;
	}
}
