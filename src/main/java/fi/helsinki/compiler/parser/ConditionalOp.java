package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

public class ConditionalOp extends Expression {
    private String name = "if";
    private Expression condition;
    private Expression thenBlock;
    private Expression elseBlock;

    public ConditionalOp(Expression condition, Expression thenBlock, Expression elseBlock, Location location) {
        super(location);
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
