package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

import java.util.Arrays;

public class Jump extends Instruction {
    private Label label;

    public Jump(Label label, Location location) {
        super("Jump", location);
        this.label = label;
    }

    @Override
    public String toString() {
        return name + "," + label;
    }
}
