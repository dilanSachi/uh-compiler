package fi.helsinki.compiler.common;

import java.util.HashSet;
import java.util.Set;

public class CommonStatics {

    private int irVariableCounter = -1;
    private int labelNameCounter = -1;
    private Set labelSet = new HashSet();

    public int getIRVariableCounter() {
        irVariableCounter += 1;
        return irVariableCounter;
    }

    public int getLabelNameCounter() {
        labelNameCounter += 1;
        return labelNameCounter;
    }

    public Set getLabelSet() {
        return labelSet;
    }
}
