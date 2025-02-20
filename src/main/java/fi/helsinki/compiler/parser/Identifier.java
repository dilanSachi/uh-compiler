package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

public class Identifier extends Expression {

    private String name;

    public Identifier(String name, Location location) {
        super(location);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
