package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.common.expressions.*;
import fi.helsinki.compiler.common.types.*;
import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeChecker {

    private Optional<Type> checkType(Expression expression, SymbolTable symbolTable) throws TypeCheckerException {
        switch (expression) {
            case IntLiteral literal: {
                Type intType = new IntType();
                literal.setType(intType);
                return Optional.of(intType);
            }
            case BooleanLiteral bool: {
                Type boolType = new BooleanType();
                bool.setType(boolType);
                return Optional.of(boolType);
            }
            case VariableDef variableDef: {
                String key = variableDef.getName();
                if (symbolTable.hasTypeLocally(key)) {
                    throw new TypeCheckerException("Variable already declared in this scope: " + variableDef.getName());
                }
                Optional<Type> valueType = checkType(variableDef.getValue(), symbolTable);
                Optional<String> definedTypeStr = variableDef.getDefinedType();
                if (definedTypeStr.isPresent()) {
                    Type definedType;
                    if (definedTypeStr.get().equals("Int")) {
                        definedType = new IntType();
                    } else {
                        definedType = new BooleanType();
                    }
                    if (!definedType.equals(valueType.get())) {
                        throw new TypeCheckerException("Mismatching types found: " + definedType + ", " + valueType);
                    }
                }
                symbolTable.putType(key, valueType.get());
                return Optional.of(new UnitType());
            }
            case Identifier identifier: {
                Type identifierType = symbolTable.getType(identifier.getName());
                identifier.setType(identifierType);
                return Optional.of(identifierType);
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
                                binaryOp.setType(identifierType);
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
                            Type boolType = new BooleanType();
                            binaryOp.setType(boolType);
                            yield Optional.of(boolType);
                        }
                        throw new TypeCheckerException("Mismatching types found for equality operators: "
                                + leftType.get() + ", " + rightType.get());
                    }
                    default: {
                        if (leftType.isPresent() && rightType.isPresent()) {
                            if (leftType.get() instanceof IntType && rightType.get() instanceof IntType) {
                                if ("+,-,*,%,/".contains(operator.getText())) {
                                    Type intType = new IntType();
                                    binaryOp.setType(intType);
                                    yield Optional.of(intType);
                                }
                                Type boolType = new BooleanType();
                                binaryOp.setType(boolType);
                                yield Optional.of(boolType);
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
                    parameter.setType(paramType.get());
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
                functionCall.setType(functionType.getReturnType());
                return Optional.of(functionType.getReturnType());
            }
            case ConditionalOp conditionalOp: {
                Optional<Type> type = checkType(conditionalOp.getCondition(), symbolTable);
                if (type.get() instanceof BooleanType) {
                    Optional<Type> thenType = checkType(conditionalOp.getThenBlock(), symbolTable);
                    if (conditionalOp.getElseBlock() != null) {
                        Optional<Type> elseType = checkType(conditionalOp.getElseBlock(), symbolTable);
                        if (thenType.get().equals(elseType.get())) {
                            conditionalOp.setType(thenType.get());
                            return thenType;
                        } else {
                            throw new TypeCheckerException("Types does not match in the conditional blocks: "
                                    + thenType.get().getTypeStr() + " ," + elseType.get().getTypeStr());
                        }
                    } else {
                        conditionalOp.setType(new UnitType());
                        return thenType;
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
                    Type intType = new IntType();
                    return Optional.of(intType);
                } else if (unaryOp.getOperator().getText().equals("not") && operandType.get() instanceof BooleanType) {
                    Type boolType = new BooleanType();
                    return Optional.of(boolType);
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
        symbolTable.putType("print_int", new FunctionType("print_int", new UnitType(), new IntType()));
        symbolTable.putType("print_boolean", new FunctionType("print_boolean", new UnitType(), new BooleanType()));
        symbolTable.putType("read_int", new FunctionType("read_int", new IntType(), new UnitType()));
        return checkType(expression, symbolTable);
    }
}
