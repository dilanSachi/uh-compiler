package fi.helsinki.compiler.parser;

public class VariableDef implements Expression {

    private String name;

    public VariableDef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
