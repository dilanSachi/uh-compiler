package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.Location;

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
