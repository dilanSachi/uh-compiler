package fi.helsinki.compiler.interpreter.operators;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.interpreter.*;
import fi.helsinki.compiler.parser.Expression;

import java.util.Optional;

public class LessThanOp extends Operator {

    @Override
    public Value operate(Expression expression1, Expression expression2, SymTab symTab) throws InterpreterException {
        Interpreter interpreter = new Interpreter();
        Optional<Value> leftValue = interpreter.interpret(expression1, symTab);
        Optional<Value> rightValue = interpreter.interpret(expression2, symTab);
        if (leftValue.get() instanceof IntValue intValue1 && rightValue.get() instanceof IntValue intValue2) {
            return new BooleanValue(intValue1.getIntValue() < intValue2.getIntValue());
        }
        throw new InterpreterException("Expected Int types for less than operator. Instead found: "
                + leftValue.get().getType() + ", " + rightValue.get().getType());
    }

    @Override
    public String getType() {
        return "LessThanOpType";
    }
}
