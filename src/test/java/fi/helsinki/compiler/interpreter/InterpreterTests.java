package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.Interpreter.Interpreter;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTests {

    @Test
    public void testBasicAddition() throws ParserException, InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(1), new Token("+", TokenType.OPERATOR, null), new Literal(2));
        Interpreter interpreter = new Interpreter();
        Object result = interpreter.interpret(binaryOp);
        assertEquals(result, 3);
    }
}
