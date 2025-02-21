package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.exceptions.IRGenerationException;

import java.util.HashMap;
import java.util.Optional;

public class SymbolTable {
    private SymbolTable parent;
    private HashMap<String, IRVariable> symbols;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.symbols = new HashMap();
    }

    public IRVariable getVariable(String variableName) throws IRGenerationException {
        if (symbols.containsKey(variableName)) {
            return symbols.get(variableName);
        }
        if (parent == null) {
            throw new IRGenerationException("IRVariable '" + variableName + "' not found in the context");
        }
        return parent.getVariable(variableName);
    }

    public boolean hasVariableLocally(String key) {
        return symbols.containsKey(key);
    }

    public Optional<SymbolTable> getVariableOwner(String variableName) {
        if (symbols.containsKey(variableName)) {
            return Optional.of(this);
        }
        if (parent == null) {
            return Optional.empty();
        }
        return parent.getVariableOwner(variableName);
    }

    public void putVariable(String key, IRVariable irVariable) {
        symbols.put(key, irVariable);
    }
}
