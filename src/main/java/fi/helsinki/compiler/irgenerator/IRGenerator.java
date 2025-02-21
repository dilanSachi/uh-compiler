package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.expressions.BooleanLiteral;
import fi.helsinki.compiler.common.expressions.Expression;
import fi.helsinki.compiler.common.Location;
import fi.helsinki.compiler.common.expressions.Identifier;
import fi.helsinki.compiler.common.types.BooleanType;
import fi.helsinki.compiler.common.types.IntType;
import fi.helsinki.compiler.common.types.Type;
import fi.helsinki.compiler.exceptions.IRGenerationException;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;
import fi.helsinki.compiler.irgenerator.instructions.LoadBoolConst;
import fi.helsinki.compiler.irgenerator.instructions.LoadIntConst;
import fi.helsinki.compiler.common.expressions.IntLiteral;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IRGenerator {

    public List<Instruction> generateIR(Map<IRVariable, Type> rootTypes, Expression rootExpression) throws IRGenerationException {
        Map<IRVariable, Type> variableTypes = rootTypes;
        IRVariable unitVariable = new IRVariable("unit");
        List<Instruction> irInstructions = new ArrayList<>();
        SymbolTable rootSymbolTable = new SymbolTable(null);
        for (IRVariable key: variableTypes.keySet()) {
            rootSymbolTable.putVariable(key.getName(), key);
        }
        IRVariable finalResult = visit(rootExpression, rootSymbolTable, irInstructions, variableTypes);
        if (variableTypes.get(finalResult) instanceof IntType) {
            // call print_int
        } else if (variableTypes.get(finalResult) instanceof BooleanType) {
            // call print_bool
        }
        return irInstructions;
    }

    public IRVariable visit(Expression expression, SymbolTable symbolTable, List<Instruction> instructions,
                            Map<IRVariable, Type> variableTypeMap) throws IRGenerationException {
        Location location = expression.getLocation();
        switch (expression) {
            case IntLiteral intLiteral: {
                IRVariable variable = newVariable(new IntType(), variableTypeMap);
                Instruction instruction = new LoadIntConst(((IntLiteral) expression).getValue(), variable, location);
                instructions.add(instruction);
                return variable;
            }
            case BooleanLiteral booleanLiteral: {
                IRVariable variable = newVariable(new BooleanType(), variableTypeMap);
                Instruction instruction = new LoadBoolConst(((BooleanLiteral) expression).getValue(), variable, location);
                instructions.add(instruction);
                return variable;
            }
            case Identifier identifier: {
                return symbolTable.getVariable(identifier.getName());
            }

            default: {

            }
        }
        return null;
    }

    public IRVariable newVariable(Type type, Map<IRVariable, Type> variableTypes) {
        IRVariable variable = new IRVariable(type.getType());
        variableTypes.put(variable, type);
        return variable;
    }
}
