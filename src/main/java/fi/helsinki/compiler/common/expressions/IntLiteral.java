package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public class IntLiteral extends Literal {

    private Long value;

    public IntLiteral(Long value, Location location) {
        super(location);
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}
