package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

public class Boolean extends Expression {
    private String value;

    public Boolean(String value, Location location) {
        super(location);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
