package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.Location;

import java.util.Optional;

public class VariableDef extends Expression {

    private String name;
    private Optional<String> type;
    private Expression value;

    public VariableDef(String name, String type, Expression value, Location location) {
        super(location);
        this.name = name;
        this.type = Optional.of(type);
        this.value = value;
    }

    public VariableDef(String name, Expression value, Location location) {
        super(location);
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
