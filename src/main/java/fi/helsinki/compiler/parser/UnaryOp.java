package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.tokenizer.Token;

public class UnaryOp implements Expression {

    private Token operator;
    private Expression expression;

    public UnaryOp(Token operator, Expression expression) {
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
