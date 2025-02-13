package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.Location;

/*
    Denotes a ';' token before the end of the block
 */
public class Unit extends Expression {

    public Unit(Location location) {
        super(location);
    }
}
