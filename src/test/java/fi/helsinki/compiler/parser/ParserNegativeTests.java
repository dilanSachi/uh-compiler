package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParserNegativeTests {

    @Test
    public void testEmptyTokenList() {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("", "Testfile.dl"));
        try {
            testParser.parse();
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
            testParser.parseInternal();
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
            testParser.parse();
        } catch (ParserException e) {
            assertEquals("Testfile.dl: L->0, C->7: expected one of: =", e.getMessage());
            return;
        }
        fail("Expected an exception to be thrown");
    }

    @ParameterizedTest
    @MethodSource ("dataProvider")
    void testInvalidBlocks(String sourceCode, String error) {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize(sourceCode, "Testfile.dl"));
        try {
            testParser.parse();
        } catch (ParserException e) {
            assertEquals(error, e.getMessage());
            return;
        }
        fail("Expected an exception to be thrown");
    }

    static Stream<Arguments> dataProvider() {
        return Stream.of(
            Arguments.of("{ a b }", "Parsing failed. Invalid tokens found. Expected ';', but found Text: b, Type: IDENTIFIER, Location: Testfile.dl: L->0, C->4"),
            Arguments.of("{ if true then { a } b c }", "Parsing failed. Invalid tokens found. Expected ';', but found Text: c, Type: IDENTIFIER, Location: Testfile.dl: L->0, C->23"));
    }
}
