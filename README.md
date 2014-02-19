# Group 3 Compiler Instructions

## Scanner

The compiler takes one parameter by default, which is the file to compile. To see the scanner work, however, requires additional parameters.

- -v enables verbose mode, which prints each lexeme to the screen.

- -t TOKENFILE prints all the lexemes to a given token file.


Example:
	
	java -jar group3scanner.jar -v -t tokenfile test/.p

(order matters)
$\Tree [.S a [.NP {\bf b} c ] d ]$