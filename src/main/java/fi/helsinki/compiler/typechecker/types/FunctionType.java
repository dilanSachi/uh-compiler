package fi.helsinki.compiler.typechecker.types;

import java.util.List;

public class FunctionType extends Type {
    private List<Type> parameterTypes;
    private Type returnType;

    public FunctionType(Type returnType, Type... parameterTypes) {
        this.returnType = returnType;
        this.parameterTypes = List.of(parameterTypes);
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public String getType() {
        return "Function";
    }
}
