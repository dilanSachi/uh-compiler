package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTests {

    @Test
    public void testBasicAddition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 2", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Literal leftLiteral = (Literal) binaryOp.getLeft();
        Token operatorToken = binaryOp.getOperatorToken();
        Literal rightLiteral = (Literal) binaryOp.getRight();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(rightLiteral.getValue(), 2);
        assertEquals(operatorToken.getText(), "+");
        Assertions.assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
    }

    @Test
    public void testBasicMultiplication() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 * 2", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Literal leftLiteral = (Literal) binaryOp.getLeft();
        Token operatorToken = binaryOp.getOperatorToken();
        Literal rightLiteral = (Literal) binaryOp.getRight();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(rightLiteral.getValue(), 2);
        assertEquals(operatorToken.getText(), "*");
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
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
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
    public void testMultipleOperations() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 * 225 - 10 / 8", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        Literal leftLiteral = (Literal) leftOp.getLeft();
        Literal rightLiteral = (Literal) leftOp.getRight();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(rightLiteral.getValue(), 225);
        operatorToken = leftOp.getOperatorToken();
        assertEquals(operatorToken.getText(), "*");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        leftLiteral = (Literal) rightOp.getLeft();
        rightLiteral = (Literal) rightOp.getRight();
        assertEquals(leftLiteral.getValue(), 10);
        assertEquals(rightLiteral.getValue(), 8);
        operatorToken = rightOp.getOperatorToken();
        assertEquals(operatorToken.getText(), "/");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
    }

    @Test
    public void testMultipleOperationsWithParentheses() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 * (29 / 32) * 225 - 10 / 8 + (127 - 38)", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp1 = (BinaryOp) binaryOp.getLeft();
        BinaryOp rightOp1 = (BinaryOp) binaryOp.getRight();
        operatorToken = leftOp1.getOperatorToken();
        assertEquals(operatorToken.getText(), "-");
        operatorToken = rightOp1.getOperatorToken();
        Literal leftliteral1 = (Literal) rightOp1.getLeft();
        Literal rightliteral1 = (Literal) rightOp1.getRight();
        assertEquals(leftliteral1.getValue(), 127);
        assertEquals(rightliteral1.getValue(), 38);
        assertEquals(operatorToken.getText(), "-");
        BinaryOp leftOp2 = (BinaryOp) leftOp1.getLeft();
        assertEquals(leftOp2.getOperatorToken().getText(), "*");
        Literal rightliteral3 = (Literal) leftOp2.getRight();
        assertEquals(rightliteral3.getValue(), 225);
        BinaryOp rightOp2 = (BinaryOp) leftOp1.getRight();
        assertEquals(rightOp2.getOperatorToken().getText(), "/");
        Literal leftliteral2 = (Literal) rightOp2.getLeft();
        Literal rightliteral2 = (Literal) rightOp2.getRight();
        assertEquals(leftliteral2.getValue(), 10);
        assertEquals(rightliteral2.getValue(), 8);
        BinaryOp leftOp3 = (BinaryOp) leftOp2.getLeft();
        assertEquals(leftOp3.getOperatorToken().getText(), "*");
        assertEquals(((Literal) leftOp3.getLeft()).getValue(), 1);
        BinaryOp rightOp3 = (BinaryOp) leftOp3.getRight();
        assertEquals(rightOp3.getOperatorToken().getText(), "/");
        assertEquals(((Literal) rightOp3.getLeft()).getValue(), 29);
        assertEquals(((Literal) rightOp3.getRight()).getValue(), 32);
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
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
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

    @Test
    public void testIfCondition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("if a then b + c else d + e; d + ex", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 2);
    }

}
