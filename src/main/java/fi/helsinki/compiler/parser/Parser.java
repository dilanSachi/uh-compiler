package fi.helsinki.compiler.parser;

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

    private Literal parseIntegerLiteral() throws ParserException {
        if (peek().getTokenType() != TokenType.INTEGER_LITERAL) {
            throw new ParserException(peek().getTokenLocation() + ": expected an integer literal");
        }
        Token token = consume();
        return new Literal(Integer.valueOf(token.getText()));
    }

    private FunctionCall parseFunctionCall(Token functionNameToken) throws ParserException {
        consume("(");
        List<Expression> parameters = new ArrayList<>();
        while (true) {
            parameters.add(parseExpression());
            if (peek().getText().equals(")")) {
                consume(")");
                break;
            } else {
                consume(",");
            }
        }
        return new FunctionCall(functionNameToken.getText(), parameters);
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
        return new Identifier(token.getText());
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
        throw new ParserException(token.getTokenLocation() + ": expected an integer literal or an identifier");
    }

    private Expression parseParenthesized() throws ParserException {
        consume("(");
        Expression expression = parseExpression();
        consume(")");
        return expression;
    }

    private Expression parseTerm() throws ParserException {
        Expression left = parseFactor();
        while (Arrays.asList("*", "/", "%").contains(peek().getText())) {
            Token operatorToken =  consume();
            Expression right = parseFactor();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    private Expression parseIfBlock() throws ParserException {
        consume("if");
        Expression condition = parseExpression();
        consume("then");
        Expression thenBlock = parseExpression();
        if (peek().getTokenType() == TokenType.KEYWORD && peek().getText().equals("else")) {
            consume("else");
            Expression elseBlock = parseExpression();
            return new ConditionalOp(condition, thenBlock, elseBlock);
        }
        return new ConditionalOp(condition, thenBlock, null);
    }

    private Expression parseAddition() throws ParserException {
        Expression left = parseTerm();
        while (Arrays.asList("+", "-").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseTerm();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    private Expression parseThan() throws ParserException {
        Expression left = parseAddition();
        while (Arrays.asList("<", "<=", ">", ">=").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseAddition();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    private Expression parseEquality() throws ParserException {
        Expression left = parseThan();
        while (Arrays.asList("==", "!=").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseThan();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    private Expression parseAnd() throws ParserException {
        Expression left = parseEquality();
        while (Arrays.asList("and").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseEquality();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    private Expression parseOr() throws ParserException {
        Expression left = parseAnd();
        while (Arrays.asList("or").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseAnd();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    private Expression parseExpression() throws ParserException {
        Expression left = parseOr();
        while (Arrays.asList("=").contains(peek().getText())) {
            Token operatorToken = consume();
            Expression right = parseExpression();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    public Expression parse() throws ParserException {
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

    public Block parse2() throws ParserException {
        if (tokens.isEmpty()) {
            throw new ParserException("Cannot parse empty token list");
        }
        Block block = new Block(new ArrayList<>());
        Token nextToken = peek();
        while (nextToken.getTokenType() != TokenType.END) {
            if (checkNextToken(TokenType.KEYWORD, Optional.of("if"))) {
                Expression ifExpression = parseIfBlock();
                block.addExpression(ifExpression);
                nextToken = peek();
            }
            if (checkNextToken(TokenType.IDENTIFIER, Optional.empty()) ||
                    checkNextToken(TokenType.STRING_LITERAL, Optional.empty()) ||
                    checkNextToken(TokenType.INTEGER_LITERAL, Optional.empty()) ||
                    checkNextToken(TokenType.BOOLEAN_LITERAL, Optional.empty())) {
                Expression expression = parseExpression();
                block.addExpression(expression);
                nextToken = peek();
            }
            if (checkNextToken(TokenType.PUNCTUATION, Optional.of(";"))) {
                consume(";");
                nextToken = peek();
            }
        }
        if (tokenPosition < tokens.size()) {
            throw new ParserException("Parsing failed. Invalid tokens found: " +
                    Arrays.toString(tokens.subList(tokenPosition, tokens.size()).toArray()));
        }
        return block;
    }

    private boolean checkNextToken(TokenType tokenType, Optional<String> text) {
        return peek().getTokenType() == tokenType && (text.isEmpty() || peek().getText().equals(text.get()));
    }

}
