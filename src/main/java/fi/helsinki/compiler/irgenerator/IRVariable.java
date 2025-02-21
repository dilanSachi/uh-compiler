package fi.helsinki.compiler.irgenerator;

/*
    Represents the name of a memory location or built-in.
 */
public class IRVariable {
    private String name;

    public IRVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
