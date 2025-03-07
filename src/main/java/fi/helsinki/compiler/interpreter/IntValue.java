package fi.helsinki.compiler.interpreter;

public class IntValue extends Value {

    private Long intValue;

    public IntValue(Long intValue) {
        this.intValue = intValue;
    }

    public Long getIntValue() {
        return intValue;
    }

    @Override
    public String getType() {
        return "Int";
    }
}
