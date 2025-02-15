package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.parser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Interpreter {

    private Optional<Value> interpret(Expression expression, SymTab symTab) throws InterpreterException {
        switch (expression) {
            case Literal literal: {
                return Optional.of(new IntValue(literal.getValue()));
            }
            case BinaryOp binaryOp: {
                Optional<Value> leftValue = interpret(binaryOp.getLeft(), symTab);
                Optional<Value> rightValue = interpret(binaryOp.getRight(), symTab);
                return switch (binaryOp.getOperator().getText()) {
                    case "+" -> Optional.of(rightValue.get().add(leftValue.get()));
                    case "-" -> Optional.of(rightValue.get().subtract(leftValue.get()));
                    case "*" -> Optional.of(rightValue.get().multiply(leftValue.get()));
                    case "/" -> Optional.of(rightValue.get().divide(leftValue.get()));
                    case "=" -> {
                        if (binaryOp.getLeft() instanceof Identifier identifier) {
                            Optional<Value> value = interpret(binaryOp.getRight(), symTab);
                            Optional<SymTab> symbolOwner = symTab.getSymbolOwner(identifier.getName());
                            if (symbolOwner.isEmpty()) {
                                throw new InterpreterException("Unknown identifier: " + identifier);
                            } else {
                                symbolOwner.get().setValue(identifier.getName(), value.get());
                            }
                            yield value;
                        } else {
                            throw new InterpreterException("Expected an identifier: " + binaryOp.getLeft());
                        }
                    }
                    default ->
                            throw new InterpreterException("BinaryOp " + binaryOp.getOperator().getText() + " not yet supported");
                };
            }
            case UnaryOp unaryOp: {
                Optional<Value> value = interpret(unaryOp.getExpression(), symTab);
                if (unaryOp.getOperator().getText().equals("not")) {
                    if (value.get() instanceof IntValue intValue) {
                        return Optional.of(new IntValue(-intValue.getIntValue()));
                    } else {
                        throw new InterpreterException("Expected an integer value for the operation");
                    }
                } else {
                    if (value.get() instanceof BooleanValue booleanValue) {
                        return Optional.of(new BooleanValue(!booleanValue.getValue()));
                    } else {
                        throw new InterpreterException("Expected a boolean value for the operation");
                    }
                }
            }
            case ConditionalOp conditionalOp: {
                Optional<Value> condition = interpret(conditionalOp.getCondition(), symTab);
                if (condition.get() instanceof BooleanValue booleanValue) {
                    if (booleanValue.getValue()) {
                        return interpret(conditionalOp.getThenBlock(), symTab);
                    }
                    return interpret(conditionalOp.getElseBlock(), symTab);
                } else {
                    throw new InterpreterException("Expected a conditional value.");
                }
            }
            case VariableDef variableDef:
                String key = variableDef.getName();
                if (symTab.hasValueLocally(key)) {
                    throw new InterpreterException("Variable already declared in this scope: " + variableDef.getName());
                }
                Optional<Value> value = interpret(variableDef.getValue(), symTab);
                symTab.setValue(key, value.get());
                return Optional.empty();
            case Identifier identifier:
                return Optional.of(symTab.getValue(identifier.getName()));
            case Block block: {
                SymTab localSymTab = new SymTab(symTab);
                List<Expression> expressionList = block.getExpressionList();
                for (int i = 0; i < expressionList.size() - 1; i++) {
                    interpret(expressionList.get(i), localSymTab);
                }
                if (!(expressionList.getLast() instanceof Unit)) {
                    return interpret(expressionList.getLast(), localSymTab);
                }
                return Optional.empty();
            }
            case FunctionCall functionCall: {
                List<Expression> parameters = functionCall.getParameters();
                List<Value> paramValues = new ArrayList<>();
                for (Expression parameter: parameters) {
                    Optional<Value> paramValue = interpret(parameter, symTab);
                    if (paramValue.isEmpty()) {
                        throw new InterpreterException("Function provided with an invalid value");
                    }
                    paramValues.add(interpret(parameter, symTab).get());
                }
                FunctionDefinition functionDefinition = (FunctionDefinition) symTab.getValue(functionCall.getFunctionName());
                functionDefinition.invoke(paramValues.toArray(new Value[]{}));
                return Optional.empty();
            }
            default: {
                throw new InterpreterException("Invalid type found: " + expression);
            }
        }
    }

    public Value interpretAST(Expression expression) throws InterpreterException {
        SymTab globalSymTab = new SymTab(null);
        globalSymTab.setValue("print_int", new PrintIntFunction());
        Optional<Value> value = interpret(expression, globalSymTab);
        if (value.isEmpty()) {
            return null;
        } else {
            return value.get();
        }
    }
}
