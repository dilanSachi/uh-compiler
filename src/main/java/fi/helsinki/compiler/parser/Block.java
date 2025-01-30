package fi.helsinki.compiler.parser;

import java.util.List;

public class Block implements Expression {

    private List<Expression> expressionList;

    public Block(List<Expression> expressionList) {
        this.expressionList = expressionList;
    }

    public void addExpression(Expression expression) {
        this.expressionList.add(expression);
    }

    public List<Expression> getExpressionList() {
        return expressionList;
    }
}
