package fi.helsinki.compiler.tokenizer;

public class NullLocation extends TokenLocation {
    public NullLocation(String file, int line, int column) {
        super(file, line, column);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
