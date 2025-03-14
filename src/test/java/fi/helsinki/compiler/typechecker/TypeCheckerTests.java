package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.common.expressions.BinaryOp;
import fi.helsinki.compiler.common.expressions.IntLiteral;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import fi.helsinki.compiler.common.types.IntType;
import fi.helsinki.compiler.common.types.Type;
import fi.helsinki.compiler.common.types.UnitType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCheckerTests {

    @Test
    public void testBasicAddition() throws TypeCheckerException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("+", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> result = typeChecker.checkType(binaryOp);
        assertTrue(result.get() instanceof IntType);
    }

    @Test
    public void testBasicSubtraction() throws TypeCheckerException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("-", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> result = typeChecker.checkType(binaryOp);
        assertTrue(result.get() instanceof IntType);
    }

    @Test
    public void testBasicMultiplication() throws TypeCheckerException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("*", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> result = typeChecker.checkType(binaryOp);
        assertTrue(result.get() instanceof IntType);
    }

    @Test
    public void testBasicDivision() throws TypeCheckerException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("/", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> result = typeChecker.checkType(binaryOp);
        assertTrue(result.get() instanceof IntType);
    }

    @Test
    public void testBasicModulus() throws TypeCheckerException {
        BinaryOp binaryOp = new BinaryOp(new IntLiteral(1L, null),
                new Token("%", TokenType.OPERATOR, null), new IntLiteral(2L, null), null);
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> result = typeChecker.checkType(binaryOp);
        assertTrue(result.get() instanceof IntType);
    }

    @Test
    public void testIfElseBlock() throws ParserException, TypeCheckerException {
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 5; var y = 12; if y > 12 then {x = x + 4} else {x = x - y}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> type = typeChecker.checkType(parser.parse());
        assertTrue(type.get() instanceof IntType);
    }

    @Test
    public void testCodeBlock() throws ParserException, TypeCheckerException {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser(tokenizer.tokenize("var a = 30; var b = 2; var c = -2; var d = 2; var e = 221;while a > 2 do {\n" +
                        "    if b <= 14 then {\n" +
                        "         while c - 2 < 3 do {\n" +
                        "               d = d * 3;\n" +
                        "               c = c + 1;\n" +
                        "         }\n" +
                        "    } else {\n" +
                        "         e = e % 10;\na = a - 1;b = b + 1;" +
                        "    }\n" +
                        "}print_int(a);print_int(b);print_int(c);print_int(d);print_int(e);",
                "Testfile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        Optional<Type> type = typeChecker.checkType(expression);
        assertTrue(type.get() instanceof UnitType);
    }

    @Test
    public void testFunctionDefinition() throws ParserException, TypeCheckerException {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser(tokenizer.tokenize("fun f(n: Int): Int {\n" +
                        "  if n > 5 then {\n" +
                        "    return 5;\n" +
                        "  }\n" +
                        "  123\n" +
                        "}",
                "Testfile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        Optional<Type> type = typeChecker.checkType(expression);
        assertTrue(type.get() instanceof IntType);
    }

    @Test
    public void testFunctionDefinitionWithFunctionCall() throws ParserException, TypeCheckerException {
        Tokenizer tokenizer = new Tokenizer();
        Parser parser = new Parser(tokenizer.tokenize("fun f(n: Int): Int {\n" +
                        "  if n > 5 then {\n" +
                        "    return 5;\n" +
                        "  }\n" +
                        "  123\n" +
                        "}\n" +
                        "\n" +
                        "f(10)\n",
                "Testfile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        Expression expression = parser.parse();
        Optional<Type> type = typeChecker.checkType(expression);
        assertTrue(type.get() instanceof IntType);
    }
}
