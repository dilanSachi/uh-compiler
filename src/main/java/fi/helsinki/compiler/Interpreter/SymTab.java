package fi.helsinki.compiler.Interpreter;

import fi.helsinki.compiler.exceptions.InterpreterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SymTab {

    private final SymTab parent;
    private final Map<String, Value> symbols = new HashMap<>();

    public SymTab(SymTab parent) {
        this.parent = parent;
    }

    public Value getValue(String key) throws InterpreterException {
        if (symbols.containsKey(key)) {
            return symbols.get(key);
        }
        if (parent == null) {
            throw new InterpreterException("");
        }
        return parent.getValue(key);
    }

    public boolean hasValueLocally(String key) {
        return symbols.containsKey(key);
    }

    public boolean hasValue(String key) {
        if (hasValueLocally(key)) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        return parent.hasValue(key);
    }

    public Optional<SymTab> getSymbolOwner(String key) {
        if (hasValueLocally(key)) {
            return Optional.of(this);
        }
        if (parent == null) {
            return Optional.empty();
        }
        return parent.getSymbolOwner(key);
    }

    public void setValue(String key, Value value) {
        symbols.put(key, value);
    }
}
