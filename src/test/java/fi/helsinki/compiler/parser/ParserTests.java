package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;
import fi.helsinki.compiler.tokenizer.Tokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
        Token operatorToken = binaryOp.getOperatorToken();
        assertEquals(rightLiteral.getValue(), 8);
        assertEquals(operatorToken.getText(), "-");
        assertEquals(operatorToken.getTokenType(), TokenType.OPERATOR);
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        BinaryOp rightOp1 = (BinaryOp) leftOp.getRight();
        assertEquals(((Literal) rightOp1.getLeft()).getValue(), 225);
        assertEquals(((Literal) rightOp1.getRight()).getValue(), 10);
        assertEquals(rightOp1.getOperatorToken().getText(), "%");
        assertEquals(((Literal) leftOp.getLeft()).getValue(), 1);
        assertEquals(leftOp.getOperatorToken().getText(), "+");
    }

    @Test
    public void testEqualOperator() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x = y + 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperatorToken().getText(), "=");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(((Identifier) rightOp.getLeft()).getName(), "y");
        assertEquals(((Literal) rightOp.getRight()).getValue(), 20);
        assertEquals(rightOp.getOperatorToken().getText(), "+");
    }

    @Test
    public void testDoubleEqualOperator() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x == y + 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "x");
        assertEquals(binaryOp.getOperatorToken().getText(), "==");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(((Identifier) rightOp.getLeft()).getName(), "y");
        assertEquals(((Literal) rightOp.getRight()).getValue(), 20);
        assertEquals(rightOp.getOperatorToken().getText(), "+");
    }

    @Test
    public void testEqualOperator2() throws ParserException {
        Tokenizer tokenizer = new Tokenizer();
        Parser testParser = new Parser(tokenizer.tokenize("x + y = 20", "Testfile.dl"));
        Block block = testParser.parse2();
        assertEquals(block.getExpressionList().size(), 1);
        BinaryOp binaryOp = (BinaryOp) block.getExpressionList().get(0);
        assertEquals(((Literal) binaryOp.getRight()).getValue(), 20);
        assertEquals(binaryOp.getOperatorToken().getText(), "=");
        BinaryOp leftOp = (BinaryOp) binaryOp.getLeft();
        assertEquals(((Identifier) leftOp.getLeft()).getName(), "x");
        assertEquals(((Identifier) leftOp.getRight()).getName(), "y");
        assertEquals(leftOp.getOperatorToken().getText(), "+");
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
        assertEquals(binaryOp3.getOperatorToken().getText(), "-");
        assertEquals(((Identifier) binaryOp2.getRight()).getName(), "c");
        assertEquals(binaryOp2.getOperatorToken().getText(), "+");
        BinaryOp binaryOp4 = (BinaryOp) binaryOp2.getLeft();
        assertEquals(((Identifier) binaryOp4.getRight()).getName(), "b");
        assertEquals(binaryOp4.getOperatorToken().getText(), "+");
        BinaryOp binaryOp5 = (BinaryOp) binaryOp4.getLeft();
        assertEquals(((Identifier) binaryOp5.getRight()).getName(), "a");
        assertEquals(binaryOp5.getOperatorToken().getText(), "+");
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
        assertEquals(condition.getOperatorToken().getText(), ">");
        assertEquals(((Literal) condition.getRight()).getValue(), 2);
        assertEquals(((Identifier) thenBlock.getLeft()).getName(), "b");
        assertEquals(thenBlock.getOperatorToken().getText(), "=");
        BinaryOp binaryOp = (BinaryOp) thenBlock.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "c");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "d");
        condition = (BinaryOp) elseBlock.getCondition();
        thenBlock = (BinaryOp) elseBlock.getThenBlock();
        assertEquals(((Identifier) condition.getLeft()).getName(), "e");
        assertEquals(condition.getOperatorToken().getText(), "==");
        assertEquals(((Literal) condition.getRight()).getValue(), 3);
        assertEquals(((Identifier) thenBlock.getLeft()).getName(), "f");
        assertEquals(thenBlock.getOperatorToken().getText(), "=");
        binaryOp = (BinaryOp) thenBlock.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "g");
        assertEquals(binaryOp.getOperatorToken().getText(), "*");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "h");
        BinaryOp elseBlock2 = (BinaryOp) elseBlock.getElseBlock();
        assertEquals(((Identifier) elseBlock2.getLeft()).getName(), "i");
        assertEquals(elseBlock2.getOperatorToken().getText(), "=");
        binaryOp = (BinaryOp) elseBlock2.getRight();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "j");
        assertEquals(binaryOp.getOperatorToken().getText(), "-");
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
        assertEquals(binaryOp.getOperatorToken().getText(), "=");
        BinaryOp rightOp = (BinaryOp) binaryOp.getRight();
        assertEquals(rightOp.getOperatorToken().getText(), "or");
        BinaryOp binaryOp1 = (BinaryOp) rightOp.getRight();
        BinaryOp binaryOp2 = (BinaryOp) rightOp.getLeft();
        assertEquals(binaryOp1.getOperatorToken().getText(), ">");
        assertEquals(((Identifier) binaryOp1.getRight()).getName(), "l");
        assertEquals(binaryOp2.getOperatorToken().getText(), "!=");
        BinaryOp binaryOp3 = (BinaryOp) binaryOp1.getLeft();
        assertEquals(binaryOp3.getOperatorToken().getText(), "<");
        BinaryOp binaryOp4 = (BinaryOp) binaryOp2.getLeft();
        BinaryOp binaryOp5 = (BinaryOp) binaryOp2.getRight();
        assertEquals(binaryOp4.getOperatorToken().getText(), "+");
        assertEquals(binaryOp5.getOperatorToken().getText(), ">=");
        assertEquals(((Identifier) binaryOp5.getRight()).getName(), "f");
        assertEquals(((Identifier) binaryOp5.getLeft()).getName(), "e");
        assertEquals(((Identifier) binaryOp4.getRight()).getName(), "d");
        BinaryOp binaryOp6 = (BinaryOp) binaryOp4.getLeft();
        assertEquals(binaryOp6.getOperatorToken().getText(), "and");
        assertEquals(((Identifier) binaryOp6.getRight()).getName(), "c");
        assertEquals(((Identifier) binaryOp6.getLeft()).getName(), "b");
        BinaryOp binaryOp7 = (BinaryOp) binaryOp3.getLeft();
        BinaryOp binaryOp8 = (BinaryOp) binaryOp3.getRight();
        assertEquals(binaryOp7.getOperatorToken().getText(), "<=");
        assertEquals(binaryOp8.getOperatorToken().getText(), "*");
        assertEquals(((Identifier) binaryOp8.getRight()).getName(), "k");
        assertEquals(((Identifier) binaryOp8.getLeft()).getName(), "j");
        assertEquals(((Identifier) binaryOp7.getLeft()).getName(), "g");
        BinaryOp binaryOp9 = (BinaryOp) binaryOp7.getRight();
        assertEquals(binaryOp9.getOperatorToken().getText(), "-");
        assertEquals(((Identifier) binaryOp9.getRight()).getName(), "i");
        assertEquals(((Identifier) binaryOp9.getLeft()).getName(), "h");
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
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "c");
        assertEquals(conditionalOp.getElseBlock(), null);
        binaryOp = (BinaryOp) expressionList.get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
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
        assertEquals(binaryOp.getOperatorToken().getText(), "-");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "y");
        ConditionalOp conditionalOp = (ConditionalOp) expressionList.get(1);
        assertEquals(conditionalOp.getName(), "if");
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "a");
        binaryOp = (BinaryOp) conditionalOp.getThenBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "b");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "c");
        binaryOp = (BinaryOp) conditionalOp.getElseBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "e");
        binaryOp = (BinaryOp) expressionList.get(2);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
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
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
        ConditionalOp conditionalOp = (ConditionalOp) binaryOp.getRight();
        assertEquals(((Identifier) conditionalOp.getCondition()).getName(), "a");
        binaryOp = (BinaryOp) conditionalOp.getThenBlock();
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "b");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
        assertEquals(((Identifier) binaryOp.getRight()).getName(), "c");
        assertEquals(conditionalOp.getElseBlock(), null);
        binaryOp = (BinaryOp) expressionList.get(1);
        assertEquals(((Identifier) binaryOp.getLeft()).getName(), "d");
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
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
        assertEquals(binaryOp1.getOperatorToken().getText(), "+");
        assertEquals(((Literal) binaryOp1.getRight()).getValue(), 1);
        assertEquals(((Literal) binaryOp2.getLeft()).getValue(), 1);
        assertEquals(binaryOp2.getOperatorToken().getText(), "-");
        assertEquals(((Identifier) binaryOp2.getRight()).getName(), "y");
        assertEquals(((Identifier) binaryOp3.getLeft()).getName(), "m");
        assertEquals(binaryOp3.getOperatorToken().getText(), "+");
        assertEquals(((Literal) binaryOp3.getRight()).getValue(), 2);
        assertEquals(((Literal) binaryOp4.getLeft()).getValue(), 2);
        assertEquals(binaryOp4.getOperatorToken().getText(), "-");
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
        assertEquals(binaryOp.getOperatorToken().getText(), "+");
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

}