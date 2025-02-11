package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTests {

    @Test
    public void testBasicAddition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 2", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Literal leftLiteral = (Literal) binaryOp.getLeft();
        Token operatorToken = binaryOp.getOperator();
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
        Token operatorToken = binaryOp.getOperator();
        Literal rightLiteral = (Literal) binaryOp.getRight();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(rightLiteral.getValue(), 2);
        assertEquals(operatorToken.getText(), "*");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
    }

    @Test
    public void testAdditionAndSubtraction() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 225 - 10 + 8", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Literal rightLiteral = (Literal) binaryOp.getRight();
        Token operatorToken = binaryOp.getOperator();
        assertEquals(rightLiteral.getValue(), 8);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        rightLiteral = (Literal) leftOp.getRight();
        operatorToken = leftOp.getOperator();
        assertEquals(rightLiteral.getValue(), 10);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        leftOp = (BinaryOp) leftOp.getLeft();
        rightLiteral = (Literal) leftOp.getRight();
        operatorToken = leftOp.getOperator();
        assertEquals(rightLiteral.getValue(), 225);
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        assertEquals(((Literal) leftOp.getLeft()).getValue(), 1);
    }

    @Test
    public void testAdditionAndSubtractionWithRemainder() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + 225 % 10 - 8", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        Literal rightLiteral = (Literal) binaryOp.getRight();
        Token operatorToken = binaryOp.getOperator();
        assertEquals(rightLiteral.getValue(), 8);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        BinaryOp rightOp1 = (BinaryOp) leftOp.getRight();
        assertEquals(((Literal) rightOp1.getLeft()).getValue(), 225);
        assertEquals(((Literal) rightOp1.getRight()).getValue(), 10);
        assertEquals(rightOp1.getOperator().getText(), "%");
        assertEquals(((Literal) leftOp.getLeft()).getValue(), 1);
        assertEquals(leftOp.getOperator().getText(), "+");
    }

    @Test
    public void testEqualOperator() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x = y + 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperator().getText(), "=");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(((Identifier) rightOp.getLeft()).getName(), "y");
        assertEquals(((Literal) rightOp.getRight()).getValue(), 20);
        assertEquals(rightOp.getOperator().getText(), "+");
    }

    @Test
    public void testDoubleEqualOperator() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x == y + 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperator().getText(), "==");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(((Identifier) rightOp.getLeft()).getName(), "y");
        assertEquals(((Literal) rightOp.getRight()).getValue(), 20);
        assertEquals(rightOp.getOperator().getText(), "+");
    }

    @Test
    public void testEqualPrecedence() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("a = b = 4", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "a");
        assertEquals(binaryOp.getOperator().getText(), "=");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(((Identifier) rightOp.getLeft()).getName(), "b");
        assertEquals(((Literal) rightOp.getRight()).getValue(), 4);
        assertEquals(rightOp.getOperator().getText(), "=");
    }

    @Test
    public void testUnaryOperator() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("2 + 3 - 4 - - not 5", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        BinaryOp binaryOp1 = (BinaryOp) binaryOp.getLeft();
        UnaryOp unaryOp = (UnaryOp) binaryOp.getRight();
        assertEquals(unaryOp.getOperator().getText(), "-");
        assertEquals(((UnaryOp) unaryOp.getExpression()).getOperator().getText(), "not");
        assertEquals(((Literal) ((UnaryOp) unaryOp.getExpression()).getExpression()).getValue(), 5);
        assertEquals(binaryOp1.getOperator().getText(), "-");
        assertEquals(((Literal) binaryOp1.getRight()).getValue(), 4);
        BinaryOp binaryOp2 = (BinaryOp) binaryOp1.getLeft();
        assertEquals(binaryOp2.getOperator().getText(), "+");
        assertEquals(((Literal) binaryOp2.getLeft()).getValue(), 2);
        assertEquals(((Literal) binaryOp2.getRight()).getValue(), 3);
    }

    @Test
    public void testNestedUnaryOperator() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("- not -2", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        UnaryOp unaryOp = (UnaryOp) block.getExpressionList().get(0);
        assertEquals(unaryOp.getOperator().getText(), "-");
        unaryOp = (UnaryOp) unaryOp.getExpression();
        assertEquals(unaryOp.getOperator().getText(), "not");
        unaryOp = (UnaryOp) unaryOp.getExpression();
        assertEquals(unaryOp.getOperator().getText(), "-");
        assertEquals(((Literal) unaryOp.getExpression()).getValue(), 2);
    }

    @Test
    public void testEqualOperator2() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x + y = 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 20);
        assertEquals(binaryOp.getOperator().getText(), "=");
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        assertEquals(((Identifier) leftOp.getLeft()).getName(), "x");
        assertEquals(((Identifier) leftOp.getRight()).getName(), "y");
        assertEquals(leftOp.getOperator().getText(), "+");
    }

    @Test
    public void testEqualOperator3() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x + a + b + c = y - 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp1 = (BinaryOp) block.getExpressionList().get(0);
        BinaryOp binaryOp2 = (BinaryOp) binaryOp1.getLeft();
        BinaryOp binaryOp3 = (BinaryOp) binaryOp1.getRight();
        assertEquals(((Identifier) binaryOp3.getLeft()).getName(), "y");
        assertEquals(((Literal) binaryOp3.getRight()).getValue(), 20);
        assertEquals(binaryOp3.getOperator().getText(), "-");
        assertEquals(((Identifier) binaryOp2.getRight()).getName(), "c");
        assertEquals(binaryOp2.getOperator().getText(), "+");
        BinaryOp binaryOp4 = (BinaryOp) binaryOp2.getLeft();
        assertEquals(((Identifier) binaryOp4.getRight()).getName(), "b");
        assertEquals(binaryOp4.getOperator().getText(), "+");
        BinaryOp binaryOp5 = (BinaryOp) binaryOp4.getLeft();
        assertEquals(((Identifier) binaryOp5.getRight()).getName(), "a");
        assertEquals(binaryOp5.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp5.getLeft()).getName(), "x");
    }

    @Test
    public void testComparisonOperatorWithConditional() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize(
                "if a > 2 then b = c + d else if e == 3 then f = g * h else i = j - k", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        ConditionalOp conditionalOp = (ConditionalOp) block.getExpressionList().get(0);
        BinaryOp condition = (BinaryOp) conditionalOp.getCondition();
        BinaryOp thenBlock = (BinaryOp) conditionalOp.getThenBlock();
        ConditionalOp elseBlock = (ConditionalOp) conditionalOp.getElseBlock();
        assertEquals(((Identifier) condition.getLeft()).getName(), "a");
        assertEquals(condition.getOperator().getText(), ">");
        assertEquals(((Literal) condition.getRight()).getValue(), 2);
        assertEquals(((Identifier) thenBlock.getLeft()).getName(), "b");
        assertEquals(thenBlock.getOperator().getText(), "=");
        BinaryOp binaryOp = (BinaryOp) thenBlock.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "c");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "d");
        condition = (BinaryOp) elseBlock.getCondition();
        thenBlock = (BinaryOp) elseBlock.getThenBlock();
        assertEquals(((Identifier) condition.getLeft()).getName(), "e");
        assertEquals(condition.getOperator().getText(), "==");
        assertEquals(((Literal) condition.getRight()).getValue(), 3);
        assertEquals(((Identifier) thenBlock.getLeft()).getName(), "f");
        assertEquals(thenBlock.getOperator().getText(), "=");
        binaryOp = (BinaryOp) thenBlock.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "g");
        assertEquals(binaryOp.getOperator().getText(), "*");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "h");
        BinaryOp elseBlock2 = (BinaryOp) elseBlock.getElseBlock();
        assertEquals(((Identifier) elseBlock2.getLeft()).getName(), "i");
        assertEquals(elseBlock2.getOperator().getText(), "=");
        binaryOp = (BinaryOp) elseBlock2.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "j");
        assertEquals(binaryOp.getOperator().getText(), "-");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "k");
    }

    @Test
    public void testMultipleComparisonOperators() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize(
                "a=(b and c) + d != e >= f or g <= h - i < j * k > l", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "a");
        assertEquals(binaryOp.getOperator().getText(), "=");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(rightOp.getOperator().getText(), "or");
        BinaryOp binaryOp1 = (BinaryOp) rightOp.getRight();
        BinaryOp binaryOp2 = (BinaryOp) rightOp.getLeft();
        assertEquals(binaryOp1.getOperator().getText(), ">");
        assertEquals(((Identifier) binaryOp1.getRight()).getName(), "l");
        assertEquals(binaryOp2.getOperator().getText(), "!=");
        BinaryOp binaryOp3 = (BinaryOp) binaryOp1.getLeft();
        assertEquals(binaryOp3.getOperator().getText(), "<");
        BinaryOp binaryOp4 = (BinaryOp) binaryOp2.getLeft();
        BinaryOp binaryOp5 = (BinaryOp) binaryOp2.getRight();
        assertEquals(binaryOp4.getOperator().getText(), "+");
        assertEquals(binaryOp5.getOperator().getText(), ">=");
        assertEquals(((Identifier) binaryOp5.getRight()).getName(), "f");
        assertEquals(((Identifier) binaryOp5.getLeft()).getName(), "e");
        assertEquals(((Identifier) binaryOp4.getRight()).getName(), "d");
        BinaryOp binaryOp6 = (BinaryOp) binaryOp4.getLeft();
        assertEquals(binaryOp6.getOperator().getText(), "and");
        assertEquals(((Identifier) binaryOp6.getRight()).getName(), "c");
        assertEquals(((Identifier) binaryOp6.getLeft()).getName(), "b");
        BinaryOp binaryOp7 = (BinaryOp) binaryOp3.getLeft();
        BinaryOp binaryOp8 = (BinaryOp) binaryOp3.getRight();
        assertEquals(binaryOp7.getOperator().getText(), "<=");
        assertEquals(binaryOp8.getOperator().getText(), "*");
        assertEquals(((Identifier) binaryOp8.getRight()).getName(), "k");
        assertEquals(((Identifier) binaryOp8.getLeft()).getName(), "j");
        assertEquals(((Identifier) binaryOp7.getLeft()).getName(), "g");
        BinaryOp binaryOp9 = (BinaryOp) binaryOp7.getRight();
        assertEquals(binaryOp9.getOperator().getText(), "-");
        assertEquals(((Identifier) binaryOp9.getRight()).getName(), "i");
        assertEquals(((Identifier) binaryOp9.getLeft()).getName(), "h");
    }

    @Test
    public void testMultipleOperations() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 * 225 - 10 / 8", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Token operatorToken = binaryOp.getOperator();
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        Literal leftLiteral = (Literal) leftOp.getLeft();
        Literal rightLiteral = (Literal) leftOp.getRight();
        assertEquals(leftLiteral.getValue(), 1);
        assertEquals(rightLiteral.getValue(), 225);
        operatorToken = leftOp.getOperator();
        assertEquals(operatorToken.getText(), "*");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        leftLiteral = (Literal) rightOp.getLeft();
        rightLiteral = (Literal) rightOp.getRight();
        assertEquals(leftLiteral.getValue(), 10);
        assertEquals(rightLiteral.getValue(), 8);
        operatorToken = rightOp.getOperator();
        assertEquals(operatorToken.getText(), "/");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
    }

    @Test
    public void testMultipleOperationsWithParentheses() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 * (29 / 32) * 225 - 10 / 8 + (127 - 38)", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Token operatorToken = binaryOp.getOperator();
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp1 = (BinaryOp) binaryOp.getLeft();
        BinaryOp rightOp1 = (BinaryOp) binaryOp.getRight();
        operatorToken = leftOp1.getOperator();
        assertEquals(operatorToken.getText(), "-");
        operatorToken = rightOp1.getOperator();
        Literal leftliteral1 = (Literal) rightOp1.getLeft();
        Literal rightliteral1 = (Literal) rightOp1.getRight();
        assertEquals(leftliteral1.getValue(), 127);
        assertEquals(rightliteral1.getValue(), 38);
        assertEquals(operatorToken.getText(), "-");
        BinaryOp leftOp2 = (BinaryOp) leftOp1.getLeft();
        assertEquals(leftOp2.getOperator().getText(), "*");
        Literal rightliteral3 = (Literal) leftOp2.getRight();
        assertEquals(rightliteral3.getValue(), 225);
        BinaryOp rightOp2 = (BinaryOp) leftOp1.getRight();
        assertEquals(rightOp2.getOperator().getText(), "/");
        Literal leftliteral2 = (Literal) rightOp2.getLeft();
        Literal rightliteral2 = (Literal) rightOp2.getRight();
        assertEquals(leftliteral2.getValue(), 10);
        assertEquals(rightliteral2.getValue(), 8);
        BinaryOp leftOp3 = (BinaryOp) leftOp2.getLeft();
        assertEquals(leftOp3.getOperator().getText(), "*");
        assertEquals(((Literal) leftOp3.getLeft()).getValue(), 1);
        BinaryOp rightOp3 = (BinaryOp) leftOp3.getRight();
        assertEquals(rightOp3.getOperator().getText(), "/");
        assertEquals(((Literal) rightOp3.getLeft()).getValue(), 29);
        assertEquals(((Literal) rightOp3.getRight()).getValue(), 32);
    }

    @Test
    public void testOperationsWithLiteralsAndIdentifiers() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + a - 10 + xy", "Testfile.dl"));
        BinaryOp binaryOp = (BinaryOp) testParser.parse();
        Expression rightLiteral = binaryOp.getRight();
        Token operatorToken = binaryOp.getOperator();
        assertEquals(((Identifier) rightLiteral).getName(), "xy");
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        rightLiteral = leftOp.getRight();
        operatorToken = leftOp.getOperator();
        assertEquals(((Literal) rightLiteral).getValue(), 10);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        leftOp = (BinaryOp) leftOp.getLeft();
        rightLiteral = leftOp.getRight();
        operatorToken = leftOp.getOperator();
        assertEquals(((Identifier) rightLiteral).getName(), "a");
        assertEquals(operatorToken.getText(), "+");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        assertEquals(((Literal) leftOp.getLeft()).getValue(), 1);
    }

    @Test
    public void testIfCondition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("if a then b + c; d + ex", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 2);
        ConditionalOp conditionalOp = (ConditionalOp) expressionList.get(0);
        assertEquals(conditionalOp.getName(), "if");
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "a");
        BinaryOp binaryOp = (BinaryOp) conditionalOp.getThenBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "b");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "c");
        assertEquals(conditionalOp.getElseBlock(), null);
        binaryOp = (BinaryOp) expressionList.get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "ex");
    }

    @Test
    public void testIfElseCondition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("2 - y;if a then b + c else d + e; d + ex", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 3);
        BinaryOp binaryOp = (BinaryOp) expressionList.get(0);
        assertEquals(((Literal) binaryOp.getLeft()).getValue(), 2);
        assertEquals(binaryOp.getOperator().getText(), "-");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "y");
        ConditionalOp conditionalOp = (ConditionalOp) expressionList.get(1);
        assertEquals(conditionalOp.getName(), "if");
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "a");
        binaryOp = (BinaryOp) conditionalOp.getThenBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "b");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "c");
        binaryOp = (BinaryOp) conditionalOp.getElseBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "e");
        binaryOp = (BinaryOp) expressionList.get(2);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "ex");
    }

    @Test
    public void testIfWithBinaryOpCondition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("1 + if a then b + c; d + ex", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 2);
        BinaryOp binaryOp = (BinaryOp) expressionList.get(0);
        assertEquals(((Literal) binaryOp.getLeft()).getValue(), 1);
        assertEquals(binaryOp.getOperator().getText(), "+");
        ConditionalOp conditionalOp = (ConditionalOp) binaryOp.getRight();
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "a");
        binaryOp = (BinaryOp) conditionalOp.getThenBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "b");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "c");
        assertEquals(conditionalOp.getElseBlock(), null);
        binaryOp = (BinaryOp) expressionList.get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "ex");
    }

    @Test
    public void testNestedIfCondition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("if a then if b then x + 1 else 1 - y " +
                "else if c then m + 2 else 2 - n", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        ConditionalOp conditionalOp1 = (ConditionalOp) expressionList.get(0);
        ConditionalOp conditionalOp2 = (ConditionalOp) conditionalOp1.getThenBlock();
        ConditionalOp conditionalOp3 = (ConditionalOp) conditionalOp1.getElseBlock();
        assertEquals(((Identifier) conditionalOp1.getCondition()).getName(), "a");
        assertEquals(((Identifier) conditionalOp2.getCondition()).getName(), "b");
        assertEquals(((Identifier) conditionalOp3.getCondition()).getName(), "c");
        BinaryOp binaryOp1 = (BinaryOp) conditionalOp2.getThenBlock();
        BinaryOp binaryOp2 = (BinaryOp) conditionalOp2.getElseBlock();
        BinaryOp binaryOp3 = (BinaryOp) conditionalOp3.getThenBlock();
        BinaryOp binaryOp4 = (BinaryOp) conditionalOp3.getElseBlock();
        assertEquals(((Identifier) binaryOp1.getLeft()).getName(), "x");
        assertEquals(binaryOp1.getOperator().getText(), "+");
        assertEquals(((Literal) binaryOp1.getRight()).getValue(), 1);
        assertEquals(((Literal) binaryOp2.getLeft()).getValue(), 1);
        assertEquals(binaryOp2.getOperator().getText(), "-");
        assertEquals(((Identifier) binaryOp2.getRight()).getName(), "y");
        assertEquals(((Identifier) binaryOp3.getLeft()).getName(), "m");
        assertEquals(binaryOp3.getOperator().getText(), "+");
        assertEquals(((Literal) binaryOp3.getRight()).getValue(), 2);
        assertEquals(((Literal) binaryOp4.getLeft()).getValue(), 2);
        assertEquals(binaryOp4.getOperator().getText(), "-");
        assertEquals(((Identifier) binaryOp4.getRight()).getName(), "n");
    }

    @Test
    public void testFunctionCall() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("testFunction(a, b, 3)", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        FunctionCall functionCall = (FunctionCall) expressionList.get(0);
        assertEquals(functionCall.getFunctionName(), "testFunction");
        List<Expression> parameters = functionCall.getParameters();
        assertEquals(parameters.size(), 3);
        assertEquals(((Identifier) parameters.get(0)).getName(), "a");
        assertEquals(((Identifier) parameters.get(1)).getName(), "b");
        assertEquals(((Literal) parameters.get(2)).getValue(), 3);
    }

    @Test
    public void testFunctionCallWithIfElse() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("testFunction(a, if x then 3 else 4, 3)", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        FunctionCall functionCall = (FunctionCall) expressionList.get(0);
        assertEquals(functionCall.getFunctionName(), "testFunction");
        List<Expression> parameters = functionCall.getParameters();
        assertEquals(parameters.size(), 3);
        assertEquals(((Identifier) parameters.get(0)).getName(), "a");
        ConditionalOp conditionalOp = (ConditionalOp) parameters.get(1);
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "x");
        assertEquals(((Literal) conditionalOp.getThenBlock()).getValue(), 3);
        assertEquals(((Literal) conditionalOp.getElseBlock()).getValue(), 4);
        assertEquals(((Literal) parameters.get(2)).getValue(), 3);
    }

    @Test
    public void testFunctionCallWithBinaryOps() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("testFunction(a, a + 21, 3)", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        FunctionCall functionCall = (FunctionCall) expressionList.get(0);
        assertEquals(functionCall.getFunctionName(), "testFunction");
        List<Expression> parameters = functionCall.getParameters();
        assertEquals(parameters.size(), 3);
        assertEquals(((Identifier) parameters.get(0)).getName(), "a");
        BinaryOp binaryOp = (BinaryOp) parameters.get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "a");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 21);
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Literal) parameters.get(2)).getValue(), 3);
    }

    @Test
    public void testNestedFunctionCalls() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("testFunction(a, newTestFunction(b, 2), 3)", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        FunctionCall functionCall1 = (FunctionCall) expressionList.get(0);
        assertEquals(functionCall1.getFunctionName(), "testFunction");
        List<Expression> parameters1 = functionCall1.getParameters();
        assertEquals(parameters1.size(), 3);
        assertEquals(((Identifier) parameters1.get(0)).getName(), "a");
        FunctionCall functionCall2 = (FunctionCall) parameters1.get(1);
        assertEquals(functionCall2.getFunctionName(), "newTestFunction");
        List<Expression> parameters2 = functionCall2.getParameters();
        assertEquals(((Identifier) parameters2.get(0)).getName(), "b");
        assertEquals(((Literal) parameters2.get(1)).getValue(), 2);
        assertEquals(((Literal) parameters1.get(2)).getValue(), 3);
    }

    @Test
    public void testNestedFunctionCallWithIf() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("testFunction(a, if x then newTestFunction(b, 2) " +
                "else newTestFunction2(c, 99), 3)", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        FunctionCall functionCall1 = (FunctionCall) expressionList.get(0);
        assertEquals(functionCall1.getFunctionName(), "testFunction");
        List<Expression> parameters1 = functionCall1.getParameters();
        assertEquals(parameters1.size(), 3);
        assertEquals(((Identifier) parameters1.get(0)).getName(), "a");
        ConditionalOp conditionalOp = (ConditionalOp) parameters1.get(1);
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "x");
        FunctionCall functionCall2 = (FunctionCall) conditionalOp.getThenBlock();
        assertEquals(functionCall2.getFunctionName(), "newTestFunction");
        List<Expression> parameters2 = functionCall2.getParameters();
        assertEquals(((Identifier) parameters2.get(0)).getName(), "b");
        assertEquals(((Literal) parameters2.get(1)).getValue(), 2);
        functionCall2 = (FunctionCall) conditionalOp.getElseBlock();
        assertEquals(functionCall2.getFunctionName(), "newTestFunction2");
        parameters2 = functionCall2.getParameters();
        assertEquals(((Identifier) parameters2.get(0)).getName(), "c");
        assertEquals(((Literal) parameters2.get(1)).getValue(), 99);
        assertEquals(((Literal) parameters1.get(2)).getValue(), 3);
    }

    @Test
    public void testBasicWhile() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("while a > 2 do b = 3", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        WhileOp whileOp = (WhileOp) expressionList.get(0);
        BinaryOp binaryOp = (BinaryOp) whileOp.getCondition();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "a");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 2);
        assertEquals(binaryOp.getOperator().getText(), ">");
        binaryOp = (BinaryOp) whileOp.getBody();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "b");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 3);
        assertEquals(binaryOp.getOperator().getText(), "=");
    }

    @Test
    public void testBasicWhileWithIf() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize(
                "while a > 2 do if b == 3 then c = 2 else d = 4", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        WhileOp whileOp = (WhileOp) expressionList.get(0);
        BinaryOp binaryOp = (BinaryOp) whileOp.getCondition();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "a");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 2);
        assertEquals(binaryOp.getOperator().getText(), ">");
        ConditionalOp conditionalOp = (ConditionalOp) whileOp.getBody();
        BinaryOp binaryOp1 = (BinaryOp) conditionalOp.getCondition();
        assertEquals(((Identifier) binaryOp1.getLeft()).getName(), "b");
        assertEquals(((Literal) binaryOp1.getRight()).getValue(), 3);
        assertEquals(binaryOp1.getOperator().getText(), "==");
        binaryOp1 = (BinaryOp) conditionalOp.getThenBlock();
        assertEquals(((Identifier) binaryOp1.getLeft()).getName(), "c");
        assertEquals(((Literal) binaryOp1.getRight()).getValue(), 2);
        assertEquals(binaryOp1.getOperator().getText(), "=");
        binaryOp1 = (BinaryOp) conditionalOp.getElseBlock();
        assertEquals(((Identifier) binaryOp1.getLeft()).getName(), "d");
        assertEquals(((Literal) binaryOp1.getRight()).getValue(), 4);
        assertEquals(binaryOp1.getOperator().getText(), "=");
    }

    @Test
    public void testBasicWhileWithMultipleBodyExpressions() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize(
                "while a > 2 do {a = a + 2; d = a - 35 / 4}", "Testfile.dl"));
        Block block = testParser.parse2();
        List<Expression> expressionList = block.getExpressionList();
        assertEquals(expressionList.size(), 1);
        WhileOp whileOp = (WhileOp) expressionList.get(0);
        BinaryOp binaryOp = (BinaryOp) whileOp.getCondition();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "a");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 2);
        assertEquals(binaryOp.getOperator().getText(), ">");
        assertEquals(((Block) whileOp.getBody()).getExpressionList().size(), 2);
        BinaryOp binaryOp1 = (BinaryOp) ((Block) whileOp.getBody()).getExpressionList().get(0);
        BinaryOp binaryOp2 = (BinaryOp) ((Block) whileOp.getBody()).getExpressionList().get(1);
        assertEquals(((Identifier) binaryOp1.getLeft()).getName(), "a");
        assertEquals(binaryOp1.getOperator().getText(), "=");
        assertEquals(((Identifier) ((BinaryOp) binaryOp1.getRight()).getLeft()).getName(), "a");
        assertEquals(((Literal) ((BinaryOp) binaryOp1.getRight()).getRight()).getValue(), 2);
        assertEquals(((BinaryOp) binaryOp1.getRight()).getOperator().getText(), "+");
        assertEquals(((Identifier) binaryOp2.getLeft()).getName(), "d");
        assertEquals(binaryOp2.getOperator().getText(), "=");
        BinaryOp binaryOp3 = (BinaryOp) binaryOp2.getRight();
        assertEquals(((Identifier) binaryOp3.getLeft()).getName(), "a");
        assertEquals(binaryOp3.getOperator().getText(), "-");
        assertEquals(((Literal) ((BinaryOp) binaryOp3.getRight()).getRight()).getValue(), 4);
        assertEquals(((BinaryOp) binaryOp3.getRight()).getOperator().getText(), "/");
        assertEquals(((Literal) ((BinaryOp) binaryOp3.getRight()).getLeft()).getValue(), 35);
    }

    @Test
    public void testVariableDefinition() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("var a = 3", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        VariableDef variableDef = (VariableDef) block.getExpressionList().get(0);
        assertEquals(variableDef.getName(), "a");
        assertTrue(variableDef.getType().isEmpty());
        assertEquals(((Literal) variableDef.getValue()).getValue(), 3);
    }

    @Test
    public void testVariableDefinitionWithType() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("var a: Int = 3", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        VariableDef variableDef = (VariableDef) block.getExpressionList().get(0);
        assertEquals(variableDef.getName(), "a");
        assertEquals(variableDef.getType().get(), "Int");
        assertEquals(((Literal) variableDef.getValue()).getValue(), 3);
    }

    @Test
    public void testVariableDefinitionWithIfBlock() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("var a: Int = if x == 3 then z else y", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        VariableDef variableDef = (VariableDef) block.getExpressionList().get(0);
        assertEquals(variableDef.getName(), "a");
        assertEquals(variableDef.getType().get(), "Int");
        ConditionalOp conditionalOp = (ConditionalOp) variableDef.getValue();
        BinaryOp binaryOp = (BinaryOp) conditionalOp.getCondition();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperator().getText(), "==");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 3);
        assertEquals(((Identifier) conditionalOp.getThenBlock()).getName(), "z");
        assertEquals(((Identifier) conditionalOp.getElseBlock()).getName(), "y");
    }

    @Test
    public void testBlockWithoutEndingSemicolon() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("{\n" +
                        "    f(a);\n" +
                        "    x = y;\n" +
                        "    f(x)\n" +
                        "}", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 3);
        FunctionCall functionCall = (FunctionCall) block.getExpressionList().get(0);
        assertEquals(functionCall.getFunctionName(), "f");
        assertEquals(functionCall.getParameters().size(), 1);
        assertEquals(((Identifier) functionCall.getParameters().get(0)).getName(), "a");
        functionCall = (FunctionCall) block.getExpressionList().get(2);
        assertEquals(functionCall.getFunctionName(), "f");
        assertEquals(functionCall.getParameters().size(), 1);
        assertEquals(((Identifier) functionCall.getParameters().get(0)).getName(), "x");
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperator().getText(), "=");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "y");
    }

    @Test
    public void testComplexBlock() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("{\n    while f() do {\n        x = 10;\n     " +
                "   y = if g(x) then {\n            x = x + 1;\n            x\n} else {\ng(x)\n};\ng(y);\n};\n123\n}",
                "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 2);
        WhileOp whileOp = (WhileOp) block.getExpressionList().get(0);
        assertEquals(((Literal) block.getExpressionList().get(1)).getValue(), 123);
        FunctionCall functionCall = (FunctionCall) whileOp.getCondition();
        assertEquals(functionCall.getFunctionName(), "f");
        assertEquals(functionCall.getParameters().size(), 0);
        Block whileBlock = (Block) whileOp.getBody();
        BinaryOp binaryOp = (BinaryOp) whileBlock.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 10);
        assertEquals(binaryOp.getOperator().getText(), "=");
        binaryOp = (BinaryOp) whileBlock.getExpressionList().get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "y");
        assertEquals(binaryOp.getOperator().getText(), "=");
        ConditionalOp conditionalOp = (ConditionalOp) binaryOp.getRight();
        functionCall = (FunctionCall) conditionalOp.getCondition();
        assertEquals(functionCall.getFunctionName(), "g");
        assertEquals(functionCall.getParameters().size(), 1);
        assertEquals(((Identifier) functionCall.getParameters().get(0)).getName(), "x");
        block = (Block) conditionalOp.getThenBlock();
        binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperator().getText(), "=");
        binaryOp = (BinaryOp) binaryOp.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperator().getText(), "+");
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 1);
        assertEquals(((Identifier) block.getExpressionList().get(1)).getName(), "x");
        block = (Block) conditionalOp.getElseBlock();
        assertEquals(((FunctionCall) block.getExpressionList().get(0)).getFunctionName(), "g");
        assertEquals(((FunctionCall) block.getExpressionList().get(0)).getParameters().size(), 1);
        assertEquals(((Identifier) ((FunctionCall) block.getExpressionList().get(0)).getParameters().get(0)).getName(), "x");
        assertEquals(((FunctionCall) whileBlock.getExpressionList().get(2)).getFunctionName(), "g");
        assertEquals(((FunctionCall) whileBlock.getExpressionList().get(2)).getParameters().size(), 1);
        assertEquals(((Identifier) ((FunctionCall) whileBlock.getExpressionList().get(2)).getParameters().get(0)).getName(), "y");
        assertTrue(whileBlock.getExpressionList().get(3) instanceof Unit);
    }

}