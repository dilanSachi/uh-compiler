package fi.helsinki.compiler.tokenizer;

public class Token {
    private String text;
    private TokenType tokenType;
    private TokenLocation tokenLocation;

    public Token(String text, TokenType tokenType, TokenLocation tokenLocation) {
        this.text = text;
        this.tokenType = tokenType;
        this.tokenLocation = tokenLocation;
    }

    public TokenLocation getTokenLocation() {
        return tokenLocation;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getText() {
        return text;
    }
}
