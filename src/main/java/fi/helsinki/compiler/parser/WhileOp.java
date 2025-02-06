package fi.helsinki.compiler.parser;

public class WhileOp implements Expression {
    private Expression condition;
    private Expression body;

    public WhileOp(Expression condition, Expression body) {
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
