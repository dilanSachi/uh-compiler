package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

public class LoadIntConst extends Instruction {
    private Long value;
    private IRVariable destination;

    public LoadIntConst(Long value, IRVariable destination, Location location) {
        super("LoadIntConst", location);
        this.destination = destination;
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public IRVariable getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return name + "(" + value + "," + destination + ")";
    }
}
