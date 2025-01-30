package fi.helsinki.compiler.parser;

public class ConditionalOp implements Expression {
    private String name = "if";
    private Expression condition;
    private Expression thenBlock;
    private Expression elseBlock;

    public ConditionalOp(Expression condition, Expression thenBlock, Expression elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public String getName() {
        return name;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getThenBlock() {
        return thenBlock;
    }

    public Expression getElseBlock() {
        return elseBlock;
    }
}
