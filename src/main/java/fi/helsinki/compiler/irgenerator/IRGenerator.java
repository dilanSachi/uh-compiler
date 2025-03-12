package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.CommonStatics;
import fi.helsinki.compiler.common.expressions.*;
import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.common.types.*;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.irgenerator.instructions.*;
import fi.helsinki.compiler.tokenizer.Token;

import java.util.*;

public class IRGenerator {

    private Map<IRVariable, Type> variableTypeMap;
    private List<Instruction> instructions = new ArrayList<>();;
    private List<Instruction> functionDefinitions = new ArrayList<>();
    private CommonStatics commonStatics;
    private Stack<Label> whileStartLabelStack = new Stack<>();
    private Stack<Label> whileEndLabelStack = new Stack<>();

    public IRGenerator(CommonStatics commonStatics) {
        this.commonStatics = commonStatics;
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
        createVariable(new FunctionType("print_bool", new UnitType(), new BooleanType()));
        createVariable(new FunctionType("read_int", new IntType(), new UnitType()));
    }

    public List<Instruction> generateIR(Expression rootExpression) throws IRGenerationException {
        SymbolTable rootSymbolTable = new SymbolTable(null);
        for (IRVariable key: variableTypeMap.keySet()) {
            rootSymbolTable.putVariable(key.getType().getTypeStr(), key);
        }
        IRVariable finalResult = visit(rootExpression, rootSymbolTable, instructions);
        if (variableTypeMap.get(finalResult) instanceof IntType) {
            instructions.add(new Call(createVariable(new FunctionType("print_int", new UnitType(), new IntType())),
                    new IRVariable[]{finalResult}, createVariable(new UnitType()), rootExpression.getLocation()));
        } else if (variableTypeMap.get(finalResult) instanceof BooleanType) {
            instructions.add(new Call(createVariable(new FunctionType("print_bool", new UnitType(), new BooleanType())),
                    new IRVariable[]{finalResult}, createVariable(new UnitType()), rootExpression.getLocation()));
        }
        if (!functionDefinitions.isEmpty()) {
            List<Instruction> moduleInstructions = new ArrayList<>();
            moduleInstructions.addAll(functionDefinitions);
            instructions.add(new ReturnIns(new IRVariable("None", new UnitType()), rootExpression.getLocation()));
            FunctionDefinitionIns mainFunctionDefIns = new FunctionDefinitionIns("main", instructions, rootExpression.getLocation());
            moduleInstructions.add(mainFunctionDefIns);
            return moduleInstructions;
        }
        return instructions;
    }

    private IRVariable visit(Expression expression, SymbolTable symbolTable, 
                             List<Instruction> instructionList) throws IRGenerationException {
        Location location = expression.getLocation();
        switch (expression) {
            case IntLiteral intLiteral: {
                IRVariable variable = createVariable(new IntType());
                Instruction instruction = new LoadIntConst(intLiteral.getValue(), variable, location);
                instructionList.add(instruction);
                return variable;
            }
            case BooleanLiteral booleanLiteral: {
                IRVariable variable = createVariable(new BooleanType());
                Instruction instruction = new LoadBoolConst(booleanLiteral.getValue(), variable, location);
                instructionList.add(instruction);
                return variable;
            }
            case Identifier identifier: {
                return symbolTable.getVariable(identifier.getName());
            }
            case UnaryOp unaryOp: {
                IRVariable variable = visit(unaryOp.getExpression(), symbolTable, instructionList);
                IRVariable resultVariable = createVariable(unaryOp.getType());
                IRVariable operatorVariable;
                if (unaryOp.getOperator().getText().equals("-")) {
                    operatorVariable = createVariable(new NegationType());
                } else {
                    operatorVariable = createVariable(new NotType());
                }
                instructionList.add(new Call(operatorVariable, new IRVariable[]{variable}, resultVariable, unaryOp.getLocation()));
                return resultVariable;
            }
            case BinaryOp binaryOp: {
                Token operator = binaryOp.getOperator();
                if (operator.getText().equals("=")) {
                    IRVariable variable = visit(binaryOp.getRight(), symbolTable, instructionList);
                    IRVariable identifier = symbolTable.getVariable(((Identifier) binaryOp.getLeft()).getName());
                    instructionList.add(new Copy(variable, identifier, binaryOp.getLeft().getLocation()));
                    return identifier;
                }
                if (operator.getText().equals("and")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable, instructionList);
                    Label andRightLabel = new Label(commonStatics, "and_right", binaryOp.getLeft().getLocation());
                    Label andSkipLabel = new Label(commonStatics, "and_skip", binaryOp.getLeft().getLocation());
                    Label andEndLabel = new Label(commonStatics, "and_end", binaryOp.getLocation());
                    instructionList.add(new CondJump(leftVariable, andRightLabel, andSkipLabel, binaryOp.getLocation()));
                    instructionList.add(andRightLabel);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable, instructionList);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructionList.add(new Copy(rightVariable, resultVariable, binaryOp.getLocation()));
                    instructionList.add(new Jump(andEndLabel, binaryOp.getLocation()));
                    instructionList.add(andSkipLabel);
                    instructionList.add(new LoadBoolConst(false, resultVariable, binaryOp.getLocation()));
                    instructionList.add(new Jump(andEndLabel, binaryOp.getLocation()));
                    instructionList.add(andEndLabel);
                    return resultVariable;
                }
                if (operator.getText().equals("or")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable, instructionList);
                    Label orRightLabel = new Label(commonStatics, "or_right", binaryOp.getLeft().getLocation());
                    Label orSkipLabel = new Label(commonStatics, "or_skip", binaryOp.getLeft().getLocation());
                    Label orEndLabel = new Label(commonStatics, "or_end", binaryOp.getLocation());
                    instructionList.add(new CondJump(leftVariable, orSkipLabel, orRightLabel, binaryOp.getLocation()));
                    instructionList.add(orRightLabel);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable, instructionList);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructionList.add(new Copy(rightVariable, resultVariable, binaryOp.getLocation()));
                    instructionList.add(new Jump(orEndLabel, binaryOp.getLocation()));
                    instructionList.add(orSkipLabel);
                    instructionList.add(new LoadBoolConst(true, resultVariable, binaryOp.getLocation()));
                    instructionList.add(new Jump(orEndLabel, binaryOp.getLocation()));
                    instructionList.add(orEndLabel);
                    return resultVariable;
                }
                if (operator.getText().equals("!=")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable, instructionList);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable, instructionList);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructionList.add(new Call(createVariable(new InequalityType()), new IRVariable[]{leftVariable,
                            rightVariable}, resultVariable, binaryOp.getLocation()));
                    return resultVariable;
                }
                if (operator.getText().equals("==")) {
                    IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable, instructionList);
                    IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable, instructionList);
                    IRVariable resultVariable = createVariable(binaryOp.getType());
                    instructionList.add(new Call(createVariable(new EqualityType()), new IRVariable[]{leftVariable,
                            rightVariable}, resultVariable, binaryOp.getLocation()));
                    return resultVariable;
                }
                IRVariable operatorVariable = symbolTable.getVariable(operator.getText());
                IRVariable leftVariable = visit(binaryOp.getLeft(), symbolTable, instructionList);
                IRVariable rightVariable = visit(binaryOp.getRight(), symbolTable, instructionList);
                IRVariable result = createVariable(binaryOp.getType());
                instructionList.add(new Call(operatorVariable, new IRVariable[]{leftVariable, rightVariable}, result, location));
                return result;
            }
            case Block block: {
                SymbolTable localSymbolTable = new SymbolTable(symbolTable);
                List<Expression> expressionList = block.getExpressionList();
                for (int i = 0; i < expressionList.size() - 1; i++) {
                    visit(expressionList.get(i), localSymbolTable, instructionList);
                }
                if (!(expressionList.getLast() instanceof Unit)) {
                    return visit(expressionList.getLast(), localSymbolTable, instructionList);
                }
                return createVariable(new UnitType());
            }
            case ConditionalOp conditionalOp: {
                if (conditionalOp.getElseBlock() != null) {
                    Label thenLabel = new Label(commonStatics, "then", conditionalOp.getThenBlock().getLocation());
                    Label elseLabel = new Label(commonStatics, "else", location);
                    Label endLabel = new Label(commonStatics, "end", location);
                    IRVariable conditionVariable = visit(conditionalOp.getCondition(), symbolTable, instructionList);
                    instructionList.add(new CondJump(conditionVariable, thenLabel, elseLabel, location));
                    instructionList.add(thenLabel);
                    Type finalType;
                    if (conditionalOp.getThenBlock() instanceof Block) {
                        List<Expression> expressionList = ((Block) conditionalOp.getThenBlock()).getExpressionList();
                        finalType = !expressionList.isEmpty() ? expressionList.get(expressionList.size() - 1).getType() : new UnitType();
                    } else {
                        finalType = conditionalOp.getThenBlock().getType();
                    }
                    IRVariable outputVariable = createVariable(finalType);
                    IRVariable thenVariable = visit(conditionalOp.getThenBlock(), symbolTable, instructionList);
                    instructionList.add(new Copy(thenVariable, outputVariable, location));
                    instructionList.add(new Jump(endLabel, location));
                    instructionList.add(elseLabel);
                    IRVariable elseVariable = visit(conditionalOp.getElseBlock(), symbolTable, instructionList);
                    instructionList.add(new Copy(elseVariable, outputVariable, location));
                    instructionList.add(endLabel);
                    return outputVariable;
                } else {
                    Label thenLabel = new Label(commonStatics, "then", conditionalOp.getThenBlock().getLocation());
                    Label endLabel = new Label(commonStatics, "end", location);
                    IRVariable conditionVariable = visit(conditionalOp.getCondition(), symbolTable, instructionList);
                    instructionList.add(new CondJump(conditionVariable, thenLabel, endLabel, location));
                    instructionList.add(thenLabel);
                    visit(conditionalOp.getThenBlock(), symbolTable, instructionList);
                    instructionList.add(endLabel);
                    return createVariable(new UnitType());
                }
            }
            case WhileOp whileOp: {
                Label doLabel = new Label(commonStatics, "do", whileOp.getCondition().getLocation());
                Label endLabel = new Label(commonStatics, "end", whileOp.getLocation());
                whileEndLabelStack.push(endLabel);
                Label startLabel = new Label(commonStatics, "while_start", whileOp.getLocation());
                whileStartLabelStack.push(startLabel);
                instructionList.add(startLabel);
                IRVariable condition = visit(whileOp.getCondition(), symbolTable, instructionList);
                instructionList.add(new CondJump(condition, doLabel, endLabel, whileOp.getBody().getLocation()));
                instructionList.add(doLabel);
                visit(whileOp.getBody(), symbolTable, instructionList);
                instructionList.add(new Jump(startLabel, whileOp.getBody().getLocation()));
                instructionList.add(endLabel);
                return createVariable(new UnitType());
            }
            case VariableDef variableDef: {
                IRVariable rightSide = visit(variableDef.getValue(), symbolTable, instructionList);
                IRVariable leftSide = createVariable(variableDef.getType());
                symbolTable.putVariable(variableDef.getName(), leftSide);
                instructionList.add(new Copy(rightSide, leftSide, variableDef.getLocation()));
                return createVariable(new UnitType());
            }
            case FunctionCall functionCall: {
                List<Expression> parameters = functionCall.getParameters();
                List<IRVariable> params = new ArrayList<>();
                for (Expression parameter: parameters) {
                    params.add(visit(parameter, symbolTable, instructionList));
                }
                IRVariable functionVariable = symbolTable.getVariable(functionCall.getFunctionName());
                IRVariable resultVariable = createVariable(functionCall.getType());
                instructionList.add(new Call(functionVariable, params.toArray(new IRVariable[]{}),
                        resultVariable, functionCall.getLocation()));
                return resultVariable;
            }
            case Break breakOp: {
                Jump jumpIns = new Jump(whileEndLabelStack.pop(), breakOp.getLocation());
                instructionList.add(jumpIns);
                return createVariable(new UnitType());
            }
            case Continue continueOp: {
                Jump jumpIns = new Jump(whileStartLabelStack.pop(), continueOp.getLocation());
                instructionList.add(jumpIns);
                return createVariable(new UnitType());
            }
            case FunctionDefinition functionDefinition: {
                SymbolTable localSymbolTable = new SymbolTable(symbolTable);
                for (FunctionArgumentDefinition argument : functionDefinition.getArguments()) {
                    IRVariable variable = new IRVariable(argument.getName(), argument.getType());
                    localSymbolTable.putVariable(argument.getName(), variable);
                }
                List<Instruction> functionInstructions = new ArrayList<>();
                visit(functionDefinition.getBlock(), localSymbolTable, functionInstructions);
                FunctionDefinitionIns funcDefInstruction = new FunctionDefinitionIns(functionDefinition.getFunctionName(),
                        functionInstructions, functionDefinition.getLocation());
                functionDefinitions.add(funcDefInstruction);
                IRVariable functionVariable = new IRVariable(functionDefinition.getFunctionName(), functionDefinition.getType());
                symbolTable.putVariable(functionDefinition.getFunctionName(), functionVariable);
                return functionVariable;
            }
            case Return returnDef: {
                IRVariable valueVariable = visit(returnDef.getValue(), symbolTable, instructionList);
                instructionList.add(new ReturnIns(valueVariable, returnDef.getLocation()));
                return valueVariable;
            }
            default: {
                throw new IRGenerationException("Invalid Expression found");
            }
        }
    }

    private IRVariable createVariable(Type type) {
        IRVariable variable = new IRVariable(commonStatics, type);
        variableTypeMap.put(variable, type);
        return variable;
    }
}
