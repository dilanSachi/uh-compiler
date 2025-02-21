package fi.helsinki.compiler.interpreter.operators;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.interpreter.*;
import fi.helsinki.compiler.common.expressions.Expression;

import java.util.Optional;

public class NegationOp extends Operator {
    @Override
    public Value operate(Expression expression1, Expression expression2, SymTab symTab) throws InterpreterException {
        Interpreter interpreter = new Interpreter();
        Optional<Value> value = interpreter.interpret(expression1, symTab);
        if (value.get() instanceof IntValue intValue) {
            return new IntValue(-intValue.getIntValue());
        }
        throw new InterpreterException("Expected Int value for negation operator. Found " + value.get().getType());
    }

    @Override
    public String getType() {
        return "NegationOpType";
    }
}
