package symbolTable;

import java.util.Iterator;
import java.util.List;

import parser.Label;

public class Entry {
	private final Kind kind;
	private final Type returns;
	private final int size;
	private final int offset;
	private final Label location;
	private final List<Entry> params;
	
	Entry(Kind kind, Type returns, int size, int offset, Label location, List<Entry> params) {
		this.kind = kind;
		this.returns = returns;
		this.size = size;
		this.offset = offset;
		this.location = location;
		this.params = params;
	}
	
	public int getSize() {
		return size;
	}
	
	public Type getReturnType() {
		return returns;
	}
	
	public List<Entry> getParamTypes() {
		return params;
	}
	
	public Label getLocation() {
		return location;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public int getOffset() {
		return offset;
	}
}