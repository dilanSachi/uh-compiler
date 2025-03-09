package fi.helsinki.compiler.assemblygen;

import fi.helsinki.compiler.irgenerator.IRVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Locals {

    private Map<IRVariable, String> variableToLocationMap = new HashMap<>();
    private int stackUsed = 0;

    public Locals(List<IRVariable> irVariables) {
        for (int i = 0; i < irVariables.size(); i++) {
            stackUsed -= 8;
            variableToLocationMap.put(irVariables.get(i), stackUsed + "(%rbp)");
        }
    }

    public String getRef(IRVariable irVariable) {
        return variableToLocationMap.get(irVariable);
    }

    public int getStackUsed() {
        return -stackUsed;
    }
}
