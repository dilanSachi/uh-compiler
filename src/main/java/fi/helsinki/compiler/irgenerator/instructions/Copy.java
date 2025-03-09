package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

public class Copy extends Instruction {
    private IRVariable source;
    private IRVariable destination;

    public Copy(IRVariable source, IRVariable destination, Location location) {
        super("Copy", location);
        this.source = source;
        this.destination = destination;
    }

    public IRVariable getSource() {
        return source;
    }

    public IRVariable getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return name + "(" + source + "," + destination + ")";
    }
}
