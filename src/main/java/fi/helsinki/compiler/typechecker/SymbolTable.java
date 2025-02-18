package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.exceptions.InterpreterException;
import fi.helsinki.compiler.typechecker.types.Type;

import java.util.HashMap;
import java.util.Optional;

public class SymbolTable {
    private SymbolTable parent;
    private HashMap<String, Type> symbols;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.symbols = new HashMap();
    }

    public Type getValue(String typeName) throws InterpreterException {
        if (symbols.containsKey(typeName)) {
            return symbols.get(typeName);
        }
        if (parent == null) {
            throw new InterpreterException("Type '" + typeName + "' not found in the context");
        }
        return parent.getValue(typeName);
    }

    public Optional<SymbolTable> getTypeOwner(String typeName) {
        if (symbols.containsKey(typeName)) {
            return Optional.of(this);
        }
        if (parent == null) {
            return Optional.empty();
        }
        return parent.getTypeOwner(typeName);
    }

    public void putValue(String key, Type type) {
        symbols.put(key, type);
    }
}
