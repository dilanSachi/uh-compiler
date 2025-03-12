package fi.helsinki.compiler.common.expressions;

import java.util.List;

public class Module {
    private List<Expression> expressions;
    private List<FunctionDefinition> functionDefinitions;

    public List<Expression> getExpressions() {
        return expressions;
    }

    public List<FunctionDefinition> getFunctionDefinitions() {
        return functionDefinitions;
    }
}
