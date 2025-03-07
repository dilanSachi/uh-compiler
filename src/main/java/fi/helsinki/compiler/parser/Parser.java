package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.common.expressions.*;
import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private final List<Token> tokens;
    private int tokenPosition;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenPosition = 0;
    }

    private Token peek() {
        if (tokenPosition == tokens.size()) {
            return new Token("", TokenType.END, tokens.getLast().getTokenLocation());
        }
        return tokens.get(tokenPosition);
    }

    private Token lookBack() {
        if (tokenPosition == 0) {
            return new Token("", TokenType.START, tokens.getFirst().getTokenLocation());
        }
        return tokens.get(tokenPosition - 1);
    }

    private Token consume(String... expected) throws ParserException {
        Token token = peek();
        if (expected.length > 0 && Arrays.stream(expected).noneMatch(token.getText()::equals)) {
            String commaSeparated = Arrays.stream(expected).map(Objects::toString)
                    .collect(Collectors.joining(", "));
            throw new ParserException(token.getTokenLocation() + ": expected one of: " + commaSeparated);
        }
        tokenPosition += 1;
        return token;
    }

    private IntLiteral parseIntegerLiteral() throws ParserException {
        if (peek().getTokenType() != TokenType.INTEGER_LITERAL) {
            throw new ParserException(peek().getTokenLocation() + ": expected an integer literal");
        }
        Token token = consume();
        return new IntLiteral(Long.valueOf(token.getText()), token.getTokenLocation());
    }

    private FunctionCall parseFunctionCall(Token functionNameToken) throws ParserException {
        consume("(");
        List<Expression> parameters = new ArrayList<>();
        while (!peek().getText().equals(")")) {
            parameters.add(parseExpression());
            if (!peek().getText().equals(")")) {
                consume(",");
            }
        }
        consume(")");
        return new FunctionCall(functionNameToken.getText(), parameters, functionNameToken.getTokenLocation());
    }

    private Expression parseIdentifier() throws ParserException {
        if (peek().getTokenType() != TokenType.IDENTIFIER) {
            throw new ParserException(peek().getTokenLocation() + ": expected an identifier");
        }
        Token token = consume();
        Token nextToken = peek();
        if (nextToken.getText().equals("(")) {
            return parseFunctionCall(token);
        }
        return new Identifier(token.getText(), token.getTokenLocation());
    }

    private Expression parseFactor() throws ParserException {
        Token token = peek();
        if (checkNextToken(TokenType.PUNCTUATION, Optional.of("("))) {
            return parseParenthesized();
        }
        if (checkNextToken(TokenType.INTEGER_LITERAL, Optional.empty())) {
            return parseIntegerLiteral();
        }
        if (checkNextToken(TokenType.IDENTIFIER, Optional.empty())) {
            return parseIdentifier();
        }
        if (checkNextToken(TokenType.KEYWORD, Optional.of("if"))) {
            return parseIfBlock();
        }
        if (checkNextToken(TokenType.PUNCTUATION, Optional.of("{"))) {
            return parseBlock();
        }
        if (checkNextToken(TokenType.BOOLEAN_LITERAL, Optional.of("true")) ||
                checkNextToken(TokenType.BOOLEAN_LITERAL, Optional.of("false"))) {
            Token booleanToken = consume();
            return new BooleanLiteral(Boolean.valueOf(booleanToken.getText()), booleanToken.getTokenLocation());
        }
        throw new ParserException("Invalid token: " + token.getText() + token.getTokenLocation() +
                ": expected an integer literal or an identifier");
    }

    private Expression parseParenthesized() throws ParserException {
        consume("(");
        Expression expression = parseExpression();
        consume(")");
        return expression;
    }

    private Expression parseUnaryAndNot() throws ParserException {
        Expression unary;
        while (Arrays.asList("-", "not").contains(peek().getText())) {
            Token operatorToken =  consume();
            Expression right = parseUnaryAndNot();
            unary = new UnaryOp(operatorToken, right, operatorToken.getTokenLocation());
            return unary;
        }
        return parseFactor();
    }

    private Expression parseTerm() throws ParserException {
        Expression left = parseUnaryAndNot();
        while (Arrays.asList("*", "/", "%").contains(peek().getText())) {
            Token operatorToken =  consume();
            Expression right = parseUnaryAndNot();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseIfBlock() throws ParserException {
        Token ifToken = consume("if");
        Expression condition = parseExpression();
        consume("then");
        Expression thenBlock;
        if (checkNextToken(TokenType.PUNCTUATION, Optional.of("{"))) {
            thenBlock = parseBlock();
        } else {
            thenBlock = parseExpression();
        }
        if (peek().getTokenType() == TokenType.KEYWORD && peek().getText().equals("else")) {
            consume("else");
            Expression elseBlock;
            if (checkNextToken(TokenType.PUNCTUATION, Optional.of("{"))) {
                elseBlock = parseBlock();
            } else {
                elseBlock = parseExpression();
            }
            return new ConditionalOp(condition, thenBlock, elseBlock, ifToken.getTokenLocation());
        }
        return new ConditionalOp(condition, thenBlock, null, ifToken.getTokenLocation());
    }

    private Expression parseWhileBlock() throws ParserException {
        Token whileToken = consume("while");
        Expression condition = parseExpression();
        consume("do");
        Expression body;
        if (checkNextToken(TokenType.PUNCTUATION, Optional.of("{"))) {
            body = parseBlock();
        } else {
            body = parseExpression();
        }
        return new WhileOp(condition, body, whileToken.getTokenLocation());
    }

    private Expression parseAddition() throws ParserException {
        Expression left = parseTerm();
        while (Arrays.asList("+", "-").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseTerm();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseThan() throws ParserException {
        Expression left = parseAddition();
        while (Arrays.asList("<", "<=", ">", ">=").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseAddition();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseEquality() throws ParserException {
        Expression left = parseThan();
        while (Arrays.asList("==", "!=").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseThan();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseAnd() throws ParserException {
        Expression left = parseEquality();
        while (Arrays.asList("and").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseEquality();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseOr() throws ParserException {
        Expression left = parseAnd();
        while (Arrays.asList("or").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseAnd();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseExpression() throws ParserException {
        Expression left = parseOr();
        while (Arrays.asList("=").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseExpression();
            left = new BinaryOp(left, operatorToken, right, operatorToken.getTokenLocation());
        }
        return left;
    }

    private Expression parseVariableDefinition() throws ParserException {
        consume("var");
        String varName = consume().getText();
        if (peek().getText().equals(":")) {
            consume(":");
            String type = consume().getText();
            Token equalToken = consume("=");
            Expression value = parseExpression();
            return new VariableDef(varName, type, value, equalToken.getTokenLocation());
        }
        Token equalToken = consume("=");
        Expression value = parseExpression();
        return new VariableDef(varName, value, equalToken.getTokenLocation());
    }

    Expression parseInternal() throws ParserException {
        if (tokens.isEmpty()) {
            throw new ParserException("Cannot parse empty token list");
        }
        Expression expression = parseExpression();
        if (tokenPosition < tokens.size()) {
            throw new ParserException("Parsing failed. Invalid tokens found: " +
                    Arrays.toString(tokens.subList(tokenPosition, tokens.size()).toArray()));
        }
        return expression;
    }

    private Block parseBlock() throws ParserException {
        Token blockToken = consume("{");
        List<Expression> expressionList = new ArrayList<>();
        Block block = new Block(expressionList, blockToken.getTokenLocation());
        while (!checkNextToken(TokenType.PUNCTUATION, Optional.of("}"))) {
            if (checkNextToken(TokenType.KEYWORD, Optional.of("if"))) {
                Expression ifExpression = parseIfBlock();
                block.addExpression(ifExpression);
            } else if (checkNextToken(TokenType.IDENTIFIER, Optional.empty()) ||
                    checkNextToken(TokenType.STRING_LITERAL, Optional.empty()) ||
                    checkNextToken(TokenType.INTEGER_LITERAL, Optional.empty()) ||
                    checkNextToken(TokenType.BOOLEAN_LITERAL, Optional.empty())) {
                Expression expression = parseExpression();
                block.addExpression(expression);
            } else if (checkNextToken(TokenType.OPERATOR, Optional.of("-")) ||
                    checkNextToken(TokenType.OPERATOR, Optional.of("not"))) {
                Expression expression = parseExpression();
                block.addExpression(expression);
            } else if (checkNextToken(TokenType.KEYWORD, Optional.of("while"))) {
                block.addExpression(parseWhileBlock());
            } else if (checkNextToken(TokenType.PUNCTUATION, Optional.of("{"))) {
                Block childBlock = parseBlock();
                block.addExpression(childBlock);
            } else if (checkNextToken(TokenType.KEYWORD, Optional.of("var"))) {
                block.addExpression(parseVariableDefinition());
            }
            if (checkNextToken(TokenType.PUNCTUATION, Optional.of(";"))) {
                Token uniToken = consume(";");
                if (checkNextToken(TokenType.PUNCTUATION, Optional.of("}"))) {
                    block.addExpression(new Unit(uniToken.getTokenLocation()));
                }
            } else if (!checkNextToken(TokenType.PUNCTUATION, Optional.of("}")) &&
                    !checkNextToken(TokenType.END, Optional.empty()) &&
                    !lookBack().getText().equals("}")) {
                throw new ParserException("Parsing failed. Invalid tokens found. Expected ';', but found " + peek());
            }
        }
        consume("}");
        return block;
    }

    public Block parse() throws ParserException {
        if (tokens.isEmpty()) {
            throw new ParserException("Cannot parse empty token list");
        }
        Block block = new Block(new ArrayList<>(), peek().getTokenLocation());
        while (!checkNextToken(TokenType.END, Optional.empty())) {
            if (checkNextToken(TokenType.KEYWORD, Optional.of("if"))) {
                Expression ifExpression = parseIfBlock();
                block.addExpression(ifExpression);
            } else if (checkNextToken(TokenType.IDENTIFIER, Optional.empty()) ||
                    checkNextToken(TokenType.STRING_LITERAL, Optional.empty()) ||
                    checkNextToken(TokenType.INTEGER_LITERAL, Optional.empty()) ||
                    checkNextToken(TokenType.BOOLEAN_LITERAL, Optional.empty())) {
                Expression expression = parseExpression();
                block.addExpression(expression);
            } else if (checkNextToken(TokenType.OPERATOR, Optional.of("-")) ||
                    checkNextToken(TokenType.OPERATOR, Optional.of("not"))) {
                Expression expression = parseExpression();
                block.addExpression(expression);
            } else if (checkNextToken(TokenType.KEYWORD, Optional.of("while"))) {
                block.addExpression(parseWhileBlock());
            } else if (checkNextToken(TokenType.PUNCTUATION, Optional.of("{"))) {
                Block childBlock = parseBlock();
                block.addExpression(childBlock);
            } else if (checkNextToken(TokenType.PUNCTUATION, Optional.of("}"))) {
                consume("}");
                return block;
            } else if (checkNextToken(TokenType.KEYWORD, Optional.of("var"))) {
                block.addExpression(parseVariableDefinition());
            }
            if (checkNextToken(TokenType.PUNCTUATION, Optional.of(";"))) {
                consume(";");
                if (checkNextToken(TokenType.END, Optional.empty())) {
                    block.addExpression(new Unit(peek().getTokenLocation()));
                }
            } else if (peek().getTokenType() != TokenType.END && !lookBack().getText().equals("}")) {
                throw new ParserException("Parsing failed. Invalid tokens found. Expected ';', but found " + peek());
            }
        }
        if (tokenPosition < tokens.size()) {
            throw new ParserException("Parsing failed. Invalid tokens found: " +
                    Arrays.toString(tokens.subList(tokenPosition, tokens.size()).toArray()));
        }
        if (block.getExpressionList().size() == 1 && block.getExpressionList().get(0) instanceof Block) {
            return (Block) block.getExpressionList().get(0);
        }
        return block;
    }

    private boolean checkNextToken(TokenType tokenType, Optional<String> text) {
        return peek().getTokenType() == tokenType && (text.isEmpty() || peek().getText().equals(text.get()));
    }

}
