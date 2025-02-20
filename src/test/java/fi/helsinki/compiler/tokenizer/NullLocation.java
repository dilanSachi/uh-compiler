package fi.helsinki.compiler.tokenizer;

import fi.helsinki.compiler.common.Location;

public class NullLocation extends Location {
    public NullLocation() {
        super(null, 0, 0);
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
