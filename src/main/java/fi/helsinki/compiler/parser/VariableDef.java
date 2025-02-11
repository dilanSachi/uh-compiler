package fi.helsinki.compiler.parser;

import java.util.Optional;

public class VariableDef implements Expression {

    private String name;
    private Optional<String> type;
    private Expression value;

    public VariableDef(String name, String type, Expression value) {
        this.name = name;
        this.type = Optional.of(type);
        this.value = value;
    }

    public VariableDef(String name, Expression value) {
        this.name = name;
        this.type = Optional.empty();
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }
}
