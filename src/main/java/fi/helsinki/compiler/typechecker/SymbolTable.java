package fi.helsinki.compiler.typechecker;

import fi.helsinki.compiler.exceptions.TypeCheckerException;
import fi.helsinki.compiler.common.types.Type;

import java.util.HashMap;
import java.util.Optional;

public class SymbolTable {
    private SymbolTable parent;
    private HashMap<String, Type> symbols;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.symbols = new HashMap();
    }

    public Type getType(String typeName) throws TypeCheckerException {
        if (symbols.containsKey(typeName)) {
            return symbols.get(typeName);
        }
        if (parent == null) {
            throw new TypeCheckerException("Type '" + typeName + "' not found in the context");
        }
        return parent.getType(typeName);
    }

    public boolean hasTypeLocally(String key) {
        return symbols.containsKey(key);
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

    public void putType(String key, Type type) {
        symbols.put(key, type);
    }
}
