package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.Location;

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
