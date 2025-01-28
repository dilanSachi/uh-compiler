package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTests {

    @Test
    public void testBasicAddition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 2", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parseExpression();
        Literal leftLiteral = (Literal) binaryOp.getLeft();
        Token operatorToken = binaryOp.getOperatorToken();
        Literal rightLiteral = (Literal) binaryOp.getRight();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(rightLiteral.getValue(), 2);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
    }

    @Test
    public void testAdditionAndSubtraction() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 225 - 10 + 8", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parseExpression();
        Literal rightLiteral = (Literal) binaryOp.getRight();
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(rightLiteral.getValue(), 8);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        rightLiteral = (Literal) leftOp.getRight();
        operatorToken = leftOp.getOperatorToken();
        assertEquals(rightLiteral.getValue(), 10);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        leftOp = (BinaryOp) leftOp.getLeft();
        rightLiteral = (Literal) leftOp.getRight();
        operatorToken = leftOp.getOperatorToken();
        assertEquals(rightLiteral.getValue(), 225);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        assertEquals(((Literal)leftOp.getLeft()).getValue(), 1);
    }
}
