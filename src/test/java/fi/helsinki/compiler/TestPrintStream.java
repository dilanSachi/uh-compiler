package fi.helsinki.compiler;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class TestPrintStream extends PrintStream {
    String[] output;

    public TestPrintStream(String[] output) throws FileNotFoundException {
        super("/tmp/comp_test.txt");
        this.output = output;
    }

    public void println(Object x) {
        this.output[0] = x.toString();
    }

    public void println(int x) {
        this.output[0] = Integer.toString(x);
    }

    public void println(String x) {
        this.output[0] = x;
    }
}
