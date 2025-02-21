package fi.helsinki.compiler.irgenerator;

import fi.helsinki.compiler.common.types.Type;

/*
    Represents the name of a memory location or built-in.
 */
public class IRVariable {
    private String name;
    private Type type;
    private static int counter = -1;

    private IRVariable(int counter, Type type) {
        this.name = "x" + counter;
        this.type = type;
    }

    public static IRVariable createVariable(Type type) {
        counter += 1;
        return new IRVariable(counter, type);
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
