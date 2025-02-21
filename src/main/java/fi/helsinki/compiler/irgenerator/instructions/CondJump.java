package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

public class CondJump extends Instruction {
    private IRVariable condition;
    private Label thenLabel;
    private Label elseLabel;

    public CondJump(IRVariable condition, Label thenLabel, Label elseLabel, Location location) {
        super("CondJump", location);
        this.condition = condition;
        this.thenLabel = thenLabel;
        this.elseLabel = elseLabel;
    }

    @Override
    public String toString() {
        return name + "(" + condition + "," + thenLabel + "," + elseLabel + ")";
    }
}
