package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public class BooleanLiteral extends Literal {
    private boolean value;

    public BooleanLiteral(boolean value, Location location) {
        super(location);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
