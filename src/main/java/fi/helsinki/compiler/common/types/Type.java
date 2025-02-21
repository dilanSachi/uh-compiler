package fi.helsinki.compiler.common.types;

public abstract class Type {
    public abstract String getTypeStr();

    @Override
    public boolean equals(Object obj) {
        return ((Type) obj).getTypeStr().equals(getTypeStr());
    }

    @Override
    public String toString() {
        return getTypeStr();
    }
}
