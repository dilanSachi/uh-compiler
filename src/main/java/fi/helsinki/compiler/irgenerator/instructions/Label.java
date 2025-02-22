package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;

import java.util.HashSet;
import java.util.Set;

public class Label extends Instruction {
    private String label;
    private static Set labelSet = new HashSet();
    private static int counter = 1;

    public Label(String label, Location location) {
        super("Label", location);
        if (labelSet.contains(label)) {
            label = label + counter;
            counter += 1;
        }
        this.label = label;
        labelSet.add(this.label);
    }

    public static void resetCounter() {
        counter = 1;
        labelSet.clear();
    }

    @Override
    public String toString() {
        return name + "(" + label + ")";
    }
}
