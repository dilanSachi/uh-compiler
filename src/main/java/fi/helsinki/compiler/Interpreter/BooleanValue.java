package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public class BooleanValue extends Value {
    private boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String getType() {
        return "Boolean";
    }
}
