package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;

public abstract class Instruction {
    Location location;
    String name;

    public Instruction(String name, Location location) {
        this.location = location;
        this.name = name;
    }
}
