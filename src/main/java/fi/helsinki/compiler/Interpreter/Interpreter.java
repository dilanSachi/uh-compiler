package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.ConditionalOp;
import fi.helsinki.compiler.parser.Expression;
import fi.helsinki.compiler.parser.Literal;

public class Interpreter {

    public Object interpret(Expression expression) throws InterpreterException {
        switch (expression) {
            case Literal literal: {
                return literal.getValue();
            }
            case BinaryOp binaryOp: {
                Integer leftValue = (Integer) interpret(binaryOp.getLeft());
                Integer rightValue = (Integer) interpret(binaryOp.getRight());
                if (binaryOp.getOperator().getText().equals("+")) {
                    return leftValue + rightValue;
                } else if (binaryOp.getOperator().getText().equals("-")) {
                    return leftValue - rightValue;
                } else if (binaryOp.getOperator().getText().equals("*")) {
                    return leftValue * rightValue;
                } else if (binaryOp.getOperator().getText().equals("/")) {
                    return leftValue / rightValue;
                }
                throw new InterpreterException("BinaryOp " + binaryOp.getOperator().getText() + " not yet supported");
            }
            case ConditionalOp conditionalOp: {
                if ((boolean) interpret(conditionalOp.getCondition())) {
                    return interpret(conditionalOp.getThenBlock());
                }
                return interpret(conditionalOp.getElseBlock());
            }
            default: {
                throw new InterpreterException("Invalid type found: " + expression);
            }
        }
    }
}
