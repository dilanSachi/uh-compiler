package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.common.types.Type;

import java.util.List;

public class FunctionDefinition extends Expression {

    private String functionName;
    private List<FunctionArgumentDefinition> arguments;
    private String returnType;
    private Block block;

    public FunctionDefinition(String functionName, List<FunctionArgumentDefinition> arguments, String returnType,
                              Block block, Location location) {
        super(location);
        this.functionName = functionName;
        this.arguments = arguments;
        this.returnType = returnType;
        this.block = block;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<FunctionArgumentDefinition> getArguments() {
        return arguments;
    }

    public String getReturnType() {
        return returnType;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public Location getLocation() {
        return super.getLocation();
    }
}
