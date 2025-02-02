package fi.helsinki.compiler.parser;

import java.util.List;

public class FunctionCall implements Expression {
    private String functionName;
    private List<Expression> parameters;

    public FunctionCall(String functionName, List<Expression> parameters) {
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
