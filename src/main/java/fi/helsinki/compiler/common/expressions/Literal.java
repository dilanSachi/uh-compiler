package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public abstract class Literal extends Expression {

    public Literal(Location location) {
        super(location);
    }
}
