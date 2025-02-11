package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserNegativeTests {

    @Test
    public void testEmptyTokenList() {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("", "Testfile.dl"));
        try {
            testParser.parse2();
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "Cannot parse empty token list");
            return;
        }
        fail("Expected an exception to be thrown");
    }

    @Test
    public void testInvalidTokenList() {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 3 + c d", "Testfile.dl"));
        try {
            testParser.parse();
        } catch (ParserException e) {
            assertEquals("Parsing failed. Invalid tokens found: [Text: d, Type: IDENTIFIER, Location: Testfile.dl: L->0, C->10]", e.getMessage());
            return;
        }
        fail("Expected an exception to be thrown");
    }

    @Test
    public void testInvalidVariableDefinition() {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("var x: Int", "Testfile.dl"));
        try {
            testParser.parse2();
        } catch (ParserException e) {
            assertEquals("Testfile.dl: L->0, C->7: expected one of: =", e.getMessage());
            return;
        }
        fail("Expected an exception to be thrown");
    }

    @ParameterizedTest @Disabled
    @ValueSource(strings = {"{ a b }", "{ if true then { a } b c }"})
    void testInvalidBlocks(String sourceCode) {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize(sourceCode, "Testfile.dl"));
        try {
            testParser.parse2();
        } catch (ParserException e) {
            assertEquals("Testfile.dl: L->0, C->7: expected one of: =", e.getMessage());
            return;
        }
        fail("Expected an exception to be thrown");
    }
}
