package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.common.Location;

public class Token {
    private String text;
    private TokenType tokenType;
    private Location location;

    public Token(String text, TokenType tokenType, Location location) {
        this.text = text;
        this.tokenType = tokenType;
        this.location = location;
    }

    public Location getTokenLocation() {
        return location;
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
        return String.format("Text: %s, Type: %s, Location: %s", this.getText(), this.tokenType, this.location);
    }
}
