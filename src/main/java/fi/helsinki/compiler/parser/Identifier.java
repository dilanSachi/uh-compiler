package fi.helsinki.compiler.parser;

public class Identifier implements Expression {

    private String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
