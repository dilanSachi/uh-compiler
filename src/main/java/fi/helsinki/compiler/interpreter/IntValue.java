package fi.helsinki.compiler.interpreter;

public class IntValue extends Value {

    private Integer intValue;

    public IntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    @Override
    public String getType() {
        return "Int";
    }
}
