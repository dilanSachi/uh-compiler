package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public class IntLiteral extends Literal {

    private Integer value;

    public IntLiteral(Integer value, Location location) {
        super(location);
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
