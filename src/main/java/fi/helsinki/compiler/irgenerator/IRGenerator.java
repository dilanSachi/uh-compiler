package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.*;
import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.common.types.BooleanType;
import fi.helsinki.compiler.common.types.IntType;
import fi.helsinki.compiler.common.types.Type;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.irgenerator.instructions.Call;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.irgenerator.instructions.LoadBoolConst;
import fi.helsinki.compiler.irgenerator.instructions.LoadIntConst;
import fi.helsinki.compiler.tokenizer.Token;

import java.util.*;

public class IRGenerator {

    private Map<IRVariable, Type> variableTypeMap;
    private List<Instruction> instructions;

    public IRGenerator() {
        variableTypeMap = new HashMap<>();
        instructions = new ArrayList<>();
    }

    public List<Instruction> generateIR(Expression rootExpression) throws IRGenerationException {
        IRVariable unitVariable = new IRVariable("unit");
        List<Instruction> irInstructions = new ArrayList<>();
        SymbolTable rootSymbolTable = new SymbolTable(null);
        for (IRVariable key: variableTypeMap.keySet()) {
            rootSymbolTable.putVariable(key.getName(), key);
        }
        IRVariable finalResult = visit(rootExpression, rootSymbolTable);
        if (variableTypeMap.get(finalResult) instanceof IntType) {
            // call print_int
        } else if (variableTypeMap.get(finalResult) instanceof BooleanType) {
            // call print_bool
        }
        return irInstructions;
    }

    public IRVariable visit(Expression expression, SymbolTable symbolTable) throws IRGenerationException {
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
            default: {

            }
        }
        return null;
    }

    public IRVariable createVariable(Type type) {
        IRVariable variable = new IRVariable(type.getType());
        variableTypeMap.put(variable, type);
        return variable;
    }
}
