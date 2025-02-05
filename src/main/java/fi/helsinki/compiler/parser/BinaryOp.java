package fi.helsinki.compiler.parser;

import fi.helsinki.compiler.tokenizer.Token;

/*
    AST node for a binary operation like `A + B`
 */
public class BinaryOp implements Expression {
    private Expression left;
    private Token operatorToken;
    private Expression right;

    public BinaryOp(Expression left, Token operatorToken, Expression right) {
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
