package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public class FunctionArgumentDefinition extends Expression {
    private final String name;
    private final String argType;

    public FunctionArgumentDefinition(String name, String type, Location location) {
        super(location);
        this.name = name;
        this.argType = type;
    }

    public String getName() {
        return name;
    }

    public String getArgType() {
        return argType;
    }
}
