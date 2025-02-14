package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.Interpreter.IntValue;
import fi.helsinki.compiler.Interpreter.Interpreter;
import fi.helsinki.compiler.Interpreter.Value;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTests {

    @Test
    public void testBasicAddition() throws InterpreterException {
        BinaryOp binaryOp = new BinaryOp(new Literal(1, null),
                new Token("+", TokenType.OPERATOR, null), new Literal(2, null), null);
        Interpreter interpreter = new Interpreter();
        Optional<Value> result = interpreter.interpret(binaryOp, null);
        assertEquals(((IntValue) result.get()).getIntValue(), 3);
    }
}
