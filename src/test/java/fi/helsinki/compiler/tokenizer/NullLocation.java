package fi.helsinki.compiler.tokenizer;

public class NullLocation extends TokenLocation {
    public NullLocation() {
        super(null, 0, 0);
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
