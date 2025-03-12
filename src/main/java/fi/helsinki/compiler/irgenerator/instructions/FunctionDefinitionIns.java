package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;

import java.util.List;

public class FunctionDefinitionIns extends Instruction {

    private List<Instruction> functionInstructions;
    private String functionName;

    public FunctionDefinitionIns(String functionName, List<Instruction> functionInstructions, Location location) {
        super("Function", location);
        this.functionName = functionName;
        this.functionInstructions = functionInstructions;
    }

    public List<Instruction> getFunctionInstructions() {
        return functionInstructions;
    }

    public String getFunctionName() {
        return functionName;
    }
}
