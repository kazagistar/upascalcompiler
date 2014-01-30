package lexer;

import java.util.Arrays;
import java.util.Iterator;

class ScannerStream implements Iterator<Byte>, Iterable<Byte> {
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
	private boolean isMarked = false;

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
		// resets isMarked to false at the begining of each new Lexem
		isMarked = false;
	}

	// Saves a current valid token state
	// Warning: cannot be called while no lexeme is started
	// also sets isMarked to true signifying that an accept state has been
	// reached
	public void mark(Token label) {
		marked_row = row;
		marked_col = col;
		marked_index = index;
		output.token = label;
		isMarked = true;
	}

	// Backtracks to a previously marked state, and emits the lexeme at that
	// location
	// checks to see if the FSA reached a accept state prior to emit() if yes,
	// return accept state, if no, set token to error, and return it.
	public Lexeme emit() {
		if (isMarked) {
			row = marked_row;
			col = marked_col;
			index = marked_index;
			output.content = new String(Arrays.copyOfRange(input,
					lexemeStart, index));
			return output;
		} else {
			// returns the full content of the Lexeme starting at first char,
			// and ending at the char that caused the FSA error.
			output.content = new String(Arrays.copyOfRange(input,
					lexemeStart, index));
			output.row = row;
			output.col = col;
			output.token = Token.MP_ERROR;
			return output;
		}
	}

	// Check the current character without advancing
	public byte peek() {
		return input[index];
	}
	
	public boolean isFinished() {
		return index == input.length;
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
		} else {
			row++;
		}
		index++;
		return rtrn;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	// So I made the iterator an iterable, so I can can iterate when I iterate?
	@Override
	public Iterator<Byte> iterator() {
		return this;
	}
}
