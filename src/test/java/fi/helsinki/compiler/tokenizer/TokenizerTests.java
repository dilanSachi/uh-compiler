package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.Location;
import fi.helsinki.compiler.exceptions.TokenizeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTests {

    @Test
    public void testBasicKeywords() {
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize("if  3\nwhile", "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("if", TokenType.KEYWORD, new Location("Testfile.dl", 0, 0)),
                new Token("3", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 0, 4)),
                new Token("while", TokenType.KEYWORD, new Location("Testfile.dl", 1, 0))};
        assertTokens(tokens, expectedTokens);
    }

    @Test
    public void testSimpleStatement() {
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize("if a <= bee then print_int(123)", "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("if", TokenType.KEYWORD, new Location("Testfile.dl", 0, 0)),
                new Token("a", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 3)),
                new Token("<=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 5)),
                new Token("bee", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 8)),
                new Token("then", TokenType.KEYWORD, new Location("Testfile.dl", 0, 12)),
                new Token("print_int", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 17)),
                new Token("(", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 26)),
                new Token("123", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 0, 27)),
                new Token(")", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 30))};
        assertTokens(tokens, expectedTokens);
    }

    @Test
    public void testStringLiteralWithDifferentCharacters() {
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize(
                "var s : String = \"This is a str|ing 093!@#$%^&*()-_+=][}{`~.>,</?\";", "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("var", TokenType.KEYWORD, new Location("Testfile.dl", 0, 0)),
                new Token("s", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 4)),
                new Token(":", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 6)),
                new Token("String", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 8)),
                new Token("=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 15)),
                new Token("\"This is a str|ing 093!@#$%^&*()-_+=][}{`~.>,</?\"", TokenType.STRING_LITERAL,
                        new Location("Testfile.dl", 0, 17)),
                new Token(";", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 66))};
        assertTokens(tokens, expectedTokens);
    }

    @Test
    public void testMultipleComparisonOperators() {
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize(
                "a=(b and c) + d != e >= f or g <= h - i < j * k > l", "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("a", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 0)),
                new Token("=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 1)),
                new Token("(", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 2)),
                new Token("b", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 3)),
                new Token("and", TokenType.OPERATOR, new Location("Testfile.dl", 0, 5)),
                new Token("c", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 9)),
                new Token(")", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 10)),
                new Token("+", TokenType.OPERATOR, new Location("Testfile.dl", 0, 12)),
                new Token("d", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 14)),
                new Token("!=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 16)),
                new Token("e", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 19)),
                new Token(">=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 21)),
                new Token("f", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 24)),
                new Token("or", TokenType.OPERATOR, new Location("Testfile.dl", 0, 26)),
                new Token("g", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 29)),
                new Token("<=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 31)),
                new Token("h", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 34)),
                new Token("-", TokenType.OPERATOR, new Location("Testfile.dl", 0, 36)),
                new Token("i", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 38)),
                new Token("<", TokenType.OPERATOR, new Location("Testfile.dl", 0, 40)),
                new Token("j", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 42)),
                new Token("*", TokenType.OPERATOR, new Location("Testfile.dl", 0, 44)),
                new Token("k", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 46)),
                new Token(">", TokenType.OPERATOR, new Location("Testfile.dl", 0, 48)),
                new Token("l", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 50))};
        assertTokens(tokens, expectedTokens);
    }

    @Test
    public void testInvalidSimpleStatement() {
        Tokenizer testTokenizer = new Tokenizer();
        try {
            testTokenizer.tokenize("if a <= bee @then print_int(123)", "Testfile.dl");
        } catch (TokenizeException e) {
            assertEquals(e.getMessage(), "Found invalid token: '@' at line: 0 and column: 12");
            return;
        }
        fail("Expected a TokenizeException");
    }

    @Test
    public void testSimpleProgram() {
        String sourceCode = "var n: Int = read_int();\n" +
                "print_int(n);\n" +
                "// This is a while loop\n" +
                "while n > 1 do {\n" +
                "    if n % 2 == 0 then { // checking if n is even\n" +
                "        n = n / 2;\n" +
                "    } else {\n" +
                "        n = 3*n + 1;\n" +
                "    }\n" +
                "    print_int(n);\n" +
                "}" +
                "## End comment";
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize(sourceCode, "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("var", TokenType.KEYWORD, new Location("Testfile.dl", 0, 0)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 4)),
                new Token(":", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 5)),
                new Token("Int", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 7)),
                new Token("=", TokenType.OPERATOR, new Location("Testfile.dl", 0, 11)),
                new Token("read_int", TokenType.IDENTIFIER, new Location("Testfile.dl", 0, 13)),
                new Token("(", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 21)),
                new Token(")", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 22)),
                new Token(";", TokenType.PUNCTUATION, new Location("Testfile.dl", 0, 23)),
                new Token("print_int", TokenType.IDENTIFIER, new Location("Testfile.dl", 1, 0)),
                new Token("(", TokenType.PUNCTUATION, new Location("Testfile.dl", 1, 9)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 1, 10)),
                new Token(")", TokenType.PUNCTUATION, new Location("Testfile.dl", 1, 11)),
                new Token(";", TokenType.PUNCTUATION, new Location("Testfile.dl", 1, 12)),
                new Token("while", TokenType.KEYWORD, new Location("Testfile.dl", 3, 0)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 3, 6)),
                new Token(">", TokenType.OPERATOR, new Location("Testfile.dl", 3, 8)),
                new Token("1", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 3, 10)),
                new Token("do", TokenType.KEYWORD, new Location("Testfile.dl", 3, 12)),
                new Token("{", TokenType.PUNCTUATION, new Location("Testfile.dl", 3, 15)),
                new Token("if", TokenType.KEYWORD, new Location("Testfile.dl", 4, 4)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 4, 7)),
                new Token("%", TokenType.OPERATOR, new Location("Testfile.dl", 4, 9)),
                new Token("2", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 4, 11)),
                new Token("==", TokenType.OPERATOR, new Location("Testfile.dl", 4, 13)),
                new Token("0", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 4, 16)),
                new Token("then", TokenType.KEYWORD, new Location("Testfile.dl", 4, 18)),
                new Token("{", TokenType.PUNCTUATION, new Location("Testfile.dl", 4, 23)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 5, 8)),
                new Token("=", TokenType.OPERATOR, new Location("Testfile.dl", 5, 10)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 5, 12)),
                new Token("/", TokenType.OPERATOR, new Location("Testfile.dl", 5, 14)),
                new Token("2", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 5, 16)),
                new Token(";", TokenType.PUNCTUATION, new Location("Testfile.dl", 5, 17)),
                new Token("}", TokenType.PUNCTUATION, new Location("Testfile.dl", 6, 4)),
                new Token("else", TokenType.KEYWORD, new Location("Testfile.dl", 6, 6)),
                new Token("{", TokenType.PUNCTUATION, new Location("Testfile.dl", 6, 11)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 7, 8)),
                new Token("=", TokenType.OPERATOR, new Location("Testfile.dl", 7, 10)),
                new Token("3", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 7, 12)),
                new Token("*", TokenType.OPERATOR, new Location("Testfile.dl", 7, 13)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 7, 14)),
                new Token("+", TokenType.OPERATOR, new Location("Testfile.dl", 7, 16)),
                new Token("1", TokenType.INTEGER_LITERAL, new Location("Testfile.dl", 7, 18)),
                new Token(";", TokenType.PUNCTUATION, new Location("Testfile.dl", 7, 19)),
                new Token("}", TokenType.PUNCTUATION, new Location("Testfile.dl", 8, 4)),
                new Token("print_int", TokenType.IDENTIFIER, new Location("Testfile.dl", 9, 4)),
                new Token("(", TokenType.PUNCTUATION, new Location("Testfile.dl", 9, 13)),
                new Token("n", TokenType.IDENTIFIER, new Location("Testfile.dl", 9, 14)),
                new Token(")", TokenType.PUNCTUATION, new Location("Testfile.dl", 9, 15)),
                new Token(";", TokenType.PUNCTUATION, new Location("Testfile.dl", 9, 16)),
                new Token("}", TokenType.PUNCTUATION, new Location("Testfile.dl", 10, 0)),};
        assertTokens(tokens, expectedTokens);
    }

    private void assertTokens(List<Token> resultTokenList, Token[] expectedTokens) {
        assertEquals(expectedTokens.length, resultTokenList.size());
        Token[] resultTokens = resultTokenList.toArray(new Token[resultTokenList.size()]);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getText(), resultTokens[i].getText());
            assertEquals(expectedTokens[i].getTokenType(), resultTokens[i].getTokenType());
            assertEquals(expectedTokens[i].getTokenLocation().getFile(), resultTokens[i].getTokenLocation().getFile());
            assertEquals(expectedTokens[i].getTokenLocation().getLine(), resultTokens[i].getTokenLocation().getLine());
            assertEquals(expectedTokens[i].getTokenLocation().getColumn(), resultTokens[i].getTokenLocation().getColumn());
        }
    }
}
