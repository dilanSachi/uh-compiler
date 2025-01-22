package fi.helsinki.compiler.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTests {

    @Test
    public void testBasicLiteral() {
        Tokenizer testTokenizer = new Tokenizer();
        List<String> tokens = testTokenizer.tokenize("if  3\nwhile");
        String[] tokensArray = tokens.toArray(new String[tokens.size()]);
        String[] expectedTokens = new String[]{"if", "3", "while"};
        assertArrayEquals(tokensArray, expectedTokens);
    }
}
