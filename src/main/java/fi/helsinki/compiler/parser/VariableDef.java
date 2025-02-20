package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

import java.util.Optional;

public class VariableDef extends Expression {

    private String name;
    private Optional<String> definedType;
    private Expression value;

    public VariableDef(String name, String definedType, Expression value, Location location) {
        super(location);
        this.name = name;
        this.definedType = Optional.of(definedType);
        this.value = value;
    }

    public VariableDef(String name, Expression value, Location location) {
        super(location);
        this.name = name;
        this.definedType = Optional.empty();
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getDefinedType() {
        return definedType;
    }

    public Expression getValue() {
        return value;
    }
}
