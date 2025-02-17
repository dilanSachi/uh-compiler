package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.Interpreter.IntValue;
import fi.helsinki.compiler.Interpreter.Interpreter;
import fi.helsinki.compiler.Interpreter.Value;
import fi.helsinki.compiler.TestPrintStream;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTests {

    @Test
    public void testBasicAddition() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(1, null),
                new Token("+", TokenType.OPERATOR, null), new Literal(2, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 3);
    }

    @Test
    public void testBasicAdditionWithPrint() throws ParserException, InterpreterException, IOException {
        String[] x = new String[1];
        PrintStream printStream = new TestPrintStream(x);
        System.setOut(printStream);
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 5; x = x + 4; print_int(x);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(x[0], "9\n");
    }
}
