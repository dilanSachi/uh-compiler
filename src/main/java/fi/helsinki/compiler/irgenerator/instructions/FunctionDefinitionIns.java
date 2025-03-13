package fi.helsinki.compiler.irgenerator.instructions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.irgenerator.IRVariable;

import java.util.List;

public class FunctionDefinitionIns extends Instruction {

    private List<Instruction> functionInstructions;
    private String functionName;
    private List<IRVariable> parameterVariables;

    public FunctionDefinitionIns(String functionName, List<Instruction> functionInstructions, List<IRVariable> parameterVariables, Location location) {
        super("Function", location);
        this.functionName = functionName;
        this.functionInstructions = functionInstructions;
        this.parameterVariables = parameterVariables;
    }

    public List<Instruction> getFunctionInstructions() {
        return functionInstructions;
    }

    public List<IRVariable> getParameterVariables() {
        return parameterVariables;
    }

    public String getFunctionName() {
        return functionName;
    }
}
