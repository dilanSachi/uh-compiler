package fi.helsinki.compiler.common.types;

import java.util.List;

public class FunctionType extends Type {
    private List<Type> parameterTypes;
    private Type returnType;
    private String functionName;

    public FunctionType(String functionName, Type returnType, Type... parameterTypes) {
        this.functionName = functionName;
        this.returnType = returnType;
        this.parameterTypes = List.of(parameterTypes);
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public String getTypeStr() {
        return functionName;
    }
}
