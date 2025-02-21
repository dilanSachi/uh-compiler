package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

import java.util.Arrays;

public class Label extends Instruction {
    public Label(IRVariable function, IRVariable[] arguments, IRVariable destination, Location location) {
        super("Label", location);
    }

    @Override
    public String toString() {
        return name;
    }
}
