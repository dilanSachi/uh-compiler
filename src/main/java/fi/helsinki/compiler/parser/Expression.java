package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.Location;

/*
 Base interface for AST nodes representing expressions.
 */
public abstract class Expression {

    private Location location;

    public Expression(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
