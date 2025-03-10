package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;

public class WhileOp extends Expression {
    private Expression condition;
    private Expression body;

    public WhileOp(Expression condition, Expression body, Location location) {
        super(location);
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getBody() {
        return body;
    }
}
