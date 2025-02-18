package fi.helsinki.compiler.interpreter;

import fi.helsinki.compiler.interpreter.functions.PrintBoolFunction;
import fi.helsinki.compiler.interpreter.functions.PrintIntFunction;
import fi.helsinki.compiler.interpreter.functions.ReadIntFunction;
import fi.helsinki.compiler.interpreter.operators.*;
import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.parser.*;
import fi.helsinki.compiler.parser.Boolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Interpreter {

    public Optional<Value> interpret(Expression expression, SymTab symTab) throws InterpreterException {
        switch (expression) {
            case Literal literal: {
                return Optional.of(new IntValue(literal.getValue()));
            }
            case Boolean bool: {
                return Optional.of(new BooleanValue(bool.getValue()));
            }
            case UnaryOp unaryOp: {
                if (unaryOp.getOperator().getText().equals("-")) {
                    return Optional.of(((Operator) symTab.getValue("negOp"))
                            .operate(unaryOp.getExpression(), null, symTab));
                } else {
                    return Optional.of(((Operator) symTab.getValue("notOp"))
                            .operate(unaryOp.getExpression(), null, symTab));
                }
            }
            case BinaryOp binaryOp: {
                return switch (binaryOp.getOperator().getText()) {
                    case "=" -> {
                        if (binaryOp.getLeft() instanceof Identifier identifier) {
                            Optional<Value> value = interpret(binaryOp.getRight(), symTab);
                            Optional<SymTab> symbolOwner = symTab.getSymbolOwner(identifier.getName());
                            if (symbolOwner.isEmpty()) {
                                throw new InterpreterException("Unknown identifier: " + identifier);
                            } else {
                                symbolOwner.get().putValue(identifier.getName(), value.get());
                            }
                            yield value;
                        } else {
                            throw new InterpreterException("Expected an identifier: " + binaryOp.getLeft());
                        }
                    }
                    default ->
                        Optional.of(((Operator) symTab.getValue(binaryOp.getOperator().getText()))
                                .operate(binaryOp.getLeft(), binaryOp.getRight(), symTab));
                };
            }
            case ConditionalOp conditionalOp: {
                Optional<Value> condition = interpret(conditionalOp.getCondition(), symTab);
                if (condition.get() instanceof BooleanValue booleanValue) {
                    if (booleanValue.getValue()) {
                        return interpret(conditionalOp.getThenBlock(), symTab);
                    }
                    if (conditionalOp.getElseBlock() != null) {
                        return interpret(conditionalOp.getElseBlock(), symTab);
                    }
                    return Optional.empty();
                } else {
                    throw new InterpreterException("Expected a conditional value.");
                }
            }
            case WhileOp whileOp: {
                while (true) {
                    Optional<Value> condition = interpret(whileOp.getCondition(), symTab);
                    if (condition.get() instanceof BooleanValue booleanValue) {
                        if (booleanValue.getValue()) {
                            interpret(whileOp.getBody(), symTab);
                        } else {
                            return Optional.empty();
                        }
                    } else {
                        throw new InterpreterException("Expected a conditional value.");
                    }
                }
            }
            case VariableDef variableDef:
                String key = variableDef.getName();
                if (symTab.hasValueLocally(key)) {
                    throw new InterpreterException("Variable already declared in this scope: " + variableDef.getName());
                }
                Optional<Value> value = interpret(variableDef.getValue(), symTab);
                symTab.putValue(key, value.get());
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
                return Optional.ofNullable(functionDefinition.invoke(paramValues.toArray(new Value[]{})));
            }
            default: {
                throw new InterpreterException("Invalid type found: " + expression);
            }
        }
    }

    public Value interpretAST(Expression expression) throws InterpreterException {
        SymTab globalSymTab = getGlobalSymTab();
        Optional<Value> value = interpret(expression, globalSymTab);
        return value.orElse(null);
    }

    private SymTab getGlobalSymTab() {
        SymTab globalSymTab = new SymTab(null);
        globalSymTab.putValue("print_int", new PrintIntFunction());
        globalSymTab.putValue("print_bool", new PrintBoolFunction());
        globalSymTab.putValue("read_int", new ReadIntFunction());
        globalSymTab.putValue("+", new AdditionOp());
        globalSymTab.putValue("-", new SubtractionOp());
        globalSymTab.putValue("*", new MultiplicationOp());
        globalSymTab.putValue("/", new DivisionOp());
        globalSymTab.putValue("%", new ModulusOp());
        globalSymTab.putValue("and", new AndOp());
        globalSymTab.putValue("or", new OrOp());
        globalSymTab.putValue("notOp", new NotOp());
        globalSymTab.putValue("negOp", new NegationOp());
        globalSymTab.putValue(">=", new GreaterThanOrEqualOp());
        globalSymTab.putValue(">", new GreaterThanOp());
        globalSymTab.putValue("<", new LessThanOp());
        globalSymTab.putValue("<=", new LessThanOrEqualOp());
        globalSymTab.putValue("!=", new InequalityOp());
        globalSymTab.putValue("==", new EqualityOp());
        return globalSymTab;
    }
}
