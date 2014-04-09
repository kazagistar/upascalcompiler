package parser;

public class Label {
	private final int label;
	private boolean written;
	
	public Label(int label){
		this.label = label;
		written = false;
	}
	
	public int getLabel(){
		return label;
	}
	
	public void write() {
		assert !written;
		written = true;
	}
	
	@Override
	public String toString() {
		return "L"+label;
	}
}
