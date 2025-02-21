package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;

public class Label extends Instruction {
    private String label;

    public Label(String label, Location location) {
        super("Label", location);
        this.label = label;
    }

    @Override
    public String toString() {
        return name + "(" + label + ")";
    }
}
