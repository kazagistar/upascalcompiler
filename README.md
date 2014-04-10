# Group 3 Compiler Instructions

## Build

1. In eclipse, `File > Export`;  select `Java` then `Runnable Jar File`.

2. Select a basic launch configuration. (You might have to go back and make one in `Run > Run Configurations...`).

3. Pick your export destination and hit finish.

To run the compiled jar file, navigate there in the command line and type `java -jar <filename> -h`, which should list all the parameter options.

## Scanner

To test the scanner only, you can pass in a file name and the following helpful parameters.

- -l disables everything other then lexing

- -v enables verbose mode, which prints each lexeme to the screen.

- -t TOKENFILE prints all the lexemes to a given token file.

- -i replaces the need for a file parameter, and instead lets you load the code from stdin, until it reads an EOF (ctrl-d/ctrl-z).

Example:
	
	java -jar group3parser.jar -l -v -t tokenfile test/fizzbuzz.p

## Parser

To use and debug the parser, a few more parameters are added (make sure you disable -l!).

- -o OUTPUTFILE writes the resulting assembly code to the given output file. If you dont pick a name, it will write to the same name as your input file but change to using a .asm suffix.

- -p will alternatively write the assembly code straight to the console instead of to a file.

Examples:

	java -jar group3parser.jar -v -p test/fizzbuzz.p
	java -jar group3parser.jar -o fizzy.asm test/fizzbuzz.p -t fizzy.tokens

