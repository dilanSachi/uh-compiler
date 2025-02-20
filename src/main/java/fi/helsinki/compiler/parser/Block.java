package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.common.Expression;
import fi.helsinki.compiler.common.Location;

import java.util.List;

public class Block extends Expression {

    private List<Expression> expressionList;

    public Block(List<Expression> expressionList, Location location) {
        super(location);
        this.expressionList = expressionList;
    }

//    public Block() {}

    public void addExpression(Expression expression) {
        this.expressionList.add(expression);
    }

    public List<Expression> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(List<Expression> expressionList) {
        this.expressionList = expressionList;
    }
}
