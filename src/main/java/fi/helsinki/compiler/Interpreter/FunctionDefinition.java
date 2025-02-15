package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public abstract class FunctionDefinition extends Value {
    public abstract void invoke(Value... values) throws InterpreterException;
}
