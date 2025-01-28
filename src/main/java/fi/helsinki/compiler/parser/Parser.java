package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.exceptions.ParserException;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.tokenizer.TokenType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Parser {

    private List<Token> tokens;
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
        if (expected.length > 0 && !Arrays.stream(expected).anyMatch(token.getText()::equals)) {
            String commaSeparated = Arrays.stream(expected).map(Objects::toString)
                    .collect(Collectors.joining(", ")).toString();
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

    private Identifier parseIdentifier() throws ParserException {
        if (peek().getTokenType() != TokenType.IDENTIFIER) {
            throw new ParserException(peek().getTokenLocation() + ": expected an identifier");
        }
        Token token = consume();
        return new Identifier(token.getText());
    }

    private Expression parseTerm() throws ParserException {
        if (peek().getTokenType() == TokenType.INTEGER_LITERAL) {
            return parseIntegerLiteral();
        }
        if (peek().getTokenType() == TokenType.IDENTIFIER) {
            return parseIdentifier();
        }
        throw new ParserException(peek().getTokenLocation() + ": expected an integer literal or an identifier");
    }

    public Expression parseExpression() throws ParserException {
        Expression left = parseTerm();
        while (Arrays.asList("+", "-").contains(peek().getText())) {
            Token operatorToken =  consume();
            Expression right = parseTerm();
            left = new BinaryOp(left, operatorToken, right);
        }
        return left;
    }

    public BinaryOp parseExpressionWithRightAssociativity() throws ParserException {
        Expression left = parseTerm();
        while (Arrays.asList("+", "-").contains(peek().getText())) {
            Token operatorToken =  consume();
            Expression right = parseTerm();
            left = new BinaryOp(left, operatorToken, right);
        }
        Token operatorToken = consume("+", "-");
        Expression right = parseTerm();
        return new BinaryOp(left, operatorToken, right);
    }

}
