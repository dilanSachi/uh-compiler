package fi.helsinki.compiler.common.expressions;

import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.tokenizer.Token;

/*
    AST node for a binary operation like `A + B`
 */
public class BinaryOp extends Expression {
    private Expression left;
    private Token operatorToken;
    private Expression right;

    public BinaryOp(Expression left, Token operatorToken, Expression right, Location location) {
        super(location);
        this.left = left;
        this.operatorToken = operatorToken;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Token getOperator() {
        return operatorToken;
    }

    public Expression getRight() {
        return right;
    }
}
