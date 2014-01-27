package lexer;

import java.util.Arrays;
import java.util.Iterator;

class ScannerStream implements Iterator<Byte> {
	// File being scanned
	private byte[] input;
	
	// Current location in the file
	private int row = 0;
	private int col = 0;
	private int index = 0;
	
	// Remembers a row or col to backtrack to
	private int marked_row = 0;
	private int marked_col = 0;
	private int marked_index = 0;
	
	// The lexeme that is currently being scanned at any given time
	private Lexeme output;
	private int lexemeStart;
	
	
	public ScannerStream(byte[] in) {
		this.input = in;
	}
	
	// Starts constructing a new lexeme
	public void lexemeStart() {
		output = new Lexeme();
		output.row = row;
		output.col = col;
		lexemeStart = index;
	}
	
	// Saves a current valid token state
	// Warning: cannot be called while no lexeme is started
	public void mark(Token label) {
		marked_row = row;
		marked_col = col;
		marked_index = index;
		output.token = label;
	}
	
	// Backtracks to a previously marked state, and emits the lexeme at that location
	public Lexeme emit () {
		row = marked_row;
		col = marked_col;
		index = marked_index;
		output.content = Arrays.toString(Arrays.copyOfRange(input, lexemeStart, index));
		return output;
	}
	
	// Check the current character without advancing
	public byte peek() {
		return input[index];
	}

	// Iterator interface to allow you to use this with for-each syntax
	// http://stackoverflow.com/questions/85190/how-does-the-java-for-each-loop-work
	
	// Test for end of file
	@Override
	public boolean hasNext() {
		return index < input.length;
	}

	// Get next character
	@Override
	public Byte next() {
		byte rtrn = input[index];
		if (rtrn == '\n') {
			row = 0;
			col++;
		}
		else {
			row++;
		}
		index++;
		return rtrn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();	
	}
}
