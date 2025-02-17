package fi.helsinki.compiler.interpreter.operators;

import fi.helsinki.compiler.interpreter.BooleanValue;
import fi.helsinki.compiler.interpreter.Interpreter;
import fi.helsinki.compiler.interpreter.SymTab;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.parser.Expression;

import java.util.Optional;

public class AndOp extends Operator {
    @Override
    public Value operate(Expression expression1, Expression expression2, SymTab symTab) throws InterpreterException {
        Interpreter interpreter = new Interpreter();
        Optional<Value> leftValue = interpreter.interpret(expression1, symTab);

        if (leftValue.get() instanceof BooleanValue booleanValue1) {
            if (booleanValue1.getValue()) {
                Optional<Value> rightValue = interpreter.interpret(expression2, symTab);
                if (rightValue.get() instanceof BooleanValue booleanValue2) {
                    if (booleanValue2.getValue()) {
                        return new BooleanValue(true);
                    }
                } else {
                    throw new InterpreterException("Expected boolean value for and operator. Found " + rightValue.get().getType());
                }
            }
        } else {
            throw new InterpreterException("Expected boolean value for and operator. Found " + leftValue.get().getType());
        }
        return new BooleanValue(false);
    }

    @Override
    public String getType() {
        return "AndOpType";
    }
}
