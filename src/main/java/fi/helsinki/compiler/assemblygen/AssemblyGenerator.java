package fi.helsinki.compiler.assemblygen;

import fi.helsinki.compiler.irgenerator.IRVariable;
import fi.helsinki.compiler.irgenerator.instructions.Instruction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssemblyGenerator {

    private List<IRVariable> resultList = new ArrayList<>();
    private Set<IRVariable> resultSet = new HashSet<>();

    public String generateAssembly(List<Instruction> instructions) {
        return null;
    }

    private List<IRVariable> getAllIRVariables(List<Instruction> instructions) throws ClassNotFoundException, IllegalAccessException {
        for (int i = 0; i < instructions.size(); i++) {
            Class instructionClass = Class.forName(instructions.get(i).getClass().getName());
            for (Field field : instructionClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().getName().equals(IRVariable.class.getName())) {
                    add((IRVariable) field.get(instructions.get(i)));
                } else if (field.getType().getName().equals(List.class.getName())) {
                    List<?> variableList = (List<?>) field.get(instructions.get(i));
                    for (int j = 0; j < variableList.size(); j++) {
                        if (variableList.get(j) instanceof IRVariable) {
                            add((IRVariable) variableList.get(j));
                        }
                    }
                }
            }
        }
        return null;
    }

    private void add(IRVariable variable) {
        if (!resultSet.contains(variable)) {
            resultList.add(variable);
            resultSet.add(variable);
        }
    }
}
