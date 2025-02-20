package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

public class Literal extends Expression {

    private Integer value;

    public Literal(Integer value, Location location) {
        super(location);
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
