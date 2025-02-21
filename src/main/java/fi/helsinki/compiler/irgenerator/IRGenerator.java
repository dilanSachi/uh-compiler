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
        variableTypeMap = new HashMap<>();
        setPredefinedVariableTypes();
    }

    private void setPredefinedVariableTypes() {
        createVariable(new AdditionType());
        createVariable(new GreaterThanType());
    }

    public List<Instruction> generateIR(Expression rootExpression) throws IRGenerationException {
        instructions = new ArrayList<>();
        instructions.add(new Label("start", rootExpression.getLocation()));
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
            case BinaryOp binaryOp: {
                Token operator = binaryOp.getOperator();
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
