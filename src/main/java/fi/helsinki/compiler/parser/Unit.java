package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

/*
    Denotes a ';' token before the end of the block
 */
public class Unit extends Expression {

    public Unit(Location location) {
        super(location);
    }
}
