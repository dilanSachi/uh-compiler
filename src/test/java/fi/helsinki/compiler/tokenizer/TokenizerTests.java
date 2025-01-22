package fi.helsinki.compiler.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenizerTests {


    @Test
    public void testBasicKeywords() {
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize("if  3\nwhile", "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("if", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 0, 0)),
                new Token("3", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 0, 4)),
                new Token("while", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 1, 0))};
        assertTokens(tokens, expectedTokens);
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
                "}";
        Tokenizer testTokenizer = new Tokenizer();
        List<Token> tokens = testTokenizer.tokenize(sourceCode, "Testfile.dl");
        Token[] expectedTokens = new Token[]{
                new Token("var", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 0, 0)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 0, 4)),
                new Token(":", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 0, 5)),
                new Token("Int", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 0, 7)),
                new Token("=", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 0, 11)),
                new Token("read_int", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 0, 13)),
                new Token("(", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 0, 21)),
                new Token(")", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 0, 22)),
                new Token(";", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 0, 23)),
                new Token("print_int", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 1, 0)),
                new Token("(", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 1, 9)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 1, 10)),
                new Token(")", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 1, 11)),
                new Token(";", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 1, 12)),
                new Token("while", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 3, 0)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 3, 6)),
                new Token(">", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 3, 8)),
                new Token("1", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 3, 10)),
                new Token("do", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 3, 12)),
                new Token("{", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 3, 15)),
                new Token("if", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 4, 4)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 4, 7)),
                new Token("%", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 4, 9)),
                new Token("2", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 4, 11)),
                new Token("==", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 4, 13)),
                new Token("0", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 4, 16)),
                new Token("then", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 4, 18)),
                new Token("{", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 4, 23)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 5, 8)),
                new Token("=", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 5, 10)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 5, 12)),
                new Token("/", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 5, 14)),
                new Token("2", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 5, 16)),
                new Token(";", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 5, 17)),
                new Token("}", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 6, 4)),
                new Token("else", TokenType.KEYWORD, new TokenLocation("Testfile.dl", 6, 6)),
                new Token("{", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 6, 11)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 7, 8)),
                new Token("=", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 7, 10)),
                new Token("3", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 7, 12)),
                new Token("*", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 7, 13)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 7, 14)),
                new Token("+", TokenType.OPERATOR, new TokenLocation("Testfile.dl", 7, 16)),
                new Token("1", TokenType.INTEGER_LITERAL, new TokenLocation("Testfile.dl", 7, 18)),
                new Token(";", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 7, 19)),
                new Token("}", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 8, 4)),
                new Token("print_int", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 9, 4)),
                new Token("(", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 9, 13)),
                new Token("n", TokenType.IDENTIFIER, new TokenLocation("Testfile.dl", 9, 14)),
                new Token(")", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 9, 15)),
                new Token(";", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 9, 16)),
                new Token("}", TokenType.PUNCTUATION, new TokenLocation("Testfile.dl", 10, 0)),};
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
