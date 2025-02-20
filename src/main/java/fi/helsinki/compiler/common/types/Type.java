package fi.helsinki.compiler.common.types;

public abstract class Type {
    public abstract String getType();

    @Override
    public boolean equals(Object obj) {
        return ((Type) obj).getType().equals(getType());
    }

    @Override
    public String toString() {
        return getType();
    }
}
