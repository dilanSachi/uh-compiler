package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.parser.Parser;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCheckerNegativeTests {

    @Test
    public void testBasicAdditionInvalidConditionType() throws ParserException {
        Parser parser = new Parser(new Tokenizer().tokenize("var x = 2; var y = false; if y > 12 " +
                "then {x = x + 4} else {x = x - y}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.checkType(parser.parse());
        } catch (TypeCheckerException e) {
            assertEquals(e.getMessage(), "Expected an Int type for '>' operator. Instead found Optional[Boolean], Optional[Int]");
            return;
        }
        fail("Expected an error");
    }

    @Test
    public void testBasicAdditionInvalidBodyType() throws ParserException {
        Parser parser = new Parser(new Tokenizer().tokenize("var x = false; var y = 2; if y > 12 " +
                "then {x = x + 4} else {x = x - y}", "TestFile.dl"));
        TypeChecker typeChecker = new TypeChecker();
        try {
            typeChecker.checkType(parser.parse());
        } catch (TypeCheckerException e) {
            assertEquals(e.getMessage(), "Expected an Int type for '+' operator. Instead found Optional[Boolean], Optional[Int]");
            return;
        }
        fail("Expected an error");
    }
}
