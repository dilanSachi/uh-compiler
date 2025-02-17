package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.TestPrintStream;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTests {

    static String[] stdOut = new String[1];
    static PrintStream printStream;

    @BeforeAll
    public static void executeBefore() {
        try {
            printStream = new TestPrintStream(stdOut);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.setOut(printStream);
    }

    @Test
    public void testBasicAddition() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(1, null),
                new Token("+", TokenType.OPERATOR, null), new Literal(2, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 3);
    }

    @Test
    public void testBasicSubtraction() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(1, null),
                new Token("-", TokenType.OPERATOR, null), new Literal(2, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), -1);
    }

    @Test
    public void testBasicMultiplication() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(100, null),
                new Token("*", TokenType.OPERATOR, null), new Literal(120, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 12000);
    }

    @Test
    public void testBasicDivision() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(140, null),
                new Token("/", TokenType.OPERATOR, null), new Literal(7, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 20);
    }

    @Test
    public void testBasicAnd() throws InterpreterException, ParserException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = true; var y = true; print_bool(x and y);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = true; var y = false; print_bool(x and y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "false\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = true; print_bool(x and y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "false\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = false; print_bool(x and y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "false\n");
    }

    @Test
    public void testBasicOr() throws InterpreterException, ParserException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = true; var y = true; print_bool(x or y);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = true; var y = false; print_bool(x or y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = true; print_bool(x or y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = false; print_bool(x or y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "false\n");
    }

    @Test
    public void testBasicAdditionWithPrint() throws ParserException, InterpreterException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 5; x = x + 4; print_int(x);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse2());
        assertEquals(stdOut[0], "9\n");
    }
}
