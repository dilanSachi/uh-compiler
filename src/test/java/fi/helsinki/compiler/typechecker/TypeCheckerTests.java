package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.interpreter.IntValue;
import fi.helsinki.compiler.interpreter.Interpreter;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.typechecker.types.IntType;
import fi.helsinki.compiler.typechecker.types.Type;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeCheckerTests {

    @Test
    public void testBasicAddition() throws TypeCheckerException {
        BinaryOp binaryOp = new BinaryOp(new Literal(1, null),
                new Token("+", TokenType.OPERATOR, null), new Literal(2, null), null);
        TypeChecker typeChecker = new TypeChecker();
        Optional<Type> result = typeChecker.checkType(binaryOp);
        assertTrue(result.get() instanceof IntType);
    }
}
