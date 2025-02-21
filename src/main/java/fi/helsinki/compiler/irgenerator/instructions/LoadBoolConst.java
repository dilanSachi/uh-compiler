package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

public class LoadBoolConst extends Instruction {
    private boolean value;
    private IRVariable destination;

    public LoadBoolConst(boolean value, IRVariable destination, Location location) {
        super("LoadBoolConst", location);
        this.value = value;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return name + "(" + value + "," + destination + ")";
    }
}
