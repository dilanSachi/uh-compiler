package fi.helsinki.compiler.tokenizer;

public class TokenLocation {
    private String file;
    private int line;
    private int column;

    public TokenLocation(String file, int line, int column) {
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
        return obj instanceof TokenLocation tokenLocation &&
                tokenLocation.getFile().equals(getFile()) &&
                tokenLocation.getColumn() == getColumn() &&
                tokenLocation.getLine() == getLine();
    }
}
