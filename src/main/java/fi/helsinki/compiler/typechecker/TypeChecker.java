package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.parser.BinaryOp;
import fi.helsinki.compiler.parser.Expression;
import fi.helsinki.compiler.parser.Literal;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.typechecker.types.IntType;
import fi.helsinki.compiler.typechecker.types.Type;

import java.util.Optional;

public class TypeChecker {

    private Optional<Type> checkType(Expression expression, SymbolTable symbolTable) throws TypeCheckerException {
        switch (expression) {
            case Literal literal: {
                return Optional.of(new IntType());
            }
            case BinaryOp binaryOp: {
                Optional<Type> leftType = checkType(binaryOp.getLeft(), symbolTable);
                Optional<Type> rightType = checkType(binaryOp.getRight(), symbolTable);
                Token operator = binaryOp.getOperator();
                return switch (binaryOp.getOperator().getText()) {
                    case "=" -> throw new TypeCheckerException("not implemented yet");
                    case "+" -> {
                        if (leftType.isPresent() && rightType.isPresent()) {
                            if (leftType.get() instanceof IntType && rightType.get() instanceof IntType) {
                                yield Optional.of(new IntType());
                            } else {
                                throw new TypeCheckerException("Expected an Int type for + operator. Instead found "
                                        + leftType + ", " + rightType);
                            }
                        } else {
                            throw new TypeCheckerException("Expected an Int type for + operator");
                        }
                    }
                    default -> throw new TypeCheckerException("Not implemented yet");
                };
            }
            default: {
                throw new TypeCheckerException("Invalid type found: " + expression);
            }
        }
    }

    public Optional<Type> checkType(Expression expression) throws TypeCheckerException {
        SymbolTable symbolTable = new SymbolTable(null);
        return checkType(expression, symbolTable);
    }
}
