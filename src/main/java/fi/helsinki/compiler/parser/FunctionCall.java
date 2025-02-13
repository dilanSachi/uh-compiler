package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.Location;

import java.util.List;

public class FunctionCall extends Expression {
    private String functionName;
    private List<Expression> parameters;

    public FunctionCall(String functionName, List<Expression> parameters, Location location) {
        super(location);
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public List<Expression> getParameters() {
        return parameters;
    }

    public String getFunctionName() {
        return functionName;
    }
}
