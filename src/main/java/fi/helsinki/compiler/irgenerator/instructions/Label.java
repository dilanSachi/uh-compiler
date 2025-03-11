package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.CommonStatics;
import fi.helsinki.compiler.common.Location;

public class Label extends Instruction {
    private String label;

    public Label(CommonStatics commonStatics, String label, Location location) {
        super("Label", location);
        if (commonStatics.getLabelSet().contains(label)) {
            label = label + commonStatics.getLabelNameCounter();
        }
        this.label = label;
        commonStatics.getLabelSet().add(this.label);
    }

    public String getLabelName() {
        return label;
    }

    @Override
    public String toString() {
        return name + "(" + label + ")";
    }
}
