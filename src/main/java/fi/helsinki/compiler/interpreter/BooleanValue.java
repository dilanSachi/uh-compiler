package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public class BooleanValue extends Value {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public BooleanValue(String value) throws InterpreterException {
        if (value.equals("true") || value.equals("false")) {
            this.value = Boolean.parseBoolean(value);
        } else {
            throw new InterpreterException("Invalid value found for boolean " + value);
        }
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "Boolean";
    }
}
