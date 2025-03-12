package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.CommonStatics;
import fi.helsinki.compiler.common.types.Type;

/*
    Represents the name of a memory location or built-in.
 */
public class IRVariable {
    private String name;
    private Type type;

    public IRVariable(CommonStatics commonStatics, Type type) {
        this.name = "x" + commonStatics.getIRVariableCounter();
        // need to check whether we need to hardcode unittype variable
        this.type = type;
    }

    public IRVariable(String varName, Type type) {
        this.name = varName;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
