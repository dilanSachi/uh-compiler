package fi.helsinki.compiler.interpreter.operators;

import fi.helsinki.compiler.interpreter.IntValue;
import fi.helsinki.compiler.interpreter.Interpreter;
import fi.helsinki.compiler.interpreter.SymTab;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.common.expressions.Expression;

import java.util.Optional;

public class MultiplicationOp extends Operator {

    @Override
    public Value operate(Expression expression1, Expression expression2, SymTab symTab) throws InterpreterException {
        Interpreter interpreter = new Interpreter();
        Optional<Value> leftValue = interpreter.interpret(expression1, symTab);
        Optional<Value> rightValue = interpreter.interpret(expression2, symTab);
        if (leftValue.get() instanceof IntValue intValue1 && rightValue.get() instanceof IntValue intValue2) {
            return new IntValue(intValue1.getIntValue() * intValue2.getIntValue());
        }
        throw new InterpreterException("Expected Int types for multiplication operator. Instead found: "
                + leftValue.get().getType() + ", " + rightValue.get().getType());
    }

    @Override
    public String getType() {
        return "MultiplicationOpType";
    }
}
