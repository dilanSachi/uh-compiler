package fi.helsinki.compiler.common;

public class Location {
    private String file;
    private int line;
    private int column;

    public Location(String file, int line, int column) {
        this.file = file;
        this.line = line;
        this.column = column;
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Location location &&
                location.getFile().equals(getFile()) &&
                location.getColumn() == getColumn() &&
                location.getLine() == getLine();
    }

    @Override
    public String toString() {
        return file + ": " + "L->" + line + ", C->" + column;
    }
}
