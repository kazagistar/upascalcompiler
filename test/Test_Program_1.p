program lab1;
    var  otherthing, choice: integer;
         floatMathTest: float;
         booleanValue: boolean;
       
    function factorial( b: integer):integer;
    var x: integer;
    begin
        if b = 0 then
            factorial := 1
        else
            factorial := b * factorial(b-1);
    end;
    
    procedure listNum( start: integer);
    var finish: integer;
    begin
        finish := -50;
        while start > finish do
        begin
            write(start, ' ');
            start := start - 1;
        end;
        writeln('');
    end;
    
    procedure scopeTest;
    var otherthing: float;
    begin
        otherthing := 1 * 2.0 / 4 + 2;
        writeln('From procedure otherthing is: ', otherthing);
    end;
    
    function retString( x,y:float; b:string):string;
        var intConverter: integer;
        procedure imbedded(b:integer);
        
            procedure imbeddedAgain(b:integer);
            
                procedure somemoreImbedding(b:integer);
                
                begin
                    writeln('I am super imbedded and awesome. B is ', b);
                end;
                
            begin
                somemoreImbedding(b);
            end;
        begin
            imbeddedAgain(b);
        end;
    begin
        writeln('trying the whole imbedded thing', b);
        intConverter := x + y;
        imbedded(intConverter);
        
        retString := 'This string was returned as a value';
    end;
    
  begin
        writeln('Welcome to my program (^_^ )');
        writeln('Please enter an integer to find the factorial of:');
        read(choice);
        if not(true) then    
            writeln(factorial(choice))
        else
            writeln('-1');
            
        listNum(100 div 2);
        listNum(factorial(9 div 3));
        
        otherthing := 15 * 15 div 5 + 1 - 2 * 2;
        writeln('From *main* otherthing is: ', otherthing);
        scopeTest;
        writeln('From *main* otherthing still is: ', otherthing);
        floatMathTest := (7.0 *2.1 - 0.6 / 13) * (factorial(13 - 11));
        writeln(floatMathTest);
        
        {scopeTest(scopeTest); This has been fixed and no longer compiles.} 
        {intersting because scopeTest has no input parameters, and it's ok because scopeTest doesn't have a value return}
        
        writeln(retString(0.001, 1.23, 'String as input argument'));
        
        booleanValue := true;
        
        writeln('Thank you for using this!');
  end.
