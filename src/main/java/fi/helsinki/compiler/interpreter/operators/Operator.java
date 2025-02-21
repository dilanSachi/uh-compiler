package fi.helsinki.compiler.interpreter.operators;

import fi.helsinki.compiler.interpreter.SymTab;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.common.expressions.Expression;

public abstract class Operator extends Value {
    public abstract Value operate(Expression value1, Expression value2, SymTab symTab) throws InterpreterException;
}
