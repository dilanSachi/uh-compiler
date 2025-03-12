package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public class Return extends Expression {

    private Expression value;

    public Return(Expression value, Location location) {
        super(location);
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }
}
