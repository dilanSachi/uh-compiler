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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token tokenObj &&
                tokenObj.getText().equals(getText()) &&
                tokenObj.getTokenType() == getTokenType() &&
                getTokenLocation().equals(tokenObj.getTokenLocation());
    }

    @Override
    public String toString() {
        return String.format("Text: %s, Type: %s, Location: %s", this.getText(), this.tokenType, this.tokenLocation);
    }
}
