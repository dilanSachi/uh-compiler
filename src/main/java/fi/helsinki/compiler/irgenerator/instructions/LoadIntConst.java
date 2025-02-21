package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

public class LoadIntConst extends Instruction {
    private int value;
    private IRVariable destination;

    public LoadIntConst(int value, IRVariable destination, Location location) {
        super("LoadIntConst", location);
        this.destination = destination;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + "(" + value + "," + destination + ")";
    }
}
