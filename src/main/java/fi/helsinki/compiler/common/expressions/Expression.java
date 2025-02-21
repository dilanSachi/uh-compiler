package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.common.types.Type;
import fi.helsinki.compiler.common.types.UnitType;

/*
 Base interface for AST nodes representing expressions.
 */
public abstract class Expression {

    private Location location;
    private Type type = new UnitType(); // Default type is assigned as UnitType before the type checking

    public Expression(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
