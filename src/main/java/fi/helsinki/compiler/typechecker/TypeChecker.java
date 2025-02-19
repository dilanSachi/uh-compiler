package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.interpreter.FunctionDefinition;
import fi.helsinki.compiler.interpreter.Value;
import fi.helsinki.compiler.parser.*;
import fi.helsinki.compiler.parser.Boolean;
import fi.helsinki.compiler.tokenizer.Token;
import fi.helsinki.compiler.typechecker.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeChecker {

    private Optional<Type> checkType(Expression expression, SymbolTable symbolTable) throws TypeCheckerException {
        switch (expression) {
            case Literal literal: {
                return Optional.of(new IntType());
            }
            case Boolean bool: {
                return Optional.of(new BooleanType());
            }
            case VariableDef variableDef: {
                String key = variableDef.getName();
                if (symbolTable.hasTypeLocally(key)) {
                    throw new TypeCheckerException("Variable already declared in this scope: " + variableDef.getName());
                }
                Optional<Type> value = checkType(variableDef.getValue(), symbolTable);
                symbolTable.putType(key, value.get());
                return Optional.of(new UnitType());
            }
            case Identifier identifier: {
                return Optional.of(symbolTable.getType(identifier.getName()));
            }
            case BinaryOp binaryOp: {
                Optional<Type> leftType = checkType(binaryOp.getLeft(), symbolTable);
                Optional<Type> rightType = checkType(binaryOp.getRight(), symbolTable);
                Token operator = binaryOp.getOperator();
                return switch (operator.getText()) {
                    case "=": {
                        if (binaryOp.getLeft() instanceof Identifier identifier) {
                            Type identifierType = symbolTable.getType(identifier.getName());
                            Optional<Type> assignedType = checkType(binaryOp.getRight(), symbolTable);
                            if (identifierType.getClass() == assignedType.get().getClass()) {
                                yield Optional.of(identifierType);
                            }
                            throw new TypeCheckerException("Expected type " + identifierType);
                        } else {
                            throw new TypeCheckerException("Only identifiers are allowed");
                        }
                    }
                    case "==": {}
                    case "!=": {
                        if (leftType.get().getClass() == rightType.get().getClass()) {
                            yield Optional.empty();
                        }
                        throw new TypeCheckerException("Mismatching types found for equality operators: "
                                + leftType.get() + ", " + rightType.get());
                    }
                    default: {
                        if (leftType.isPresent() && rightType.isPresent()) {
                            if (leftType.get() instanceof IntType && rightType.get() instanceof IntType) {
                                if ("+,-,*,%,/".contains(operator.getText())) {
                                    yield Optional.of(new IntType());
                                }
                                yield Optional.of(new BooleanType());
                            } else {
                                throw new TypeCheckerException("Expected an Int type for '" + operator.getText()
                                        + "' operator. Instead found " + leftType + ", " + rightType);
                            }
                        } else {
                            throw new TypeCheckerException("Expected an Int type for '" +
                                    operator.getText() + "' operator");
                        }
                    }
                };
            }
            case FunctionCall functionCall: {
                List<Expression> parameters = functionCall.getParameters();
                List<Type> paramTypes = new ArrayList<>();
                for (Expression parameter: parameters) {
                    Optional<Type> paramType = checkType(parameter, symbolTable);
                    if (paramType.isEmpty()) {
                        throw new TypeCheckerException("Invalid parameter type " + paramType
                                + " for function " + functionCall.getFunctionName());
                    }
                    paramTypes.add(paramType.get());
                }
                FunctionType functionType = (FunctionType) symbolTable.getType(functionCall.getFunctionName());
                List<Type> expectedParamTypes = functionType.getParameterTypes();
                if (paramTypes.size() != functionType.getParameterTypes().size()) {
                    throw new TypeCheckerException("Mismatching parameters provided to the function" +
                            functionCall.getFunctionName());
                }
                for (int i = 0; i < paramTypes.size(); i++) {
                    if (!paramTypes.get(i).equals(expectedParamTypes.get(i))) {
                        throw new TypeCheckerException("Expected type of " + expectedParamTypes.get(i) +
                                ". Instead found " + paramTypes.get(i));
                    }
                }
                return Optional.of(functionType.getReturnType());
            }
            case ConditionalOp conditionalOp: {
                Optional<Type> type = checkType(conditionalOp.getCondition(), symbolTable);
                if (type.get() instanceof BooleanType) {
                    Optional<Type> thenType =  checkType(conditionalOp.getThenBlock(), symbolTable);
                    Optional<Type> elseType =  checkType(conditionalOp.getElseBlock(), symbolTable);
                    if (thenType.isPresent() && elseType.isPresent()) {
                        if (thenType.get().equals(elseType.get())) {
                            return thenType;
                        } else {
                            throw new TypeCheckerException("Types does not match in the conditional blocks: "
                                    + thenType.get().getType() + " ," + elseType.get().getType());
                        }
                    } else {
                        throw new TypeCheckerException("Invalid type found");
                    }
                } else {
                    throw new TypeCheckerException("Expected a Boolean type for the conditional type.");
                }
            }
            case WhileOp whileOp: {
                Optional<Type> condition = checkType(whileOp.getCondition(), symbolTable);
                if (condition.get() instanceof BooleanType) {
                    return checkType(whileOp.getBody(), symbolTable);
                } else {
                    throw new TypeCheckerException("Expected a Boolean type for the conditional type.");
                }
            }
            case UnaryOp unaryOp: {
                Optional<Type> operandType = checkType(unaryOp.getExpression(), symbolTable);
                if (unaryOp.getOperator().getText().equals("-") && operandType.get() instanceof IntType) {
                    return Optional.of(new IntType());
                } else if (unaryOp.getOperator().getText().equals("not") && operandType.get() instanceof BooleanType) {
                    return Optional.of(new BooleanType());
                }
                throw new TypeCheckerException("Invalid unary operation");
            }
            case Block block: {
                SymbolTable localSymbolTable = new SymbolTable(symbolTable);
                List<Expression> expressionList = block.getExpressionList();
                for (int i = 0; i < expressionList.size() - 1; i++) {
                    checkType(expressionList.get(i), localSymbolTable);
                }
                if (!(expressionList.getLast() instanceof Unit)) {
                    return checkType(expressionList.getLast(), localSymbolTable);
                }
                return Optional.of(new UnitType());
            }
            default: {
                throw new TypeCheckerException("Invalid type found: " + expression.getLocation());
            }
        }
    }

    public Optional<Type> checkType(Expression expression) throws TypeCheckerException {
        SymbolTable symbolTable = new SymbolTable(null);
        symbolTable.putType("print_int", new FunctionType(new UnitType(), new IntType()));
        symbolTable.putType("print_boolean", new FunctionType(new UnitType(), new BooleanType()));
        symbolTable.putType("read_int", new FunctionType(new IntType(), new UnitType()));
        return checkType(expression, symbolTable);
    }
}
