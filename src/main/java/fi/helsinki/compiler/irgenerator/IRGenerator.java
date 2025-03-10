package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.*;
import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.common.types.*;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.irgenerator.instructions.*;
import fi.helsinki.compiler.tokenizer.Token;

import java.util.*;

public class IRGenerator {

    private Map<IRVariable, Type> variableTypeMap;
    private List<Instruction> instructions;

    public IRGenerator() {
        IRVariable.resetCounter();
        Label.resetCounter();
        variableTypeMap = new HashMap<>();
        setPredefinedVariableTypes();
    }

    private void setPredefinedVariableTypes() {
        createVariable(new AdditionType());
        createVariable(new GreaterThanType());
        createVariable(new GreaterThanOrEqualType());
        createVariable(new LessThanOrEqualType());
        createVariable(new LessThanType());
        createVariable(new SubtractionType());
        createVariable(new MultiplicationType());
        createVariable(new DivisionType());
        createVariable(new ModulusType());
        createVariable(new FunctionType("print_int", new UnitType(), new IntType()));
        createVariable(new FunctionType("print_boolean", new UnitType(), new BooleanType()));
        createVariable(new FunctionType("read_int", new IntType(), new UnitType()));
    }

    public List<Instruction> generateIR(Expression rootExpression) throws IRGenerationException {
        instructions = new ArrayList<>();
//        instructions.add(new Label("start", rootExpression.getLocation()));
        SymbolTable rootSymbolTable = new SymbolTable(null);
        for (IRVariable key: variableTypeMap.keySet()) {
            rootSymbolTable.putVariable(key.getType().getTypeStr(), key);
        }
        IRVariable finalResult = visit(rootExpression, rootSymbolTable);
        if (variableTypeMap.get(finalResult) instanceof IntType) {
            instructions.add(new Call(createVariable(new FunctionType("print_int", new UnitType(), new IntType())),
                    new IRVariable[]{finalResult}, createVariable(new UnitType()), rootExpression.getLocation()));
        } else if (variableTypeMap.get(finalResult) instanceof BooleanType) {
            instructions.add(new Call(createVariable(new FunctionType("print_bool", new UnitType(), new BooleanType())),
                    new IRVariable[]{finalResult}, createVariable(new UnitType()), rootExpression.getLocation()));
        }
        return instructions;
    }

    private IRVariable visit(Expression expression, SymbolTable symbolTable) throws IRGenerationException {
        Location location = expression.getLocation();
        switch (expression) {
            case IntLiteral intLiteral: {
                IRVariable variable = createVariable(new IntType());
                Instruction instruction = new LoadIntConst(intLiteral.getValue(), variable, location);
                instructions.add(instruction);
                return variable;
            }
            case BooleanLiteral booleanLiteral: {
                IRVariable variable = createVariable(new BooleanType());
                Instruction instruction = new LoadBoolConst(booleanLiteral.getValue(), variable, location);
                instructions.add(instruction);
                return variable;
            }
            case Identifier identifier: {
                return symbolTable.getVariable(identifier.getName());
            }
            case UnaryOp unaryOp: {
                IRVariable variable = visit(unaryOp.getExpression(), symbolTable);
                IRVariable resultVariable = createVariable(unaryOp.getType());
                IRVariable operatorVariable;
                if (unaryOp.getOperator().getText().equals("-")) {
                    operatorVariable = createVariable(new NegationType());
                } else {
                    operatorVariable = createVariable(new NotType());
                }
                instructions.add(new Call(operatorVariable, new IRVariable[]{variable}, resultVariable, unaryOp.getLocation()));
                return resultVariable;
            }
            case BinaryOp binaryOp: {
                Token operator = binaryOp.getOperator();
                if (operator.getText().equals("=")) {
                    IRVariable variable = visit(binaryOp.getRight(), symbolTable);
                    IRVariable identifier = symbolTable.getVariable(((Identifier) binaryOp.getLeft()).getName());
                    instructions.add(new Copy(variable, identifier, binaryOp.getLeft().getLocation()));
                    return identifier;
                }
                if (operator.getText().equals("and")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable);
                    Label andRightLabel = new Label("and_right", binaryOp.getLeft().getLocation());
                    Label andSkipLabel = new Label("and_skip", binaryOp.getLeft().getLocation());
                    Label andEndLabel = new Label("and_end", binaryOp.getLocation());
                    instructions.add(new CondJump(leftVariable, andRightLabel, andSkipLabel, binaryOp.getLocation()));
                    instructions.add(andRightLabel);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructions.add(new Copy(rightVariable, resultVariable, binaryOp.getLocation()));
                    instructions.add(new Jump(andEndLabel, binaryOp.getLocation()));
                    instructions.add(andSkipLabel);
                    instructions.add(new LoadBoolConst(false, resultVariable, binaryOp.getLocation()));
                    instructions.add(new Jump(andEndLabel, binaryOp.getLocation()));
                    instructions.add(andEndLabel);
                    return resultVariable;
                }
                if (operator.getText().equals("or")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable);
                    Label orRightLabel = new Label("or_right", binaryOp.getLeft().getLocation());
                    Label orSkipLabel = new Label("or_skip", binaryOp.getLeft().getLocation());
                    Label orEndLabel = new Label("or_end", binaryOp.getLocation());
                    instructions.add(new CondJump(leftVariable, orSkipLabel, orRightLabel, binaryOp.getLocation()));
                    instructions.add(orRightLabel);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructions.add(new Copy(rightVariable, resultVariable, binaryOp.getLocation()));
                    instructions.add(new Jump(orEndLabel, binaryOp.getLocation()));
                    instructions.add(orSkipLabel);
                    instructions.add(new LoadBoolConst(true, resultVariable, binaryOp.getLocation()));
                    instructions.add(new Jump(orEndLabel, binaryOp.getLocation()));
                    instructions.add(orEndLabel);
                    return resultVariable;
                }
                if (operator.getText().equals("!=")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructions.add(new Call(createVariable(new InequalityType()), new IRVariable[]{leftVariable,
                            rightVariable}, resultVariable, binaryOp.getLocation()));
                    return resultVariable;
                }
                if (operator.getText().equals("==")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructions.add(new Call(createVariable(new EqualityType()), new IRVariable[]{leftVariable,
                            rightVariable}, resultVariable, binaryOp.getLocation()));
                    return resultVariable;
                }
                IRVariable operatorVariable = symbolTable.getVariable(operator.getText());
                IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable);
                IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable);
                IRVariable result = createVariable(binaryOp.getType());
                instructions.add(new Call(operatorVariable, new IRVariable[]{leftVariable, rightVariable}, result, location));
                return result;
            }
            case Block block: {
                SymbolTable localSymbolTable = new SymbolTable(symbolTable);
                List<Expression> expressionList = block.getExpressionList();
                for (int i = 0; i < expressionList.size() - 1; i++) {
                    visit(expressionList.get(i), localSymbolTable);
                }
                if (!(expressionList.getLast() instanceof Unit)) {
                    return visit(expressionList.getLast(), localSymbolTable);
                }
                return createVariable(new UnitType());
            }
            case ConditionalOp conditionalOp: {
                if (conditionalOp.getElseBlock() != null) {
                    Label thenLabel = new Label("then", conditionalOp.getThenBlock().getLocation());
                    Label elseLabel = new Label("else", location);
                    Label endLabel = new Label("end", location);
                    IRVariable conditionVariable = visit(conditionalOp.getCondition(), symbolTable);
                    instructions.add(new CondJump(conditionVariable, thenLabel, elseLabel, location));
                    instructions.add(thenLabel);
                    List<Expression> expressionList = ((Block) conditionalOp.getThenBlock()).getExpressionList();
                    Type finalType = !expressionList.isEmpty() ? expressionList.get(expressionList.size() - 1).getType() : new UnitType();
                    IRVariable outputVariable = createVariable(finalType);
                    IRVariable thenVariable = visit(conditionalOp.getThenBlock(), symbolTable);
                    instructions.add(new Copy(thenVariable, outputVariable, location));
                    instructions.add(new Jump(endLabel, location));
                    instructions.add(elseLabel);
                    IRVariable elseVariable = visit(conditionalOp.getElseBlock(), symbolTable);
                    instructions.add(new Copy(elseVariable, outputVariable, location));
                    instructions.add(endLabel);
                    return outputVariable;
                } else {
                    Label thenLabel = new Label("then", conditionalOp.getThenBlock().getLocation());
                    Label endLabel = new Label("end", location);
                    IRVariable conditionVariable = visit(conditionalOp.getCondition(), symbolTable);
                    instructions.add(new CondJump(conditionVariable, thenLabel, endLabel, location));
                    instructions.add(thenLabel);
                    visit(conditionalOp.getThenBlock(), symbolTable);
                    instructions.add(endLabel);
                    return createVariable(new UnitType());
                }
            }
            case WhileOp whileOp: {
                Label doLabel = new Label("do", whileOp.getCondition().getLocation());
                Label endLabel = new Label("end", whileOp.getLocation());
                Label startLabel = new Label("while_start", whileOp.getLocation());
                instructions.add(startLabel);
                IRVariable condition = visit(whileOp.getCondition(), symbolTable);
                instructions.add(new CondJump(condition, doLabel, endLabel, whileOp.getBody().getLocation()));
                instructions.add(doLabel);
                visit(whileOp.getBody(), symbolTable);
                instructions.add(new Jump(startLabel, whileOp.getBody().getLocation()));
                instructions.add(endLabel);
                return createVariable(new UnitType());
            }
            case VariableDef variableDef: {
                IRVariable rightSide = visit(variableDef.getValue(), symbolTable);
                IRVariable leftSide = createVariable(variableDef.getType());
                symbolTable.putVariable(variableDef.getName(), leftSide);
                instructions.add(new Copy(rightSide, leftSide, variableDef.getLocation()));
                return createVariable(new UnitType());
            }
            case FunctionCall functionCall: {
                List<Expression> parameters = functionCall.getParameters();
                List<IRVariable> params = new ArrayList<>();
                for (Expression parameter: parameters) {
                    params.add(visit(parameter, symbolTable));
                }
                IRVariable functionVariable = symbolTable.getVariable(functionCall.getFunctionName());
                IRVariable resultVariable = createVariable(functionCall.getType());
                instructions.add(new Call(functionVariable, params.toArray(new IRVariable[]{}),
                        resultVariable, functionCall.getLocation()));
                return resultVariable;
            }
            default: {

            }
        }
        return null;
    }

    private IRVariable createVariable(Type type) {
        IRVariable variable = IRVariable.createVariable(type);
        variableTypeMap.put(variable, type);
        return variable;
    }
}
