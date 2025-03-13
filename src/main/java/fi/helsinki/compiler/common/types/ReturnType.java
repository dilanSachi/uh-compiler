package fi.helsinki.compiler.common.types;

public class ReturnType extends Type {
    private Type actualReturnType;

    public ReturnType(Type actualReturnType) {
        this.actualReturnType = actualReturnType;
    }

    public Type getActualReturnType() {
        return actualReturnType;
    }

    @Override
    public String getTypeStr() {
        return "ReturnType";
    }
}
