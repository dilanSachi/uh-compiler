package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

public abstract class FunctionDefinition extends Value {
    public abstract Value invoke(Value... values) throws InterpreterException;
}
