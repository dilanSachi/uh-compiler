package fi.helsinki.compiler.common.types;

public class NegationType extends Type {
    @Override
    public String getTypeStr() {
        return "unary_-";
    }
}
