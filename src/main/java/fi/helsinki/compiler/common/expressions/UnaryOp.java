package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.tokenizer.Token;

public class UnaryOp extends Expression {

    private Token operator;
    private Expression expression;

    public UnaryOp(Token operator, Expression expression, Location location) {
        super(location);
        this.expression = expression;
        this.operator = operator;
    }

    public Expression getExpression() {
        return expression;
    }

    public Token getOperator() {
        return operator;
    }
}
