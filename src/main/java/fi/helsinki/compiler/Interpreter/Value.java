package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public abstract class Value {
    public Value add(Value value) throws InterpreterException {
        throw new InterpreterException("Operation not valid for the value");
    };

    public Value subtract(Value value) throws InterpreterException {
        throw new InterpreterException("Operation not valid for the value");
    }

    public Value multiply(Value value) throws InterpreterException {
        throw new InterpreterException("Operation not valid for the value");
    }

    public Value divide(Value value) throws InterpreterException {
        throw new InterpreterException("Operation not valid for the value");
    }

    public abstract String getType();
}
