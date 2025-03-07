package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.TestPrintStream;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.common.expressions.BinaryOp;
import fi.helsinki.compiler.common.expressions.IntLiteral;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("+", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 3);
    }

    @Test
    public void testBasicSubtraction() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("-", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), -1);
    }

    @Test
    public void testBasicMultiplication() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(100L, null),
                new Token("*", TokenType.OPERATOR, null), new IntLiteral(120L, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 12000);
    }

    @Test
    public void testBasicDivision() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(140L, null),
                new Token("/", TokenType.OPERATOR, null), new IntLiteral(7L, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 20);
    }

    @Test
    public void testBasicModulus() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(140L, null),
                new Token("%", TokenType.OPERATOR, null), new IntLiteral(7L, null), null);
        Interpreter interpreter = new Interpreter();
        Value result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 0);

        binaryOp = new BinaryOp(new IntLiteral(143L, null),
                new Token("%", TokenType.OPERATOR, null), new IntLiteral(7L, null), null);
        interpreter = new Interpreter();
        result = interpreter.interpretAST(binaryOp);
        assertEquals(((IntValue) result).getIntValue(), 3);
    }

    @Test
    public void testBasicAnd() throws InterpreterException, ParserException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = true; var y = true; print_bool(x and y);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = true; var y = false; print_bool(x and y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "false\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = true; print_bool(x and y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "false\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = false; print_bool(x and y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "false\n");
    }

    @Test
    public void testBasicOr() throws InterpreterException, ParserException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = true; var y = true; print_bool(x or y);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = true; var y = false; print_bool(x or y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = true; print_bool(x or y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "true\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = false; var y = false; print_bool(x or y);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "false\n");
    }

    @Test
    public void testBasicComparisonOperators() throws InterpreterException, ParserException {
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 10; var y = 20; y > x", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        Value value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), true);

        parser = new Parser(new Tokenizer().tokenize("var x = 10; var y = 20; y >= x", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), true);

        parser = new Parser(new Tokenizer().tokenize("var x = 20; var y = 20; y <= x", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), true);

        parser = new Parser(new Tokenizer().tokenize("var x = 10; var y = 20; y < x", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), false);

        parser = new Parser(new Tokenizer().tokenize("var x = 10; var y = 20; y != x", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), true);

        parser = new Parser(new Tokenizer().tokenize("var x = 10; var y = 20; y == x", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), false);
    }

    @Test
    public void testBasicUnaryOperators() throws InterpreterException, ParserException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = true; print_bool(not x);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "false\n");
        stdOut[0] = "";
        parser = new Parser(new Tokenizer().tokenize("var x = 239; print_int(-x);", "TestFile.dl"));
        interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "-239\n");
        stdOut[0] = "";
    }

    @Test
    public void testBasicAdditionWithPrint() throws ParserException, InterpreterException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 5; x = x + 4; print_int(x);", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "9\n");
    }

    @Test
    public void testIfElseBlock() throws ParserException, InterpreterException {
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 5; var y = 12; if y > 12 then {x = x + 4} else {x = x - y}", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        Value value = interpreter.interpretAST(parser.parse());
        assertEquals(((IntValue) value).getIntValue(), -7);

        parser = new Parser(new Tokenizer().tokenize("var x = 5; var y = 13; if y > 12 then {x = x + 4} else {x = x - y}", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((IntValue) value).getIntValue(), 9);

        parser = new Parser(new Tokenizer().tokenize("var x = 5; var y = 11; if y > 12 then {x = x + 4}", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertNull(value);

        parser = new Parser(new Tokenizer().tokenize("var x = 5; var y = 13; if y > 12 then {x = x + 4}", "TestFile.dl"));
        interpreter = new Interpreter();
        value = interpreter.interpretAST(parser.parse());
        assertEquals(((IntValue) value).getIntValue(), 9);
    }

    @Test
    public void testWhileBlock() throws ParserException, InterpreterException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 4; while x < 10 do {x = x + 1; print_int(x);}", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "5\n6\n7\n8\n9\n10\n");
    }

    @Test
    public void testSymbolTableContext() throws ParserException, InterpreterException {
        stdOut[0] = "";
        Parser parser = new Parser(new Tokenizer().tokenize("{\n" +
                "    var x = 1;\n" +
                "    {  # <----------- Inner scope begins\n" +
                "        var x = 2;  # Shadows the 'x' in the outer scope\n" +
                "        var y = 3;\n" +
                "        print_int(x);   ## Prints 2\n" +
                "        print_int(y);   ## Prints 3\n" +
                "    }  # <----------- Inner scope ends\n" +
                "    print_int(x);  # Prints 1\n" +
                "}", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "2\n3\n1\n");
    }

    @Test
    public void testReadIntFunction() throws ParserException, InterpreterException {
        stdOut[0] = "";
        System.setIn(new ByteArrayInputStream("4".getBytes()));
        Parser parser = new Parser(new Tokenizer().tokenize("var x = read_int(); while x < 10 do {x = x + 1; print_int(x);}", "TestFile.dl"));
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "5\n6\n7\n8\n9\n10\n");
    }

    @Test
    public void testCodeBlock() throws ParserException, InterpreterException {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser(tokenizer.tokenize("var a = 30; var b = 2; var c = -2; var d = 2; var e = 221;while a > 2 do {\n" +
                        "    if b <= 14 then {\n" +
                        "         while c - 2 < 3 do {\n" +
                        "               d = d * 3;\n" +
                        "               c = c + 1;\n" +
                        "         }\n" +
                        "    } else \n" +
                        "         e = e % 10;\na = a - 1;b = b + 1;" +
                        "    \n" +
                        "}print_int(a);print_int(b);print_int(c);print_int(d);print_int(e);",
                "Testfile.dl"));
        stdOut[0] = "";
        Interpreter interpreter = new Interpreter();
        interpreter.interpretAST(parser.parse());
        assertEquals(stdOut[0], "2\n30\n5\n4374\n1\n");
    }

    @Test
    public void testShortCircuiting() throws ParserException, InterpreterException {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser(tokenizer.tokenize("var evaluated_right_hand_side = false;\n" +
                "true or { evaluated_right_hand_side = true; true };\n" +
                "evaluated_right_hand_side", "Testfile.dl"));
        Interpreter interpreter = new Interpreter();
        Value value = interpreter.interpretAST(parser.parse());
        assertEquals(((BooleanValue) value).getValue(), false);
    }
}
