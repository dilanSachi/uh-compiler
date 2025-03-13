package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

public class ReturnIns extends Instruction {

    IRVariable value;

    public ReturnIns(IRVariable value, Location location) {
        super("Return", location);
        this.value = value;
    }

    public IRVariable getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Return(" + value+ ")";
    }
}
