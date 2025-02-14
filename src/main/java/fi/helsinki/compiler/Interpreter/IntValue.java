package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public class IntValue extends Value {

    private Integer intValue;

    public IntValue(Integer intValue) {
        this.intValue = intValue;
    }

    @Override
    public Value add(Value value) throws InterpreterException {
        if (value instanceof IntValue intValue2) {
            return new IntValue(intValue2.getIntValue() + intValue);
        }
        throw new InterpreterException("Cannot do addition with mismatching types: " + getType() + ", " + value.getType());
    }

    @Override
    public Value subtract(Value value) throws InterpreterException {
        if (value instanceof IntValue intValue2) {
            return new IntValue(intValue2.getIntValue() - intValue);
        }
        throw new InterpreterException("Cannot do subtraction with mismatching types: " + getType() + ", " + value.getType());
    }

    @Override
    public Value multiply(Value value) throws InterpreterException {
        if (value instanceof IntValue intValue2) {
            return new IntValue(intValue2.getIntValue() * intValue);
        }
        throw new InterpreterException("Cannot do multiplication with mismatching types: " + getType() + ", " + value.getType());
    }

    @Override
    public Value divide(Value value) throws InterpreterException {
        if (value instanceof IntValue intValue2) {
            return new IntValue(intValue2.getIntValue() / intValue);
        }
        throw new InterpreterException("Cannot do division with mismatching types: " + getType() + ", " + value.getType());
    }

    public Integer getIntValue() {
        return intValue;
    }

    @Override
    public String getType() {
        return "Int";
    }
}
