package fi.helsinki.compiler.parser;

public class Boolean implements Expression {
    private String value;

    public Boolean(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
