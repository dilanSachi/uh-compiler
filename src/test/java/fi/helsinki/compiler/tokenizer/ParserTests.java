package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.parser.*;
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
    public void testBasicAdditionWithRightAssociativity() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 2", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parseExpressionWithRightAssociativity();
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

    @Test
    public void testAdditionAndSubtractionWithRightAssociativity() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 225 - 10 + 8", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parseExpressionWithRightAssociativity();
        Literal leftLiteral = (Literal) binaryOp.getLeft();
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        leftLiteral = (Literal) rightOp.getLeft();
        operatorToken = rightOp.getOperatorToken();
        assertEquals(leftLiteral.getValue(), 225);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        rightOp = (BinaryOp) rightOp.getRight();
        leftLiteral = (Literal) rightOp.getLeft();
        operatorToken = rightOp.getOperatorToken();
        assertEquals(leftLiteral.getValue(), 10);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        assertEquals(((Literal) rightOp.getRight()).getValue(), 8);
    }

    @Test
    public void testOperationsWithLiteralsAndIdentifiers() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + a - 10 + xy", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parseExpression();
        Expression rightLiteral = binaryOp.getRight();
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(((Identifier) rightLiteral).getName(), "xy");
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        rightLiteral = leftOp.getRight();
        operatorToken = leftOp.getOperatorToken();
        assertEquals(((Literal) rightLiteral).getValue(), 10);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        leftOp = (BinaryOp) leftOp.getLeft();
        rightLiteral = leftOp.getRight();
        operatorToken = leftOp.getOperatorToken();
        assertEquals(((Identifier) rightLiteral).getName(), "a");
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        assertEquals(((Literal) leftOp.getLeft()).getValue(), 1);
    }

    @Test
    public void testOperationsWithLiteralsAndIdentifiersWithRightAssociativity() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + a - 10 + xy", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parseExpressionWithRightAssociativity();
        Expression leftLiteral = binaryOp.getLeft();
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(((Literal) leftLiteral).getValue(), 1);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        leftLiteral = rightOp.getLeft();
        operatorToken = rightOp.getOperatorToken();
        assertEquals(((Identifier) leftLiteral).getName(), "a");
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        rightOp = (BinaryOp) rightOp.getRight();
        leftLiteral = rightOp.getLeft();
        operatorToken = rightOp.getOperatorToken();
        assertEquals(((Literal) leftLiteral).getValue(), 10);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        assertEquals(((Identifier) rightOp.getRight()).getName(), "xy");
    }
}
