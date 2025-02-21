package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

import java.util.Arrays;

public class Call extends Instruction {
    private IRVariable function;
    private IRVariable[] arguments;
    private IRVariable destination;

    public Call(IRVariable function, IRVariable[] arguments, IRVariable destination, Location location) {
        super("Call", location);
        this.function = function;
        this.arguments = arguments;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return name + "(" + function.getType() + "," + String.join(",", Arrays.toString(arguments)) + "," + destination + ")";
    }
}
